#!/bin/bash
#sh runbasic.sh
export CLASSPATH=.:/home/darwin/horde/
sleep 4
screen -dm -L -s /usr/bin/bash -S dar java sim.app.horde.scenarios.robot.darwin.DarwinHorde soccer_localhost.arena
