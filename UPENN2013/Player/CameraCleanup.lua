require('init')
require('gcm')
require('vcm')
require('wcm')
require('Body')
require('unix')
require('os')
require('Motion')
require('OPCommManager')

--first kill everything that could stop me from sitting
os.execute("kill $(ps aux | grep '[l]ua run_cognition.lua' | awk '{print $2}')")
os.execute("kill $(ps aux | grep '[l]ua hoard_connection.lua' | awk '{print $2}')")

-- Write log
file = io.open("CameraCleanup.txt", "a")
file:write("Camera died so standing still and killing cognition time = " .. tostring(os.date()).. "\n");
file:close()

