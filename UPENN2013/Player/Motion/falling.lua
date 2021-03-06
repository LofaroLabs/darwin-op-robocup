module(..., package.seeall);

require('Body')
require('walk')
require('vcm')

t0 = 0;
timeout = Config.falling_timeout or 0.3;


qLArmFront = vector.new({45,9,-135})*math.pi/180;
qRArmFront = vector.new({45,-9,-135})*math.pi/180;

---
--Prepare the body to safely fall. This primarily involves setting all joints
--to zero hardness, so that the motors will be safe after the fall.
function entry()
  print(_NAME.." entry");

  -- relax all the joints while falling
  Body.set_body_hardness(0);

--[[
  --Ukemi motion (safe fall)
  local imuAngleY = Body.get_sensor_imuAngle(2);
  if (imuAngleY > 0) then --Front falling 
print("UKEMI FRONT")
    Body.set_larm_hardness({0.6,0,0.6});
    Body.set_rarm_hardness({0.6,0,0.6});
    Body.set_larm_command(qLArmFront);
    Body.set_rarm_command(qRArmFront);
  else
  end
--]]

  t0 = Body.get_time();
  Body.set_syncread_enable(1); --OP specific
  walk.stance_reset();--reset current stance
  vcm.set_vision_enable(0);
end

---
--Update the body after the fall has occurred. This is primarily used to set
--the actuator commands to the values of the motors in the robot's position
--after it has completed its fall.
function update()
  local t = Body.get_time();
  -- set the robots command joint angles to thier current positions
  --  this is needed to that when the hardness is re-enabled
  if (t-t0 > timeout) then
    return "done"
  end
end

function exit()
  vcm.set_vision_enable(1);
  local qSensor = Body.get_sensor_position();
  Body.set_actuator_command(qSensor);
end
