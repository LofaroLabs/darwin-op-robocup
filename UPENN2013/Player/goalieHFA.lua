module(... or '', package.seeall)

-- Get Platform for package path
cwd = '.';
local platform = os.getenv('PLATFORM') or '';
if (string.find(platform,'webots')) then cwd = cwd .. '/Player';
end

-- Get Computer for Lib suffix
local computer = os.getenv('COMPUTER') or '';
if (string.find(computer, 'Darwin')) then
  -- MacOS X uses .dylib:
--  package.cpath = cwd .. '/Lib/?.dylib;' .. package.cpath;
  package.cpath = cwd .. '/Lib/?.so;' .. package.cpath;
else
  package.cpath = cwd .. '/Lib/?.so;' .. package.cpath;
end

package.path = cwd .. '/?.lua;' .. package.path;
package.path = cwd .. '/Util/?.lua;' .. package.path;
package.path = cwd .. '/Config/?.lua;' .. package.path;
package.path = cwd .. '/Lib/?.lua;' .. package.path;
package.path = cwd .. '/Dev/?.lua;' .. package.path;
package.path = cwd .. '/Motion/?.lua;' .. package.path;
package.path = cwd .. '/Motion/keyframes/?.lua;' .. package.path;
package.path = cwd .. '/Motion/Walk/?.lua;' .. package.path;
package.path = cwd .. '/Vision/?.lua;' .. package.path;
package.path = cwd .. '/World/?.lua;' .. package.path;
package.path = cwd .. '/Lib/json4lua-0.9.50/?/?.lua;' .. package.path
require('hfa')
require('kitty')
require('init')
require('unix')
require('Config')
require('shm')
require('vector')
require('vcm')
require('gcm')
require('wcm')
require('mcm')
require('Speak')
require('getch')
require('Body')
require('Motion')
local hoard_functions = require "hoard_functions"
json = require("json")
unix.usleep(2*1E6);
--gcm.say_id();
--Speak.talk("My Player ID Is defiantly the number " .. Config.game.playerID);
darwin = true;

ready = true;
smindex = 0;
initToggle = true;
updateAllTimer=0;
sendFeaturesTimer =0;
-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();
connected = false;

Lgoalie_corner = Config.world.Lgoalie_corner;


local client

function setClient(someClient)
	client = someClient;
end

function inspect(key, value)
	table.foreach(value,print)
end

sentBehavior = false;
function sendBehavior(sendInfo)
    if(wcm.get_horde_sentBehavior()==0) then
    	client:send(sendInfo)
   	print("sent " .. tostring(sendInfo));  
    end
    wcm.set_horde_sentBehavior(1);
end


function connectToHorde(port)
		local socket = require("socket")
        local client = assert(socket.connect("127.0.0.1", port))
        --local client = server:accept()
        --client:settimeout(0);--non blocking read
		return client;
end

gotoPoseFacingStart = function(hfa) 
  			action  = {}
                        action["action"] = "gotoPoseFacing";
                        action["args"] = {};
			ballGlobal= {};
			ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
			ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
			print(ballGlobal)
 		    -- my pose global
       		pose=wcm.get_pose();

            -- determine which goal post the ball is closest to
       	    -- so need its global coords
       		--[[ballGlobal = util.pose_global({ball.x, ball.y, 0}, {pose.x, pose.y, pose.a})			
			ballGlobal.x = ballGlobal[1];
			ballGlobal.y = ballGlobal[2];
			--]]
			dest = getMidpoint()
			action.args.facing = {};
			action.args.facing.x = ballGlobal.x
			action.args.facing.y = ballGlobal.y
			action.args.facing.a = 0
			action.args.gotoPose = {};   
            action.args.gotoPose.x = dest.x
            action.args.gotoPose.y = dest.y
            action.args.gotoPose.a = 0
			action.ackNumber = wcm.get_horde_ackNumber();
            print("i am currently at: " .. pose.x .. ", " .. pose.y);
			print("trying to face " .. ballGlobal.x .. ", " .. ballGlobal.y);
			print("also moving to around " .. dest.x .. ", " .. dest.y);
			
			print(json.encode(action) .. "\n") 
           --if(vcm.get_ball_detect() == 1) then
                sendBehavior(json.encode(action) .. "\n");
           -- end
