#!/bin/bash

killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen

screen -dm -L -s /usr/bin/bash -S dcm  lua ./run_dcm.lua
sleep 1

screen -dm -L -s /usr/bin/bash -S buttons lua ./gmu_buttons.lua


