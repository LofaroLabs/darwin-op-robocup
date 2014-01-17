module(... or "", package.seeall)

require('init')


require('vcm');
require('vector');

vcm.set_vision_enable(vector.ones(1));

while true do
	print("Vision enabled = " .. vcm.get_vision_enable());
	print("Detect ball = " .. vcm.get_ball_detect());
end
