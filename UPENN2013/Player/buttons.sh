#!/bin/bash
GODIR=/home/darwin/dev/merc/darwin/UPENN2013/Player
cd $GODIR
#screen -dm -L -s /usr/bin/bash -S dcm  
lua ./run_dcm.lua &
sleep 5
#screen -dm -L -s /usr/bin/bash -S cog 
lua ./run_cognition.lua &
sleep 5

lua ./gmu_buttons.lua &

touch ./test.txt
