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
                        action["args"] = "nan";      
                        client:send(json.encode(action) .. "\n");
end	
walkForwardGo = function(hfa)

end
walkForwardStop = function (hfa)
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

walkForward = makeBehavior("walkForward", walkForwardStart, walkForwardStop, walkForwardGo);
stop = makeBehavior("stop", stopStart, stopStop, stopGo);
myMachine = makeHFA("myMachine", makeTransition({
	[start] = walkForward,
	[walkForward] = function() if vcm.get_ball_detect()~=0 then return stop else return walkForward end end,
	[stop] = function() if vcm.get_ball_detect()==0 then return walkForward else return stop end end,
	}),false);

connectionThread = coroutine.create(function ()
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
			print("ball detect? : " .. tostring(vcm.get_ball_detect()));
			pulse(myMachine);
		end
        end
end)

--start "main"
if(darwin) then 
		--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
      	print("starting connection thread\n");
	coroutine.resume(connectionThread);
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

