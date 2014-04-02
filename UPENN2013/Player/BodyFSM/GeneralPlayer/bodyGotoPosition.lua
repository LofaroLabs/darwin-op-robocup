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

function entry()
  print(_NAME.." entry");
  HeadFSM.sm:set_state('headLookGoalPose');
  t0 = Body.get_time();
end

function update()
  local t = Body.get_time();

   pose = wcm.get_pose();
   centerPosition = wcm.get_horde_gotoPose();
   print("I'm in update!!\n");
   print(centerPosition);
   -- centerPosition = {x,y,a} in global coordinates
   -- pose_relative will convert centerPosition to coordinates relative to the robot.
  endPoseRelative  = util.pose_relative(centerPosition, {pose.x, pose.y, pose.a});
  print("I converted\n");
    
  endPoseX = endPoseRelative[1];
  endPoseY = endPoseRelative[2];
  print("X " .. endPoseX .. " Y: " .. endPoseY);
  scaleFactor = 20*(math.abs(endPoseX)+math.abs(endPoseY));
  rotateVel = 0;
  if(math.abs(endPoseY)/math.abs(endPoseX)>.25) then
      if(endPoseY>0) then
           rotateVel = 1;
      else
           rotateVel = -1;
      end
  end
  walk.set_velocity(endPoseX/scaleFactor, endPoseY/scaleFactor,rotateVel);

end

function exit()
  --HeadFSM.sm:set_state('headLookGoalGMU');
end

