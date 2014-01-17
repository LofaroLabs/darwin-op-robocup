module(..., package.seeall);


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
vision.lut_file = 'lutGrasp.raw';


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
walk.tStep = 0.51;
walk.tZmp = 0.17;
walk.bodyHeight = 0.31;
walk.stepHeight = 0.016;
walk.footY = 0.0475;

walk.supportX = 0.0175;
walk.supportY = 0.000;

walk.hipRollCompensation = 3*math.pi/180;
walk.tSensorDelay = 0.10;

-- Devive Interface Libraries
dev = {};
dev.body = 'NaoBody'; 
dev.camera = 'NaoCam';
dev.kinematics = 'NaoKinematics';

-- keyframe files
km = {};
km.kick_right = 'km_Nao_KickForwardRight.lua';
km.kick_left = 'km_Nao_KickForwardLeft.lua';
km.standup_front = 'km_Nao_StandupFromFront.lua';
km.standup_back = 'km_Nao_StandupFromBack.lua';


