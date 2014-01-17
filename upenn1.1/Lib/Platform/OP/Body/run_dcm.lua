require('unix');
require('shm');
dcm = require('DarwinOPCommManager');

tDelay = 0.010;
ncount = 20;

print('Starting device comm manager...');
dcm.entry()

sensorShm = shm.open('dcmSensor');
actuatorShm = shm.open('dcmActuator');

print('Running controller');
loop = true;
count = 0;
t0 = unix.time();
while (loop) do
   count = count + 1;
   unix.usleep(1E6*tDelay);
   dcm.update()
   if (count % ncount == 0) then
      local t1 = unix.time();
      print(string.format("Controller update: %f FPS\n", ncount/(t1-t0)));
      t0 = t1;

      print(string.format("IMU Acc: %.3f %.3f %.3f",
			  unpack(sensorShm:get('imuAcc'))));
      print(string.format("IMU Gyr: %.3f %.3f %.3f",
			  unpack(sensorShm:get('imuGyr'))));

      local iangle=sensorShm:get('imuAngle');
      print(string.format("IMU Angle: %.3f %.3f",iangle[1]*180/math.pi,iangle[2]*180/math.pi));
      print(string.format("Button: %d %d",  unpack(sensorShm:get('button'))));      
      print(string.format("Position:\n Head: %f %f\n Larm: %f %f %f\n Lleg: %f %f %f %f %f %f\n Rleg:  %f %f %f %f %f %f\n Rarm: %f %f %f\n",
			  unpack(vector.new(sensorShm:get('position'))*180/math.pi)
		    ));
--[[
      print(string.format("Command:\n %f %f\n %f %f %f\n %f %f %f %f %f %f\n %f %f %f %f %f %f\n %f %f %f\n",
			  unpack(vector.new(actuatorShm:get('command'))*180/math.pi)
		    ));
      print(string.format("Hardness:\n %f %f\n %f %f %f\n %f %f %f %f %f %f\n %f %f %f %f %f %f\n %f %f %f\n",
			  unpack(actuatorShm:get('hardness'))
		    ));
--]]
   end
end

dcm.exit()
