#!/bin/bash

# starts robot in kitty soccer mode

killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen
cd $PLAYER # make sure you are in the right folder
screen -dm -L -s /usr/bin/bash -S dcm  lua ./run_dcm.lua
sleep 1
screen -dm -L -s /usr/bin/bash -S cog lua ./run_cognition.lua
sleep 3
cd hfaExamples
screen -dm -L -s /usr/bin/bash -S kitty lua ./kittySoccerHFA.lua


