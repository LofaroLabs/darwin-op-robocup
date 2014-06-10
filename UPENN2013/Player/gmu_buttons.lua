module(... or '', package.seeall)

-- Get Platform for package path
cwd = '.';
package.cpath = cwd .. '/Lib/?.so;' .. package.cpath;

package.path = cwd .. '/Config/?.lua;' .. package.path;
package.path = cwd .. '/Util/?.lua;' .. package.path;
package.path = cwd .. '/Lib/?.lua;' .. package.path;
package.path = cwd .. '/Dev/?.lua;' .. package.path;
require('Speak') -- for speaking so we know what happened when button pressed
require('Body') -- for the button presses
require('os')

function update() 
	if ((Body.get_time() - tButton) > 0.25) then
		tButton = Body.get_time();
		if (Body.get_change_role() == 1) then
			scriptNumber = scriptNumber + 1;
			print("Incrementing leftbutton script")
			if     scriptNumber == 0 then Speak.talk('Ready Aim Fire')
                        elseif scriptNumber == 1 then Speak.talk('Vision Calibration Mode')
			elseif scriptNumber == 2 then Speak.talk('Demo Mode')
			elseif scriptNumber == 3 then Speak.talk('Reset Internet Mode')
			elseif scriptNumber == 4 then Speak.talk('Kitty Soccer Mode')
			elseif scriptNumber == 5 then Speak.talk('Image Calibration')
			elseif scriptNumber == 6 then Speak.talk('Reset Darwin')
			elseif scriptNumber == 7 then Speak.talk('Shall We Play A Game')
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

