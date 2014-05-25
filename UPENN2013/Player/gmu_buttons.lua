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
			--if we want to just run main then press the role button
			--Speak.talk('Changed role is one')
			-- I need to be able to tell it to stop saying the button has been pressed
			print("Executing leftbutton script")
			os.execute("sh leftbutton.sh")	
				
		end
		
		if (Body.get_change_state() == 1) then
		--	Speak.talk('Changed state')
			print("Executing middle button script")
			os.execute("sh middlebutton.sh")
		end	
	end

end

tButton = 0;

local tDelay = 0.005 * 1E6;
while 1 do
	update();
	unix.usleep(tDelay);
end

