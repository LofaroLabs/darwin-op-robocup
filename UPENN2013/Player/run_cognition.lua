module(... or "", package.seeall)
local dbg = require('debugger')
require('cognition')

maxFPS = Config.vision.maxFPS;
maxFPS = 18; -- was 18
tperiod = 1.0/maxFPS;
--print = function()end
function print() 
end
--setDebugFalse();
cognition.entry();
vcm.set_vision_enable(vector.ones(1));
number = 0;
while (true) do
  number = number +1;
  setDebugTrue(); 
  print("number is " .. number);
  setDebugFalse(); 
--print("!!@@ hi");
  
  tstart = unix.time();
  --dbg();
  cognition.update();

  tloop = unix.time() - tstart;

  if (tloop < tperiod) then
    unix.usleep((tperiod - tloop)*(1E6));
  end
end

cognition.exit();

