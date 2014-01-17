module(..., package.seeall);

require('Body')
require('World')
require('walk')
require('vector')
require('Config')

t0 = 0;
timeout = 20.0;

-- maximum walk velocity
maxStep = Config.BodyFSM.chase.maxStep;

-- ball distance threshold
rClose = Config.BodyFSM.chase.rClose;

-- ball detection timeout
tLost = 3.0;

function entry()
  print("Body FSM:".._NAME.." entry");

  t0 = Body.get_time();
end

function update()
  local t = Body.get_time();

  -- get ball position
  ball = World.get_ball();
  ballR = math.sqrt(ball.x^2 + ball.y^2);

  -- calculate walk velocity based on ball position
  vStep = vector.new({0,0,0});
  vStep[1] = .6*ball.x;
  vStep[2] = .75*ball.y;
  scale = math.min(maxStep/math.sqrt(vStep[1]^2+vStep[2]^2), 1);
  vStep = scale*vStep;

  ballA = math.atan2(ball.y, ball.x+0.10);
  vStep[3] = 0.75*ballA;
  walk.set_velocity(vStep[1],vStep[2],vStep[3]);


  if (t - ball.t > tLost) then
    return "ballLost";
  end
  if (t - t0 > timeout) then
    return "timeout";
  end
  if (ballR < rClose) then
    return "ballClose";
  end
  if (t - t0 > 1.0 and Body.get_sensor_button()[1] > 0) then
    return "button";
  end
end

function exit()
end
