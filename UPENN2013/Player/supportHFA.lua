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
hfa = require('hfa')
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
Speak.talk("My Player ID Is defiantly the number " .. Config.game.playerID);
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
              	return client;
end

gotoPoseFacingStart = function(hfa) 
  			action  = {}
                        action["action"] = "gotoPoseFacing";
                        action["args"] = {};
			ball=wcm.get_ball();
 		       	-- my pose global
       		 	pose=wcm.get_pose();

        		-- determine which goal post the ball is closest to
       			-- so need its global coords
       			ballGlobal = util.pose_global({ball.x, ball.y, 0}, {pose.x, pose.y, pose.a})			
			dest = getMidpoint()
			action.args.facing = {};
			action.args.facing.x = ballGlobal.x
			action.args.facing.y = ballGlobal.y
			action.args.facing.a = 0
			action.args.gotoPose = {};   
                        action.args.gotoPose.x = dest.x
                        action.args.gotoPose.y = dest.y
                        action.args.gotoPose.a = 0
     			print(json.encode(action) .. "\n") 
                        client:send(json.encode(action) .. "\n");
end	
gotoPoseFacingGo = function(hfa)

end
gotoPoseFacingStop = function (hfa)
end

stopStart = function(hfa)
	action  = {}
        action["action"] = "stop";
        action["args"] = "nan";
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
        action["args"] = "nan";
        client:send(json.encode(action) .. "\n");
end
locateBallStop = function()end
locateBallGo = function()end
gotoBallGo = function()end
gotoBallStart = function()
	print("going to ball")
 	action  = {}
        action["action"] = "gotoBall";
        action["args"] = "nan";
        client:send(json.encode(action) .. "\n");
end
gotoBallStop = function()end

approachTargetStart = function()
	print("approach target")
	 action  = {}
        action["action"] = "approachBall";
        action["args"] = "nan";
        client:send(json.encode(action) .. "\n");
end
approachTargetStop = function()end
approachTargetGo = function()end
kickBallStart = function() 
	print("kicking ball");
 	action  = {}
        action["action"] = "kickBall";
        action["args"] = "nan";
        client:send(json.encode(action) .. "\n");
end
kickBallStop = function()end
kickBallgo = function()end


stopPoseStart = function()
	print("Stop")

	action = {}
	action["action"] = "stop";
	action["args"] = "nan";
	client:send(json.encode(action) .. "\n");
end

stopPose = makeBehvior("stopPose", stopPoseStart, nil, nil);
walkForward = makeBehavior("walkForward", walkForwardStart, walkForwardStop, walkForwardGo);
stop = makeBehavior("stop", stopStart, stopStop, stopGo);
gotoPoseFacing = makeBehavior("gotoPoseFacing", gotoPoseFacingStart, gotoPoseFacingStop, gotoPoseFacingGo);
gotoBall = makeBehavior("gotoBall", gotoBallStart, gotoBallStop, gotoBallGo);
approachTarget = makeBehavior("approachTarget", approachTargetStart, approachTargetStop, approachTargetGo);
kickBall = makeBehavior("kickBall", kickBallStart, kickBallStop, kickBallGo);


myMachine = makeHFA("myMachine", makeTransition({
	[start] = locateBall --gotoPoseFacing,
	[locateBall] = function() if ballLost  then return locateBall else return gotoPoseFacing end end,
	[gotoPoseFacing] = function() if distToMidpoint() < 0.3 then return stopPose else return gotoPoseFacing   end end
	[stopPose] = function() if distToMidpoint() > 0.3 then return gotoPoseFacing elseif closestToBall() >= 1 then return done  else  return stopPose end end
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

	

	


	return 0;
end





function getMidpoint()

	
	if gcm.get_team_color() == 1 then
    		-- red attacks cyan goal
    		postDefend = PoseFilter.postYellow;
  	else
    		-- blue attack yellow goal
    		postDefend = PoseFilter.postCyan;
  	end
	
	-- global 
	LPost = postDefend[1];
	RPost = postDefend[2];
	-- relative
	ball=wcm.get_ball();
	-- my pose global
  	pose=wcm.get_pose();
	
	-- determine which goal post the ball is closest to
	-- so need its global coords
	ballGlobal = util.pose_global({ball.x, ball.y, 0}, {pose.x, pose.y, pose.a})
	if dist(ballGlobal, LPost) > dist(ballGlobal, RPost) then
		farPost = LPost
	else
		farPost = RPost
	end
	
	midpoint = {}
	midpoint.x = (ballGlobal.x - farPost.x) / 2
	midpoint.y = (ballGlobal.y - farPost.y) /2
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
                local tDelay = 0.005 * 1E6; -- Loop every 5ms


 -- setup the server
               client = connectToHorde(4009);--initialize connection, wait for it.....
               connected = true;
--               darwinComm = setupUDPDarwins();
                     
		print("connected")
  
                while connected do
--[[                	action  = {}
			action["action"] = "walkForward";
			action["args"] = "nan";      
			client:send(json.encode(action) .. "\n");
		]]--
			isBallLost();
			--print("ball detect? : " .. tostring(vcm.get_ball_detect()));
			pulse(myMachine);
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

