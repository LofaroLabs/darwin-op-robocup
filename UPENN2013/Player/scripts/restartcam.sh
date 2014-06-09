#!/bin/bash
#probably need to run as sudo
echo '1-8' | sudo tee /sys/bus/usb/drivers/usb/unbind
echo '1-8' | sudo tee /sys/bus/usb/drivers/usb/bind
uvcdynctrl -L ../default.gpfl

