module(..., package.seeall);

require('Body')
require('keyframe')
require('unix')
require('Config');
require('walk');

local cwd = unix.getcwd();
if string.find(cwd, "WebotsController") then
  cwd = cwd.."/Player";
end
cwd = cwd.."/Motion"

keyframe.load_motion_file(cwd.."/"..Config.km.standup_front,
                          "standupFromFront");
keyframe.load_motion_file(cwd.."/"..Config.km.standup_back,
                          "standupFromBack");

function entry()
  print("Motion SM:".._NAME.." entry");

  keyframe.entry();
  Body.set_body_hardness(1);
  -- start standup routine (back/front)
  local imuAngleY = Body.get_sensor_imuAngle(2);
  if (imuAngleY > 0) then
    print("standupFromFront");
    keyframe.do_motion("standupFromFront");
  else
    print("standupFromBack");
    keyframe.do_motion("standupFromBack");
  end
  walk.set_velocity(0,0,0);
end

function update()
  keyframe.update();
  if (keyframe.get_queue_len() == 0) then
    local imuAngle = Body.get_sensor_imuAngle();
    local maxImuAngle = math.max(math.abs(imuAngle[1]),
                        math.abs(imuAngle[2]));
    if (maxImuAngle > 40*math.pi/180) then
      return "fail";
    else
      return "done";
    end
  end
end

function exit()
  keyframe.exit();
end
