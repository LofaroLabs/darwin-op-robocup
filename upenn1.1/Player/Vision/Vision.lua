module(..., package.seeall);

require('Config');
require('Body');
require('Camera');
require('ImageProc');
require('HeadTransform');
require('walk');
require('carray');
require('vector');
require 'shm'

colorOrange = 1;
colorYellow = 2;
colorCyan = 4;
colorField = 8;
colorWhite = 16;

camera = {};
camera.width = Camera.get_width();
camera.height = Camera.get_height();
camera.npixel = camera.width*camera.height;
camera.image = Camera.get_image();

print("Width:",camera.width);
print("Height:",camera.height);

nxA = camera.width/2; --Label width
nyA = camera.height/2; --Label height
x0A = 0.5 * (nxA-1);
y0A = 0.5 * (nyA-1);

focalA = Config.camera.focal_length/(Config.camera.focal_base/nxA);

scaleB = 4;
nxB = nxA/scaleB;
x0B = 0.5 * (nxB-1);
nyB = nyA/scaleB;
y0B = 0.5 * (nyB-1);
focalB = focalA/scaleB;

headZ = Config.head.camOffsetZ;
diameter = 0.065; --diameter of the ball

saveCount = 0;

t = shm.new('vision', 640000)

-- Timing
tDelay = 0.010;
ncount = 20;
count = 0;
t0 = unix.time()

function entry()

  --Setting up camera parameters 

  Camera.set_param("brightness",Config.camera.brightness);
  unix.usleep(20000);
  Camera.set_param("contrast",Config.camera.contrast);
  unix.usleep(20000);
  Camera.set_param("saturation",Config.camera.saturation);
  unix.usleep(20000);
  Camera.set_param("white balance temperature, auto",Config.camera.white_auto);
  unix.usleep(20000);
  Camera.set_param("gain",Config.camera.gain);
  unix.usleep(20000);
  Camera.set_param("white balance temperature",Config.camera.white_balance);
  unix.usleep(20000);
  Camera.set_param("sharpness",Config.camera.sharpness);
  unix.usleep(20000);
  Camera.set_param("exposure, auto priority",Config.camera.exposure_auto_priority)
  unix.usleep(20000);
  Camera.set_param("exposure, auto",Config.camera.exposure_auto)
  unix.usleep(20000);
  Camera.set_param("exposure (absolute)",Config.camera.exposure)
  unix.usleep(20000);

  HeadTransform.entry();

  camera.lut = carray.new('c', 262144);
  load_lut(Config.vision.lut_file);

  labelA = {};
  labelA.m = camera.width/2;
  labelA.n = camera.height/2;

  labelB = {};
  labelB.m = labelA.m/scaleB;
  labelB.n = labelA.n/scaleB;

  ball = {};
  ball.detect = 0;

  -- Initialize Image sharing
  img = Camera.get_image();
  t:set('big_img', img, 153600 );

end

function update()

  -- Add timer measurements
  count = count + 1;
  if (count % ncount == 0) then
    local t1 = unix.time();
--    print(string.format("Controller update: %f FPS\n", ncount/(t1-t0)));
    t0 = t1;
  end

  HeadTransform.update();

  -- get image from camera
  camera.image = Camera.get_image();

  if camera.image == -1  then
    unix.usleep(1E6*tDelay);
    return
  end

  if camera.image == -2 then
    print "Buffer error";
    exit();
  end

  labelA.data  = ImageProc.yuyv_to_label(camera.image,
                                          carray.pointer(camera.lut),
					  camera.width/2, camera.height);

  -- determine total number of pixels of each color/label
  colorCount = ImageProc.color_count(labelA.data, labelA.m * labelA.n);

  -- bit-or the segmented image
  labelB.data = ImageProc.block_bitor(labelA.data, labelA.m, labelA.n,
                                      scaleB, scaleB);

  -- run ball detector
  ball = ballDetect(colorOrange);

  -- Turn on eye led if ball is found
  if ball.detect==1 then
    Body.set_actuator_eyeled({0,31,0});
  else
    Body.set_actuator_eyeled({15,15,15});
  end

  -- Update the shared memory segment
  t:set('big_img', camera.image, 153600);
  t:set('big_labelA', labelA.data, 38400 );

end

function exit()
  HeadTransform.exit();
  -- Release the shared memory
end

function bboxStats(color, bboxB)

  bboxA = {};
  bboxA[1] = scaleB*bboxB[1];
  bboxA[2] = scaleB*bboxB[2] + scaleB - 1;
  bboxA[3] = scaleB*bboxB[3];
  bboxA[4] = scaleB*bboxB[4] + scaleB - 1;

  return ImageProc.color_stats(labelA.data,
                                labelA.m, labelA.n,
                                color, bboxA);
end

function bboxArea(bbox)
  return (bbox[2]-bbox[1]+1)*(bbox[4]-bbox[3]+1);
end

function ballDetect(color)
  local ball = {};
  ball.detect = 0;

  -- threshold check on the total number of ball pixels in the image
  if (colorCount[color] < 6) then
    return ball;
  end

  -- find connected components of ball pixels
  ballPropsB = ImageProc.connected_regions(labelB.data,
                                            labelB.m, labelB.n,
                                            color);
  if (#ballPropsB == 0) then
    return ball;
  end

  -- only consider the largest connect component as a possible ball
  ball.propsB = ballPropsB[1];
  ball.propsA = bboxStats(color, ballPropsB[1].boundingBox);

  -- threshold checks on the region properties
  if ((ball.propsA.area < 6) or
      (ball.propsA.area < 0.5*bboxArea(ball.propsA.boundingBox))) then
--    print("Fill rate fail");
    return ball;
  end

  dArea = math.sqrt((4/math.pi)*ball.propsA.area);

  ballCentroid = ball.propsA.centroid;

  -- Coordinates of ball
  scale = math.max(dArea/diameter, ball.propsA.axisMajor/diameter);
  v = HeadTransform.coordinatesA(ballCentroid, scale);

  -- Project to ground plane
  if (v[3] < 0) then
    v = (headZ / (headZ - v[3]) )*v;
  end

  -- Discount body offset
  uBodyOffset = walk.get_body_offset();
  v[1] = v[1]+uBodyOffset[1];
  v[2] = v[2]+uBodyOffset[2];

  ball.v = v;
  ball.detect = 1;

  ball.r = math.sqrt(ball.v[1]^2 + ball.v[2]^2);
  ball.dr = 0.25*ball.r;
  ball.da = 10*math.pi/180;
  return ball;
end

function load_lut(fname)
  local cwd = unix.getcwd();
  if string.find(cwd, "WebotsController") then
    cwd = cwd.."/Player";
  end
  cwd = cwd.."/Data/";
  local f = io.open(cwd..fname, "r");
  assert(f, "Could not open lut file");
  local s = f:read("*a");
  for i = 1,string.len(s) do
    camera.lut[i] = string.byte(s,i,i);
  end
end


function save_rgb(rgb)
  saveCount = saveCount + 1;
  local filename = string.format("/tmp/rgb_%03d.raw", saveCount);
  local f = io.open(filename, "w+");
  assert(f, "Could not open save image file");
  for i = 1,3*camera.width*camera.height do
    local c = rgb[i];
    if (c < 0) then
      c = 256+c;
    end
    f:write(string.char(c));
  end
  f:close();
end