end	
gotoPoseFacingGo = function(hfa)
			action  = {}
            action["action"] = "updateGotoPoseFacing";
            action["args"] = {};
			--ball=wcm.get_ballGlobal();
 		    	
			ballGlobal= {};
            ballGlobal.x = wcm.get_ballGlobal_x();
            ballGlobal.y = wcm.get_ballGlobal_y();
            print(ballGlobal)

			-- my pose global
       		pose=wcm.get_pose();
            -- determine which goal post the ball is closest to
       	    -- so need its global coords
       		--[[ballGlobal = util.pose_global({ball.x, ball.y, 0}, {pose.x, pose.y, pose.a})			
			ballGlobal.x = ballGlobal[1];
			ballGlobal.y = ballGlobal[2];
			]]--
			dest = getMidpoint()
			action.args.facing = {};
			action.args.facing.x = ballGlobal.x
			action.args.facing.y = ballGlobal.y
			action.args.facing.a = 0
			action.args.gotoPose = {};   
            action.args.gotoPose.x = dest.x
            action.args.gotoPose.y = dest.y
            action.args.gotoPose.a = 0
			action.ackNumber = wcm.get_horde_ackNumber();
            print("i am currently at: " .. pose.x .. ", " .. pose.y);	
			print("trying to face " .. ballGlobal.x .. ", " .. ballGlobal.y);
			print("also moving to around " .. dest.x .. ", " .. dest.y);
			print(json.encode(action) .. "\n"); 
            
		    if(vcm.get_ball_detect() == 1) then		
				sendBehavior(json.encode(action) .. "\n");
			end
end
gotoPoseFacingStop = function (hfa)
end

stopStart = function(hfa)
	action  = {}
        action["action"] = "stop";
        action["args"] = "";
		action.ackNumber = wcm.get_horde_ackNumber();
        print(json.encode(action) .. "\n");
		sendBehavior(json.encode(action) .. "\n");
end
stopGo = function(hfa)
end
stopStop = function(hfa)
end

locateBallStart = function(hfa)
	print("locating ball") 
action  = {}
        action["action"] = "moveTheta";
        action["args"] = "";
		action.ackNumber =  wcm.get_horde_ackNumber();
		sendBehavior(json.encode(action) .. "\n");
end
locateBallStop = function()end
locateBallGo = function()end
gotoBallGo = function()end
gotoBallStart = function()
	print("going to ball")
 	action  = {}
        action["action"] = "gotoBall";
        action["args"] = "";
		action.ackNumber =  wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
end
gotoBallStop = function()end
gotoPositionStart = function(behavior, targets)
        action = {}
        action["action"] = "gotoPose"
        action["args"]  = targets["openSpot"]
        action.ackNumber = wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
end
approachTargetStart = function()
--[[
	print("approach target")
	 action  = {}
        action["action"] = "approachBall";
        action["args"] = "";
		action.ackNumber =  wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
        
    ]]--
    
    print("approachTargetStart to target ball")
	action = {}
	action["action"] = "approachTarget";
	action["args"] = {}
	action.args.x = wcm.get_horde_penaltyBoundsX() * -1; -- multiply by -1 to get attacking side
	action.args.y = 0
	action.args.a = 0
	action.ackNumber = wcm.get_horde_ackNumber();
	sendBehavior(json.encode(action) .. "\n")	
end
approachTargetStop = function()end
approachTargetGo = function()end
kickBallStart = function() 
	print("kicking ball");
 	action  = {}
        action["action"] = "kickBall";
        action["args"] = "";
		action.ackNumber = wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
end
kickBallStop = function()end
kickBallgo = function()end


