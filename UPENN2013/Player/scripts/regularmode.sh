#!/bin/bash

# This is the regular mode and it will start up the processes that start
# dcm, cognition, horde connection, and horde itself.

killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen

screen -dm -L -s /usr/bin/bash -S dcm  lua ./run_dcm.lua
sleep 1
screen -dm -L -s /usr/bin/bash -S cog lua ./run_cognition.lua
sleep 3
screen -dm -L -s /usr/bin/bash -S horde lua ./hoard_connection.lua

sleep 3

# now start horde




