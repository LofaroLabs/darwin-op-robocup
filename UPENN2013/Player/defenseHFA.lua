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

local client

function setClient(someClient)
	client = someClient;
end

function inspect(key, value)
	table.foreach(value,print)
end

sentBehavior = false;
function sendBehavior(sendInfo)
    client:send(sendInfo)
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
	print("approach target")
	 action  = {}
        action["action"] = "approachBall";
        action["args"] = "";
		action.ackNumber =  wcm.get_horde_ackNumber();
        sendBehavior(json.encode(action) .. "\n");
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
                print(" yellow ")
        else
				
                print("not yellow")
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
                action.args.gotoPose.x = .2 * (ballGlobal.x/math.abs(ballGlobal.x))
                action.args.gotoPose.y = .2 * (ballGlobal.y/math.abs(ballGlobal.y))
                action.args.gotoPose.a = 0
		sendBehavior(json.encode(action) .. "\n");
end
gotoPosition = makeBehavior("gotoPosition", nil,nil,gotoPositionStart)
stopPose = makeBehavior("stopPose", nil, nil, stopPoseStart);
walkForward = makeBehavior("walkForward", nil, walkForwardStop, walkForwardStart);
stopMoving = makeBehavior("stopMoving", nil, nil, stopPoseStart);
gotoPoseFacing = makeBehavior("gotoPoseFacing", nil, gotoPoseFacingStop, gotoPoseFacingStart);
gotoBall = makeBehavior("gotoBall", nil, gotoBallStop, gotoBallStart);
approachTarget = makeBehavior("approachTarget", nil, approachTargetStop, approachTargetStart);
kickBall = makeBehavior("kickBall", nil, kickBallStop, kickBallStart);
locateBall = makeBehavior("locateBall",nil,nil,locateBallStart);
safety = makeBehavior("safety" , nil, nil,safetyStart);
declare = makeBehavior("declare", nil, nil, declareStart);
undeclare = makeBehavior("undeclare", nil,nil, undeclareStart);
kittyMachine = kitty.kittyMachine
--kittyMachine
print(tostring(kittyMachine) .. " ok in support")
--super SUPER SUPER SUPER TODO IMPORTANT TODO NOW--- 
-- IF YOU EXPECT THIS MACHINE TO WORK WITH MORE THAN ONE PLAYER LIKE A REAL GAME CHANGE THE LOGIC FOR CLOSEST BALL, IT'S COMPLETELY BACKWARDS ( ON PURPOSE FOR TESTING--
defer = makeBehavior("defer",nil,nil,deferStart);

defend = makeHFA("defend", makeTransition({
	[start] = kittyMachine,
	[kittyMachine] = function()
			print("in kitty machine in dkitty. goalie close? " .. wcm.get_horde_goalieCloseEnough())
			if(wcm.get_horde_goalieCloseEnough() == 1)
				then return defer;
			end
			return kittyMachine;
			
		end,
	[defer] = function()
		print("IN DEFER kitty goalie close? " .. wcm.get_horde_goalieCloseEnough());
		if( not (wcm.get_horde_goalieCloseEnough()==1))
			then return kittyMachine
		end
		return defer;
	end
}), false)
supportGoalie = makeHFA("supportGoalie", makeTransition({
	[start] = locateBall,
	[locateBall] = function()
			print("in support goalie locate ball");
		print(vcm.get_ball_detect() .. " this is the vaue for ball detect");	
			if(wcm.get_horde_ballLost()==1)
				
				then return locateBall;
			end
			return gotoPoseFacing;
	end,
	[gotoPoseFacing] = function() print("considering transitioning out of gotoPoseFacing");

                                        if wcm.get_horde_ballLost()==1 then
                                                print("locate ball");
                                                return locateBall
                                        elseif wcm.get_horde_yelledReady() == 1  then
                                                print("going to stop pose from goto");
                                                return stopPose;
                                        else
                                                print("going back to myself in gotopose");
                                                return gotoPoseFacing
                                        end
                                end,
        [stopPose] = function()
                                        print("stop pose in supprt");
                               		if wcm.get_horde_ballLost() == 1 then
                                                return locateBall;
                                        end
                                        if distToMidpoint() > 0.3 then --- we will want to check the facing angle too... 
                                                return gotoPoseFacing
                                        else
                                                print("goto stop pse againin from stop pose")
                                                return stopPose
                                        end 
				end,
	

}), false)
support  = makeHFA("support", makeTransition({

	[start] = supportGoalie,

	[supportGoalie] =  function() 
			print("in support goalie considering going to safety but status is " .. tostring(wcm.get_horde_status()));
			if(wcm.get_horde_status()>=3) then
				return safety;
			end
			return supportGoalie;

		  end,
	[safety] = function()
		print("in safety support considering support goalie, but status is " .. tostring(wcm.get_horde_status()))
		if(wcm.get_horde_status()<3) 
			then return supportGoalie;
		end
		return safety
	   end,


}), false)
--declare = "declare"
--undeclare = "undeclare"
DefenseHFA = makeHFA("DefenseHFA", makeTransition({

	[start] = support,

	[support] = function() 
				print("in support, checking status and declared " .. tostring(wcm.get_horde_status()) .. " and " .. tostring(wcm.get_horde_declared()))
                if(wcm.get_horde_status() == 0 and not (wcm.get_horde_declared()==1))
                        then return declare
                end
                return support;
            end,

	[declare] = function()
                print("in declare")
				wcm.set_horde_declared(1);
                return defend
            end,

	[defend] = function()
				print("status is " .. tostring(wcm.get_horde_status()) .. " in defend transition")
                if(wcm.get_horde_status() >=2)
                        then return undeclare
				end
				return defend;
	   end,
	[undeclare] = function()
			wcm.set_horde_declared(0);
			return support;
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
					pulse(DefenseHFA);
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

