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

function inspect(key, value)
	table.foreach(value,print)
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
			action.ackNumber = ackNumber;
            print("i am currently at: " .. pose.x .. ", " .. pose.y);
			print("trying to face " .. ballGlobal.x .. ", " .. ballGlobal.y);
			print("also moving to around " .. dest.x .. ", " .. dest.y);
			
			print(json.encode(action) .. "\n") 
           --if(vcm.get_ball_detect() == 1) then
                client:send(json.encode(action) .. "\n");
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
			action.ackNumber = ackNumber;
            print("i am currently at: " .. pose.x .. ", " .. pose.y);	
			print("trying to face " .. ballGlobal.x .. ", " .. ballGlobal.y);
			print("also moving to around " .. dest.x .. ", " .. dest.y);
			print(json.encode(action) .. "\n"); 
            
		    if(vcm.get_ball_detect() == 1) then		
				client:send(json.encode(action) .. "\n");
			end
end
gotoPoseFacingStop = function (hfa)
end

stopStart = function(hfa)
	action  = {}
        action["action"] = "stop";
        action["args"] = "";
		action.ackNumber = ackNumber;
        print(json.encode(action) .. "\n");
		client:send(json.encode(action) .. "\n");
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
		action.ackNumber = ackNumber;
        client:send(json.encode(action) .. "\n");
end
locateBallStop = function()end
locateBallGo = function()end
gotoBallGo = function()end
gotoBallStart = function()
	print("going to ball")
 	action  = {}
        action["action"] = "gotoBall";
        action["args"] = "";
		action.ackNumber = ackNumber;
        client:send(json.encode(action) .. "\n");
end
gotoBallStop = function()end

approachTargetStart = function()
	print("approach target")
	 action  = {}
        action["action"] = "approachBall";
        action["args"] = "";
		action.ackNumber = ackNumber;
        client:send(json.encode(action) .. "\n");
end
approachTargetStop = function()end
approachTargetGo = function()end
kickBallStart = function() 
	print("kicking ball");
 	action  = {}
        action["action"] = "kickBall";
        action["args"] = "";
		action.ackNumber = ackNumber;
        client:send(json.encode(action) .. "\n");
end
kickBallStop = function()end
kickBallgo = function()end


stopPoseStart = function()
	print("Stop")

	action = {}
	action["action"] = "stop";
	action["args"] = "";
	action.ackNumber = ackNumber;
	client:send(json.encode(action) .. "\n");
end

stopPose = makeBehavior("stopPose", nil, nil, stopPoseStart);
walkForward = makeBehavior("walkForward", nil, walkForwardStop, walkForwardStart);
stopMoving = makeBehavior("stopMoving", nil, nil, stopPoseStart);
gotoPoseFacing = makeBehavior("gotoPoseFacing", nil, gotoPoseFacingStop, gotoPoseFacingStart);
gotoBall = makeBehavior("gotoBall", nil, gotoBallStop, gotoBallStart);
approachTarget = makeBehavior("approachTarget", nil, approachTargetStop, approachTargetStart);
kickBall = makeBehavior("kickBall", nil, kickBallStop, kickBallStart);
locateBall = makeBehavior("locateBall",nil,nil,locateBallStart);

