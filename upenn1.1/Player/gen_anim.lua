module(... or "", package.seeall)

require('unix')
webots = false;
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

require('vector')
require('Body')

function clean_jointangle(j0)
    local j1=vector.new(j0);
    j1[1]=0;j1[2]=0; --Head angles
    j1[3],j1[4],j1[5]=j1[18],-j1[19],j1[20];
    j1[6],j1[7],j1[8]=-j1[12],-j1[13],j1[14];
    j1[9],j1[10],j1[11]=j1[15],j1[16],-j1[17];
    return j1;
end
Body.set_syncread_enable(1);
loop=true;
jointangles={};
local count=1;
while loop do
print("Press Enter key to record keyframe, g to exit");
keyin=io.stdin:read()
local qSensor=Body.get_sensor_position();

--table.insert(jointangles,clean_jointangle(qSensor));
table.insert(jointangles,qSensor);
if keyin=='g' then loop=false;end
print("Frame ",count," recorded");
count=count+1;
end

print("Enter filename")
keyin=io.stdin:read();
local f=assert(io.open(keyin,'w'))
f:write("local mot={};\n");
f:write("mot.servos={\n");
for i=1,20 do
	f:write(i,",");
end
f:write("};\nmot.keyframes={");
for i=1,#jointangles do
	f:write("  {\n    angles={\n");
	for j=1,20 do
		f:write(jointangles[i][j],",");
	end
	f:write("\n    },\n");
	f:write("duration = 1.0; \n  },\n");
end
f:write("};\n\nreturn mot;")
f:close();

