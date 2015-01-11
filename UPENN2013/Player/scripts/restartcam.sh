#!/bin/bash
#probably need to run as sudo
echo '1-1' | sudo tee /sys/bus/usb/drivers/usb/unbind
sleep 6
echo '1-1' | sudo tee /sys/bus/usb/drivers/usb/bind
sleep 6
sudo ln -s /dev/video1 /dev/video0
sleep 1
uvcdynctrl -d /dev/video0 -L ./scripts/c920.gpfl
uvcdynctrl -d /dev/video1 -L ./scripts/c920.gpfl
./scripts/cheatvision
#uvcdynctrl -s "Exposure (Absolute)" 200
