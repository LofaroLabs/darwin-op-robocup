#!/bin/bash

until screen -S tog lua toggle_features.lua; do
	echo "hi"
done
echo "HI"


lua toggle_off.lua 
