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
--        gcm.set_game_state(3);
 
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
        features["particleX"] = wcm.get_particle_x();
        features["particleY"] = wcm.get_particle_y();
	features["particleA"] = wcm.get_particle_a();
	features["ready"] = wcm.get_horde_ready();
	features["passKick"] = wcm.get_horde_passKick();
        
	print("sending some features, yo\n");-- wcm.set_horde_doneFrontApproach("true");
        --print(json.encode(features) .. "\n");
	client:settimeout(nil);
	client:send(json.encode(features) .. "\n");
        -- Send the features to horde via the client
        -- args may contain the amount of time to wait between sending

end
--[[function setupUDPDarwins()
    --local myClient = 
   local host, port = "localhost", 4010
   -- load namespace
   local socket = require("socket")
   -- convert host name to ip address
   local ip = assert(socket.dns.toip(host))
   -- create a new UDP object
   local udp = assert(socket.udp())
   return udp
end]]--

function checkTimeout()
	--print("commparing values");
	if(Body.get_time() - wcm.get_horde_timeMark() > 5.0) then
		--print("setting value");
		wcm.set_horde_passKick(0);
	end
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
--               darwinComm = setupUDPDarwins();
                     
		print("connected")
  
                while connected do
                        print("update all")
			updateAll();--move mah body, update FSM
                	print("send features");
			sendFeatures(client);--send all the features to horde
                        print("checkTimeout");
			checkTimeout(); -- very special case for passKick timing out the feature to 0 after a second
			client:settimeout(0);--non blocking read
			local line, err = client:receive() -- read in horde commands
			if not err then
                                print(line);
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
        print("printing servData");
	print(servData);  
	print("In update")
	req = json.decode(servData)
        print("fuckshit\n")
	print("unholywords\n");
	unix.usleep(.04*1E6);
	print("Received action "..req.action);
	hoard_functions.hordeFunctions[req.action](req.args, client)--this is wrong, only here for the send.... TODO
	print("after horde function");
	--unix.usleep(1*1E6);	
--updateAll
	--wcm.set_horde_state(req.action);
--  hordeFunctions["walkForward"](nil,nil);  
end

function initMotion()--should be cleaned up, gets servos hard and standing up
	gcm.set_game_state(3);
 	BodyFSM.entry();
	Motion.entry();
        unix.usleep(1.00*1E6);

        Body.set_body_hardness(.00);
        Motion.event("sit");
        k = 0;
        while(.005 * k < 5.27) do
                Motion.update();
                Body.update();
                unix.usleep(.005*1E6);
                k=k+1;
        end
	Motion.event("sit");
	--BodyFSM.sm:set_state('bodyStop')		
	BodyFSM.update();
	
--	BodyFSM.entry();	
end
--start "main"
if(darwin) then 
--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
        initMotion();
	print("starting connection thread\n");
	coroutine.resume(connectionThread);
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

