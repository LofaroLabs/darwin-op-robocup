module(..., package.seeall);

require('Config');
require('Body');
require('Vision');
require('Camera');
require('Transform');
require('vector');

tHead = Transform.eye();
camPosition = 0;

camOffsetZ = Config.head.camOffsetZ; -- Approximate ground to camera height
pitchMin = -60 * math.pi / 180;
pitchMax = 60 * math.pi / 180;
yawMin = -90 * math.pi / 180;
yawMax = 90 * math.pi / 180;
cameraPos = Config.head.cameraPos; --neck joint to camera
cameraAngle = Config.head.cameraAngle;
neckX=0.013;  --COM to neck joint 
neckZ=0.0765; --COM to neck joint

bodyTilt=Config.walk.bodyTilt;
supportX=Config.walk.supportX;
bodyHeight=Config.walk.bodyHeight;

function entry()
end

function update()
  -- get_select is 0 indexed so add 1
  sel = Camera.get_select() + 1;

  headAngles = Body.get_head_position();
  tNeck = Transform.trans(supportX,0,bodyHeight);
  tNeck = tNeck*Transform.rotY(bodyTilt);
  tNeck = tNeck*Transform.trans(neckX,0,neckZ);
  tNeck = tNeck*Transform.rotZ(headAngles[1])*Transform.rotY(headAngles[2]);
  tHead = tNeck*Transform.trans(
	cameraPos[sel][1], cameraPos[sel][2], cameraPos[sel][3]);
  tHead = tHead*Transform.rotX(cameraAngle[sel][1]);
  tHead = tHead*Transform.rotY(cameraAngle[sel][2]);
  tHead = tHead*Transform.rotZ(cameraAngle[sel][3]);
end

function exit()
end

function coordinatesA(c, scale)

  scale = scale or 1;
  local v = vector.new({Vision.focalA,
                       (Vision.x0A - c[1]),
                       (Vision.y0A - c[2]),
                       scale});

  v = tHead*v;
  v = v/v[4];

  return v;
end

function coordinatesB(c, scale)
  scale = scale or 1;
  local v = vector.new({Vision.focalB,
                        (Vision.x0B - c[1]),
                        (Vision.y0B - c[2]),
                        scale});
  v = tHead*v;
  v = v/v[4];
  return v;
end

function getNeckOffset()
  local v=vector.new({0,0,0,1});
  v=tNeck*v;
  v=v/v[4];
  return v;
end

function ikineCam(x, y, z, select)
  --Bottom camera by default (cameras are 0 indexed so add 1)
  select = (select or 0) + 1;

  --Look at ground by default
  z = z or 0;
  v=getNeckOffset();

  x = x - v[1]; z = z - v[3];
  --Cancel out body tilt angle
  v = Transform.rotY(-bodyTilt) * vector.new({ x, y, z, 1});
  v = v / v[4];
  x,y,z = v[1], v[2], v[3];
  local norm = math.sqrt(x^2 + y^2 + z^2);
  local yaw = math.atan2(y, x);
  local pitch = math.asin(-z/(norm + 1E-10));

  yaw = math.min(math.max(yaw, yawMin), yawMax);
  pitch = math.min(math.max(pitch, pitchMin), pitchMax);

  pitch = pitch - cameraAngle[select][2];
  return yaw, pitch;
end
