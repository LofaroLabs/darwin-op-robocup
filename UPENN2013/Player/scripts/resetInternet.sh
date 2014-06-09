#!/bin/bash
#MUST run with root permisions
# if down and up wlan0 and eth0?  anything else?
ifdown wlan0
ifup wlan0

ifdown eth0
ifup eth0


