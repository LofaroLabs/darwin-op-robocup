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
distanceTolerance = .4;
angleTolerance = .3;
function entry()
  print(_NAME.." entry");
  HeadFSM.sm:set_state('headLookGoalGMU');
  t0 = Body.get_time();
  alreadyDone = false;
  print("yellin ready"); 
 wcm.set_horde_yelledReady(0);-- added this need to check.
 print("yelllED ready");
end

function update()
  pose = wcm.get_pose();
  endPosition = wcm.get_horde_gotoPose();-- goto an arbitrary pose
  facing = wcm.get_horde_facing(); 
   print("I'm in update!!\n");
   print(endPosition);
   -- centerPosition = {x,y,a} in global coordinates
   -- pose_relative will convert centerPosition to coordinates relative to the robot.
 
  endPoseRelative  = util.pose_relative(endPosition, {pose.x, pose.y, pose.a});
  endPoseX = endPoseRelative[1];
  endPoseY = endPoseRelative[2];
  endFacingRelative = util.pose_relative(facing, {pose.x, pose.y, pose.a});
  scaleFactor = 7.5*(math.abs(endPoseX)+math.abs(endPoseY));
  walk.start();
  if(alreadyDone) then --checking if we've already gotten there to our best tolerance
      print("nitpick adjustments");
      if(endFacingRelative[2]>0) then
           rotateVel = 1;
      else
           rotateVel = -1;
      end
      print("velocity is set to: " .. (endPoseX/scaleFactor/5 + -.005) ); 
        walk.set_velocity(endPoseX/scaleFactor/5 + -.005, endPoseY/scaleFactor/5,rotateVel/10);
	return;
  end
  local t = Body.get_time();
  print("about to grab gootPose");
  print("I converted\n");
  angleRelativeFacing = math.atan2(endFacingRelative[2],endFacingRelative[1]) 
  if(math.abs(angleRelativeFacing)> angleTolerance) then -- now that our distance is fine, let's look at the angle we need to go to
      print("my pose X is: ".. pose.x)
      print("adjusting angle first" .. endFacingRelative[3]); 
      if(endFacingRelative[2]>0) then
           rotateVel = 1;
      else
           rotateVel = -1;
      end
     walk.set_velocity(0, 0,rotateVel);
	return;
--   elseif true then
--   	print("DEBUG STOPPED IM FACING AS EXPECTD");
--	return;
   elseif(math.abs(endPoseX)+math.abs(endPoseY)<distanceTolerance and math.abs(endFacingRelative[3]) < angleTolerance) then
	walk.stop();
	--walk.set_velocity(0,0,0);
--	Motion.sm:set_state('standstill');
--	alreadyDone = true;
	wcm.set_horde_yelledReady(1);
     --	wcm.set_horde_passKick(1);
--	wcm.set_horde_timeMark(Body.get_time());
	return;
  end
  print("X " .. endPoseX .. " Y: " .. endPoseY);
  rotateVel = 0;
  --if we are not close enough to our goal position
  --moving back and forth while moving need to fix TODO
  if(math.abs(endPoseX)+math.abs(endPoseY)>distanceTolerance) then
      print("walking toward final point " .. (math.abs(endPoseX)+math.abs(endPoseY)));
	rotateVel = 0; 
--     if(endPoseY>0) then
 --          rotateVel = .5;
  --    else
  --         rotateVel = -.5;
  --    end
   walk.set_velocity(endPoseX/scaleFactor, endPoseY/scaleFactor,rotateVel);
  end
end

function exit()
  wcm.set_horde_yelledReady(0);
  -- wcm.set_horde_passKick(1);
  --wcm.set_horde_timeMark(Body.get_time());
  Motion.sm:add_event('walk');
  HeadFSM.sm:set_state('headLookGoalGMU');
end

