module(..., package.seeall);

require('Body')
require('World')
require('walk')
require('vector')

t0 = 0;
timeout = 10.0;

-- turn velocity
vSpin = 0.3;
direction = 1;

function entry()
  print("Body FSM:".._NAME.." entry");

  t0 = Body.get_time();

  -- set turn direction to last known ball position
  ball = World.ball;
  if (ball.y > 0) then
    direction = 1;
  else
    direction = -1;
  end
end

function update()
  local t = Body.get_time();

  ball = World.ball;

  -- search/spin until the ball is found
  walk.set_velocity(0, 0, direction*vSpin);

  if (t - ball.t < 0.1) then
    return "ball";
  end
  if (t - t0 > timeout) then
    return "timeout";
  end
  if (t - t0 > 1.0 and Body.get_sensor_button()[1] > 0) then
    return "button";
  end
end

function exit()
end
