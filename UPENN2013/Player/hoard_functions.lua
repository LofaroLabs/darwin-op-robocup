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
--require('hcm')
require('Speak')
require('getch')
require('Body')
require('Motion')
json = require("json")

initGamePlaying = false;
initPenalized = false;
gcm.say_id();

darwin = true;

ready = true;
smindex = 0;
initToggle = true;

-- main loop
myFunctions = {}



myFunctions["getServos"] = function (args, client)
	print("You found getServos with args " .. args);
	thedata = Body.get_sensor_data();
	table.foreach(thedata, inspect);
	client:send(json.encode(thedata).."\n");
	print("Sent the sevo data!")
	print(json.encode(thedata))
end

myFunctions["setServos"] = function (args, client)

	print("You found setServos!!!");
	table.foreach(args, print)
	print(args.index .. " " .. args.current);
	Body.set_servo_command(args.index, math.pi/180*args.current)
end

myFunctions["setServoHardness"] = function (args, client)

	print("you called setServoHardness");
	Body.set_servo_hardness(args.index, args.hardness);

end

co = coroutine.create(function (args, client)
	if(args~=nil) then 
		unix.usleep(args *1E6);
	end
	
	features[1] = wcm.get_pose();
	features[2] = vcm.get_ball_detect();
	features[3] = wcm.get_ball_x();
	features[4] = wcm.get_ball_y();
	client:send(json.encode(features).. "\n");
	-- Send the features to horde via the client
	-- args may contain the amount of time to wait between sending
	
end )
myFunctions["StartSending"] = function (args, client)
--	coroutine.resume(co,args, client);
	wcm.set_horde_sendStatus("StartSending");
end

myFunctions["StopSending"] = function (args, client)
	--coroutine.yield(co)
	wcm.set_horde_sendStatus("StopSending");	
end


myFunctions["disconnect"] = function (args, client)
	client:close();
	connected = false;
end


myFunctions["doHordeMotion"] = function(args, client)


	hordeFunctions[args.action](args.args, client);

end

hordeFunctions = {}
hordeFunctions["headMotion"] = function(args, client)
end
hordeFunctions["yellKick"] = function(args, client)
	BodyFSM.sm:set_state('bodyPassKick');
end
-- upenn position, should only be used when our game state is set to !3
hordeFunctions["position"] = function(args, client)
	--print("calling body position");
	--BodyFSM.update();
	
	--print("game fsm set");
	if (gcm.in_penalty() and initPenalized==false) then
		print("setting game and body state");
		initPenalized = true;
		--initGamePlaying = false;
		GameFSM.sm:set_state('gamePenalized');
		BodyFSM.sm:set_state('bodyIdle');
	elseif gcm.get_game_state()~=3 and initGamePlaying == false then
		initGamePlaying = true;
		initPenalized = false;
        	print("setting game and body state in init game playing");
		GameFSM.sm:set_state('gamePlaying');
 		BodyFSM.sm:set_state('bodyPosition');
		BodyFSM.update();
	--	GameFSM.update();
		
	end
	--print("game fsm update");
	GameFSM.update();
	GameFSM.update();
	GameFSM.update();-- updating 3 times because it takes more than one update to resolve when the penality is over
	--if(gcm.in_penalty() == 1) then
	-- walk.update();
	--end
	--print("done with game fsm updates");
end

hordeFunctions["yellReady"] = function(args, client)
	BodyFSM.sm:set_state('bodyYellReady');
end
hordeFunctions["yellFail"] = function(args, client)
	BodyFSM.sm:set_state('bodyYellFail');
end



hordeFunctions["walkForward"] = function(args,client)
	BodyFSM.sm:set_state('bodyWalkForward');
end

hordeFunctions["gotoBall"] = function(args,client)
	BodyFSM.sm:set_state('bodyGotoBall');
end
hordeFunctions["dribbleBall"] = function(args,client)
	BodyFSM.sm:set_state('bodyDribble');
end

