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
setBodyFSM = true;-- assume we start with GMU fsm
previousState = "nil";
fpsTimer = Body.get_time();
lastCommand = nil;
function updateAll(newState)
	--gcm.set_game_state(3);
       	--print("Motion update");
	Motion.update();
	--print("Body update");
       	Body.update();
	--print("body FSM update");
        BodyFSM.update();
	--print("HeadFSM update");
        HeadFSM.update();
--	GameFSM.update();	
	fpsTimer = Body.get_time(); 
end
count = 0;
function sendFeatures (client)
        if(wcm.get_horde_sendStatus()~="StartSending") then
        	--print("Start sending was false");
	 	return;
        end
	--print("wcm send status was true");
		features = {}
        features["playerID"] = Config.game.playerID;
        features["role"] = Config.game.role;
		xPoseArr = {}
		xPoseArr[1] = wcm.get_team_attacker_pose()[1];
		xPoseArr[2] = wcm.get_team_goalie_pose()[1];
		yPoseArr = {}
		yPoseArr[1] = wcm.get_team_attacker_pose()[2];
        yPoseArr[2] = wcm.get_team_goalie_pose()[2];
		aPoseArr = {}
		aPoseArr[1] = wcm.get_team_attacker_pose()[3];
		aPoseArr[2] = wcm.get_team_goalie_pose()[3];
	--print("mine: " .. wcm.get_pose().x .. " 1: " .. xPoseArr[1] .. " 2: " .. xPoseArr[2]);
	--print("role: " .. Config.game.role .. " playerID: " .. Config.game.playerID);
        features["poseX"] = xPoseArr;
        features["poseY"] = yPoseArr;
        features["poseA"] = aPoseArr;
        
	
        features["ballDetect"] = vcm.get_ball_detect();
        features["ballX"] = wcm.get_ball_x();
        features["ballY"] = wcm.get_ball_y();
        features["doneApproach"] = wcm.get_horde_doneApproach();
        features["particleX"] = wcm.get_particle_x();
        features["particleY"] = wcm.get_particle_y();
		features["particleA"] = wcm.get_particle_a();
	--print("gonna broadcast my features");
		features["yelledReady"] = wcm.get_horde_yelledReady();
		features["yelledKick"] = wcm.get_horde_yelledKick();
    	features["yelledFail"] = wcm.get_horde_yelledFail(); 
	--print("sending some features, yo\n");-- wcm.set_horde_doneFrontApproach("true");
       -- print(json.encode(features) .. "\n");
	
		client:settimeout(.01);
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
lineID = 0
lastState = 100;
function checkTimeout()
	--print("commparing values");
	if(wcm.get_horde_timeMark() ~= nil) then
	--	print(" " .. wcm.get_horde_timeMark()); 
	end
	if(Body.get_time() - wcm.get_horde_timeMark() > 5.0) then
	--	print("setting value");
		wcm.set_horde_passKick(0);
	end
	if((Body.get_time() - fpsTimer) > .1) then
                print("time since last frame: " .. (Body.get_time() - fpsTimer) .. updateAllTimer .. " " .. sendFeaturesTimer);
        end
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
                local tDelay = 0.005 * 1E6; -- Loop every 5ms


 -- setup the server
               client = connectToHorde(4009);--initialize connection, wait for it.....
               connected = true;
