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
vision.ball_diameter = 0.065;
vision.lut_file = 'lutWebots.raw';

-- Head Parameters

head = {};
head.camOffsetZ = 0.48;
head.pitchMin = -35*math.pi/180;
head.pitchMax = 30*math.pi/180;
head.yawMin = -120*math.pi/180;
head.yawMax = 120*math.pi/180;
head.cameraPos = {{0.05390, 0.0, 0.06790},
                  {0.04880, 0.0, 0.02381}}; 
head.cameraAngle = {{0.0, 0.0, 0.0},
                    {0.0, 40*math.pi/180, 0.0}};

-- Walk Parameters
walk = {};
walk.bodyHeight = 0.31;
walk.footY = 0.0475;

walk.bodyTilt = 0*math.pi/180;
walk.qLArm=math.pi/180*vector.new({90,16,-40,0});
walk.qRArm=math.pi/180*vector.new({90,-16,-40,0});

walk.stanceLimitX={-0.10,0.10};
walk.stanceLimitY={0.07,0.20};
walk.stanceLimitA={-10*math.pi/180,30*math.pi/180};
walk.velLimitX={-.04,.07};
walk.velLimitY={-.03,.03};
walk.velLimitA={-.3,.3};

walk.tStep = 0.40;
walk.tZmp = 0.17;
walk.stepHeight = 0.016;
walk.phSingle={0.2,0.8};
walk.supportX = 0.0175;
walk.supportY = 0.030;

walk.hipRollCompensation = 2*math.pi/180;
walk.hipPitchCompensation = 0;
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

-- Sitting params
sit={};
sit.bodyHeight=0.18+0.05; --Fixed with new kinematics
sit.bodyHeight=0.17+0.05; --Fixed with new kinematics
sit.supportX=-0.020;
sit.supportX=-0.010;
sit.bodyTilt=5*math.pi/180;
sit.dpLimit=vector.new({.1,.01,.03,.1,.3,.1});

-- Devive Interface Libraries
dev = {};
dev.body = 'NaoWebotsBody'; 
dev.camera = 'NaoWebotsCam';
dev.kinematics = 'NaoWebotsKinematics';
dev.walk = 'WebotsWalk';
dev.kick = 'WebotsKick';

-- keyframe files
km = {};
km.kick_right = 'km_Webots_KickForwardRight.lua';
km.kick_left = 'km_Webots_KickForwardLeft.lua';
km.standup_front = 'km_Webots_StandupFromFront.lua';
km.standup_back = 'km_Webots_StandupFromBack.lua';


--Behavior state machine parameters

BodyFSM={};
BodyFSM.chase={};
BodyFSM.chase.maxStep=0.04;
BodyFSM.chase.rClose=0.35;

BodyFSM.approach={};
BodyFSM.approach.maxStep=0.025;
BodyFSM.approach.xTarget=0.18;
BodyFSM.approach.xKick=0.20;
BodyFSM.approach.yTarget = 0.04;
BodyFSM.approach.yKickLimit={0.01,0.05};
