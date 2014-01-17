module(..., package.seeall);
require('controller');

controller.wb_robot_init();
timeStep = controller.wb_robot_get_basic_time_step();
tDelta = .001*timeStep;

-- Get webots tags:
tags = {};

-- Minihubo joint names in webots
jointNames = { --No head 
              "LSP", "LSR", "LSY","LEP",
              "LHY", "LHR", "LHP", "LKP", "LAP", "LAR", 
              "RHY", "RHR", "RHP", "RKP", "RAP", "RAR",
              "RSP", "RSR", "RSY","REP",
	      "HY",
             };

nJoint = #jointNames;
indexHead = 1;			--No head
nJointHead = 0;
indexLArm = 1;			--LArm: 3 4 5 
nJointLArm = 4; 		
indexLLeg = 5;			--LLeg:6 7 8 9 10 11
nJointLLeg = 6;
indexRLeg = 11; 		--RLeg: 12 13 14 15 16 17
nJointRLeg = 6;
indexRArm = 17; 		--RArm: 18 19 20
nJointRArm = 4;
indexWaist = 21;
nJointWaist = 1;

jointReverse={
	1,--LArm: 1 2 3 4
	7,9,--LLeg: 5 6 7 8 9 10
	12,13,15,--RLeg: 11 12 13 14 15 16
	17,18,--RArm: 17 18 19 20
	--Waist: 21
}

jointBias={
	-math.pi/2,0,0,0,
	0,0,0,0,0,0,
	0,0,0,0,0,0,
	-math.pi/2,0,0,0,
	0,
}


moveDir={};
for i=1,nJoint do moveDir[i]=1; end
for i=1,#jointReverse do moveDir[jointReverse[i]]=-1; end



tags.joints = {};
for i,v in ipairs(jointNames) do
  tags.joints[i] = controller.wb_robot_get_device(v);
  controller.wb_servo_enable_position(tags.joints[i], timeStep);
end

tags.accelerometer = controller.wb_robot_get_device("imuacc");
controller.wb_accelerometer_enable(tags.accelerometer, timeStep);
tags.gyro = controller.wb_robot_get_device("imugyro");
controller.wb_gyro_enable(tags.gyro, timeStep);
tags.gps = controller.wb_robot_get_device("zero");
controller.wb_gps_enable(tags.gps, timeStep);



controller.wb_robot_step(timeStep);

actuator = {};
actuator.command = {};
actuator.velocity = {};
actuator.position = {};
actuator.hardness = {};
for i = 1,nJoint do
  actuator.command[i] = 0;
  actuator.velocity[i] = 0;
  actuator.position[i] = 0;
  actuator.hardness[i] = 0;
end

function set_actuator_command(a, index)
  index = index or 1;
  if (type(a) == "number") then
    actuator.command[index] = moveDir[index]*(a+jointBias[index]);
  else
    for i = 1,#a do
      actuator.command[index+i-1] = moveDir[index+i-1]*(a[i]+jointBias[index+i-1]);
    end
  end
end

get_time = controller.wb_robot_get_time;

function set_actuator_velocity(a, index)
  index = index or 1;
  if (type(a) == "number") then
    actuator.velocity[index] = a;
  else
    for i = 1,#a do
      actuator.velocity[index+i-1] = a[i];
    end
  end
end

function set_actuator_hardness(a, index)
  index = index or 1;
  if (type(a) == "number") then
    actuator.hardness[index] = a;
  else
    for i = 1,#a do
      actuator.hardness[index+i-1] = a[i];
    end
  end
end

function get_sensor_position(index)
  if (index) then
    return moveDir[index]*controller.wb_servo_get_position(tags.joints[index])-jointBias[index];
  else
    local t = {};
    for i = 1,nJoint do
      t[i] = moveDir[i]*controller.wb_servo_get_position(tags.joints[i])-jointBias[i];
    end
    return t;
  end
end

imuAngle = {0, 0, 0};
aImuFilter = 1 - math.exp(-tDelta/0.5);
function get_sensor_imuAngle(index)
  if (not index) then
    return imuAngle;
  else
    return imuAngle[index];
  end
end

-- Two buttons in the array
function get_sensor_button(index)
  local randThreshold = 0.001;
  if (math.random() < randThreshold) then
    return {1,0};
  else
    return {0,0};
  end
