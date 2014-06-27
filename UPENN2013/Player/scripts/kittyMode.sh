#!/bin/bash

# starts robot in kitty soccer mode

cd $PLAYER # make sure you are in the right folder
screen -dm -L -s /usr/bin/bash -S kitty lua ./kittySoccerHFA.lua


