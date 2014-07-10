cwd = os.getenv('PWD')
require('init')

require('wcm')
require('unix')
require('Config')
require('shm')
require('vector')
require('mcm')
require('Speak')
require('getch')
require('Body')
require('Motion')
require('dive')
require ('UltraSound')
require('grip')
Motion.entry();
darwin = false;
webots = false;
goalDist = 0;
-- Enable OP specific 
if(Config.platform.name == 'OP') then
  darwin = true;
  --SJ: OP specific initialization posing (to prevent twisting)
--  Body.set_body_hardness(0.3);
--  Body.set_actuator_command(Config.stance.initangle)
end

--TODO: enable new nao specific
declaredOne = 0;
newnao = false; --Turn this on for new naos (run main code outside naoqi)
newnao = true;
role = 0
getch.enableblock(1);
unix.usleep(1E6*1.0);
Body.set_body_hardness(0);

--This is robot specific 
webots = false;
init = false;
calibrating = false;
ready = false;
if( webots or darwin) then
  ready = true;
end

initToggle = true;
targetvel=vector.zeros(3);
button_pressed = {0,0};

globalBallToggle = 0;

ballDistToggle = 0;
insist = false;
insistBall = false;
somethingPressed = false
ballDistToGoalToggle = 0;
function process_keyinput()
  local str=getch.get();
  if #str>0 then
    somethingPressed = true;
    local byte=string.byte(str,1);
    -- Walk velocity setting
    if byte==string.byte("r") then
	if(wcm.get_horde_yelledReady()==0) then
	  wcm.set_horde_yelledReady(1);      
        else
	  wcm.set_horde_yelledReady(0);
	end 
    end
    if byte==string.byte("p") then
        if (wcm.get_horde_yelledKick()==0) then
          wcm.set_horde_yelledKick(1);
        else
          wcm.set_horde_yelledKick(0);
        end
    end

	if byte==string.byte("s") then
		if (wcm.get_horde_status() >= 4) then
			wcm.set_horde_status(0)
		else
			wcm.set_horde_status(wcm.get_horde_status() + 1)
		end
	end
	
	if byte==string.byte("g") then
		if (wcm.get_horde_goalieCloseEnough() == 1) then
			wcm.set_horde_goalieCloseEnough(0);
		else
			wcm.set_horde_goalieCloseEnough(1);		
		end
	end
	
	if byte==string.byte("q") then
		if(wcm.get_horde_dummyTraining() ==0) then
			wcm.set_horde_dummyTraining(1);
		else
			wcm.set_horde_dummyTraining(0);		
		end
	end

   if byte==string.byte("y") then
        if (wcm.get_horde_yelledFail()==0) then
          wcm.set_horde_yelledFail(1);
         
        else
          wcm.set_horde_yelledFail(0);
        end
    end
   if byte==string.byte("f") then
        if (wcm.get_horde_doneApproach()==0) then
          wcm.set_horde_doneApproach(1);
          insist = true;
	else
 	  insist = false;
          wcm.set_horde_doneApproach(0);
        end
    end

    if byte==string.byte("c") then
        if (wcm.get_team_isClosestToGoalDefend()==0) then
          	wcm.set_team_isClosestToGoalDefend(1);
        else
		wcm.set_team_isClosestToGoalDefend(0); 
        end
    end
    
    if byte==string.byte("l") then
	globalBallToggle = (globalBallToggle +1) % 4;
        if (globalBallToggle == 0) then
          	wcm.set_team_closestToBallLoc({-0.1,0,0});
        elseif globalBallToggle == 1 then
        	wcm.set_team_closestToBallLoc({0.1,0,0});
        elseif globalBallToggle == 2 then
		wcm.set_team_closestToBallLoc({0.4,0,0});
	elseif globalBallToggle == 3 then
		wcm.set_team_closestToBallLoc({-0.4,0,0})
	end
    end


	if byte==string.byte("v") then
        if (wcm.get_horde_goalSign()> 0) then
          	wcm.set_horde_goalSign(-1)
        else
        	wcm.set_horde_goalSign(1)
        end
    end


    if byte==string.byte("u") then
    	declaredOne = (declaredOne+1) % 4 
    end

    if byte==string.byte("k") then
        if (wcm.get_team_is_smallest_eta()==0) then 
		wcm.set_team_is_smallest_eta(1);
        else
                wcm.set_team_is_smallest_eta(0);
        end
    end
    
    if byte==string.byte("n") then
        if (wcm.get_team_connected()==0) then 
			wcm.set_team_connected(1);
        else
			wcm.set_team_connected(0);
        end
    end

   if byte==string.byte("d") then
        if(vcm.get_ball_detect()==0) then
          vcm.set_ball_detect(1);
          insistBall = true;
	else
	  insistBall = false;
	  vcm.get_ball_detect(0);         
        end
    end
    if(byte==string.byte("b")) then
	ballDistToggle = (ballDistToggle +1) % 5;
    end
    if(byte==string.byte(",")) then
	ballDistToGoalToggle = (ballDistToGoalToggle + 1) % 3;
    end
    if(byte == string.byte("r")) then
	role = (role+1)%5
   	wcm.set_horde_role(role); 	
    end
    if(byte == string.byte('o')) then
	goalDist = (goalDist +1) %3;
     end
	if(byte == string.byte('z')) then
		wcm.set_horde_playerID((wcm.get_horde_playerID()+1)%5)
	end
	if(byte == string.byte('x')) then
		wcm.set_horde_timeOut((wcm.get_horde_timeOut()+1)%2)
	end
  end
