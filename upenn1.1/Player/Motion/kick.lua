module(..., package.seeall);

require('Body')
require('keyframe')
require('walk')
require('vector')
require('Config')

local cwd = unix.getcwd();
if string.find(cwd, "WebotsController") then
  cwd = cwd.."/Player";
end
cwd = cwd.."/Motion"

-- default kick type
kickType = "kickForwardLeft";
active = false;

bodyHeight = walk.bodyHeight;
footY = walk.footY;
bodyTilt = walk.bodyTilt;
supportX = walk.supportX;
qLArm = walk.qLArm;
qRArm = walk.qRArm;

--Base position for torso and feet in position6D
pTorso0 = vector.new({0, 0, bodyHeight, 0,bodyTilt,0});
pLLegStance = vector.new({-supportX, footY, 0, 0,0,0});
pRLegStance = vector.new({-supportX, -footY, 0, 0,0,0});

--Hip roll angle compensation values 
qLHipRollCompensation=7*math.pi/180;
qRHipRollCompensation=-7*math.pi/180;

--OP specific shift parameters
pTorsoShiftLeft0=-0.045;
pTorsoShiftRight0=0.045;

pStepBackward=-0.08;
pStepForward=0.12;
pStepSide= 0.01;

pStepHeight=0.06;
pStepHeight2=0.06;
pLandHeight=0.0; 

aStep1 = 20*math.pi/180;
aStep2 = -20*math.pi/180;

--kick timing parameters
tWait=0.5;
tShift=0.6;
tLift=0.2;
tKick=0.2;
tLand=0.3;
tStand=1;

function entry()
  print("Motion SM:".._NAME.." entry");
  walk.stop();
  started = false;
  active = true;
  if kickType=="kickForwardLeft" then 
    pTorsoShift=pTorsoShiftLeft0;
  else 
    pTorsoShift=pTorsoShiftRight0;  
  end
    -- disable joint encoder reading
  --Actual position for torso and feet in position6D
  pTorso = vector.new({0, 0, bodyHeight, 0,bodyTilt,0});
  pLLeg= vector.new({-supportX, footY, 0, 0,0,0});
  pRLeg= vector.new({-supportX, -footY, 0, 0,0,0});

  Body.set_syncread_enable(0);
end

function update()
  if not started then
    if walk.active then
      walk.update(); --wait until walking is stopped
    else
      -- start kick
      started = true;
      Body.set_head_hardness(.5);
      Body.set_larm_hardness(.1);
      Body.set_rarm_hardness(.1);
      Body.set_lleg_hardness(1);
      Body.set_rleg_hardness(1);
      t0 = Body.get_time();
      print("Kick start");
    end
  else
      local t = Body.get_time();
      local dt = t - t0-tWait;
      if dt<0 then 
	--Wait a bit to stabilize 
      elseif dt<tShift then 	
	--Shift torso to side
  	pTorso[2]=pTorso0[2]+pTorsoShift*dt/tShift;
      elseif dt<tShift+tLift then	
	--Lift leg
	local dt2=1- (tShift+tLift-dt)/tLift;
	if kickType=="kickForwardLeft" then
	  pLLeg[1]=pLLegStance[1]+pStepBackward*dt2;
	  pLLeg[2]=pLLegStance[2]-pStepSide*dt2;
	  pLLeg[3]=pLLegStance[3]+pStepHeight*dt2;
	  pLLeg[5]=aStep1*dt2;
	else
	  pRLeg[1]=pRLegStance[1]+pStepBackward*dt2;
	  pRLeg[2]=pRLegStance[2]+pStepSide*dt2;
	  pRLeg[3]=pRLegStance[3]+pStepHeight*dt2;
	  pRLeg[5]=aStep1*dt2;
	end
      elseif dt<tShift+tLift+tKick then	
	--Performing kick
 	if kickType=="kickForwardLeft" then
   	  pLLeg[1]=pLLegStance[1]+pStepForward;
	  pLLeg[3]=pLLegStance[3]+pStepHeight2;
	  pLLeg[5]=aStep2;
	else
	  pRLeg[1]=pRLegStance[1]+pStepForward;
	  pRLeg[3]=pRLegStance[3]+pStepHeight2;
	  pRLeg[5]=aStep2;
	end
      elseif dt<tShift+tLift+tKick+tLand then	
        --Landing
	local dt2=(tShift+tLift+tKick+tLand-dt)/tLand;
	if kickType=="kickForwardLeft" then
	  pLLeg[1]=pLLegStance[1]+pStepForward*dt2;
	  pLLeg[2]=pLLegStance[2]-pStepSide*dt2;
	  pLLeg[3]=pLLegStance[3]+pLandHeight+(pStepHeight2-pLandHeight)*dt2;
	  pLLeg[5]=0;
	else
	  pRLeg[1]=pRLegStance[1]+pStepForward*dt2;
	  pRLeg[2]=pRLegStance[2]+pStepSide*dt2;
	  pRLeg[3]=pRLegStance[3]+pLandHeight+(pStepHeight2-pLandHeight)*dt2;
	  pRLeg[5]=0;
	end
      elseif dt<tShift+tLift+tKick+tLand+tStand then	
	--Shift torso back 
	local dt2=(tShift+tLift+tKick+tLand+tStand-dt)/tStand;
	pTorso[2]=pTorso0[2]+pTorsoShift*dt2;
      else   
	print("Kick done");
	return "done";  
      end

      q = Kinematics.inverse_legs(pLLeg, pRLeg, pTorso, 0);
      if dt>tShift and dt<tShift+tLift+tKick then
        if kickType=="kickForwardLeft" then
          q[8] = q[8] + qRHipRollCompensation;
        else
          q[2] = q[2] + qLHipRollCompensation;
        end
      end
      Body.set_lleg_command(q);
  end
end

function exit()
  print("Kick exit");
  active = false;
  walk.active=true;
end

function set_kick(newKick)
  -- set the kick type (left/right)
  kickType = newKick;
end
