require('init')
require('gcm')
require('vcm')
require('wcm')
require('Body')
require('unix')
require('os')

--relax the servos
Body.relaxServos();


-- Write log
file = io.open("Camera.txt", "a")
file:write("Camera died servos relaxed and killing cognition time = " .. tostring(os.date()));
file:close()

os.execute("kill $(ps aux | grep '[l]ua run_cognition.lua' | awk '{print $2}')")

