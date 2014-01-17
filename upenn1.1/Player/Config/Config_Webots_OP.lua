module(..., package.seeall);
require('vector')

-- Camera Parameters

camera = {};
camera.auto_exposure = 0;
camera.auto_white_balance = 0;
camera.autogain = 0;
camera.exposure = 150;
camera.gain = 61;
camera.brightness = 100;
camera.contrast = 75;
camera.saturation = 200;
camera.red_balance = 100;
camera.blue_balance = 120;
camera.hue = 0;
camera.focal_length = 383; -- in pixels
camera.focal_base = 320; -- image width used in focal length calculation

-- Vision Parameters
vision = {};
vision.lut_file = 'lutWebots.raw';

-- Head Parameters
head = {};
head.camOffsetZ = 0.37; -- Approximate ground to camera height
head.cameraAngle = {{0.0, 0*45*math.pi/180, 0.0}};
head.cameraPos = {{0.05, 0.0, 0.05}}; --neck joint to camera

-- Devive Interface Libraries
dev = {};
dev.body = 'DarwinOPWebotsBody'; 
dev.camera = 'DarwinOPWebotsCam';
dev.kinematics = 'DarwinOPKinematics';

-- keyframe files
km = {};
km.standup_front = 'km_OP_StandupFromFront.lua';
km.standup_back = 'km_OP_StandupFromBack.lua';

-- Walk Parameters
walk = {};
walk.bodyHeight = 0.295;
walk.footY = 0.0375;

walk.bodyTilt= 20*math.pi/180;
walk.qLArm=math.pi/180*vector.new({90,16,-40});
walk.qRArm=math.pi/180*vector.new({90,-16,-40});

walk.stanceLimitX={-0.10,0.10};
walk.stanceLimitY={0.07,0.20};
walk.stanceLimitA={-10*math.pi/180,30*math.pi/180};
walk.velLimitX={-.04,.07};
walk.velLimitY={-.03,.03};
walk.velLimitA={-.3,.3};

walk.tStep = 0.40;
walk.tZmp = 0.165;
walk.stepHeight = 0.045;
walk.phSingle={0.2,0.8};
walk.supportX = -0.000;
walk.supportY = 0.025;

walk.hipRollCompensation = 3*math.pi/180;
walk.hipPitchCompensation = -0*math.pi/180;
walk.hardnessArm=vector.new({.4,.2,.2});
walk.hardnessLeg=vector.new({1,1,1,1,1,1});

--Encoder feedback parameters, alpha/gain
walk.tSensorDelay = 0.10;
walk.torsoSensorParamX={1-math.exp(-.010/0.2), 0} 
walk.torsoSensorParamY={1-math.exp(-.010/0.2), 0}

--Imu feedback parameters, alpha / gain / deadband / max
gyroFactor = 0.273*math.pi/180 * 300 / 1024; --dps to rad/s conversion
walk.ankleImuParamX={1,-0.75*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.kneeImuParamX={1,-1.5*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.ankleImuParamY={1,-1*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.hipImuParamY={1,-1*gyroFactor, 2*math.pi/180, 10*math.pi/180};

--Behavior state machine parameters

BodyFSM={};
BodyFSM.chase={};
BodyFSM.chase.maxStep=0.05;
BodyFSM.chase.rClose=0.35;

BodyFSM.approach={};
BodyFSM.approach.maxStep=0.03;
BodyFSM.approach.xTarget=0.15;
BodyFSM.approach.xKick=0.17;
BodyFSM.approach.yTarget = 0.04;
BodyFSM.approach.yKickLimit={0.01,0.05};