hordeFunctions["approachTarget"] = function(args,client)
	dest = json.decode(args)
        print("Dest " .. dest.x);
        --print("The args more specfied... X" .. args.x .. " Y " .. args.y .. " theta: " .. args.a);
        wcm.set_horde_kickToPose(vector.new({dest.x, dest.y, dest.a}));
	BodyFSM.sm:set_state('bodyApproachTarget');

end
hordeFunctions["approachBall"] = function(args,client)
        print("Approaching Ball");
	BodyFSM.sm:set_state('bodyApproach');
	print("Set State to bodyApproach for the BodyFSM");
	--sm.entry();
end
hordeFunctions["kickBall"] = function(args,client)
        BodyFSM.sm:set_state('bodyKickGMU');
end
hordeFunctions["moveX"] = function(args,client)
	BodyFSM.sm:set_state('bodyMoveX');
	--walk.set_velocity(.02,0,0);
end

hordeFunctions["moveY"] = function(args,client)
        BodyFSM.sm:set_state('bodyMoveY');
        --walk.set_velocity(0.0,1,0);
end

hordeFunctions["moveTheta"] = function(args,client)
        BodyFSM.sm:set_state('bodyMoveTheta');
      
end
hordeFunctions["stop"] = function(args,client)
        BodyFSM.sm:set_state('bodyStop');
       
end
hordeFunctions["StartSending"] = function (args, client)
--      coroutine.resume(co,args, client);
        wcm.set_horde_sendStatus("StartSending");
end
hordeFunctions["kickLeft"] = function(args,client)
        BodyFSM.sm:set_state('bodyKickLeftGMU');
end
hordeFunctions["kickRight"] = function(args,client)
        BodyFSM.sm:set_state('bodyKickRightGMU');
end
hordeFunctions["gotoPose"] = function(args, client)
	-- set the wcm values to the x,y,a from the args
	print("The args for gotoPose: " .. args .. "\n");
	print("HI");
	dest = json.decode(args)
	print("Dest " .. dest.x);
	--print("The args more specfied... X" .. args.x .. " Y " .. args.y .. " theta: " .. args.a);
	wcm.set_horde_gotoPose(vector.new({dest.x, dest.y, dest.a}));
	
	-- call the state
	BodyFSM.sm:set_state('bodyGotoPosition');
end

hordeFunctions["updateGotoPoseFacing"] = function(args, client)
	-- set the wcm values to the x,y,a from the args
	print("The args for gotoPose: " .. tostring(args) .. "\n");
	print("HI");
	decodedArgs = args;
	dest = decodedArgs["gotoPose"];
	facing = decodedArgs["facing"];
	print("Dest " .. dest.x);
	--print("The args more specfied... X" .. args.x .. " Y " .. args.y .. " theta: " .. args.a);
	wcm.set_horde_gotoPose(vector.new({dest.x, dest.y, dest.a}));
	wcm.set_horde_facing(vector.new({facing.x,facing.y,facing.a}));
	-- call the state
	--BodyFSM.sm:set_state('bodyGotoWhileFacing');
end

hordeFunctions["gotoPoseFacing"] = function(args, client)
	-- set the wcm values to the x,y,a from the args
	print("The args for gotoPose: " .. tostring(args) .. "\n");
	print("HI");
	decodedArgs = args;
	dest = decodedArgs["gotoPose"];
	facing = decodedArgs["facing"];
	print("Dest " .. dest.x);
	--print("The args more specfied... X" .. args.x .. " Y " .. args.y .. " theta: " .. args.a);
	wcm.set_horde_gotoPose(vector.new({dest.x, dest.y, dest.a}));
	wcm.set_horde_facing(vector.new({facing.x,facing.y,facing.a}));
	-- call the state
	BodyFSM.sm:set_state('bodyGotoWhileFacing');
end


package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
--package.path = cwd..'/GameFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
package.path = cwd..'/BodyFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
 package.path = cwd..'/GameFSM/'..Config.fsm.game..'/?.lua;'..package.path;
require('BodyFSMGMU')
require('HeadFSM')
require('GameFSM')
