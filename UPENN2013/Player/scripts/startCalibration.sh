killall -q CalibrationServer naoqi-bin naoqi hal espeak lua luajit luajit2 screen
sh restartcam.sh &
sleep 13
./CalibrationServer & 

sleep 
