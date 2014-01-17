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
  local newYaw = currentYaw + angle

  print("currentYaw", currentYaw)
  print("newYaw", newYaw)
  
  if newYaw <= -85.0 then newYaw = -85.0
  elseif newYaw >= 85.0 then newYaw = 85.0 end

  Body.set_head_command({newYaw/180.0*3.141592, headAngles[2]})
end 

function move_head_pitch(angleP)
  local headAngles = Body.get_head_position()
  local currentPitch = headAngles[2] * 180.0 / 3.141592
  local newPitch = currentPitch + angleP

  print("currentPitch", currentPitch)
  print("newPitch", newPitch)

  if newPitch <= -45.0 then newPitch = -45.0
  elseif newPitch >= 22.0 then newPitch = 22.0 end

  Body.set_head_command({headAngles[1], newPitch/180.0*3.141592})
end

function move_head(a1, a2)  --a1: delta yaw, a2: delta pitch
  local headAngles = Body.get_head_position()
  local currentYaw = headAngles[1] * 180.0/3.141592
  local newYaw = currentYaw + a1
  local currentPitch = headAngles[2] * 180.0/3.141592
  local newPitch = currentPitch + a2

  if newYaw <= -85.0 then newYaw = -85.0
  elseif newYaw >= 85.0 then newYaw = 85.0 end
 
  if newPitch <= -45.0 then newPitch = -45.0
  elseif newPitch >= 22.0 then newPitch = 22.0 end

  newPitch = newPitch/180.0*3.141592
  newYaw = newYaw/180.0*3.141592

  Body.set_head_command({newYaw, newPitch})
end

function process_keyinput()
  line = mcm.get_walk_wii_message()
--  print(line)

  -- set walk vector by using accel from the Wiimote
  if line == "WIIMOTE NONE" then 
    targetvel[1] = 0.0;
    targetvel[2] = 0.0;
    targetvel[3] = 0.0;
    if walk.active then walk.stop();end
    Motion.event("standup")
  
  elseif string.match(line, 'WIIMOTE.*') then
    local count = 0
    print("walk")
    Motion.event("walk");
    walk.start();

    for i in string.gmatch(line, "%S+") do
      if i ~= "WIIMOTE" then
        count = count + 1
        if count == 1 then
          targetvel[3] = i * 4.0 / 10.0 * (-1.0)
        elseif count == 2 then
          if (i+0.0) < 0 then
            targetvel[1] = i * 3.0/100.0
          else
            targetvel[1] = i / 10.0
          end
        end
      end
    end
  end

  -- nunchuk accel (control the side velocity)
  if line == "NUNCHUK NONE" then
    targetvel[2] = 0.0
  elseif string.match(line, 'NUNCHUK.*') then 
    for i in string.gmatch(line, "%S+") do
      if i ~= "NUNCHUK" then
        targetvel[2] = i * 3.0/100.0*(-1.0)
      end
    end
  end

  -- kick , cannot do both walk and kick
  if line == "PLUS PRESSED" or line == "B + PLUS" then
    kick.set_kick("kickForwardRight");
    Motion.event("kick");
  elseif line == "MINUS PRESSED" or line == "B + MINUS" then
    kick.set_kick("kickForwardLeft");
    Motion.event("kick");
  end

  -- control the head
  if line == "MOVE HEAD UP" then
    move_head_pitch(-10)
  elseif line == "MOVE HEAD DOWN" then
    move_head_pitch(10)
  elseif line == "MOVE HEAD LEFT" then
    move_head_yaw(10)
  elseif line == "MOVE HEAD RIGHT" then 
    move_head_yaw(-10)
  elseif line == "MOVE HEAD UP AND LEFT" then 
    move_head(10,-10)
  elseif line == "MOVE HEAD UP AND RIGHT" then
    move_head(-10,-10)
  elseif line == "MOVE HEAD DOWN AND LEFT" then
    move_head(10,10)
  elseif line == "MOVE HEAD DOWN AND RIGHT" then
    move_head(-10,10)
  end  

--[[
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
    move_head_pitch(-10)

 -- Move head down
  elseif line == "B + DOWN" then
    move_head_pitch(10)

 -- Move head right
  elseif line == "B + RIGHT" then
    move_head_yaw(-10)

 -- Move head left
  elseif line == "B + LEFT" then
    move_head_yaw(10)
]]--  end

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