--super SUPER SUPER SUPER TODO IMPORTANT TODO NOW--- 
-- IF YOU EXPECT THIS MACHINE TO WORK WITH MORE THAN ONE PLAYER LIKE A REAL GAME CHANGE THE LOGIC FOR CLOSEST BALL, IT'S COMPLETELY BACKWARDS ( ON PURPOSE FOR TESTING--
myMachine = makeHFA("myMachine", makeTransition({
	[start] = locateBall, --gotoPoseFacing,
	[locateBall] = function() if ballLost  then return locateBall else return gotoPoseFacing end end,
	[gotoPoseFacing] = function() print("considering transitioning out of gotoPoseFacing"); 
					if ballLost then 
						print("locate ball"); 
						return locateBall 
					elseif closestToBall()==1 then 
						print("trans to stop "); 
						return stopMoving 
					--elseif distToMidpoint() < 0.3 then
					elseif wcm.get_horde_yelledReady() == 1  then 
						print("going to stop pose from goto"); 
						return stopPose;
					else 
						print("going back to myself in gotopose"); 
						return gotoPoseFacing   
					end 
				end,
	[stopPose] = function()
					if ballLost == true then
						return locateBall;
					end
					if distToMidpoint() > 0.3 then --- we will want to check the facing angle too... 
						return gotoPoseFacing 
					elseif closestToBall() == 1 then 
						print("go to done from stopPose")
						return locateBall
					else 
						print("goto stop pse againin from stop pose") 
						return stopPose 
					end end,
	[stopMoving] = function() 
					if ballLost then 
						return locateBall 
					elseif closestToBall() == 0 then 
						return gotoPoseFacing ; 
					else
						print("I stoped moving"); 
						return stopMoving 
					end end,
 	--[gotoBall] = function() if ballLost then return locateBall elseif (math.abs(wcm.get_ball_x())+math.abs(wcm.get_ball_y())) < .2 then return approachTarget else  return gotoBall  end end,
	--[approachTarget] = function() if ballLost then return locateBall elseif wcm.get_horde_doneApproach()~= 0 then return kickBall else return approachTarget end end, 
	--[kickBall] = function() unix.usleep(1 * 1E6); return locateBall; end
	--[done] = start;	
--[done] = done;
	}),false);
ballLost = true;
lastTimeFound = Body.get_time();
function isBallLost()
	--print("got into ball lost")
	if vcm.get_ball_detect() ~= 0 then
		ballLost = false;
		lastTimeFound = Body.get_time();
	elseif(Body.get_time() - lastTimeFound > 5) then
		ballLost = true;
	end
	--print("got out of ball lost");
end


function closestToBall()
	return wcm.get_team_is_smallest_eta();
end





function getMidpoint()

	
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
	--print(tostring(LPost))
    --print(tostring(RPost))
    -- relative
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
	LPost.x = LPost[1]
	LPost.y = LPost[2]
	RPost.x = RPost[1]
	RPost.y = RPost[2]
    farPost = {}
	if dist(ballGlobal, LPost) > dist(ballGlobal, RPost) then
		farPost.x = LPost[1]
		farPost.y = LPost[2]
		print("the far post is at coordinates: " .. tostring(farPost.x) .. ", " .. tostring(farPost.y))
		print("the near post is at coordinates: " .. tostring(RPost.x) .. ", " .. tostring(RPost.y))
	else
		farPost.x = RPost[1]
		farPost.y = RPost[2]
	
		print("the far post is at coordinates: " .. tostring(farPost.x) .. ", " .. tostring(farPost.y))
		print("the near post is at coordinates: " .. tostring(LPost.x) .. ", " .. tostring(LPost.y))
	end
	--print("going to the po	
	midpoint = {}
	midpoint.x = (ballGlobal.x + farPost.x) / 2
	midpoint.y = (ballGlobal.y + farPost.y) /2
	midpoint.a = 0
		
	return midpoint
end

function distToMidpoint()
	return dist(wcm.get_pose(), getMidpoint());
end
-- simple dist function
function dist(curA, targetB)
    --print("curA: " .. tostring(curA));
    --print("targetB:  " .. tostring(targetB));
	--print("curA.x: " .. curA.x);
	--print("targetB.x " .. targetB.x);
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


        ackNumber = 1
		print("connected")
        while connected do
			recval = client:receive()
			-- convert the json to get the ackNumber
			err, recJson = pcall(json.decode,recval);
			--print(tostring(recJson))
			if (err == true and recJson.ackNumber == ackNumber) then
				isBallLost();
				pulse(myMachine);
				print("cur rec number " .. tostring(ackNumber) .. "..........................................")
				ackNumber = ackNumber + 1;
			end
		end
    end
end

--start "main"
if(darwin) then 
		--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
      	print("starting connection thread\n");
	--coroutine.resume(connectionThread);
	connectionThread()
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

