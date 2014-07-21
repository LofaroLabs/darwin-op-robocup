require('init')
require('gcm')
require('vcm')
require('wcm')
require('Body')
while 1 do
	print(tostring(gcm.get_game_state()) .. " game state is " )
    if gcm.get_team_color() == 1 then

            -- red attacks cyan goali
        print(" yellow ")
          --  postDefend = PoseFilter.postYellow;
    else
        print("not yellow")
            -- blue attack yellow goal
       --     postDefend = PoseFilter.postCyan;
    end
	print("Body time " .. Body.get_time()); 
	print("Dummy train = " .. tostring(wcm.get_horde_dummyTraining()))
	print("SendStatus DNW = " .. tostring(wcm.get_team_connected()));
	print("my x, y, a is : " .. wcm.get_pose().x .. ", " .. wcm.get_pose().y ..  ", " .. wcm.get_pose().a);
	print("ball x, y, a is : " .. wcm.get_ball().x .. ", " .. wcm.get_ball().y .. ", " .. tostring(wcm.get_ball().a)); 
--[[	print("using different call, ball x, y, a is " .. wcm.get_ball_x() .. ", " .. wcm.get_ball_y())
	print("Can See ball " .. vcm.get_ball_detect());
	print("am i closest to goal post " .. tostring(wcm.get_team_isClosestToGoalDefend()))
	print("player ID " .. tostring(Config.game.playerID))
	print("team yelled kick " .. wcm.get_team_yelledKick()[3]);
--]]
	print("goalie close? " .. tostring(wcm.get_horde_goalieCloseEnough()))
end