stopPoseStart = function()
	print("Stop")

	action = {}
	action["action"] = "stop";
	action["args"] = "";
	action.ackNumber =  wcm.get_horde_ackNumber();
	sendBehavior(json.encode(action) .. "\n");
end
safetyStart = function()
	print("safety")
	action = {}
	action["action"] = "gotoPose"
	 goalSideAngle = 3.14;
	if gcm.get_team_color() == 1 then
		goalSideAngle = 0
                -- red attacks cyan goali
                --print(" yellow ")
        else
				
               -- print("not yellow")
        end

	action.args = {["x"] = 0, ["y"] = 0, ["a"]= goalSideAngle}
	action.ackNumber = wcm.get_horde_ackNumber()
	sendBehavior(json.encode(action) .. "\n")
end
declareStart = function()
	wcm.set_horde_declared(1);
end
undeclareStart = function()
	wcm.set_horde_declared(0);
end
deferStart = function()
	 	action  = {}
                action["action"] = "gotoPoseFacing";
                action["args"] = {};
                ballGlobal= {};

		ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
                ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
                print(ballGlobal)
                    -- my pose global
                pose=wcm.get_pose();
                dest = getMidpoint()
                action.args.facing = {};
                action.args.facing.x = ballGlobal.x
                action.args.facing.y = ballGlobal.y
                action.args.facing.a = 0
		action.args.gotoPose = {};
                action.args.gotoPose.x = .75 * (ballGlobal.x/math.abs(ballGlobal.x))
                action.args.gotoPose.y = .75 * (ballGlobal.y/math.abs(ballGlobal.y))
                action.args.gotoPose.a = 0
		action.ackNumber =  wcm.get_horde_ackNumber();
		sendBehavior(json.encode(action) .. "\n");
end
function walkForwardStart()
                        action  = {}
                        action["action"] = "walkForward";
                        action["args"] = "nan";
			action.ackNumber =  wcm.get_horde_ackNumber();
                        sendBehavior(json.encode(action) .. "\n");
end

function gotoPoseWhileLookingBackwardsStart()
         	action = {}
		action["action"] = "gotoPoseWhileLookingBackwards";
                action["args"] = {};
                ballGlobal= {};

		ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
                ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
                print(ballGlobal)
                    -- my pose global
                pose=wcm.get_pose();
                
                action.args.facing = {};
                action.args.facing.x = ballGlobal.x
                action.args.facing.y = ballGlobal.y
                action.args.facing.a = 0
		action.args.gotoPose = {};
				penaltyBounds = getPenaltyBounds();
                action.args.gotoPose.x = penaltyBounds[1]
                if ballGlobal.y < penaltyBounds[2]  then
                	action.args.gotoPose.y = ballGlobal.y;
                else
                	action.args.gotoPose.y = penaltyBounds[2];
                end
                action.args.gotoPose.a = 0
		action.ackNumber =  wcm.get_horde_ackNumber();
		sendBehavior(json.encode(action) .. "\n");

end
function gotoPoseWhileLookingBackwardsGo()
         	action = {}
		action["action"] = "updateGotoPoseWhileLookingBackwards";
                action["args"] = {};
                ballGlobal= {};

		ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
                ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
                print(ballGlobal)
                    -- my pose global
                pose=wcm.get_pose();
                
                action.args.facing = {};
                action.args.facing.x = ballGlobal.x
                action.args.facing.y = ballGlobal.y
                action.args.facing.a = 0
		action.args.gotoPose = {};
				penaltyBounds = getPenaltyBounds();
                action.args.gotoPose.x = penaltyBounds[1]
                if ballGlobal.y < penaltyBounds[2]  then
                	action.args.gotoPose.y = ballGlobal.y;
                else
                	action.args.gotoPose.y = penaltyBounds[2];
                end
                action.args.gotoPose.a = 0
		action.ackNumber =  wcm.get_horde_ackNumber();
		sendBehavior(json.encode(action) .. "\n");

