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
local hoard_util = require "hoard_util"

smindex=0;
package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;

package.path = cwd..'/BodyFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
require('BodyFSM')
require('HeadFSM')
count = 0;
updateMotion = coroutine.create(function() 
  count = count +1;
  while (1==1) do 
  --	print("loopin");
	Motion.update();
  	Body.update();
  	BodyFSM.update();
  	if(count==1000) then
		print("victory");	
		hoard_util.hordeFunctions["approachBall"](nil,nil);
  	end
  	if(count==2000) then
  		hordeFunctions["walkForward"](nil,nil);
  	end
  	if(count==3000) then
 	 	hordeFunctions["kickBall"](nil,nil);
  	end
  	if(count%100==0) then 
  		print("dufus, updating motion, nerd " .. count .. "\n");
  	end 
      count = count + 1;
      unix.usleep(.005*1E6);
  end
end)
function initMotion()
	print("initing motion");
	BodyFSM.entry();
	Motion.entry();
        unix.usleep(1.00*1E6);

        Body.set_body_hardness(.50);
        Motion.event("standup");
        k = 0;
        while(.005 * k < 5.27) do
                Motion.update();
                Body.update();
                unix.usleep(.005*1E6);
                k=k+1;
        end
	Motion.event("standup");
	unix.usleep(3.0*1E6);
	BodyFSM.sm:set_state('bodyStop')		
	BodyFSM.update();
	
--	BodyFSM.entry();	
end
print("am i a darwin?");
darwin = true;
if(darwin) then 
	print("IM A DARWIN GUYS");
	initMotion();
	print("init motion done, updateMotion");
	coroutine.resume(updateMotion);
end
--connection drew stuff, seriously i'm ruining this beautiful code

