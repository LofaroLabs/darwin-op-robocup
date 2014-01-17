cwd = os.getenv('PWD')
require('init')

require('unix')
require('Config')
require('shm')
require('vector')
require('mcm')
require('Speak')
require('getch')
require('Body')
require('Motion')
require('dive')
require ('UltraSound')
require('grip')
Motion.entry();
darwin = false;
webots = false;

-- Enable OP specific 
if(Config.platform.name == 'OP') then
  darwin = true;
  --SJ: OP specific initialization posing (to prevent twisting)
--  Body.set_body_hardness(0.3);
--  Body.set_actuator_command(Config.stance.initangle)
end

--TODO: enable new nao specific
newnao = false; --Turn this on for new naos (run main code outside naoqi)
newnao = true;

getch.enableblock(1);
unix.usleep(1E6*1.0);
Body.set_body_hardness(0);

--This is robot specific 
webots = false;
init = false;
calibrating = false;
ready = false;
if( webots or darwin) then
  ready = true;
end

initToggle = true;
targetvel = vector.zeros(3);
button_pressed = {0,0};

function move_head_yaw(angle) -- in degree
  local headAngles = Body.get_head_position()
  local currentYaw = headAngles[1] * 180.0/3.141592
  local currentPitch = headAngles[2] * 180.0/3.141592
  local newYaw = currentYaw + angle

  print("currentYaw", currentYaw)
  print("currentPitch", currentPitch)
  print("newYaw", newYaw)
  if newYaw > -50.0 and newYaw < 22.0 then
    Body.set_head_command({newYaw/180.0*3.141592, headAngles[2]})
  end
end 

function move_head_pitch(angleP)
  local headAngles = Body.get_head_position()
  local currentPitch = headAngles[2] * 180.0 / 3.141592
  local newPitch = currentPitch + angleP
  if newPitch > -90 and newPitch < 90 then
    Body.set_head_command({headAngles[1], newPitch/180.0*3.141592})
  end
end

function process_keyinput()
  line = mcm.get_walk_wii_message()
--  print(line)

      -- set walk vector (x,y,z) x-forward, y-left, z-counter-clockwise	
  if line == "UP RELEASED" then        targetvel[1]=targetvel[1]+0.02;
  elseif line == "RIGHT RELEASED" then targetvel[2]=targetvel[2]-0.02;
  elseif line == "DOWN RELEASED" then  targetvel[1]=targetvel[1]-0.02;
  elseif line == "LEFT RELEASED" then  targetvel[2]=targetvel[2]+0.02;
  elseif line == "HOME RELEASED" then  targetvel[1],targetvel[2],targetvel[3]=0,0,0;
  elseif line == "MINUS RELEASED" then targetvel[3]=targetvel[3]+0.1;
  elseif line == "PLUS RELEASED" then  targetvel[3]=targetvel[3]-0.1;
      
      -- walk
  elseif line == "A RELEASED" then 
  print(walk)
  Motion.event("walk");
  walk.start();

  -- Stand
  elseif line == "B RELEASED" then 
    if walk.active then walk.stop();end
      Motion.event("standup");

  -- Kick forward right
  elseif line == "1 RELEASED" then 
    kick.set_kick("kickForwardLeft");
    Motion.event("kick");

 -- Kick forward left
  elseif line == "2 RELEASED" then 
    kick.set_kick("kickForwardRight");
    Motion.event("kick");

 -- Move head up
  elseif line == "B + UP" then 
    move_head_pitch(-2)

 -- Move head down
  elseif line == "B + DOWN" then
    move_head_pitch(2)

 -- Move head right
  elseif line == "B + RIGHT" then
    print("MOVE HEAD RIGHT .................... ")
    move_head_yaw(-2)

 -- Move head left
  elseif line == "B + LEFT" then
    move_head_yaw(2)
  end

Motion.event("standup")
walk.set_velocity(unpack(targetvel));
--print("Command velocity:",unpack(walk.velCommand))
mcm.set_walk_wii_message("none")
end

-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();

function update()
  count = count + 1;
  if (not init)  then
    if (calibrating) then
      if (Body.calibrate(count)) then
        Speak.talk('Calibration done');
        calibrating = false;
        ready = true;
      end
    elseif (ready) then
      init = true;
    else
      if (count % 20 == 0) then
-- start calibrating w/o waiting
--        if (Body.get_change_state() == 1) then
          Speak.talk('Calibrating');
          calibrating = true;
--        end
      end
      -- toggle state indicator
      if (count % 100 == 0) then
        initToggle = not initToggle;
        if (initToggle) then
          Body.set_indicator_state({1,1,1}); 
        else
          Body.set_indicator_state({0,0,0});
        end
      end
    end
  else
    -- update state machines 
    process_keyinput();
    Motion.update();

    -- testing script for UltraSound
    Left, Right = UltraSound.check_obstacle()
    if Left and (not Right) then
      Body.set_actuator_ledChest({0,0,1})
      Body.set_actuator_ledFaceLeft(vector.ones(8), 1)
    elseif Right and (not Left) then
      Body.set_actuator_ledChest({0,1,0})
      Body.set_actuator_ledFaceRight(vector.ones(8), 1)
    elseif Left and Right then
      Body.set_actuator_ledChest({1,0,0})
      Body.set_actuator_ledFaceLeft(vector.ones(8), 1)
      Body.set_actuator_ledFaceRight(vector.ones(8), 1)
    else      
      Body.set_actuator_ledChest({0,0,0})
      Body.set_actuator_ledFaceLeft(vector.zeros(8), 1)
      Body.set_actuator_ledFaceRight(vector.zeros(8), 1)
    end
    Body.update();
  end
  local dcount = 50;
  if (count % 50 == 0) then
--    print('fps: '..(50 / (unix.time() - tUpdate)));
    tUpdate = unix.time();
    -- update battery indicator
    Body.set_indicator_batteryLevel(Body.get_battery_level());
  end
  
  -- check if the last update completed without errors
  lcount = lcount + 1;
  if (count ~= lcount) then
    print('count: '..count)
    print('lcount: '..lcount)
    Speak.talk('missed cycle');
    lcount = count;
  end

  --Stop walking if button is pressed and the released
  if (Body.get_change_state() == 1) then
    button_pressed[1]=1;
  else
    if button_pressed[1]==1 then
      Motion.event("sit");
    end
    button_pressed[1]=0;
  end
end

-- if using Webots simulator just run update
if (webots) then
  while (true) do
    -- update motion process
    update();
    io.stdout:flush();
  end
end

--Now both nao and darwin runs this separately
if (darwin) or (newnao) then
  local tDelay = 0.005 * 1E6; -- Loop every 5ms
  while 1 do
    update();
    unix.usleep(tDelay);
  end
end
