module(..., package.seeall);

require('Filter2D');
require('Body')
require('Vision');
require('walk');
require('vector');

ballFilter = Filter2D.new();
ball = {};
ball.t = 0;  --Detection time
ball.x = 1.0;
ball.y = 0;

count = 0;

function entry()
  count = 0;
end

function update()
  count = count + 1;

  if Vision.ball and Vision.ball.detect == 1 then
    ball.t = Body.get_time();
    ballFilter:observation_xy(Vision.ball.v[1], Vision.ball.v[2],
                              Vision.ball.dr, Vision.ball.da);
  end

  ball.x, ball.y = ballFilter:get_xy();
end

function exit()
end

function get_ball()
  return ball;
end

function pose_global(pRelative, pose)
  local ca = math.cos(pose[3]);
  local sa = math.sin(pose[3]);
  return vector.new{pose[1] + ca*pRelative[1] - sa*pRelative[2],
                    pose[2] + sa*pRelative[1] + ca*pRelative[2],
                    pose[3] + pRelative[3]};
end

function pose_relative(pGlobal, pose)
  local ca = math.cos(pose[3]);
  local sa = math.sin(pose[3]);
  local px = pGlobal[1]-pose[1];
  local py = pGlobal[2]-pose[2];
  local pa = pGlobal[3]-pose[3];
  return vector.new{ca*px + sa*py, -sa*px + ca*py, mod_angle(pa)};
end

function mod_angle(a)
  -- Reduce angle to [-pi, pi)
  a = a % (2*math.pi);
  if (a >= math.pi) then
    a = a - 2*math.pi;
  end
  return a;
end