end

-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();

function update()
   process_keyinput();
end
wcm.set_horde_dummyTraining(0);
  while (true) do
    -- update motion process
 --  unix.usleep(.5 * 1E6);
    update();
   if(ballDistToggle == 0) then
	ballDist = .582259821
   elseif(ballDistToggle==1) then
   	ballDist = .2711234985
   elseif(ballDistToggle==2) then
	ballDist = .15;
   elseif(ballDistToggle == 3) then
	ballDist = 1.01
   else
        ballDist = 0.0;
   end
   if(ballDistToggle~=4) then
   	wcm.set_ball_x(ballDist);
   end
   ballGlobal = {}
   if(ballDistToGoalToggle == 0) then
	ballGlobal[1] = -1*wcm.get_horde_goalSign() * 1.6; 
   	ballGlobal[2] = 0;
   elseif ballDistToGoalToggle == 1 then
   	ballGlobal[1] = -1*wcm.get_horde_goalSign() * 1.4; 
   	ballGlobal[2] = 0;
   else
   	ballGlobal = wcm.get_team_closestToBallLoc();
   end
  
   wcm.set_team_closestToBallLoc(ballGlobal);
  -- wcm.set_ball_y(0);
   if (insist) then
   	wcm.set_horde_doneApproach(1);
   end
   if(insistBall) then
	vcm.set_ball_detect(1);
   end
   
   if(wcm.get_horde_dummyTraining() == 1) then
   declared = vector.zeros(3);
   --print("\n\n declared one is " .. declare
   if(declaredOne==0) then
	declared[1] = 1;
   elseif(declaredOne == 1) then
	declared[2] = 1;
   elseif(declaredOne == 2) then 
	--print("HEY THIS HAPPENED")
	declared[3] = 1;
   end 
   end
   
   
   
   if(goalDist ==0) then
   --let localization handle
	vcm.set_vision_enable(1)
   elseif(goalDist ==1) then
	vcm.set_vision_enable(0);
	myNewPose = {}
	
	myNewPose[1] = -1*1.6*wcm.get_horde_goalSign();
	myNewPose[2] = 0;
	myNewPose[3] = 0;
wcm.set_horde_pose(myNewPose);	

   elseif goalDist == 2 then
	vcm.set_vision_enable(0);
	myNewPose = {}
        myNewPose[1] = -1*1.4*wcm.get_horde_goalSign();
        myNewPose[2] = 0;
        myNewPose[3] = 0;
	wcm.set_horde_pose(myNewPose);
   end
   if wcm.get_horde_dummyTraining() == 1 then
   	wcm.set_horde_doDeclare(declared);
   end
   io.stdout:flush();
   if(somethingPressed) then
	os.execute('clear')
	print("Yellfailed :".. wcm.get_horde_yelledFail() ..  "\ndetect ball: " .. vcm.get_ball_detect() ..  " ball dist:" .. wcm.get_ball_x().. " frontApproach: " .. tostring(wcm.get_horde_doneApproach()))
	print("ready: " .. tostring(wcm.get_horde_yelledReady()) .. " passkick: " .. tostring(wcm.get_horde_yelledKick()) .. " (c)ClosestToGoalDefend: " .. tostring(wcm.get_team_isClosestToGoalDefend()) ) 
	print(" (k)ClosestToBall: " .. tostring(wcm.get_team_is_smallest_eta()) .. "\n (g)GoalieCloseEnough " .. tostring(wcm.get_horde_goalieCloseEnough())) 
	print(" (s)Status " .. tostring(wcm.get_horde_status()) .. " (q)dummyTraining " .. tostring(wcm.get_horde_dummyTraining()) )
	print( " (u)DoDeclared " .. tostring(wcm.get_horde_doDeclare()) .. " (l)ClosestBallX " .. tostring(wcm.get_team_closestToBallLoc()[1]) .. " (v)GoalDefendSign " .. tostring(wcm.get_horde_goalSign()))
	print( " (n)connected " .. tostring(wcm.get_team_connected()) .. " declared " .. tostring(wcm.get_horde_declared()) .. " role " .. wcm.get_horde_role());
  	print("(o)goal dist " .. tostring(wcm.get_horde_pose()[1]))
	print("(z) player ID " .. tostring(wcm.get_horde_playerID()));
	print("(x) timOut " .. tostring(wcm.get_horde_timeOut()))
	print("(,) dist to goal " .. tostring(wcm.get_team_closestToBallLoc()[1]))
	somethingPressed = false;
  end	
 end
