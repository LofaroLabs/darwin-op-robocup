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

gcm.say_id();

darwin = true;

ready = true;
smindex = 0;
initToggle = true;

-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();
connected = false;

package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;

package.path = cwd..'/BodyFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
require('BodyFSM')
require('HeadFSM')
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

previousState = "nil";
function updateAll(newState)
	gcm.set_game_state(3);
       	Motion.update();
       	Body.update();
        BodyFSM.update();
        HeadFSM.update();
end
count = 0;
function sendFeatures (client)
        if(wcm.get_horde_sendStatus()~="StartSending") then
         	return;
        end
	features = {};
        features["poseX"] = wcm.get_pose().x;
        features["poseY"] = wcm.get_pose().y;
        features["poseA"] = wcm.get_pose().a;
        features["ballDetect"] = vcm.get_ball_detect();
        features["ballX"] = wcm.get_ball_x();
        features["ballY"] = wcm.get_ball_y();
        features["doneFrontApproach"] = wcm.get_horde_doneFrontApproach();
        --print("sending some features, yo\n");-- wcm.set_horde_doneFrontApproach("true");
        --print(json.encode(features) .. "\n");
	client:settimeout(nil);
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
connectionThread = coroutine.create(function ()
        print("got into con thread");
	if( darwin ) then
                local tDelay = 0.005 * 1E6; -- Loop every 5ms


 -- setup the server
               client = connectToHorde(4009);--initialize connection, wait for it.....
               connected = true;
               print("connected")
  
                while connected do
                        updateAll();--move mah body, update FSM
                	sendFeatures(client);
                        client:settimeout(0);--non blocking read
			local line, err = client:receive()
			if not err then
                                --print(line);
                                if(line~=nil) then
					updateAction(line, client);
				end
	--			print("update success\n");
                        elseif err == "closed" then
                               connected = false;
                        end    
                        unix.usleep(tDelay);
                end
        end
end)

function updateAction(servData, client)
  count = count + 1;
  --Update battery info
  wcm.set_robot_battery_level(Body.get_battery_level());
  vcm.set_camera_teambroadcast(1); --Turn on wireless team broadcast
  
	print("In update")
	req = json.decode(servData)

	print("Received action "..req.action);
	hoard_functions.hordeFunctions[req.action](req.args, client)--this is wrong, only here for the send.... TODO
	--updateAll
	--wcm.set_horde_state(req.action);
--  hordeFunctions["walkForward"](nil,nil);  
end

function initMotion()--should be cleaned up, gets servos hard and standing up
	BodyFSM.entry();
	Motion.entry();
        unix.usleep(1.00*1E6);

        Body.set_body_hardness(.0);
        Motion.event("sit");
        k = 0;
        while(.005 * k < 5.27) do
                Motion.update();
                Body.update();
                unix.usleep(.005*1E6);
                k=k+1;
        end
	--Motion.event("sit");
	unix.usleep(5.0*1E6);
	BodyFSM.sm:set_state('bodyKickTo')		
	BodyFSM.update();
	HeadFSM.sm:set_state('headTrackGMU');	
--	BodyFSM.entry();	
end
--start "main"
if(darwin) then 
--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	initMotion();
	print("starting connection thread\n");
	while(1) do
		unix.usleep(.05*1E6);
		updateAll();
		unix.usleep(.05*1E6);
		updateAll();
--		BodyFSM.sm:set_state('bodyKickGMU');
	end
--	coroutine.resume(connectionThread);
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code
