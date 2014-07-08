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
require('unix')
require('init')
require('gcm')
setDebugFalse();
scriptNumber = 0;
function mainMenuUpdate() 
			print("Incrementing leftbutton script")
			if   scriptNumber == 1 then 
				Speak.talk('Soccer Mode Menu')--kitty vs horde
			-- this is full soccer
                        -- elseif scriptNumber == 1 then 
			--	Speak.talk('L.U.T. Calibration Server')
			--elseif scriptNumber == 2 then 
			--	Speak.talk('Reset Vision');
			elseif scriptNumber== 2 then	
				Speak.talk('Vision Menu');	
			elseif scriptNumber == 3 then 
				Speak.talk('Reset Internet Mode')
			elseif scriptNumber == 4 then 
				Speak.talk('Reset Darwin')
				--elseif scriptNumber == 5 then Speak.talk('Nothing')
			elseif(scriptNumber == 5) then
				Speak.talk("I.D. menu")
			else   scriptNumber = 0; -- May be able to remove this and change last elseif to else
			end

			--if we want to just run main then press the role button
			--Speak.talk('Changed role is one')
			-- I need to be able to tell it to stop saying the button has been pressed
			-- print("Executing leftbutton script")
			-- Speak.talk('Kiddie Soccer')
			--os.execute("sh leftbutton.sh " .. tostring(leftCount % 2))
			leftCount = leftCount + 1;
end
function mainMenuExecute()
			--Speak.talk('Execute')
			print("Executing selected button script")
			-- os.execute("sh middlebutton.sh ".. tostring(middleCount % 2))
			middleCount = middleCount + 1;

			if scriptNumber == 1 then
				Speak.talk('Soccer menu Start')
				MenuID = "soccer menu"   
				scriptNumber = 0;                            
            elseif scriptNumber == 2 then
				Speak.talk('Vision menu start')
				-- then we call the vision calibration
				MenuID = "vision menu"    
				scriptNumber = 0;                           
			elseif scriptNumber == 3 then
				Speak.talk('Reset Internet Mode')
				os.execute("echo 111111 | sudo -S sh scripts/resetInternet.sh")
			elseif scriptNumber == 4 then
				Speak.talk('Reset Darwin')
				os.execute("sleep 1");				
				os.execute("echo 111111 | sudo -S reboot")
			elseif scriptNumber == 5 then
				Speak.talk("I.D. menu start")
				MenuID = "id menu"
				--file = io.open("kickOutput.txt", "w")
				--file:write("KICK START\n");
			elseif scriptNumber == 7 then Speak.talk('Nothing')
                        else   scriptNumber = 0; -- May be able to remove this and change last elseif to else

			end

end
function idMenuUpdate()
	if(scriptNumber < 6) then 
		Speak.talk(tostring(scriptNumber-1))
	elseif scriptNumber == 6 then
		Speak.talk("main menu");
	else
		scriptNumber = 0;
	end
end
function idMenuExecute()
	file = io.open("/home/darwin/dev/merc/darwin/UPENN2013/Player/Config/playerNum.lua", "w")
	file:write("playerID = ".. tostring((scriptNumber-1)) .. "\n");
	file:close();
	os.execute("/home/darwin/dev/merc/darwin/UPENN2013/Player/buttons.sh");
end
function visionMenuUpdate()
	if scriptNumber == 1 then
		Speak.talk("calibration server");
	elseif scriptNumber ==2 then
		Speak.talk("restart camera and settings")
	elseif scriptNumber == 3 then
		Speak.talk("main menu");
	else
		scriptNumber = 0;
	end
	
end
function visionMenuExecute()
	if scriptNumber == 1 then
                Speak.talk("calibration server");
                killAll()
		os.execute("echo 111111 | sudo -S sh scripts/startCalibration.sh &")
	elseif scriptNumber ==2 then
                Speak.talk("restart camera and settings")
                killAll();
		os.execute("echo 111111 | sudo -S sh scripts/restartcam.sh")
        elseif scriptNumber == 3 then
                Speak.talk("main menu");
		MenuID = "main menu"
        	scriptNumber = 0;
	else
                scriptNumber = 0;
        end
end
function soccerMenuUpdate()
	if scriptNumber == 1 then
		Speak.talk('kitty soccer')
	elseif  scriptNumber == 2 then
		Speak.talk('horde soccer')
	elseif scriptNumber == 3 then 
		Speak.talk("goalie soccer")
	elseif scriptNumber == 4 then
		Speak.talk('game state menu')
	elseif scriptNumber == 5 then
		Speak.talk('main menu')
	else
		scriptNumber = 0;
	end
end
function killAll()
	os.execute("kill $(ps aux | grep '[k]itty' | awk '{print $2}')")
	os.execute("kill $(ps aux | grep '[hH]orde' | awk '{print $2}')")
	os.execute("kill $(ps aux | grep '[l]ua run_cognition.lua' | awk '{print $2}')")
    os.execute("echo 111111| sudo -S kill $(ps aux | grep '[c]onnection' | awk '{print $2}')")
	os.execute("echo 111111 | sudo -S kill $(ps aux | grep '[C]alibrationServer' | awk '{print $2}')")
