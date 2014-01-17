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
  package.cpath = cwd .. '/Lib/?.dylib;' .. package.cpath;
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

Motion.entry();

darwin = false;
webots = false;

-- Enable OP specific 
if(Config.platform.name == 'OP') then
  darwin = true;
  --SJ: OP specific initialization posing (to prevent twisting)
  Body.set_body_hardness(0.3);
  Body.set_actuator_command(Config.stance.initangle)
  unix.usleep(1E6*0.5);
  Body.set_body_hardness(0);
  Body.set_lleg_hardness({0.2,0.6,0,0,0,0});
  Body.set_rleg_hardness({0.2,0.6,0,0,0,0});
end 


-- Enable Webots specific
if (string.find(Config.platform.name,'Webots')) then
  webots = true;
end

init = false;
calibrating = false;
ready = false;
if( webots or darwin) then
  ready = true;
end

smindex = 0;
initToggle = true;

--SJ: Now we use a SINGLE state machine for goalie and attacker
package.path = cwd..'/BodyFSM/'..Config.fsm.body[smindex+1]..'/?.lua;'..package.path;
package.path = cwd..'/HeadFSM/'..Config.fsm.head[smindex+1]..'/?.lua;'..package.path;
require('BodyFSM')
require('HeadFSM')
require('behavior')


BodyFSM.entry();
HeadFSM.entry();

-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();

--Start with PAUSED state
gcm.set_game_paused(1);
role = 1; --Attacker
waiting = 1;

button_role,button_state = 0,0;
tButtonState = 0;
forced_role = 1;
gcm.set_team_forced_role(forced_role);



function update()
  count = count + 1;
  t = Body.get_time();
  --Update battery info
  wcm.set_robot_battery_level(Body.get_battery_level());
  vcm.set_camera_teambroadcast(1); --Turn on wireless team broadcast

  --Check center button press for playing/waiting change

  if (Body.get_change_state() == 1) then
    button_state=1;
    if (t-tButtonState>1.0) then --Button pressed for 1 sec
      waiting = 1-waiting;
      if waiting==0 then
        Speak.talk('Playing');
        BodyFSM.sm:set_state('bodySearch');   
        HeadFSM.sm:set_state('headScan');
        Motion.event("standup");
      else
	batlevel = string.format("Battery Level %.1f",
		Body.get_battery_level());
	Speak.talk(batlevel)
        Motion.event("sit");
      end
      tButtonState = t;
    end
  else
    button_state= 0;
    tButtonState = t;
  end

  --Check role button press
  if (Body.get_change_role() == 1) then
    button_role=1;
  else
    if button_role==1 then --Button released
      --forced_role = gcm.get_team_forced_role();
      --Cycle roles
      forced_role = forced_role + 1;
      if forced_role>3 then forced_role = 0;end 
      --gcm.set_team_forced_role(forced_role);
      button_role=0;
      gcm.set_team_role(forced_role)

      print("NEW ROLE NUMBER ")
      print(forced_role)
    end
  end

  if waiting>0 then --Waiting mode, indicate role
    gcm.set_game_paused(1);
    if forced_role==1 then
      Body.set_indicator_ball({1,0,0});--Attacker
    elseif forced_role==2 then --Defender
      Body.set_indicator_ball({1,1,0});
    else --Goalie
      Body.set_indicator_ball({0,0,1});--Goalie
    end
    Motion.update();
    Body.update();
  else --Playing mode, update state machines  
    gcm.set_game_paused(0);
    --print("role is:")
    --print(gcm.get_team_role())
    --gcm.set_team_role(forced_role)
    BodyFSM.update();
    HeadFSM.update();
    Motion.update();
    Body.update();
  end

  local dcount = 50;
  if (count % 50 == 0) then
--    print('fps: '..(50 / (unix.time() - tUpdate)));
    tUpdate = unix.time();
    -- update battery indicator
    Body.set_indicator_batteryLevel(Body.get_battery_level());
  end
  
end

-- if using Webots simulator just run update
if (webots) then
  print("##@@@## you should only see this if you're using the webots simulator")
  require('cognition');
  cognition.entry();

  -- set game state to Playing
  gcm.set_game_state(3);

  while (true) do
    -- update cognitive process
    cognition.update();
    -- update motion process
    update();

    io.stdout:flush();
  end

end

if( darwin ) then
  local tDelay = 0.005 * 1E6; -- Loop every 5ms
  while 1 do
    update();
    unix.usleep(tDelay);
  end
end
