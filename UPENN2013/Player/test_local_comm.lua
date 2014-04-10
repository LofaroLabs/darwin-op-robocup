module(... or "", package.seeall)

require('init')
require('wcm')
require('vcm')
--require('send')
require('Config')
while true do
	print("" .. tostring(wcm.get_team_attacker_pose()[1]));
       	print("" .. tostring(wcm.get_team_defender_pose()));        
        unix.usleep((.5)*(1E6));   
end

