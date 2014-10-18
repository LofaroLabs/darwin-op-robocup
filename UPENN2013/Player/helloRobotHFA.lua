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

lastTimeFound = Body.get_time();
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
                        
			print("walk forward");
			print("ball lost value is :" ..wcm.get_horde_ballLost());
			action["action"] = "setVelocity";
                        -- showing a few ways to index lua
			action["args"] = {}
			action["args"]["x"] = -1* .005;
			action.args.y = 0;
			action.args["a"] = 0;
     
                        action["ackNumber"] = wcm.get_horde_ackNumber();
			wcm.set_horde_sentBehavior(1);
			client:send(json.encode(action) .. "\n");
end	
walkForwardGo = function(hfa)
	--print("should be walking forward");
end
walkForwardStop = function (hfa)
end

stopStart = function(hfa)
	print("stop");
	print("ball lost value is :" ..wcm.get_horde_ballLost());	
	action  = {}
        action["action"] = "stop";
        action["args"] = "nan";
	action.ackNumber = wcm.get_horde_ackNumber();
        wcm.set_horde_sentBehavior(1);
	
	client:send(json.encode(action) .. "\n");
end
stopGo = function(hfa)
--	print("i have stopped");
end
stopStop = function(hfa)
end

walkForward = makeBehavior("walkForward", walkForwardStart, walkForwardStop, walkForwardGo);
stop = makeBehavior("stop", stopStart, stopStop, stopGo);
myMachine = makeHFA("myMachine", makeTransition({
	[start] = walkForward,
	[walkForward] = function() if wcm.get_horde_ballLost()==0 then return stop else return walkForward end end,
	[stop] = function() if wcm.get_horde_ballLost()==1 then return walkForward else return stop end end,
	}),false);

function connectionThread()
        print("got into con thread");
	if( darwin ) then
                local tDelay = 0.005 * 1E6; -- Loop every 5ms


 -- setup the server
               client = connectToHorde(4009);--initialize connection, wait for it.....
               connected = true;
--               darwinComm = setupUDPDarwins();
                		startSending = {}
        	startSending.action="StartSending";
        	startSending.args = "";
		startSending.ackNumber = 0;
	        print("to send " .. tostring(json.encode(startSending)) .. " \n ");
       		client:send(json.encode(startSending) .. "\n");
		print("connected");
  		wcm.set_horde_ackNumber(1);
		setDebugTrue();
                while connected do
			--print("setting time out");
			client:settimeout(.05);
			--print("setting recval");
			recval = client:receive()
			--print("about to do my pcall");
			status, recJson = pcall(json.decode,recval);
			--print("comparing status");
			if(recval ~= nil and status == true) then
               		 status = string.sub(recval, 1, 1) == "{"
            		end
			if(recval == nil) then
				status = false;
			end
			--print("recjason is " .. tostring(recJson))
			setDebugTrue();
			if(recJson~=nil) then
			--	print("status is " .. tostring(status) .. " received acknumber: " .. tostring(recJson.ackNumber) .. "expected " .. tostring(wcm.get_horde_ackNumber())); 
				if (status == true and recJson.ackNumber == wcm.get_horde_ackNumber()) then
			--		print("I GOT A MESSAGE");
					isBallLost();
				    	--kitty.wcm.get_horde_ballLost() = wcm.get_horde_ballLost()	
					while wcm.get_horde_sentBehavior() == 0 do
			--			print("thinking of a new state for you....");
						isBallLost();
						print("pulsing on " .. wcm.get_horde_ballLost());
						pulse(myMachine);
					end
					wcm.set_horde_sentBehavior(0);
	
			--		print("ball detect? : " .. tostring(vcm.get_ball_detect()));
					--pulse(myMachine);
					wcm.set_horde_ackNumber( wcm.get_horde_ackNumber()+1);
				else
			--		print("waiting for an ack");
				end
				
			else
			--	print("havent Received a message since i sent the last one");
			end
		end
        end
end
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


--start "main"
if(darwin) then 
	setDebugTrue();
	print("hey buddy");
		--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
      	print("starting connection thread\n");
	connectionThread();
	--coroutine.resume(connectionThread);
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

