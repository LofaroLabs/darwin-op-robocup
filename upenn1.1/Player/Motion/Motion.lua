module(..., package.seeall);

require('Body')
require('fsm')
require('vector')
require('relax')
require('stance')
require('walk')
require('sit')

require('falling')
require('standup')
require('kick')

sm = fsm.new(relax);
sm:add_state(stance);
sm:add_state(walk);
sm:add_state(sit);
sm:add_state(standup);
sm:add_state(falling);
sm:add_state(kick);


sm:set_transition(relax, "standup", stance); 

sm:set_transition(stance, "done", walk);

sm:set_transition(walk, "kick", kick);
sm:set_transition(walk, "sit", sit); 
sm:set_transition(walk, "fall", falling);

sm:set_transition(sit, "done", relax); 

sm:set_transition(falling, "done", standup);

sm:set_transition(standup, "fail", standup);
sm:set_transition(standup, "done", stance);

sm:set_transition(kick, "done", walk);

bodyTilt=walk.bodyTilt;

function entry()
   sm:entry()
end

function update()
  --Check button presses
  if (Body.get_sensor_button()[1] > 0) then
    sm:add_event("button");
  end
  if (Body.get_sensor_button()[2] > 0) then
    sm:add_event("button2");
  end

  -- Check if the robot is falling
  local imuAngle = Body.get_sensor_imuAngle();
  local maxImuAngle = math.max(math.abs(imuAngle[1]), math.abs(imuAngle[2]-bodyTilt));
  if (maxImuAngle > 34*math.pi/180) then
    sm:add_event("fall");
  end

  sm:update();
end

function exit()
  sm:exit();
end

function event(e)
  sm:add_event(e);
end
