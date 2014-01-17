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
head.cameraAngle = {{0.0, 45*math.pi/180, 0.0}}; 

-- Devive Interface Libraries
dev = {};
dev.body = 'HuboWebotsBody'; 
dev.camera = 'DarwinOPWebotsCam';
dev.kinematics = 'HuboKinematics';

-- keyframe files
km = {};
km.standup_front = 'km_Hubo_StandupFromFront.lua';
km.standup_back = 'km_Hubo_StandupFromBack.lua';

-- Walk Parameters
walk = {};
walk.bodyHeight = 0.295;
walk.footY = 0.0375;

walk.bodyTilt= 0*math.pi/180;

walk.qLArm=math.pi/180*vector.new({90,0,0,90});
walk.qRArm=math.pi/180*vector.new({90,-0,0,90});


walk.stanceLimitX={-0.15,0.15};
walk.stanceLimitY={0.07,0.30};
walk.stanceLimitA={-10*math.pi/180,30*math.pi/180};
walk.velLimitX={-.15, .15};
walk.velLimitY={-.10,.10};
walk.velLimitA={-.4,.4};

walk.tStep = 0.32;
walk.tZmp = 0.165;
walk.stepHeight = 0.035;
walk.phSingle={0.15,0.85};
walk.supportX = 0.015;
walk.supportY = 0.030;
walk.supportX = 0.0;

walk.hipRollCompensation = 1*math.pi/180;
walk.hipPitchCompensation = -0*math.pi/180;
walk.hardnessArm=vector.new({.4,.2,.2});
walk.hardnessLeg=vector.new({1,1,1,1,1,1});

--Encoder feedback parameters, alpha/gain
walk.tSensorDelay = 0.10;
walk.torsoSensorParamX={1-math.exp(-.010/0.2), 0} 
walk.torsoSensorParamY={1-math.exp(-.010/0.2), 0}

--Imu feedback parameters, alpha / gain / deadband / max
gyroFactor = 0.273*math.pi/180 * 300 / 1024; --dps to rad/s conversion
walk.ankleImuParamX={0.5,-0.25*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.kneeImuParamX={0.5,-0.5*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.ankleImuParamY={0.5,-2.5*gyroFactor, 2*math.pi/180, 10*math.pi/180};
walk.hipImuParamY={0.5,-2.5*gyroFactor, 2*math.pi/180, 10*math.pi/180};



--Behavior state machine parameters

BodyFSM={};
BodyFSM.chase={};
BodyFSM.chase.maxStep=0.04;
BodyFSM.chase.rClose=0.35;

BodyFSM.approach={};
BodyFSM.approach.maxStep=0.025;
BodyFSM.approach.xTarget=0.12;
BodyFSM.approach.xKick=0.14;
BodyFSM.approach.yKickLimit={0.01,0.05};
