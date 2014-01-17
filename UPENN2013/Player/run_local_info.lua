module(... or "", package.seeall)

require('init')
require('wcm')
require('send')
require('Config')
local numParticles = 200; --let's only print 100 particles
local bot_id = Config.game.playerID;
local team_id = Config.game.teamNumber;
local id_concat = "," .. bot_id .. "," .. team_id .. "\n";
local particle_concat = "P#";
while true do
	local my_send_data = toCSV(wcm.get_pose()) .. id_concat;
	print(send_local_data(my_send_data))
        local particleX = wcm.get_particle_x();
	local particleY = wcm.get_particle_y();
	local particleA = wcm.get_particle_a();
	
        for i=1,numParticles do
		
		local particle_send = particle_concat .. i .. "," .. particleX[i] .. "," .. particleY[i] .. "," .. particleA[i] .. "\n";
		--print(particle_send);
                send_local_data(particle_send);
	end
        --local send_particle_data = 
        unix.usleep((.5)*(1E6));
   
end

