module(..., package.seeall);
require('vector')

-- Robot specific leg servo biases

servo = {};

--[[
-- For older version of MX-28 (1024 steps, 300 degree )
servo.moveRange = 300;
servo.maxSteps = 1024;
servo.posZero={
	512,512, --Head
	205,665,819, --LArm
	512,512,512,512,512,512, --LLeg
	512,512,512,512,512,512, --RLeg
	819,358,205, --RArm
};
--]]

-- For newer version of MX-28 firmware (4096 steps, 360 degree)
servo.moveRange = 360;
servo.maxSteps = 4096;
servo.posZero={
	2048,2048, --Head
	1024,2560,3072, --LArm
	2048,2048,2048,2048,2048,2048, --LLeg
	2048,2048,2048,2048,2048,2048, --RLeg
	3072,1536,1024, --RArm
};

--Robot specific leg Bias (in servo steps)
servo.legBias={3,1,9,1,1,-3,-8,-3,-10,-4,1,-5}

-- Camera Parameters
camera = {};
camera.exposure_auto_priority = 0; 
camera.exposure_auto = 1;
camera.exposure = 1005;
camera.gain = 255;
camera.brightness = 216;
camera.contrast = 2;
camera.saturation = 48;
camera.sharpness=0;
camera.white_auto = 0;
camera.white_balance = 1000;
camera.focal_length = 533; -- OP camera specific
camera.focal_base = 640; -- image width used in focal length calculation


-- Vision Parameters
vision = {};
vision.lut_file = 'lutGrasp.raw';

-- Head Parameters
head = {};
head.camOffsetZ = 0.37; -- Approximate ground to camera height
head.cameraAngle = {{0.0, 45*math.pi/180, 0.0}};
head.cameraPos = {{0.05, 0.0, 0.05}}; --neck joint to camera

-- Devive Interface Libraries
dev = {};
dev.body = 'DarwinOPBody'; 
dev.camera = 'darwinCam';
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
walk.velLimitX={-.06, .08};
walk.velLimitY={-.03,.03};
walk.velLimitA={-.4,.4};

walk.tStep = 0.32;
walk.tZmp = 0.165;
walk.stepHeight = 0.035;
walk.phSingle={0.15,0.85};
walk.supportX = 0.015;
walk.supportY = 0.020;

walk.hipRollCompensation = 5*math.pi/180;
walk.hipPitchCompensation = -0*math.pi/180;
walk.hardnessArm=vector.new({.4,.2,.2});
walk.hardnessLeg=vector.new({1,1,1,1,1,1});

--Encoder feedback parameters, alpha/gain
walk.tSensorDelay = 0.10;
walk.torsoSensorParamX={1-math.exp(-.010/0.2), 0} 
walk.torsoSensorParamY={1-math.exp(-.010/0.2), 0}

--Imu feedback parameters, alpha / gain / deadband / max
gyroFactor = 0.273*math.pi/180 * 300 / 1024; --dps to rad/s conversion
walk.ankleImuParamX={0.5,-0.5*gyroFactor, 0, 25*math.pi/180};
walk.kneeImuParamX={0.5,-1.0*gyroFactor, 0, 25*math.pi/180};
walk.ankleImuParamY={0.5,-0.5*gyroFactor, 0, 25*math.pi/180};
walk.hipImuParamY={0.5,-0.5*gyroFactor, 0, 25*math.pi/180};

--[[ Slow walk
walk.tStep = 0.50;
walk.phSingle={0.2,0.8};
walk.supportX = 0.015;
walk.supportY = 0.005;
walk.stepHeight = 0.025;
--]]

--[[    low and fast walk
walk.qLArm=math.pi/180*vector.new({140,16,-120});
walk.qRArm=math.pi/180*vector.new({140,-16,-120});
walk.bodyHeight = 0.285;
walk.tStep = 0.28;
walk.supportX = 0.030;
walk.supportY = 0.010;
walk.phSingle={0.1,0.9};
walk.stepHeight = 0.035;
--]]


--Behavior state machine parameters

BodyFSM={};
BodyFSM.chase={};
BodyFSM.chase.maxStep=0.04;
BodyFSM.chase.rClose=0.35;

BodyFSM.approach={};
BodyFSM.approach.maxStep=0.025;
BodyFSM.approach.xTarget=0.12;
BodyFSM.approach.yTarget=0.04;
BodyFSM.approach.xKick=0.14;
BodyFSM.approach.yKickLimit={0.01,0.05};
