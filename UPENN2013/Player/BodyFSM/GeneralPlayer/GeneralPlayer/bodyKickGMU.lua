-- Test SM for walk kick
-- Not for distribute


module(..., package.seeall);

require('Body')
require('vector')
require('Motion');
require('kick');
require('HeadFSM')
require('Config')
require('wcm')
require('unix');
require('walk');

t0 = 0;
timeout = Config.fsm.bodyWalkKick.timeout;

walkkick_th = 0.14; --Threshold for step-back walkkick for OP
ball = "nil";

function entry()
  print(_NAME.." entry");

  t0 = Body.get_time();
  follow=false;
  kick_dir=wcm.get_kick_dir();


  print("KICK DIR:",kick_dir)
  ball = wcm.get_ball();
 
  HeadFSM.sm:set_state('headTrackGMU');

--  HeadFSM.sm:set_state('headIdle');
end

function update()
      local t = Body.get_time();
      if(ball.y>0) then
      	kick.set_kick("kickForwardLeft");
      	Motion.event("kick");
      else
        kick.set_kick("kickForwardRight");
        Motion.event("kick");
      end
      --[[kick.set_kick("kickSideLeft");
      Motion.event("kick");
      kick.set_kick("kickSideRight");
      Motion.event("kick"); ]]--
  --SJ: should be done in better way?
  if walk.walkKickRequest==0 and follow ==false then
    follow=true;
    HeadFSM.sm:set_state('headKickFollow');
  end

end

function exit()
    wcm.set_horde_passKick(1);
    wcm.set_horde_timeMark(Body.get_time()) 
 -- HeadFSM.sm:set_state('headTrack');
end
