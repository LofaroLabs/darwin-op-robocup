#!/bin/bash
sleep 1
ifdown wlan0
sleep 1
ifup eth0
sleep 1
ifdown eth0
sleep 2
ifup eth0