end



function gotoWhileFacingGoalieStart()
	action  = {}
	action["action"] = "gotoWhileFacingGoalie";
    action["args"] = {};
    ballGlobal= {};

	ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
    ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
    print("Ball Global x = " .. tostring(ballGlobal.x) .. " y = " .. tostring(ballGlobal.y))
    -- my pose global
	pose=wcm.get_pose();
                
    action.args.facing = {};
    action.args.facing.x = ballGlobal.x
    action.args.facing.y = ballGlobal.y
    action.args.facing.a = 0
	action.args.gotoPose = {};
	penaltyBounds = getPenaltyBounds();
    action.args.gotoPose.x = penaltyBounds[1] -(.3*wcm.get_horde_goalSign()) 
    if math.abs(ballGlobal.y) < penaltyBounds[2] * .5  then
        action.args.gotoPose.y = ballGlobal.y;
    else
	ysign = ballGlobal.y / math.abs(ballGlobal.y)
        action.args.gotoPose.y = penaltyBounds[2] * ysign * .5;
    end
    action.args.gotoPose.a = 0
	action.ackNumber =  wcm.get_horde_ackNumber();
	sendBehavior(json.encode(action) .. "\n");


end

function gotoWhileFacingGoalieGo()
    action  = {}
    action["action"] = "updateGotoWhileFacingGoalie";
    action["args"] = {};
    ballGlobal= {};

	ballGlobal.x = wcm.get_team_closestToBallLoc()[1]--wcm.get_ballGlobal_x();
    ballGlobal.y = wcm.get_team_closestToBallLoc()[2]--wcm.get_ballGlobal_y();
    print(ballGlobal)
    -- my pose global
	pose=wcm.get_pose();
                
    action.args.facing = {};
    action.args.facing.x = ballGlobal.x
    action.args.facing.y = ballGlobal.y
    action.args.facing.a = 0
	action.args.gotoPose = {};
	penaltyBounds = getPenaltyBounds();
    action.args.gotoPose.x = penaltyBounds[1] - (0.3 * wcm.get_horde_goalSign());
    if math.abs(ballGlobal.y) < penaltyBounds[2] * .5  then
        action.args.gotoPose.y = ballGlobal.y;
    else
        ysign = ballGlobal.y / math.abs(ballGlobal.y)
        action.args.gotoPose.y = penaltyBounds[2] * ysign * .5;
    end  
   action.args.gotoPose.a = 0
	action.ackNumber =  wcm.get_horde_ackNumber();
	sendBehavior(json.encode(action) .. "\n");


end

function relocalizeStart()
	action  = {}
    action["action"] = "lookBackwards";
    action["args"] = "";
	action.ackNumber =  wcm.get_horde_ackNumber();
	sendBehavior(json.encode(action) .. "\n");


end


function turnThetaLookGoalStart()
	action  = {}
        action["action"] = "turnThetaLookGoal";
        action["args"] = "";
        action.ackNumber =  wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
end


gotoPosition = makeBehavior("gotoPosition", nil,nil,gotoPositionStart)
stopPose = makeBehavior("stopPose", nil, nil, stopPoseStart);
walkForward = makeBehavior("walkForward", nil, nil, walkForwardStart);
stopMoving = makeBehavior("stopMoving", nil, nil, stopPoseStart);
gotoPoseFacing = makeBehavior("gotoPoseFacing", nil, gotoPoseFacingStop, gotoPoseFacingStart);
gotoBall = makeBehavior("gotoBall", nil, gotoBallStop, gotoBallStart);
approachTarget = makeBehavior("approachTarget", nil, approachTargetStop, approachTargetStart);
kickBall = makeBehavior("kickBall", nil, kickBallStop, kickBallStart);
locateBall = makeBehavior("locateBall",nil,nil,locateBallStart);
safety = makeBehavior("safety" , nil, nil,safetyStart);
declare = makeBehavior("declare", nil, nil, declareStart);
undeclare = makeBehavior("undeclare", nil,nil, undeclareStart);
gotoPoseWhileLookingBackwards = makeBehavior("gotoPoseWhileLookingBackwards", gotoPoseWhileLookingBackwardsStart, nil, gotoWhileFacingGoalieGo);
gotoWhileFacingGoalie = makeBehavior("gotoWhileFacingGoalie", gotoWhileFacingGoalieStart, nil, gotoWhileFacingGoalieGo);
relocalize = makeBehavior("relocalize", nil, nil, relocalizeStart);
turnThetaLookGoal = makeBehavior("turnThetaLookGoal", nil, nil, turnThetaLookGoalStart);

