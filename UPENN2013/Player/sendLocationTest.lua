cwd = os.getenv('PWD')
require('init')

require('wcm')
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
targetvel=vector.zeros(3);
button_pressed = {0,0};

ballDistToggle = 0;
 insist = false;
insistBall = false;
function process_keyinput()
  local str=getch.get();
  if #str>0 then
    local byte=string.byte(str,1);
    -- Walk velocity setting
    if byte==string.byte("r") then
	if(wcm.get_horde_ready()==0) then
	  wcm.set_horde_ready(1);      
        else
	  wcm.set_horde_ready(0);
	end 
    end
    if byte==string.byte("p") then
        if (wcm.get_horde_passKick()==0) then
          wcm.set_horde_passKick(1);
        else
          wcm.set_horde_passKick(0);
        end
    end
   if byte==string.byte("f") then
        if (wcm.get_horde_doneFrontApproach()==0) then
          wcm.set_horde_doneFrontApproach(1);
          insist = true;
	else
 	  insist = false;
          wcm.set_horde_doneFrontApproach(0);
        end
    end
   if byte==string.byte("d") then
        if (vcm.get_ball_detect()==0) then
          vcm.set_ball_detect(1);
          insistBall = true;
	else
	  insistBall = false;
	  vcm.get_ball_detect(0);         
        end
    end
    if(byte==string.byte("b")) then
	ballDistToggle = (ballDistToggle +1) % 4;
    end

  end
end

-- main loop
count = 0;
lcount = 0;
tUpdate = unix.time();

function update()
   process_keyinput();
end
  while (true) do
    -- update motion process
    update();
   if(ballDistToggle == 0) then
	ballDist = .582259821
   elseif(ballDistToggle==1) then
   	ballDist = .2711234985
   elseif(ballDistToggle==2) then
	ballDist = .15;
   else
        ballDist = 0.0;
   end
   if(ballDistToggle~=3) then
   	wcm.set_ball_x(ballDist);
   end
   wcm.set_ball_y(0);
   if (insist) then
   	wcm.set_horde_doneFrontApproach(1);
   end
   if(insistBall) then
	vcm.set_ball_detect(1);
   end
   io.stdout:flush();
	print("detect ball: " .. vcm.get_ball_detect() ..  " ball dist:" .. wcm.get_ball_x().. " frontApproach: " .. tostring(wcm.get_horde_doneFrontApproach()) .. " ready: " .. tostring(wcm.get_horde_ready()) .. " passkick: " .. tostring(wcm.get_horde_passKick()));
 end
