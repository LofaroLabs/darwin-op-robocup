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
require('HeadFSM')
require('getch')
require('HeadTransform')
require('Motion');
require('walk');

walk.active=false;
walk.enable=false;
getch.enableblock(1);

-- initialize state machines
Vision.entry();
World.entry();
HeadFSM.entry();
Motion.entry();

Body.set_head_hardness({0.4,0.4});
Body.set_syncread_enable(0);

-- main loop
count = 0;
local t0=Body.get_time();
local last_update_time=t0;
local headangle=vector.new({0,0});
local headsm_running=0;
local last_vision_update_time=t0;

function update()
  count = count + 1;
  local t= Body.get_time();
  World.update();
  Body.update();
  Motion.update();

  if t-last_vision_update_time>0.03 then
    Vision.update();
    if headsm_running==1 then 
      HeadFSM.update();
    elseif headsm_running==2 then
      ball = World.ball;
      headangle[1],headangle[2] = HeadTransform.ikineCam(ball.x, ball.y, 0, 0);
      Body.set_head_command(headangle);
      headsm_running=0; --move only one time to check head transform
    end
    last_vision_update_time=t;
  end

  local str=getch.get();
  if #str>0 then
    local byte=string.byte(str,1);
    if byte==string.byte("i") then		
      headsm_running=0;
      headangle[2]=headangle[2]-5*math.pi/180;
    elseif byte==string.byte("j") then	
      headangle[1]=headangle[1]+5*math.pi/180;
      headsm_running=0;
    elseif byte==string.byte("k") then	
      headangle[1],headangle[2]=0,0;
      headsm_running=0;
    elseif byte==string.byte("l") then	
      headangle[1]=headangle[1]-5*math.pi/180;
      headsm_running=0;
    elseif byte==string.byte(",") then	
      headangle[2]=headangle[2]+5*math.pi/180;
      headsm_running=0;
    elseif byte==string.byte("1") then	headsm_running=1;
    elseif byte==string.byte("2") then	headsm_running=2;
    elseif byte==string.byte("7") then	Motion.event("sit");
    elseif byte==string.byte("8") then	Motion.event("standup");
    end
    if headsm_running==0 then	
      local actual_head_angle={headangle[1],
			       headangle[2] - Config.head.cameraAngle[1][2] };
      Body.set_head_command(actual_head_angle);
      print(string.format("Head angle: %d, %d",
		 	  headangle[1]*180/math.pi,headangle[2]*180/math.pi));
    end
  end

  if t-last_update_time>2.0 then
    last_update_time=t;
    local ball=World.ball;
    if t-ball.t<0.1 then
      print("Ball detected");
      if Vision.ball.v then	
	print(string.format("Ball pos: %.2f %.2f %.2f"
	  	           ,unpack(Vision.ball.v)   ));
	print(string.format("Ball centroid:%d %d",
			    unpack(Vision.ball.propsA.centroid)));
      end
    else	
      print("Nothing detected");
    end
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

while 1 do
  update()
end
