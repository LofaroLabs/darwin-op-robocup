module(..., package.seeall);
require('controller');
require('carray');
require('ImageProc');

controller.wb_robot_init();
timeStep = controller.wb_robot_get_basic_time_step();

-- Get webots tags:
tags = {};
tags.camera = controller.wb_robot_get_device("Camera");
controller.wb_camera_enable(tags.camera, timeStep);

positionTop = 0;
positionBottom = 0.70;
--tags.cameraSelect = controller.wb_robot_get_device("CameraSelect");
--controller.wb_servo_set_position(tags.cameraSelect, positionBottom);
--controller.wb_servo_enable_position(tags.cameraSelect, timeStep);

controller.wb_robot_step(timeStep);

height = controller.wb_camera_get_height(tags.camera);
width = controller.wb_camera_get_width(tags.camera);
image = carray.cast(controller.wb_camera_get_image(tags.camera),
		    'c', 3*height*width);

function set_param()
end

function get_param()
  return 0;
end

function get_height()
  return height;
end

function get_width()
  return width;
end

function get_image()
  --rgb2yuyv
  return ImageProc.rgb_to_yuyv(carray.pointer(image), width*height);
end

function get_camera_position()
  return 0;
--  return controller.wb_servo_get_position(tags.cameraSelect);
end

function select(bottom)
--[[
  if (bottom ~= 0) then
    controller.wb_servo_set_position(tags.cameraSelect, positionBottom);
  else
    controller.wb_servo_set_position(tags.cameraSelect, positionTop);
  end
--]]
end

function get_select()
  return 0;
--[[
  if (controller.wb_servo_get_position(tags.cameraSelect) < .5*positionBottom) then
    --top camera
    return 0;
  else
    return 1;
  end
--]]
end
