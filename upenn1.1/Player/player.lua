module(... or "", package.seeall)

require('unix')
webots = false;
darwin = true;
local cwd = unix.getcwd();
-- the webots sim is run from the WebotsController dir (not Player)
if string.find(cwd, "WebotsController") then
  webots = true;
  cwd = cwd.."/Player"
  package.path = cwd.."/?.lua;"..package.path;
end

computer = os.getenv('COMPUTER') or "";
if (string.find(computer, "Darwin")) then
   -- MacOS X uses .dylib:
   package.cpath = cwd.."/Lib/?.dylib;"..package.cpath;
else
   package.cpath = cwd.."/Lib/?.so;"..package.cpath;
end

package.path = cwd.."/Util/?.lua;"..package.path;
package.path = cwd.."/Config/?.lua;"..package.path;
package.path = cwd.."/Lib/?.lua;"..package.path;
package.path = cwd.."/Dev/?.lua;"..package.path;
package.path = cwd.."/Motion/?.lua;"..package.path;
package.path = cwd.."/Vision/?.lua;"..package.path;
package.path = cwd.."/World/?.lua;"..package.path;
package.path = cwd.."/BodyFSM/?.lua;"..package.path;
package.path = cwd.."/HeadFSM/?.lua;"..package.path;

require('Config');
require('Body')
require('vector')
require('Vision')
require('World')
require('BodyFSM')
require('HeadFSM')
require('Motion')
require('getch')

-- initialize state machines
Vision.entry();
World.entry();
Motion.entry();

BodyFSM.entry();
HeadFSM.entry();


-- main loop
count = 0;
last_vision_update_time = Body.get_time();

function update()
  t= Body.get_time();
  count = count + 1;
  if t-last_vision_update_time>0.03 then
    Vision.update();
    World.update();
  end

  BodyFSM.update();
  HeadFSM.update();

  Motion.update();
  Body.update();

  local str=getch.get();
  if #str>0 then
	local byte=string.byte(str,1);
	if byte==string.byte("8") then	Motion.event("button");end
  end
end

-- if using Webots simulator just run update
if (webots) then
  print('webots');
  BodyFSM.sm:add_event('button');
  while 1 do 
    update();
  end
end

if (darwin) then
  print('DARwIn');
  while 1 do 
    update();
  end
end

