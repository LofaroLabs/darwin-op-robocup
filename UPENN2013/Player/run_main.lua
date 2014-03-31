module(... or "", package.seeall)
package.cpath = './Lib/?.so;' .. package.cpath  
require('unix');
require('main');

--Config.fsm.playMode
print("hellow world");
while 1 do 
  print("hi" .. Config.fsm.playMode);
  tDelay = 0.005*1E6;
  main.update();
  unix.usleep(tDelay);
end