end


function get_head_position()
--  local q = get_sensor_position();
--  return {unpack(q, indexHead, indexHead+nJointHead-1)};
    return ({0,0})
end
function get_larm_position()
  local q = get_sensor_position();
  return {unpack(q, indexLArm, indexLArm+nJointLArm-1)};
end
function get_rarm_position()
  local q = get_sensor_position();
  return {unpack(q, indexRArm, indexRArm+nJointRArm-1)};
end
function get_lleg_position()
  local q = get_sensor_position();
  return {unpack(q, indexLLeg, indexLLeg+nJointLLeg-1)};
end
function get_rleg_position()
  local q = get_sensor_position();
  return {unpack(q, indexRLeg, indexRLeg+nJointRLeg-1)};
end


function set_body_hardness(val)
  if (type(val) == "number") then
    val = val*vector.ones(nJoint);
  end
  set_actuator_hardness(val);
end
function set_head_hardness(val)
--[[
  if (type(val) == "number") then
    val = val*vector.ones(nJointHead);
  end
  set_actuator_hardness(val, indexHead);
--]]
end
function set_larm_hardness(val)
  if (type(val) == "number") then
    val = val*vector.ones(nJointLArm);
  end
  set_actuator_hardness(val, indexLArm);
end
function set_rarm_hardness(val)
  if (type(val) == "number") then
    val = val*vector.ones(nJointRArm);
  end
  set_actuator_hardness(val, indexRArm);
end
function set_lleg_hardness(val)
  if (type(val) == "number") then
    val = val*vector.ones(nJointLLeg);
  end
  set_actuator_hardness(val, indexLLeg);
end
function set_rleg_hardness(val)
  if (type(val) == "number") then
    val = val*vector.ones(nJointRLeg);
  end
  set_actuator_hardness(val, indexRLeg);
end
function set_waist_hardness( val )
  set_actuator_hardness(val, indexWaist);
end
function set_head_command(val)
--  set_actuator_command(val, indexHead);
end
function set_lleg_command(val)
  set_actuator_command(val, indexLLeg);
end
function set_rleg_command(val)
  set_actuator_command(val, indexRLeg);
end
function set_larm_command(val)
  set_actuator_command(val, indexLArm);
end
function set_rarm_command(val)
  set_actuator_command(val, indexRArm);
end
function set_waist_command(val)
  set_actuator_command(val, indexWaist);
end


function update()
  -- Set actuators
  for i = 1,nJoint do
    if actuator.hardness[i] > 0 then
      if actuator.velocity[i] > 0 then
        local delta = actuator.command[i] - actuator.position[i];
        local deltaMax = tDelta*actuator.velocity[i];
        if (delta > deltaMax) then
          delta = deltaMax;
        elseif (delta < -deltaMax) then
          delta = -deltaMax;
        end
        actuator.position[i] = actuator.position[i]+delta;
      else
	    actuator.position[i] = actuator.command[i];
      end
      controller.wb_servo_set_position(tags.joints[i],
                                        actuator.position[i]);
    end
  end

  if (controller.wb_robot_step(timeStep) < 0) then
    --Shut down controller:
    os.exit();
  end

  -- Process sensors
  accel = controller.wb_accelerometer_get_values(tags.accelerometer);
  gyro = controller.wb_gyro_get_values(tags.gyro);

--  print("acc",unpack(accel))
--  print("gyr",unpack(gyro))

  local gAccel = 9.80;
  accX = - accel[3]/gAccel;
  accY = -accel[1]/gAccel;
  if ((accX > -1) and (accX < 1) and (accY > -1) and (accY < 1)) then
    imuAngle[1] = imuAngle[1] + aImuFilter*(math.asin(accY) - imuAngle[1]);
    imuAngle[2] = imuAngle[2] + aImuFilter*(math.asin(accX) - imuAngle[2]);
  end
  imuAngle[3] = imuAngle[3] + tDelta * gyro[2];

end


-- Extra for compatibility
function set_syncread_enable(val)
end

function set_actuator_eyeled( val )
end


function get_sensor_imuGyr( )
  return {0,0,0};
end

function get_sensor_imuAcc( )
  return {0,0,0};
end

function get_sensor_gps()
  gps = controller.wb_gps_get_values(tags.gps);
  return gps;
end
