#!/bin/bash

#if [ "1" = "1" ]
#then
#	touch hi.txt
#fi

if [ "$1" = "0" ]; then
	`echo "111111" | sudo -S ifdown wlan0`
	#touch m1.txt
fi


if [ "$1" = "0" ]; then
	
	`echo "111111" | sudo killall killall -q naoqi-bin naoqi hal espeak lua luajit luajit2 screen`
	screen -dm -s /usr/bin/bash -S cog lua ./run_dcm.lua
	sleep 1
	screen -dm -s /usr/bin/bash -S cog lua ./gmu_buttons.lua
	sleep 1
	screen -dm -s /usr/bin/bash -S cog lua ./run_cognition.lua
	sleep 3	
	screen -dm -s /usr/bin/bash -S cog lua ./hoard_connection.lua
#touch m2.txt
fi


if [ "$1" = "1" ]; then
        #`echo "111111" | sudo -S ifdown wlan0`
        #touch m1.txt
fi
