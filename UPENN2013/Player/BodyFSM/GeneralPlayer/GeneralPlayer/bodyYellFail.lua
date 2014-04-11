module(..., package.seeall);

require('Body')
require('Motion')

function entry()
   print(_NAME..' entry');
   wcm.set_horde_yelledFail(1);
   started = false;
end

function update()
	wcm.set_horde_yelledFail(1);
        wcm.set_horde_timeMark(Body.get_time()); 
end

function exit()
  wcm.set_horde_yelledFail(0);
  Motion.sm:add_event('walk');
end
