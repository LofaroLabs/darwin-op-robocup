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

walkForwardStart = function(hfa) 
  			action  = {}
                        action["action"] = "walkForward";
                        action["args"] = countReceives;
			print(json.encode(action) .. "\n");      
                        client:send(json.encode(action) .. "\n");
end	
walkForwardGo = function(hfa)

end
walkForwardStop = function (hfa)
end

stopStart = function(hfa)
	action  = {}
        action["action"] = "stop";
        action["args"] = countReceives;
	print(json.encode(action) .. "\n");  
        client:send(json.encode(action) .. "\n");
end
stopGo = function(hfa)
end
stopStop = function(hfa)
end

locateBallStart = function(hfa)
	print("locating ball Start") 
	action  = {}
        action["action"] = "moveTheta";
        action["args"] = countReceives;
	print(json.encode(action) .. "\n");  
        client:send(json.encode(action) .. "\n");
	print("Locating ball Start done");
end
locateBallStop = function() print("Locate Ball stop");  end
locateBallGo = function()   print("Locate Ball Go");  end
gotoBallGo = function()end
gotoBallStart = function()
	print("going to ball")
 	action  = {}
        action["action"] = "gotoBall";
        action["args"] = countReceives;
	print(json.encode(action) .. "\n");  
        client:send(json.encode(action) .. "\n");
end
gotoBallStop = function()end

approachTargetStart = function()
	print("approach target")
	 action  = {}
        action["action"] = "approachBall";
        action["args"] = countReceives;
	print(json.encode(action) .. "\n");  
        client:send(json.encode(action) .. "\n");
end
approachTargetStop = function()end
approachTargetGo = function()end
kickBallStart = function() 
	print("kicking ball");
 	action  = {}
        action["action"] = "kickBall";
        action["args"] = countReceives;
	print(json.encode(action) .. "\n");  
        client:send(json.encode(action) .. "\n");
end
kickBallStop = function()end
kickBallgo = function()end

walkForward = makeBehavior("walkForward", nil, walkForwardStop, walkForwardStart);
stop = makeBehavior("stop", nil, stopStop, stopStart);
locateBall = makeBehavior("locateBall", nil, locateBallStop, locateBallStart);
gotoBall = makeBehavior("gotoBall", nil, gotoBallStop, gotoBallStart);
approachTarget = makeBehavior("approachTarget", nil, approachTargetStop, approachTargetStart);
kickBall = makeBehavior("kickBall", nil, kickBallStop, kickBallStart);

kittyMachine = makeHFA("myMachine", makeTransition({
	[start] = function()  print("transitoin for start to locate ball " .. tostring(countReceives));  return locateBall; end,
	[locateBall] = function() if ballLost  then return locateBall else return gotoBall end end,
	[gotoBall] = function() if ballLost then return locateBall elseif (math.abs(wcm.get_ball_x())+math.abs(wcm.get_ball_y())) < .2 then return approachTarget else  return gotoBall  end end,
	[approachTarget] = function() if ballLost then return locateBall elseif wcm.get_horde_doneApproach()~= 0 then print("We are done done approach? " .. tostring(wcm.get_horde_doneApproach())); return kickBall else return approachTarget end end, 
	[kickBall] = function() return done; end
	--[done] = start;	
--[done] = done;
	}),false);

--myMachine = makeHFA("myMachine", makeTransition({
--	[start] = locateBall,
--	[locateBall] = kickBall}), false);
ballLost = true;
lastTimeFound = Body.get_time();
function isBallLost()
	print("got into ball lost")
	if vcm.get_ball_detect() ~= 0 then
		ballLost = false;
		lastTimeFound = Body.get_time();
	elseif(Body.get_time() - lastTimeFound > 5) then
		ballLost = true;
	end
	print("got out of ball lost" .. tostring(ballLost));
end
connectionThread = function ()
        print("got into con thread");
	if( darwin ) then
                local tDelay = 0.005 * 1E6; -- Loop every 5ms




		







 -- setup the server
               client = connectToHorde(4009);--initialize connection, wait for it.....
               connected = true;
--               darwinComm = setupUDPDarwins();
                --client:send("wer\n")    
		print("connected")
 		countReceives = 0 
                while connected do
--[[                	action  = {}
			action["action"] = "walkForward";
			action["args"] = "nan";      
			client:send(json.encode(action) .. "\n");
		]]--
		--	isBallLost();
		--	print("Going to receive something")
			recval = client:receive()
		--	print("received " .. tostring(recval));
			
			while(recval ~= "request") do
				--print(tostring(recval))
				recval = client:receive()
			 end
		--	print("Got a request!");
			isBallLost();
			--print("ball detect? : " .. tostring(vcm.get_ball_detect()));
			pulse(myMachine);
			print("cur rec number " .. tostring(countReceives) .. "..........................................")
			countReceives = countReceives + 1;
		--	print("Pulsed! going to wait until i get an ack");
			while(recval ~= "ack") do
		--		print(tostring(recval))
				recval = client:receive()
			end
		--	print("got an ack");
		end
        end
end

--start "main"
--[[if(darwin) then 
		--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
      	print("starting connection thread\n");
	--coroutine.resume(connectionThread);
	connectionThread()
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end]]--
--connection drew stuff, seriously i'm ruining this beautiful code

