#!/bin/bash

# demo mode just runs the main

killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen
cd $PLAYER # make sure you are in the right folder
screen -dm -L -s /usr/bin/bash -S dcm  lua ./run_dcm.lua
sleep 1
screen -dm -L -s /usr/bin/bash -S cog lua ./run_cognition.lua
sleep 3

screen -dm -L -s /usr/bin/bash -S main lua ./run_main.lua

 


