---------------------------------
-- test_walk.lua
---------------------------------

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

require('Config');
require('Body')
require('vector')
require('Motion')
require('walk')
require("getch")
require('kick')

Motion.entry();
Motion.event("standup");

getch.enableblock(1);

print(" Key commands \n 7:sit down 8:stand up\n i/j/l/,/h/; :control walk velocity\n k : walk in place\n 1/2 :kick");
t0=Body.get_time();
count=0;


-- main loop
function update()
  Motion.update();
  local t=Body.get_time();
  if t>t0 then
     count=count+1;
     print("Count:" , count)
     if count==1 then 
	walk.set_velocity(0.06,0,0);
     elseif count==2 then 
	walk.set_velocity(0,0.04,0);
     elseif count==3 then 
	walk.set_velocity(-0,0,0.3);
     elseif count==4 then 
	walk.set_velocity(-0.04,-0,0);
     elseif count==5 then 
        Motion.event("kick");
     elseif count==6 then 
	count=0;
	walk.set_velocity(0.0,0,0);
     end
     t0=t+5.0;
  end

--[[
  local str=getch.get();
  if #str>0 then
    local byte=string.byte(str,1);
    if byte==string.byte("i") then      targetvel[1]=targetvel[1]+0.01;
    elseif byte==string.byte("j") then	targetvel[3]=targetvel[3]+0.1;
    elseif byte==string.byte("k") then	targetvel[1],targetvel[2],targetvel[3]=0,0,0;
    elseif byte==string.byte("l") then	targetvel[3]=targetvel[3]-0.1;
    elseif byte==string.byte(",") then	targetvel[1]=targetvel[1]-0.01;
    elseif byte==string.byte("h") then	targetvel[2]=targetvel[2]+0.01;
    elseif byte==string.byte(";") then	targetvel[2]=targetvel[2]-0.01;
    elseif byte==string.byte("1") then	
      kick.set_kick("kickForwardLeft");Motion.event("kick");
    elseif byte==string.byte("2") then	
      kick.set_kick("kickForwardRight");Motion.event("kick");
    elseif byte==string.byte("7") then	Motion.event("sit");
    elseif byte==string.byte("8") then	walk.stop(); Motion.event("standup");
    elseif byte==string.byte("9") then	Motion.event("walk");walk.start();
    end
    print(string.format("\n Walk Velocity: (%.2f, %.2f, %.2f)\n",unpack(targetvel)));
    walk.set_velocity(unpack(targetvel));
  end
  walk.set_velocity(0.06,0,0);
--]]

end

while 1 do
 update()
 Body.update();
end

