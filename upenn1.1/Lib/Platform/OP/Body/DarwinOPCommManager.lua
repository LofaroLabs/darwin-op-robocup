module(..., package.seeall);

local cwd = unix.getcwd();
package.path = cwd.."/../Config/?.lua;"..package.path;

require('DynamixelPacket');
require('Dynamixel');
require('unix');
require('shm');
require('carray');
require('vector');
require('Config')

idMap = {
   19, 20, --Head
   2, 4, 6, --LArm
   8, 10, 12, 14, 16, 18, --LLeg
   7, 9, 11, 13, 15, 17, --RLeg
   1, 3, 5, --RArm 
};
nJoint = #idMap;

moveRange = Config.servo.moveRange;
maxSteps = Config.servo.maxSteps;
posZero = Config.servo.posZero;
scale = maxSteps/math.rad(moveRange)*vector.ones(nJoint);
--scale = 1024/math.rad(300)*vector.ones(nJoint);

--Reverse directions of following joints:
dirReverse = {
   2, -- Head
   4, -- LArm
   6,7,8,9, -- LLeg
   12,13,16, --RLeg
   18,19,20, -- RArm
}; --


for i = 1,#dirReverse do
   scale[dirReverse[i]] = -scale[dirReverse[i]];
end

-- Robot specific leg bias 
legBias = Config.servo.legBias;
for i=1,12 do posZero[i+5] = posZero[i+5] + legBias[i]; end

-- Setup shared memory
function shm_init()
   shm.destroy('dcmSensor');
   sensorShm = shm.new('dcmSensor');
   sensorShm.time = vector.zeros(1);
   sensorShm.count = vector.zeros(1);
   sensorShm.position = vector.zeros(nJoint);
   sensorShm.button = vector.zeros(2); 
   sensorShm.imuAngle = vector.zeros(3);
   sensorShm.imuAcc = vector.zeros(3);
   sensorShm.imuGyr = vector.zeros(3);

   shm.destroy('dcmActuator');
   actuatorShm = shm.new('dcmActuator');
   actuatorShm.command = vector.zeros(nJoint);
   actuatorShm.velocity = vector.zeros(nJoint);
   actuatorShm.hardness = vector.zeros(nJoint);
   actuatorShm.offset = vector.zeros(nJoint);
   actuatorShm.led = vector.zeros(1);

   actuatorShm.velocityChanged=vector.zeros(1);
   actuatorShm.hardnessChanged=vector.zeros(1);
   actuatorShm.backled = vector.zeros(3);  --red blue green
   actuatorShm.eyeled = vector.zeros(3);   --RGB15 eye led
   actuatorShm.headled = vector.zeros(3);  --RGB15 head led
   actuatorShm.readType=vector.zeros(1);   --0: Head only 1: All servos
end

-- Setup CArray mappings into shared memory
function carray_init()
   sensor = {};
   for k,v in sensorShm.next, sensorShm do
      sensor[k] = carray.cast(sensorShm:pointer(k));
   end

   actuator = {};
   for k,v in actuatorShm.next, actuatorShm do
      actuator[k] = carray.cast(actuatorShm:pointer(k));
   end
end

function sync_command()
   local addr = 30;
   local ids = {};
   local data = {};
   local n = 0;
   for i = 1,#idMap do
      if (actuator.hardness[i] > 0) then
	 n = n+1;
	 ids[n] = idMap[i];
	 local word = posZero[i] + scale[i]*
	    (actuator.command[i]+actuator.offset[i]);
	 data[n] = math.min(math.max(word, 0), maxSteps-1);
      else
	 actuator.command[i] = sensor.position[i];
      end
   end
   if (n > 0) then
      Dynamixel.sync_write_word(ids, addr, data);
   end
end

function sync_hardness()
   local addr=34; 
   local ids = {};
   local data = {};
   local n = 0;
   for i = 1,#idMap do
	 n = n+1;
	 ids[n] = idMap[i];
	 data[n] = 1023*actuator.hardness[i];
   end
   if (n > 0) then
      Dynamixel.sync_write_word(ids, addr, data);
   end
end


function sync_velocity()
   local addr = 32;
   local ids = {};
   local data = {};
   local n = 0;
   for i = 1,#idMap do
      if (actuator.hardness[i] > 0) then
	 n = n+1;
	 ids[n] = idMap[i];
	 data[n] = 1023*actuator.velocity[i];
      end
   end
   if (n > 0) then
      Dynamixel.sync_write_word(ids, addr, data);
   end
end


function sync_led()
   local packet;

   --Eye LED
   packet=actuator.eyeled[1]+32*actuator.eyeled[2]+1024*actuator.eyeled[3];
   Dynamixel.sync_write_word({200},28,{packet});
   unix.usleep(100);

   --Head LED
   packet=actuator.headled[1]+32*actuator.headled[2]+1024*actuator.headled[3];
   Dynamixel.sync_write_word({200},26,{packet});
   unix.usleep(100);


   --Back LED	
   packet=actuator.backled[1]+2*actuator.backled[2]+4*actuator.backled[3];
   Dynamixel.sync_write_byte({200},25,{packet});
   unix.usleep(100);

end

function nonsync_read()

  --Position reading
	local idMax=2;
	if actuator.readType[1]==1 then idMax=#idMap; end
	for i = 1,idMax do
 	    local id = idMap[i];
	    local raw=Dynamixel.get_position(id);
	    if raw then
		    sensor.position[i] = (raw-posZero[i])/scale[i] - actuator.offset[i];
	    end
	end
  --IMU reading
  --Gyr: 0.0008V / dps / (1.5 /512) V/step
	local data=Dynamixel.read_data(200,38,12);
	local offset=1;
	if data then
		for i=1,3 do
	       		sensor.imuGyr[i] = (1/0.273)*(DynamixelPacket.byte_to_word(data[offset],data[offset+1])-512);
			sensor.imuAcc[i] = (1/128)*(DynamixelPacket.byte_to_word(data[offset+6],data[offset+7])-512);
	   	      offset=offset+2;
		end
--Quick and dirty imu angle filter
		local angX=-math.atan2(sensor.imuAcc[1],sensor.imuAcc[3]);
		local angY=-math.atan2(sensor.imuAcc[2],sensor.imuAcc[3]);
		local alpha=0.1;
		sensor.imuAngle[1],sensor.imuAngle[2]=
		(1-alpha)*sensor.imuAngle[1]+alpha*angX,
		(1-alpha)*sensor.imuAngle[2]+alpha*angY;
	end
--Button reading
	data=Dynamixel.read_data(200,30,1);
	if data then
	sensor.button[1]=math.floor(data[1]/2);
	sensor.button[2]=data[1]%2;
	end
end

function entry()
   Dynamixel.open();

--Turning on the microcontroller
   Dynamixel.set_torque_enable(200,1);
   unix.usleep(200000);
   Dynamixel.ping_probe();
   shm_init();
   carray_init();
   actuator.readType[1]=1;
end

nButton = 0;
count = 0;

function update()
   count=count+1;
   nonsync_read();
   sync_command();
   unix.usleep(100);
   if count%20==0 then
     sync_led();
   end

   if actuator.hardnessChanged[1]==1 then
	sync_hardness();
	unix.usleep(100);
	actuator.hardnessChanged[1]=0;
   end
   if actuator.velocityChanged[1]==1 then
        sync_velocity();
	actuator.velocityChanged[1]=0;
        unix.usleep(100);
   end
end

function exit()
   Dynamixel.close();
end