kittyMachine = kitty.kittyMachine
--kittyMachine
print(tostring(kittyMachine) .. " ok in support")
--super SUPER SUPER SUPER TODO IMPORTANT TODO NOW--- 
-- IF YOU EXPECT THIS MACHINE TO WORK WITH MORE THAN ONE PLAYER LIKE A REAL GAME CHANGE THE LOGIC FOR CLOSEST BALL, IT'S COMPLETELY BACKWARDS ( ON PURPOSE FOR TESTING--
defer = makeBehavior("defer",nil,nil,deferStart);

RelocalizeHFA = makeHFA("RelocalizeHFA", makeTransition({

        [start] = resetTimer,
	[turnThetaLookGoal] = function()
			if (currentTimer(RelocalizeHFA) > 5) then
				return done;
			else
				return turnThetaLookGoal;
			end
		end,

	[resetTimer] = function()
			return turnThetaLookGoal;
		end           
       }), false)

badLocalization = false;
DefendGoalHFA = makeHFA("DefendGoalHFA", makeTransition({

        [start] = kittyMachine,

        [kittyMachine] = function()
                if(wcm.get_horde_goalieCloseEnough()~=1) then -- or math.abs(wcm.get_pose()['x']) < 1.0) then -- change to ball x position?
                 	 --      print("going to backwards " .. tostring(gotoPoseWhileLookingBackwards));
                 	badLocalization = true;
			return gotoWhileFacingGoalie;
                end
                
				return kittyMachine;
            end,


        [gotoWhileFacingGoalie] = function()
                --print("status is " .. tostring(wcm.get_horde_status()) .. " in defend transition")
                if(wcm.get_horde_goalieCloseEnough() == 1.0) then
			print("going to kittch machine " .. tostring(kittyMachine));
                        return kittyMachine
                end
		return gotoWhileFacingGoalie;
         end,
       }), false)



GoalieHFA = makeHFA("GoalieHFA", makeTransition({

        [start] = resetTimer,

        [DefendGoalHFA] = function()
                if(wcm.get_horde_goalieCloseEnough() ~= 1 and currentTimer(GoalieHFA) > 20) then
            		badLocalization = false;
            		return resetTimer;
                end
				return DefendGoalHFA;
            end,
        [RelocalizeHFA] = function()
        		if(wcm.get_horde_seeTwoPosts() == 1 and wcm.get_horde_goalCloseDist() < 1) then
				wcm.set_horde_moveParticlesToCenter(1);
			end
			

			if(wcm.get_horde_goalieCloseEnough() == 1 or GoalieHFA.done == true or getOnOffense() == 0) then
                		return DefendGoalHFA;
                	end
				return RelocalizeHFA;
        
        	end,
	[resetTimer] = function()
			return RelocalizeHFA;
		end
            
       }), false)

wcm.set_horde_ballLost(1)
lastTimeFound = Body.get_time();
function isBallLost()
	--print("got into ball lost")
	if vcm.get_ball_detect() ~= 0 then
		wcm.set_horde_ballLost(0);
		lastTimeFound = Body.get_time();
	elseif(Body.get_time() - lastTimeFound > 5) then
		wcm.set_horde_ballLost(1);
	end
	--print("got out of ball lost");
end


