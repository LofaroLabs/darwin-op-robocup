module(... or "",package.seeall)
cwd = os.getenv('PWD')
-- Get Platform for package path
package.cpath = cwd .. '/Lib/?.so;' .. package.cpath;

package.path = cwd .. '/Config/?.lua;' .. package.path;
package.path = cwd .. '/Util/?.lua;' .. package.path;
package.path = cwd .. '/Lib/?.lua;' .. package.path;
package.path = cwd .. '/Dev/?.lua;' .. package.path;
require('init')
require('gcm')


require('Speak') -- for speaking so we know what happened when button pressed
require('Body') -- for the button presses
require('os')

function update() 
	if ((Body.get_time() - tButton) > 0.25) then
		tButton = Body.get_time();
		
		if (Body.get_change_role() == 1) then
			scriptNumber = scriptNumber + 1;
			print("Incrementing leftbutton script")
			if     scriptNumber == 0 then 
				Speak.talk('Ready Aim Fire')
				-- this is full soccer
                        elseif scriptNumber == 1 then 
				Speak.talk('Vision Calibration Mode')
			elseif scriptNumber == 2 then Speak.talk('Demo Mode')
			elseif scriptNumber == 3 then 
				Speak.talk('Reset Internet Mode')
			elseif scriptNumber == 4 then 
				Speak.talk('Kitty Soccer Mode')
			elseif scriptNumber == 5 then 
				Speak.talk('Reset Camera')
			elseif scriptNumber == 6 then 
				Speak.talk('Reset Darwin')
			elseif scriptNumber == 7 then 
				Speak.talk('Set Initial')
			elseif scriptNumber == 8 then
				Speak.talk('Set Ready')
			elseif scriptNumber ==9 then
				Speak.talk('Set set');
			elseif(scriptNumber ==10) then 
				Speak.talk('set Play')
			elseif(scriptNumebr == 11) then 
				Speak.talk('set Finish')
			else   scriptNumber = 0; -- May be able to remove this and change last elseif to else
			end

			--if we want to just run main then press the role button
			--Speak.talk('Changed role is one')
			-- I need to be able to tell it to stop saying the button has been pressed
			--
			-- print("Executing leftbutton script")
			-- Speak.talk('Kiddie Soccer')
			--os.execute("sh leftbutton.sh " .. tostring(leftCount % 2))
			leftCount = leftCount + 1;
					
		end
		
		if (Body.get_change_state() == 1) then
			Speak.talk('Execute')
			print("Executing selected button script")
			-- os.execute("sh middlebutton.sh ".. tostring(middleCount % 2))
			middleCount = middleCount + 1;


			if     scriptNumber == 0 then
                                Speak.talk('Ready Aim Fire')
                                -- this is full soccer
                                os.execute("kill $(ps aux | grep '[.]/CalibrationServer' | awk '{print $2}')")
                                os.execute("sh runbasic.sh");
                                os.execute("sh scripts/fullsoccer.sh ");
                        elseif scriptNumber == 1 then
                                Speak.talk('Vision Calibration Mode')
                                -- then we call the vision calibration
                                os.execute("kill $(ps aux | grep '[.]/lua run_cognition.lua' | awk '{print $2}')")
                                os.execute("echo 111111 | sudo -S sh scripts/startCalibration.sh")
                        elseif scriptNumber == 2 then Speak.talk('Demo Mode')
                                os.execute("kill $(ps aux | grep '[.]/CalibrationServer' | awk '{print $2}')")
                                os.execute("sh runbasic.sh");
                                os.execute("sh scripts/demomode.sh");
                        elseif scriptNumber == 3 then
                                Speak.talk('Reset Internet Mode')
                                os.execute("echo 111111 | sudo -S sh scripts/resetInternet.sh")
                        elseif scriptNumber == 4 then
                                Speak.talk('Kitty Soccer Mode')
                                os.execute("sh scripts/kittyMode.sh");
                        elseif scriptNumber == 5 then
                                Speak.talk('Reset Camera')
                                os.execute("echo 111111 | sudo -S sh scripts/restartcam.sh")
                        elseif scriptNumber == 6 then
                                Speak.talk('Reset Darwin')
                                os.execute("kill $(ps aux | grep '[.]/CalibrationServer' | awk '{print $2}')")
                                os.execute("sh runbasic.sh")
                        elseif scriptNumber == 7 
				then gcm.set_game_state(0);
				Speak.talk("done");
                        elseif scriptNumber == 8 then
				gcm.set_game_state(1);
				Speak.talk("done");
			elseif scriptNumber == 9 then
				gcm.set_game_state(2);
				Speak.talk("done");
			elseif scriptNumber == 10 then
				gcm.set_game_state(3);
				Speak.talk("done");
			elseif scriptNumber == 11 then
				gcm.set_game_state(4);
				Speak.talk("done");
			else   
				

				scriptNumber = 0; -- May be able to remove this and change last elseif to else

			end


		end	
	end

end

tButton = 0;
middleCount = 0;
leftCount = 0;

scriptNumber = 0;	-- Default Mode

Speak.talk('George Mason Awesome Robo Patriots')

local tDelay = 0.005 * 1E6;


while 1 do
	update();
	unix.usleep(tDelay);
end

