#!/bin/bash
#sh runbasic.sh
sleep 4
screen -dm -L -s /usr/bin/bash -S dar java sim.app.horde.scenarios.robot.darwin.DarwinHorde soccer_localhost.arena