function closestToBall()
	return wcm.get_team_is_smallest_eta();
end

function getGoalieBallDistance()
	return distGeneral({0,0}, {wcm.get_ball_x(), wcm.get_ball_y()});
--	local ballGlobal = wcm.get_team_closestToBallLoc()
--	local myPose = wcm.get_pose()
	
--	return distGeneral({ballGlobal[1], ballGlobal[2]},{myPose.x, myPose.y});  
end

function getOnOffense()
	local ballGlobal = wcm.get_team_closestToBallLoc()
	return wcm.get_horde_goalSign() ~= (ballGlobal[1] / math.abs(ballGlobal[1]));
end

function getGoalBallDistance()

	local ballGlobal = wcm.get_team_closestToBallLoc()
	local goalMidpoint = getDefendGoalMidpoint() 
	
	return distGeneral({ballGlobal[1], ballGlobal[2]},goalMidpoint);  
end

function canSeePost() 
	return wcm.get_horde_canSeePost()
end

function getPenaltyBounds()
	return {wcm.get_horde_penaltyBoundsX(), wcm.get_horde_penaltyBoundsY()};	
end


function getDefendGoalMidpoint()


        if gcm.get_team_color() == 1 then

                -- red attacks cyan goali
                print(" yellow ")
                postDefend = PoseFilter.postYellow;
        else
                print("not yellow")
                -- blue attack yellow goal
                postDefend = PoseFilter.postCyan;
        end

        -- global 
        LPost = postDefend[1];
        RPost = postDefend[2];
	
	return {LPost[1], 0}
end

function getMidpoint()
	themid = wcm.get_horde_midpointBallGoal();
	midpoint = {}
	midpoint.x = themid[1]
	midpoint.y = themid[2]
	midpoint.a = 0
		
	return midpoint
end

function distToMidpoint()
	return dist(wcm.get_pose(), getMidpoint());
end
-- simple dist function
function dist(curA, targetB)
	return math.sqrt(math.pow(curA.x - targetB.x,2) + math.pow(curA.y - targetB.y,2))
end

function distGeneral(curA, targetB)
        return math.sqrt(math.pow(curA[1] - targetB[1],2) + math.pow(curA[2] - targetB[2],2))
end
      
connectionThread = function ()
        print("got into con thread");
	if( darwin ) then
        --local tDelay = 0.005 * 1E6; -- Loop every 5ms
--      setup the server
        client = connectToHorde(4009);--initialize connection, wait for it.....
        connected = true;

		startSending = {}
        startSending.action="StartSending";
        startSending.args = "";
		startSending.ackNumber = 0;
        print("to send " .. tostring(json.encode(startSending)) .. " \n ");
        client:send(json.encode(startSending) .. "\n");

	-- kitty needs a client
	--kitty.client = client
	kitty.setClient(client)
        wcm.set_horde_ackNumber(1);
		print("connected")
        while connected do
			client:settimeout(.05);
			recval = client:receive()
			-- convert the json to get the ackNumber
			status, recJson = pcall(json.decode,recval);
			if status == true then
                status = string.sub(recval, 1, 1) == "{"
            end

			--print(tostring(recJson))
			if (status == true and recJson.ackNumber == wcm.get_horde_ackNumber()) then
				isBallLost();
			    --kitty.wcm.get_horde_ballLost() = wcm.get_horde_ballLost()	
				while wcm.get_horde_sentBehavior() == 0 do
					isBallLost();
					pulse(GoalieHFA);
				end
				wcm.set_horde_sentBehavior(0);
				print("cur rec number " .. tostring(wcm.get_horde_ackNumber()) .. "..........................................")
				wcm.set_horde_ackNumber(wcm.get_horde_ackNumber()+1)
			end
		end
    end
end

--start "main"
if(darwin) then 
	Speak.talk("my id is " .. Config.game.playerID);

		--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
      	print("starting connection thread\n");
	--coroutine.resume(connectionThread);
	connectionThread()
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

