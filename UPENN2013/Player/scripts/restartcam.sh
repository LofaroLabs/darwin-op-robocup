#!/bin/bash
#probably need to run as sudo
echo '1-2' | sudo tee /sys/bus/usb/drivers/usb/unbind
sleep 6
echo '1-2' | sudo tee /sys/bus/usb/drivers/usb/bind
sleep 6
./scripts/cheatvision
uvcdynctrl -d /dev/video0 -L ./scripts/c920.gpfl
./scripts/cheatvision

#uvcdynctrl -s "Exposure (Absolute)" 200