--               darwinComm = setupUDPDarwins();
                     
		print("connected")
  
                while connected do
                        --print("update all")
			updateAllTimer = Body.get_time();
			updateAll();--move mah body, update FSM
			updateAllTimer = Body.get_time()-updateAllTimer;
                        --print("send features");
			sendFeaturesTimer = Body.get_time();
			--sendFeatures(client);--send all the features to horde
			sendFeaturesTimer = Body.get_time() - sendFeaturesTimer;
                        --print("checkTimeout");
			--checkTimeout(); -- very special case for passKick timing out the feature to 0 after a second
			client:settimeout(0);--non blocking read
			
			client:send("request\n");
	--		print("sending request");
			local line, err = client:receive() -- read in horde commands
			if(line~=nil) then
				client:send("ack\n")
				print("---------------------------- ID OF LINE IS " .. lineID .. " ----------------------------------")
				local req = json.decode(line)	
				
				print("Received: " .. tostring(line))
				if(req.args-1 >  lineID) then
					return;
				end
				lineID = lineID+1;
			end	
			--print("are we in penalty?")
			--print("vector of penalites: ", gcm.get_game_penalty())	
			--print("printing: ".. tostring(in_penalty()));
			--print("maybe? doing horde stuff, idk " .. wcm.get_horde_sendStatus() .. " " .. gcm.get_game_state() .. " " .. tostring(in_penalty()));
				
			if (gcm.get_game_state() ~= 3 or in_penalty()) then
				if(line~=nil and not string.find(line, "update") and not err) then
					lastCommand = line
				end
				--print("not doing horde stuff, that's for sure " .. wcm.get_horde_sendStatus() .. " " .. gcm.get_game_state() .. " " .. tostring(in_penalty()));
				--print("not calling horde function");
				local state = gcm.get_game_state();
				
		        	if state ~= 3 then
  					if (state == 0 and lastState ~= 0) then
    						
						BodyFSM.sm:set_state('bodyIdle')-- 'initial';
  						BodyFSM.update();
						BodyFSM.update();
						BodyFSM.sm:set_state('bodyStop');
						HeadFSM.sm:set_state('headIdle')
						
					elseif state == 1 and lastState ~= 1 then
						BodyFSM.sm:set_state('bodyReady') -- ready
						BodyFSM.update();
						BodyFSM.update();
						BodyFSM.update();
						BodyFSM.sm:set_state('bodyReadyMove') -- ready
						HeadFSM.sm:set_state('headLookGoalGMU')
					elseif (state == 2 and lastState ~=2 ) then
    						BodyFSM.sm:set_state('bodyStop') --'set';
  						HeadFSM.sm:set_state('headTrack');
				--	elseif (state == 3) then
    				--		return 'playing';
  					elseif (state == 4 and lastState ~=4) then
    						BodyFSM.sm:set_state('bodyIdle')	-- 'finished';
  						HeadFSM.sm:set_state('headIdle');
					end
					
					--GameFSM.update();
				end
				lastState = state;
				if in_penalty() then
					hoard_functions.hordeFunctions["position"](nil,nil); -- if we are not playing, do upenn positions
				end
			elseif not err then
				lastState = 3;
                --print(line);

                if(line~=nil and (line~=lastReceivedState or string.find(line,"update"))) then -- uf we received somethin:g
					print("last Received was " .. tostring(lastReceivedState));
					updateAction(line, client);
					i = 0
					while i<100 do
						
						updateAll();
					
						unix.usleep(.005 * 1E6);
						i=i+1;
					end	
					lastReceivedState = line;
				end
				print("update success\n");
                if err == "closed" then
                               connected = false;
                end
				if lastCommand ~= nil then
					updateAction(lastCommand,client);
					lastCommand = nil;
				end
			end    
            end
			 unix.usleep(tDelay);

        end
end
function in_penalty() 
	--print(Config.game.playerID);
	
	--print(vector.tostring(gcm.get_game_penalty()));
	--print("if i error here i'm a lemon");
	local k = gcm.get_game_penalty();
	--print(vector.tostring(k));
	--print("okay, now if i error im a giant lemon");
	--print((k[Config.game.playerID]>0));	
	--print((k[Config.game.playerID]>0));
	--print((k[Config.game.playerID]>0));	
	--print((k[Config.game.playerID]>0));	
	--print((k[Config.game.playerID]>0));	
	--print((k[Config.game.playerID]>0));	
	--print((k[Config.game.playerID]>0));	
	local p = k[Config.game.playerID]>0;
	--print("p is .. ".. tostring(p));
	return p;
end
function updateAction(servData, client)
  count = count + 1;
  --Update battery info
  wcm.set_robot_battery_level(Body.get_battery_level());
  vcm.set_camera_teambroadcast(1); --Turn on wireless team broadcast
        print("printing servData");
	--print(servData);  
	--print("In update")
	req = json.decode(servData)
        --print("fuckshit\n")
	print("unholywords\n");
	unix.usleep(.04*1E6);
	print("Received action "..req.action);
	--BodyFSM = require('BodyFSM');
	hoard_functions.hordeFunctions[req.action](req.args, client)--this is wrong, only here for the send.... TODO
	--print("after horde function");
	--unix.usleep(1*1E6);	
--updateAll
	--wcm.set_horde_state(req.action);
--  hordeFunctions["walkForward"](nil,nil);  
end

function initMotion()--should be cleaned up, gets servos hard and standing up
	BodyFSM.entry();
	Motion.entry();
  	BodyFSM.update();
	BodyFSM.update();
	BodyFSM.sm:set_state('bodyReady');
	BodyFSM.update();
	BodyFSM.update();
	BodyFSM.sm:set_state('bodyUnpenalized');
	BodyFSM.update();
	BodyFSM.update();
	BodyFSM.sm:set_state('bodyIdle');
	BodyFSM.update();
	BodyFSM.update();

	--      GameFSM.entry();
	
		Motion.update();
		Motion.update();
		Motion.update();
	unix.usleep(.05*1E6);
	
		Motion.update();
		Motion.update();
		Motion.update();--BodyFSM.sm:set_state('bodyIdle')
	--GameFSM.sm:set_state('gameInitial')
		
	--BodyFSM.update();
	
--	BodyFSM.entry();	
end
--start "main"
if(darwin) then 
	--        hoard_functions.hordeFunctions["murder all humans"](nil,nil);
	--Motion.event("standup");	
    wcm.set_horde_yelledReady(0);
	wcm.set_horde_yelledKick(0);
	initMotion();
	print("starting connection thread\n");
	--coroutine.resume(connectionThread);
	connectionThread();
	print("connection lost")
--	wcm.set_horde_state("gotoBall");
end
--connection drew stuff, seriously i'm ruining this beautiful code

