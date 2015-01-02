
module(... or "", package.seeall)

require('cognition')
require('Body')
require('wcm')
maxFPS = Config.vision.maxFPS;
tperiod = 1.0/maxFPS;
--print = function()end
setDebugTrue();
cognition.entry();
vcm.set_vision_enable(vector.ones(1));
local basetime = Body.get_time();
local deltatime;
while (true) do
  deltatime = Body.get_time() - basetime;
  setDebugTrue();
  print("num times called ".. wcm.get_horde_numTimesCalled());
	wcm.set_horde_numTimesCalled(0);
	print("time: " .. deltatime);
  setDebugFalse();
  basetime = Body.get_time();

  tstart = unix.time();

  cognition.update();

  tloop = unix.time() - tstart;

  if (tloop < tperiod) then
    unix.usleep((tperiod - tloop)*(1E6));
  end
end

cognition.exit();

 
