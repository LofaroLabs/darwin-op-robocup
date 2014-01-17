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
require('Motion');
require('walk');
require('keyframe')

walk.enable=false;
Body.set_syncread_enable(1);
keyframe.is_upper=true; --upper body only

print("test keyframe file name:");
keyin=io.stdin:read()
keyframe.load_motion_file(keyin,'wave');


-- initialize state machines
Motion.entry();

demo_phase=1;
demo_interval={.1,1,5,3,1};
t0=Body.get_time();
demo_ended=false;

-- main loop

function update()
  local t=Body.get_time();
  if demo_phase==3 then

  if (keyframe.get_queue_len()==0) then 
	t0=t;
	demo_phase=demo_phase+1;
	Motion.event("sit");

  end

  elseif t-t0>demo_interval[demo_phase] then
	t0=t;
	demo_phase=demo_phase+1;
	if demo_phase>#demo_interval then
	end
	if demo_phase==1 then
	elseif demo_phase==2 then
		Motion.event("standup");
	elseif demo_phase==3 then
		Body.set_actuator_hardness(vector.ones(20)*0.5);
		keyframe.entry();
		keyframe.do_motion('wave');
	elseif demo_phase==4 then
		Motion.event("sit");
	elseif demo_phase==#demo_interval then
		demo_ended=true;
		return;
	end

  end

  if (keyframe.get_queue_len()>0) then keyframe.update(); end
  Body.update();
  Motion.update();
end

-- if using Webots simulator just run update
if (webots) then
  print('webots');
  BodyFSM.sm:add_event('button');
  while 1 do 
    update();
  end
end

while not demo_ended do
  update();
end
