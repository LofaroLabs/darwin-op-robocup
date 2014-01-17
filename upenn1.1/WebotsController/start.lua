require('controller');

print("\nStarting Webots Lua controller...");

--[[
playerID = os.getenv('PLAYER_ID') + 0;
teamID = os.getenv('TEAM_ID') + 0;
--]]
playerId = 1;
teamID = 18;

dofile("Player/player.lua");
--dofile("Player/test_walk.lua");
--dofile("Player/test_webots.lua");


