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
	client:send(json.encode(thedata));
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
	client:send(json.encode(features));
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

hordeFunctions["walkForward"] = function(args,client)
	BodyFSM.sm:set_state('bodyWalkForward');
end

hordeFunctions["gotoBall"] = function(args,client)
	BodyFSM.sm:set_state('bodyGotoBall');
end
hordeFunctions["approachBall"] = function(args,client)
        BodyFSM.sm:set_state('bodyApproachGMU');
end
hordeFunctions["kickBall"] = function(args,client)
        BodyFSM.sm:set_state('bodyKickGMU');
end
hordeFunctions["moveX"] = function(args,client)
	BodyFSM.sm:set_state('bodyNull');
	walk.set_velocity(.02,0,0);
end

hordeFunctions["moveY"] = function(args,client)
        BodyFSM.sm:set_state('bodyNull');
        walk.set_velocity(.02,1,0);
end

hordeFunctions["moveTheta"] = function(args,client)
        BodyFSM.sm:set_state('bodyNull');
        walk.set_velocity(0,0,1);
end
hordeFunctions["stop"] = function(args,client)
        BodyFSM.sm:set_state('bodyStop');
       
end
hordeFunctions["StartSending"] = function (args, client)
--      coroutine.resume(co,args, client);
        wcm.set_horde_sendStatus("StartSending");
end

package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;

package.path = cwd..'/BodyFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
require('BodyFSMGMU')
require('HeadFSM')

