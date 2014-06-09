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

package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
package.path = cwd..'/GameFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;


package.path = cwd..'/BodyFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
--currentBodyFSM = require('BodyFSM')
require('BodyFSM');
require('HeadFSM');
--HeadFSM.entry();
--HeadFSM.sm:set_state('headStart');
--Body.set_head_hardness(.5); -- required to at least set the hardness in order for motions to work
leftArmMotion = math.pi/180*vector.new({60,30,-30});
--Body.set_larm_hardness({0.5,0.5,0.5});
--Body.set_larm_:command(leftArmMotion);
function inspect(key, value)
	table.foreach(value,print)
end

--table.foreach(Body.get_sensor_data(),inspect)

--my stuff, ugly
--        gcm.set_game_state(3);
count = 0;
function sendFeatures (client)
        --if(wcm.get_horde_sendStatus()~="StartSending") then
        	--print("Start sending was false");
	 --	return;
      --  end
	--print("wcm send status was true");
		features = {}
        features["ackNumber"] = ackNumber;
		client:settimeout(.01);
		client:send(json.encode(features) .. "\n");
        -- Send the features to horde via the client
        -- args may contain the amount of time to wait between sending

end
function connectToHorde(port)
		local socket = require("socket")
                local server = assert(socket.bind("*", port))
                local client = server:accept()
              	return client;
end
lastReceivedState = nil;
connectionThread = function ()
        print("got into con thread");
	if( darwin ) then

		-- setup the server
		client = connectToHorde(4009);--initialize connection, wait for it.....
		connected = true;
                   
		print("connected")
		ackNumber = 0;
		local testCount = 5
		while ackNumber ~= testCount do
			sendFeatures(client);--send all the features to horde
			client:settimeout(0);--non blocking read

			local line, err = client:receive() -- read in horde commands
			if(line~=nil) then
				--client:send("ack\n")
				
				print("---------------------------- ACK Number IS " .. ackNumber .. " ----------------------------------")
				local req = json.decode(line)	
				
				print("Received: " .. tostring(line))
				if(req.ackNumber ==  ackNumber) then
					ackNumber = ackNumber+1;
				end
			end
		end
			 unix.usleep(tDelay);

	end
end

--start "main"
if(darwin) then 
	--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
    wcm.set_horde_yelledReady(0);
	wcm.set_horde_yelledKick(0);

	print("starting connection thread\n");
	connectionThread();
	print("connection lost")

end
--connection drew stuff, seriously i'm ruining this beautiful code

