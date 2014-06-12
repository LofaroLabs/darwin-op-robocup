require('gcm')


if Config.game.role == 0 then
	BodyFSM = require("BodyFSMAdvanced");
else
	BodyFSM = require("BodyFSMGMU");
end

