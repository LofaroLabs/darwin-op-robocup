require('init')
require('gcm')
require('vcm')
require('wcm')
while 1 do

    if gcm.get_team_color() == 1 then

            -- red attacks cyan goali
        print(" yellow ")
          --  postDefend = PoseFilter.postYellow;
    else
        print("not yellow")
            -- blue attack yellow goal
       --     postDefend = PoseFilter.postCyan;
    end

	print("my x, y, a is : " .. wcm.get_pose().x .. ", " .. wcm.get_pose().y ..  ", " .. wcm.get_pose().a);

	print("am i closest to goal post " .. tostring(wcm.get_team_isClosestToGoalDefend()))
end
