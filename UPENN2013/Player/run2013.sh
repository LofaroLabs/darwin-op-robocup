killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen

screen -dm -L -s /usr/bin/bash -S dcm  lua /home/darwin/dev/merc/UPENN2013/Player/run_dcm.lua
sleep 1
screen -dm -L -s /usr/bin/bash -S cog lua /home/darwin/dev/merc/UPENN2013/Player/run_cognition.lua
sleep 3
#screen -dm -L -s /usr/bin/bash -S motion lua /home/darwin/dev/merc/UPENN2013/Player/hoard_motion.lua
#sleep 14
screen -dm -L -s /usr/bin/bash -S mon lua /home/darwin/dev/merc/UPENN2013/Player/run_monitor.lua
sleep 12
screen -dm -L -s /usr/bin/bash -S connection lua /home/darwin/dev/merc/UPENN2013/Player/hoard_connection.lua
sleep 1
screen -dm -L -s /usr/bin/bash -S features lua /home/darwin/dev/merc/UPENN2013/Player/hoard_features.lua
sleep 1
echo `screen -ls`


