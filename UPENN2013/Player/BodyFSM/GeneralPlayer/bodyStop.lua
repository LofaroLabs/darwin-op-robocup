module(..., package.seeall);

require('Body')
require('Motion')

function entry()
  print(_NAME..' entry');
  alreadySet = false;  
  HeadFSM.sm:set_state('headLookGoalGMU');
  walk.set_velocity(0,0,0);
  walk.stop();
  started = false;
end
tempTimer = 0.0;

function update()
  ballFound = vcm.get_ball_detect()
  if(ballFound == 0 and not alreadySet) then
	setDebugTrue()
	print("hey im a dumbass");
	setDebugFalse()
--        HeadFSM.sm:set_state('headScanGMU');
--	HeadFSM.update();	
	if( HeadFSM.sm.get_current_state(HeadFSM.sm)._NAME ~="headScanGMU") then
		setDebugTrue();
		print("the state was actually " .. HeadFSM.sm.get_current_state(HeadFSM.sm)._NAME);
		setDebugFalse();
		HeadFSM.sm:set_state("headScanGMU");
	
		--	HeadFSM.sm.get_current_state(HeadFSM.sm)	
	end
			  	alreadySet = true;
  elseif ballFound == 1 and HeadFSM.sm.get_current_state(HeadFSM.sm)._NAME~="headTrackGMU" then
	HeadFSM.sm:set_state("headTrackGMU");
  else
     alreadySet = false
  end
   --for webots : we have to stop with 0 bodytilt
  --[[if not started then
    if not walk.active then
    Motion.sm:set_state('standstill');
    started = true;
    tempTimer = Body.get_time();
    end
  end]]--
 
end

function exit()
  Motion.sm:add_event('walk');
  walk.set_velocity(0,0,0);
  walk.start();
  Motion.update();--he wasnt getting out of stop trying to tease him back into it.
  HeadFSM.sm:set_state('headLookGoalGMU');

end
