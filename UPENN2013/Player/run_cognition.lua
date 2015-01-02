
module(... or "", package.seeall)

require('profiler')
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
do_times = 100;
did_done = 0;
profiler.start("prof_iteration_"..did_done..".out")
while (did_done < do_times) do
  did_done = did_done + 1;
  deltatime = basetime - Body.get_time();
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
profiler.stop()

cognition.exit();

 