end
function soccerMenuExecute()
	if scriptNumber == 1 then
		 Speak.talk('Kitty Soccer Mode')
		 killAll();
		 os.execute("echo 111111 | sudo -S sh scripts/restartcam.sh")
		 print("just killed all")
		 os.execute("sh noKillRunBasic.sh")
		 unix.usleep(7*1E6)
		 print("ran cog")
		 os.execute("sh scripts/kittyMode.sh");     
		 print("kitty mode running");         
	elseif  scriptNumber == 2 then
		Speak.talk('horde soccer')
		killAll();
		os.execute("echo 111111 | sudo -S sh scripts/restartcam.sh")
		unix.usleep(2*1E6);
		os.execute("sh noKillRunBasic.sh")
		os.execute("sh runhorde.sh");

		PLAYING = 1       -- Used to say we want the game state menu at top 
		scriptNumber = 0  -- to take us to the beginning of the menu
	elseif scriptNumber == 3 then 
		Speak.talk('imma goalie');
		os.execute("sh noKillRunBasic.sh");
		os.execute("lua goalieHFA.lua")
		
		PLAYING = 1			-- Used to say we want the game state menu at top
		scriptNumber = 0 -- to take us to the beginning of the menu
	elseif scriptNumber == 4 then
        Speak.talk('game state menu')
		MenuID = "game state menu"
		scriptNumber = 0; 
	elseif scriptNumber == 5 then
		Speak.talk('main menu')
		MenuID = 'main menu'
	else
		scriptNumber = 0;
	end
end
function gameStateMenuUpdate()
        if scriptNumber == 1 then
                Speak.talk('initial')
                gcm.set_game_state(0);
        elseif  scriptNumber == 2 then
                Speak.talk('ready')
                gcm.set_game_state(1);
        elseif scriptNumber == 3 then
                Speak.talk('set')
                gcm.set_game_state(2);
        elseif scriptNumber == 4 then
                Speak.talk('play')
                gcm.set_game_state(3);
        elseif scriptNumber == 5 then
                Speak.talk('finish')
                gcm.set_game_state(4);
        elseif scriptNumber == 6 then
                Speak.talk('main menu')
                WANT_MAIN = 1
        else
                scriptNumber = 0;
        end
end

function gameStateMenuExecute()
	Speak.talk("penalty");
	teamPenalty = gcm.get_game_penalty();
	teamPenalty[Config.game.playerID] = 1 - teamPenalty[Config.game.playerID];
	gcm.set_game_penalty(teamPenalty);
	
end
WANT_MAIN = 0
PLAYING = 0
MenuID = "main menu";		
function update() 
	if ((Body.get_time() - tButton) > 0.25) then
		tButton = Body.get_time();
		
		
		if PLAYING == 0 then -- so if we aren't in either horde soccer or goalie do this
			if (Body.get_change_role() == 1) then
				scriptNumber = scriptNumber + 1;
				if(MenuID == "main menu") then
					mainMenuUpdate();		
				elseif(MenuID == "soccer menu") then 
					soccerMenuUpdate();
				elseif(MenuID == "vision menu") then
					visionMenuUpdate();
				elseif(MenuID == "game state menu") then
					gameStateMenuUpdate()
				elseif(MenuID == "id menu") then
					idMenuUpdate();
				end

			end


			if (Body.get_change_state() == 1) then
				if(MenuID == "main menu") then
					--Speak.talk("middle pressed");
					mainMenuExecute();
				elseif(MenuID == "soccer menu") then 
					soccerMenuExecute();
				elseif(MenuID == "vision menu") then
					visionMenuExecute();
				elseif(MenuID == "game state menu") then
					gameStateMenuExecute()
				 elseif(MenuID == "id menu") then
					idMenuExecute();
				end
		
		

			end	
		else -- else we will have the 
			-- we are playing so show the game menu first
			setDebugTrue();
			print("PLAYING!!")
			setDebugFalse();
			if (Body.get_change_role() == 1) then
				setDebugTrue();
				print("ScriptNumber = " .. tostring(scriptNumber));
				setDebugFalse();
				scriptNumber = scriptNumber + 1;
				gameStateMenuUpdate()
			end
			
			
			if (Body.get_change_state() == 1) then
				if WANT_MAIN == 1 then -- reset to get into main menu
					PLAYING = 0
					MenuID = "main menu"
					scriptNumber = 0
					WANT_MAIN = 0
				else -- otherwise if middle button is pressed then penalize
					gameStateMenuExecute()
				end
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
--gcm.set_game_state(0);
--Config.game.playerID = 900;
while 1 do
	--Config.game.playerID = 2;
	print(Config.game.playerID);
 	print(tostring(gcm.get_game_state()))
	update();
	unix.usleep(tDelay);
end

