module(..., package.seeall);

require('Body')
require('walk')
require('vector')
require('Config')
require('wcm')
require('gcm')

t0 = 0;

maxStep = Config.fsm.bodyGotoCenter.maxStep;
rClose = Config.fsm.bodyGotoCenter.rClose;
timeout = Config.fsm.bodyGotoCenter.timeout;
--TODO: Goalie handling, velocity limit 
alreadyDone = false;
function entry()
  print(_NAME.." entry");
  HeadFSM.sm:set_state('headLookGoalPose');
  t0 = Body.get_time();
  alreadyDone = false;
  wcm.set_horde_ready(0);-- added this need to check.
end

function update()
  if(alreadyDone) then --checking if we've already gotten there to our best tolerance
	return;
  end
  local t = Body.get_time();

   pose = wcm.get_pose();
   endPosition = wcm.get_horde_gotoPose();-- goto an arbitrary pose
   print("I'm in update!!\n");
   print(endPosition);
   -- centerPosition = {x,y,a} in global coordinates
   -- pose_relative will convert centerPosition to coordinates relative to the robot.
  endPoseRelative  = util.pose_relative(endPosition, {pose.x, pose.y, pose.a});
  print("I converted\n");
 
  endPoseX = endPoseRelative[1];
  endPoseY = endPoseRelative[2];
  if(math.abs(endPoseX)+math.abs(endPoseY)<.2 and math.abs(pose.a - endPoseRelative[3]) >.3) then
	Motion.sm:set_state('standstill');
	alreadyDone = true;
	wcm.set_horde_ready(1);
--	wcm.set_horde_passKick(1);
--	wcm.set_horde_timeMark(Body.get_time());
	return;
  end
  print("X " .. endPoseX .. " Y: " .. endPoseY);
  scaleFactor = 20*(math.abs(endPoseX)+math.abs(endPoseY));
  rotateVel = 0;
  --if we are not close enough to our goal position
  if(math.abs(endPoseX)+math.abs(endPoseY)>.2 and math.abs(endPoseY)/math.abs(endPoseX)>.3) then
      if(endPoseY>0) then
           rotateVel = 1;
      else
           rotateVel = -1;
      end
   walk.set_velocity(endPoseX/scaleFactor, endPoseY/scaleFactor,rotateVel);
  elseif(math.abs(pose.a - endPoseRelative[3]) >.3) then -- now that our distance is fine, let's look at the angle we need to go to
       if(endPoseRelative[3]<0) then
           rotateVel = 1;
      else
           rotateVel = -1;
      end
     walk.set_velocity(0, 0,rotateVel);
  end
end

function exit()
  wcm.set_horde_ready(0);
  -- wcm.set_horde_passKick(1);
  --wcm.set_horde_timeMark(Body.get_time());
  Motion.sm:add_event('walk');
  HeadFSM.sm:set_state('headLookGoalGMU');
end

