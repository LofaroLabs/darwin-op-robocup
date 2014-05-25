#!/bin/bash

#if [ "1" = "1" ]
#then
#	touch hi.txt
#fi

if [ "$1" = "1" ]; then
	#`echo "111111" | sudo -S ifdown wlan0`
	touch m1.txt
fi


if [ "$1" = "2" ]; then
	#`echo "111111" | sudo -S ifup wlan0`
	touch m2.txt
fi



