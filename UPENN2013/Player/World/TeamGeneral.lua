module(..., package.seeall);

require('Config');
require('Body');
require('Comm');
require('Speak');
require('vector');
require('util')
require('serialization');

require('wcm');
require('gcm');

Comm.init(Config.dev.ip_wireless,Config.dev.ip_wireless_port);
print('Receiving Team Message From',Config.dev.ip_wireless);
playerID = gcm.get_team_player_id();

msgTimeout = Config.team.msgTimeout;
nonAttackerPenalty = Config.team.nonAttackerPenalty;
nonDefenderPenalty = Config.team.nonDefenderPenalty;
fallDownPenalty = Config.team.fallDownPenalty;
ballLostPenalty = Config.team.ballLostPenalty;

walkSpeed = Config.team.walkSpeed;
turnSpeed = Config.team.turnSpeed;



flip_correction = Config.team.flip_correction or 0;

confused_threshold_x= Config.team.confused_threshold_x or 3.0;
confused_threshold_y= Config.team.confused_threshold_y or 3.0;
flip_threshold_x= Config.team.flip_threshold_x or 1.0;
flip_threshold_y= Config.team.flip_threshold_y or 1.5;
flip_threshold_t= Config.team.flip_threshold_t or 0.5;
flip_check_t = Config.team_flip_check_t or 3.0;

confusion_handling = Config.confusion_handling or 0;


goalie_ball={0,0,0};

--Player ID: 1 to 5
--to prevent confusion, now we use these definitions
ROLE_GOALIE = 0;
ROLE_ATTACKER = 1;
ROLE_DEFENDER = 2;
ROLE_SUPPORTER = 3;
ROLE_DEFENDER2 = 4;
ROLE_CONFUSED = 5;
ROLE_RESERVE_PLAYER = 6;
ROLE_RESERVE_GOALIE = 7;

count = 0;

state = {};
state.robotName = Config.game.robotName;
state.teamNumber = gcm.get_team_number();
state.id = playerID;
state.teamColor = gcm.get_team_color();
state.time = Body.get_time();
state.role = -1;
state.pose = {x=0, y=0, a=0};
state.ball = {t=0, x=1, y=0, vx=0, vy=0, p = 0};
state.attackBearing = 0.0;--Why do we need this?
state.penalty = 0;
state.tReceive = Body.get_time();
state.battery_level = wcm.get_robot_battery_level();
state.fall=0;

role = Config.game.role
--Added key vision infos
state.goal=0;  --0 for non-detect, 1 for unknown, 2/3 for L/R, 4 for both
state.goalv1={0,0};
state.goalv2={0,0};
state.goalB1={0,0,0,0,0};--Centroid X Centroid Y Orientation Axis1 Axis2
state.goalB2={0,0,0,0,0};
state.landmark=0; --0 for non-detect, 1 for yellow, 2 for cyan
state.landmarkv={0,0};
state.corner=0; --0 for non-detect, 1 for L, 2 for T
state.cornerv={0,0};

--Game state info
state.gc_latency=0;
state.tm_latency=0;

--Body state 
state.bodyState = gcm.get_fsm_body_state();

states = {};
states[playerID] = state;

--We maintain pose of all robots 
--For obstacle avoidance
poses={};
player_roles=vector.zeros(10);
t_poses=vector.zeros(10);
tLastMessage = 0;


function recv_msgs()
  print("@!@!trying to receive messages");
  
  while (Comm.size() > 0) do 
    print("@!@!about to get the message.....");
    msg=Comm.receive();
    --Ball GPS Info hadling
    if msg and #msg==14 then --Ball position message
      ball_gpsx=(tonumber(string.sub(msg,2,6))-5)*2;
      ball_gpsy=(tonumber(string.sub(msg,8,12))-5)*2;
      wcm.set_robot_gps_ball({ball_gpsx,ball_gpsy,0});
    else --Regular team message
      t = serialization.deserialize(msg);
      --    t = unpack_msg(msg);
      if t and (t.teamNumber) and (t.id) then
        tLastMessage = Body.get_time();
        
        --Messages from upenn code
        --Keep all pose data for collison avoidance 
        if t.teamNumber ~= state.teamNumber then
          poses[t.id+5]=t.pose;
          player_roles[t.id+5]=t.role;
          t_poses[t.id+5]=Body.get_time();
        elseif t.id ~=playerID then
          poses[t.id]=t.pose;
          player_roles[t.id]=t.role;
          t_poses[t.id]=Body.get_time();
        end
        --Is the message from our team?
        if (t.teamNumber == state.teamNumber) and (t.id ~= playerID) then
          t.tReceive = Body.get_time();
          print("@!@!deciding if message is from my team")
          t.labelB = {}; --Kill labelB information
          states[t.id] = t;
        end
      end
    end
  end
end

function update_obstacle()
  --Update local obstacle information based on other robots' localization info
  local t = Body.get_time();
  local t_timeout = 2.0;
  local closest_pose={};
  local closest_dist =100;
  local closest_index = 0;
  local closest_role = 0;
  pose = wcm.get_pose();
  avoid_other_team = Config.avoid_other_team or 0;
  if avoid_other_team>0 then num_teammates = 10;end
  obstacle_count = 0;
  obstacle_x=vector.zeros(10);
  obstacle_y=vector.zeros(10);
  obstacle_dist=vector.zeros(10);
  obstacle_role=vector.zeros(10);
  for i=1,10 do
    if t_poses[i]~=0 and 
      t-t_poses[i]<t_timeout and
      player_roles[i]<ROLE_RESERVE_PLAYER then
      obstacle_count = obstacle_count+1;
      local obstacle_local = util.pose_relative({poses[i].x,poses[i].y,0},{pose.x,pose.y,pose.a}); 
      dist = math.sqrt(obstacle_local[1]^2+obstacle_local[2]^2);
      obstacle_x[obstacle_count]=obstacle_local[1];
      obstacle_y[obstacle_count]=obstacle_local[2];
      obstacle_dist[obstacle_count]=dist;
      if i<6 then --Same team
        obstacle_role[obstacle_count] = player_roles[i]; --0,1,2,3,4
      else --Opponent team
        obstacle_role[obstacle_count] = player_roles[i]+5; --5,6,7,8,9
      end
    end
  end
  wcm.set_obstacle_num(obstacle_count);
  wcm.set_obstacle_x(obstacle_x);
  wcm.set_obstacle_y(obstacle_y);
  wcm.set_obstacle_dist(obstacle_dist);
  wcm.set_obstacle_role(obstacle_role);
  --print("Closest index dist", closest_index, closest_dist);
end

function entry()
end


function update()
  --print("====PLAYERID:",playerID);
  count = count + 1;
  state.time = Body.get_time();
  state.teamNumber = gcm.get_team_number();
  state.teamColor = gcm.get_team_color();
  state.pose = wcm.get_pose();
  state.ball = wcm.get_ball();
  state.role = role;
  state.attackBearing = wcm.get_attack_bearing();
  state.battery_level = wcm.get_robot_battery_level();
  state.fall=wcm.get_robot_is_fall_down();
  state.bodyState = gcm.get_fsm_body_state();
  state.yelledReady = wcm.get_horde_yelledReady();
  state.status = wcm.get_horde_status();
  state.declared = wcm.get_horde_declared();
  state.ballRelative = util.pose_relative({wcm.get_ballGlobal_x(), wcm.get_ballGlobal_y(), 0}, {state.pose.x, state.pose.y, state.pose.a});
   
  print("yelledReady = " .. tostring(state.yelledReady))
	

  if gcm.get_team_color() == 1 then

            -- red attacks cyan goali
        print("  yellow ")
            postDefend = PoseFilter.postYellow;
			postAttack = PoseFilter.postCyan;
    else
        print("not yellow")
            -- blue attack yellow goal
            postDefend = PoseFilter.postCyan;
			postAttack = PoseFilter.postYellow;
    end

    -- global 
    DLPost = postDefend[1];
    DRPost = postDefend[2];
	avgDGoal = {(DLPost[1] + DRPost[1]) / 2, (DLPost[2] + DRPost[2]) / 2, 0}

	ALPost = postAttack[1];
    ARPost = postAttack[2];
    avgAGoal = {(ALPost[1] + ARPost[1]) / 2, (ALPost[2] + ARPost[2]) / 2, 0}

	-- now calculate the distance the robot is from each of the goals

	state.distToGoalDefend = math.sqrt((avgDGoal[1] - state.pose.x) * (avgDGoal[1] - state.pose.x) + (avgDGoal[2] - state.pose.y)*(avgDGoal[2] - state.pose.y));
	state.distToGoalOffend = math.sqrt((avgAGoal[1] - state.pose.x) * (avgAGoal[1] - state.pose.x) + (avgAGoal[2] - state.pose.y)*(avgAGoal[2] - state.pose.y));

	if state.id == ROLE_GOALIE then
	
		-- calculate the distance and set the shared memory and the state
		if get_distanceBetween(state.ballRelative, {state.pose.x, state.pose.y}) <= wcm.getMinGoalieDist then
			state.goalieCloseEnough = 1
			wcm.set_horde_goalCloseDist(1)
		else
			state.goalieCloseEnough = 0
			wcm.set_horde_goalCloseDist(0)
		end
		
	end




  if gcm.in_penalty() then  state.penalty = 1;
  else  state.penalty = 0;
  end

  --Set gamecontroller latency info
  state.gc_latency=gcm.get_game_gc_latency();
  state.tm_latency=Body.get_time()-tLastMessage;

  pack_vision_info(); --Vision info

  vision_send_interval = Config.team.vision_send_interval or 10;

  if count%vision_send_interval==0 then
    pack_labelB(); --labelB info
  end

  --Now pack state name too
  state.body_state = gcm.get_fsm_body_state();

  if (math.mod(count, 1) == 0) then --TODO: How often can we send team message?
    msg=serialization.serialize(state);
    print("@!@!1 trying to send message now");
    Comm.send(msg, #msg);
    state.tReceive = Body.get_time();
    states[playerID] = state;
  end

  -- receive new messages every frame
  recv_msgs();

  -- eta and defend distance calculation:
  eta = {};
  ddefend = {};
  roles = {};
  t = Body.get_time();
  smallest = math.huge;
  smallest_id = 0;

  shortestDefendGoalDist = math.huge;
  shortestAttackGoalDist = math.huge;
  shortestDefendID = 0;
  shortestAttackID = 0


	local numZero = 0
	local numOne = 0
	for id = 1,5 do
		
		if not states[id] or not states[id].declared then

			-- ignore him...
		else
			if states[id].declared == 1 then
				numOne = numOne + 1
			else
				numZero = numZero + 1
			end				
		end
	end
	-- zero is the default so originally everyon will be zero so 
	if numZero > numOne and numOne ~= 0  then
		wcm.set_horde_declared(1);
	else
		wcm.set_horde_declared(0);
	end
 



  for id = 1,5 do 
    if not states[id] or not states[id].ball.x then  -- no info from player, ignore him
      eta[id] = math.huge;
      ddefend[id] = math.huge;
      roles[id]=ROLE_RESERVE_PLAYER; 
    else    -- Estimated Time of Arrival to ball (in sec)
--[[
--Old ETA calculation:
      eta[id] = rBall/0.10 +  4*math.max(tBall-1.0,0)+
      math.abs(states[id].attackBearing)/3.0; --1 sec to turn 180 deg
--]]

      --New ETA calculation considering turning, ball uncertainty
      --walkSpeed: seconds needed to walk 1m
      --turnSpeed: seconds needed to turn 360 degrees
      --TODO: Consider sidekick

      rBall = math.sqrt(states[id].ballRelative[1]^2 + states[id].ballRelative[2]^2);
      tBall = states[id].time - states[id].ball.t;
      eta[id] = rBall/walkSpeed + --Walking time
        --math.abs(states[id].attackBearing)/(2*math.pi)*turnSpeed+ --Turning 
        ballLostPenalty * math.max(tBall-1.0,0);  --Ball uncertainty

      roles[id]=states[id].role;
      dgoalPosition = vector.new(wcm.get_goal_defend());-- distance to our goal

      ddefend[id] = 	math.sqrt((states[id].pose.x - dgoalPosition[1])^2 +
		 (states[id].pose.y - dgoalPosition[2])^2);

  --[[    if (states[id].role ~= ROLE_ATTACKER ) then       -- Non attacker penalty:
        eta[id] = eta[id] + nonAttackerPenalty/walkSpeed;
      end

      -- Non defender penalty:
      if (states[id].role ~= ROLE_DEFENDER and states[id].role~=ROLE_DEFENDER2) then 
        ddefend[id] = ddefend[id] + 0.3;
      end

      if (states[id].fall==1) then  --Fallen robot penalty
        eta[id] = eta[id] + fallDownPenalty;
      end--]]

		if states[id].distToGoalOffend < shortestAttackGoalDist then
			shortestAttackGoalDist = states[id].distToGoalOffend
			shortestAttackID = id
		end

		if states[id].distToGoalDefend < shortestDefendGoalDist then
			shortestDefendGoalDist = states[id].distToGoalDefend;
			shortestDefendID = id;
		end


      --Store this
      if id==playerID then
        wcm.set_team_my_eta(eta[id]);
      end
      print("ETA for id " .. id .. " is " .. eta[id]);
      if eta[id] < smallest then
	smallest_id = id
	smallest = eta[id];
      end


      --Ignore goalie, reserver, penalized player, confused player
      --[[if (states[id].penalty > 0) or 
        (t - states[id].tReceive > msgTimeout) or
        (states[id].role >=ROLE_CONFUSED) or 
        (states[id].role ==0) then
        eta[id] = math.huge;
        ddefend[id] = math.huge;
      end]]--

    end
  end


	if shortestAttackID == state.id then
		wcm.set_team_isClosestToGoalOffend(1);
	else
		wcm.set_team_isClosestToGoalOffend(0);
	end

	if shortestDefendID == state.id then
        wcm.set_team_isClosestToGoalDefend(1);
    else
        wcm.set_team_isClosestToGoalDefend(0);
    end


  -- set the ball pose of the bot that is closest
  -- convert the relative ball loc to global loc
  if smallest_id ~= 0 then
    closestToBallLoc = util.pose_global(states[smallest_id].ballRelative, {states[smallest_id].pose.x, states[smallest_id].pose.y, states[smallest_id].pose.a})
    wcm.set_team_closestToBallLoc(closestToBallLoc)


   -- get the midpoint
  themid = getMidpoint();
  wcm.set_horde_midpointBallGoal({themid.x, themid.y});
end

  if smallest_id == playerID then
	wcm.set_team_is_smallest_eta(1);
  else
        wcm.set_team_is_smallest_eta(0);
  end

  
  update_shm() 
  update_status();
  update_goalieCloseEnough();
  update_teamdata();
  update_obstacle();
  check_confused();
  check_flip2();
end

-- 0 = i am closest or we are without comm then we are all closest
-- 1 = i am second closest and within N
-- 2 = i am second closest
-- 3 = i am third closest and within N
-- 4 = i am third closest
function update_status()

	
	local ballDist = state.ballRelative; -- the position of the ball relative to me based off the global pos
	local myDist = get_distanceBetween(ballDist, {state.pose.x, state.pose.y});
	local distIDPairs = {}
	for id = 1,5 do
		
		if states[id] and states[id].role ~= GOALIE_ROLE and states[id].pose and states[id].ballRelative then
			local data = {}
			data.id = states[id].id
			data.dist = get_distanceBetween(states[id].ballRelative, {states[id].pose.x, states[id].pose.y});
			data.status = states[id].status
			distIDPairs[id] = data;
		end
	
	end
	-- sort everyone
	table.sort(distIDPairs, function (a, b) return a.dist < b.dist end)
	
	-- loop
	
	local secondClosestWithin = false
	for i=1, #distIDPairs do
		
		if i == 1 then
			distIDPairs[i].status = 0; -- you are the closest since you are first
		elseif distIDPairs[i].dist <= wcm.get_horde_distN() and secondClosestWithin == false then
			distIDPairs[i].status = 1
			secondClosestWithin = true
		elseif distIDPairs[i].dist <= wcm.get_horde_distN() and secondClosestWithin == true then
			distIDPairs[i].status = 3
			secondClosestWithin = true
 		elseif secondClosestWithin == false then
			distIDPairs[i].status = 2
			secondClosestWithin = true -- since the closest was not close enough
		elseif secondclosestWithin == true then
			distIDPairs[i].status = 4
		else 
			print("ERROR no State set!!!");
		end
		
		if distIDPairs[i].id == state.id then
			wcm.set_horde_status(distIDPairs[i].status);
		end
	end
	
	

end


function update_goalieCloseEnough()

	-- If i am the goalie then i check otherwise i just get the value that the
	-- was given to me by the goalie telling me it is close enough or not
	
	for id = 1,5 do
	
		if states[id] and states[id].role == GOALIE_ROLE and states[id].goalieCloseEnough then
			wcm.set_horde_goalieCloseEnough(states[id].goalieCloseEnough)
			return;
		end
	
	end

end

function update_teamdata()
  attacker_eta = math.huge;
  defender_eta = math.huge;
  defender2_eta = math.huge;
  supporter_eta = math.huge;
  goalie_alive = 0; 

  attacker_pose = {0,0,0};
  defender_pose = {0,0,0};
  defender2_pose = {0,0,0};
  supporter_pose = {0,0,0};
  goalie_pose = {0,0,0};

  best_scoreBall = 0;
  best_ball = {0,0,0};
  for id = 1,5 do
    --Update teammates pose information
    if states[id] and states[id].tReceive and
      (t - states[id].tReceive < msgTimeout) then

      --Team ball calculation here
      --Score everyone's ball position info and pick the best one
      if id~=playerID and states[id].role<4 then
        rBall = math.sqrt(states[id].ball.x^2 + states[id].ball.y^2);
        tBall = states[id].time - states[id].ball.t;
        pBall = states[id].ball.p;
        scoreBall = pBall * 
        math.exp(-rBall^2 / 12.0)*
        math.max(0,1.0-tBall);
        --print(string.format("r%.1f t%.1f p%.1f s%.1f",rBall,tBall,pBall,scoreBall))
        if scoreBall > best_scoreBall then
          best_scoreBall = scoreBall;
          posexya=vector.new( 
            {states[id].pose.x, states[id].pose.y, states[id].pose.a} );
          best_ball=util.pose_global(
            {states[id].ball.x,states[id].ball.y,0}, posexya);
        end
      end

      if states[id].role==ROLE_GOALIE then
        goalie_alive =1;
        goalie_pose = {
          states[id].pose.x,states[id].pose.y,states[id].pose.a};
        goalie_ball = util.pose_global(
          {states[id].ball.x,states[id].ball.y,0},	  goalie_pose);
        goalie_ball[3] = states[id].time - states[id].ball.t ;



      elseif states[id].role==ROLE_ATTACKER then
          attacker_pose = {states[id].pose.x,states[id].pose.y,states[id].pose.a};
          attacker_eta = eta[id];
      elseif states[id].role==ROLE_DEFENDER then
          defender_pose = {states[id].pose.x,states[id].pose.y,states[id].pose.a};
          defender_eta = eta[id];
      elseif states[id].role==ROLE_SUPPORTER then
          supporter_eta = eta[id];
          supporter_pose = {states[id].pose.x,states[id].pose.y,states[id].pose.a};
      end
    end
  end

  local teamPoseX = {}
  local teamPoseY = {}
  local teamPoseA = {}
  local teamYellReady = {}
  for id = 1, 5 do
    
    if states[id] and states[id].yelledReady then
      	 --print("Id = ".. id .. " yelledReady = " .. tostring(states[id].yelledReady))
	teamYellReady[id] = states[id].yelledReady
	teamPoseX[id] = states[id].pose.x;
	teamPoseY[id] =  states[id].pose.y;
	teamPoseA[id] =  states[id].pose.a;
    else
	teamYellReady[id] = 0
	 -- not here so just put them at the center
	teamPoseX[id] = 0;
	teamPoseY[id] = 0;
	teamPoseA[id] = 0;
    end
  end
  -- all the yelled ready people
  wcm.set_team_yelledReady(teamYellReady)

  wcm.set_team_teamPoseX(teamPoseX);
  wcm.set_team_teamPoseY(teamPoseY);
  wcm.set_team_teamPoseA(teamPoseA);


  wcm.set_robot_team_ball(best_ball);
  wcm.set_robot_team_ball_score(best_scoreBall);

  wcm.set_team_attacker_eta(attacker_eta);
  wcm.set_team_defender_eta(defender_eta);
  wcm.set_team_supporter_eta(supporter_eta);
  wcm.set_team_defender2_eta(defender2_eta);
  wcm.set_team_goalie_alive(goalie_alive);

  wcm.set_team_attacker_pose(attacker_pose);
  wcm.set_team_defender_pose(defender_pose);
  wcm.set_team_goalie_pose(goalie_pose);
  wcm.set_team_supporter_pose(supporter_pose);
  wcm.set_team_defender2_pose(defender2_pose);

end

function get_distance(curA, targetB)
	return math.sqrt(math.pow(curA.x - targetB.x, 2) + math.pow(curA.y - targetB.y, 2))
end

function get_distanceBetween(A, B)
	return math.sqrt(math.pow(A[1] - B[1], 2) + math.pow(A[2] - B[2], 2))
end

function getMidpoint()
 if gcm.get_team_color() == 1 then

                -- red attacks cyan goali
                print("  yellow ")
                postDefend = PoseFilter.postYellow;
        else
                print("not yellow")
                -- blue attack yellow goal
                postDefend = PoseFilter.postCyan;
        end

        -- global 
        LPost = postDefend[1];
        RPost = postDefend[2];

        ballGlobal= {};
        ballGlobal.x = wcm.get_team_closestToBallLoc()[1]
        ballGlobal.y = wcm.get_team_closestToBallLoc()[2]


        -- my pose global
        pose=wcm.get_pose();

        LPost.x = LPost[1]
        LPost.y = LPost[2]
        RPost.x = RPost[1]
        RPost.y = RPost[2]
    farPost = {}
        if get_distance(ballGlobal, LPost) > get_distance(ballGlobal, RPost) then
                farPost.x = LPost[1]
                farPost.y = LPost[2]
                print("the far post is at coordinates: " .. tostring(farPost.x) .. ", " .. tostring(farPost.y))
                print("the near post is at coordinates: " .. tostring(RPost.x) .. ", " .. tostring(RPost.y))
        else 
		farPost.x = RPost[1]
                farPost.y = RPost[2]

                print("the far post is at coordinates: " .. tostring(farPost.x) .. ", " .. tostring(farPost.y))
                print("the near post is at coordinates: " .. tostring(LPost.x) .. ", " .. tostring(LPost.y))
        end
        --print("going to the po        
        midpoint = {}
        midpoint.x = (ballGlobal.x + farPost.x) / 2
        midpoint.y = (ballGlobal.y + farPost.y) /2
        midpoint.a = 0

        return midpoint



end







function exit() end
function get_role()   return role; end
function get_player_id()    return playerID; end
function update_shm() 
   gcm.set_team_role(Config.game.role);
end

function set_role(r)
   Body.set_indicator_role(Config.game.role);  
--[[if role ~= r then 
    role = r;
    Body.set_indicator_role(role);
    if role == ROLE_ATTACKER then  Speak.talk('Attack');
    elseif role == ROLE_DEFENDER then  Speak.talk('Defend');
    elseif role == ROLE_SUPPORTER then Speak.talk('Support');
    elseif role == ROLE_GOALIE then Speak.talk('Goalie');
    elseif role == ROLE_DEFENDER2 then Speak.talk('Defender Two')
    elseif role == ROLE_CONFUSED then Speak.talk('Confused')
    elseif role == ROLE_RESERVE_PLAYER then Speak.talk('Player waiting')
    elseif role == ROLE_RESERVE_GOALIE then Speak.talk('Goalie waiting')
    else Speak.talk('ERROR: Unknown Role');
    end
  end]]--
  update_shm();
end

function pack_labelB()
  labelB = vcm.get_image_labelB();
  width = vcm.get_image_width()/8; 
  height = vcm.get_image_height()/8;
  count = vcm.get_image_count();
  array = serialization.serialize_label_rle(
    labelB, width, height, 'uint8', 'labelB',count);
  state.labelB = array;
end

function pack_vision_info()
  --Added Vision Info 
  state.goal=0;
  state.goalv1={0,0};
  state.goalv2={0,0};
  if vcm.get_goal_detect()>0 then
    state.goal = 1 + vcm.get_goal_type();
    local v1=vcm.get_goal_v1();
    local v2=vcm.get_goal_v2();
    state.goalv1[1],state.goalv1[2]=v1[1],v1[2];
    state.goalv2[1],state.goalv2[2]=0,0;
    centroid1 = vcm.get_goal_postCentroid1();
    orientation1 = vcm.get_goal_postOrientation1();
    axis1 = vcm.get_goal_postAxis1();
    state.goalB1 = {centroid1[1],centroid1[2],
    orientation1,axis1[1],axis1[2]};
    if vcm.get_goal_type()==3 then --two goalposts 
      state.goalv2[1],state.goalv2[2]=v2[1],v2[2];
      centroid2 = vcm.get_goal_postCentroid2();
      orientation2 = vcm.get_goal_postOrientation2();
      axis2 = vcm.get_goal_postAxis2();
      state.goalB2 = {centroid2[1],centroid2[2],
      orientation2,axis2[1],axis2[2]};
    end
  end
  state.landmark=0;
  state.landmarkv={0,0};
  state.corner=0;
  state.cornerv={0,0};
  if vcm.get_corner_detect()>0 then
    state.corner = vcm.get_corner_type();
    local v = vcm.get_corner_v();
    state.cornerv[1],state.cornerv[2]=v[1],v[2];
  end
end

function check_flip2()
  local is_confused = wcm.get_robot_is_confused();
  
  if is_confused==0 then return; end
  print("cofused is true, now i gotta wait some time");
  local pose = wcm.get_pose();
  local ball = wcm.get_ball();
  local ball_global = util.pose_global({ball.x,ball.y,0},{pose.x,pose.y,pose.a});
  local t = Body.get_time();
  local t_confused = wcm.get_robot_t_confused();

  --Wait a bit before trying correction
  if t-t_confused < flip_check_t then return; end
  print("okay that amount of time has passed");

  print(string.format("Goalie ball :%.1f %.1f %.1f",
		goalie_ball[1],goalie_ball[2],goalie_ball[3] ));
  print(string.format("Player ball: %.1f %.1f %.1f", 
		ball_global[1],ball_global[2],t-ball.t));



  if t-ball.t<flip_threshold_t	and goalie_ball[3]<flip_threshold_t then
     --Check X position
     if ((math.abs(ball_global[1])>flip_threshold_x) and
        (math.abs(goalie_ball[1])>flip_threshold_x)) or
	(math.abs(goalie_ball[1]-ball_global[1])>.75) then
       if ball_global[1]*goalie_ball[1]<0 then
         wcm.set_robot_flipped(1);
       end
       --Now we are sure about our position
       wcm.set_robot_is_confused(0);
       if confusion_handling == 1 then
         set_role(ROLE_ATTACKER);
       end

     --Check Y position
     elseif ((math.abs(ball_global[2])>flip_threshold_y) and
            (math.abs(goalie_ball[2])>flip_threshold_y)) or
	    (math.abs(goalie_ball[2]-ball_global[2])>.75) then
       if ball_global[2]*goalie_ball[2]<0 then
         wcm.set_robot_flipped(1);
       end

       --Now we are sure about our position
       wcm.set_robot_is_confused(0);
       if confusion_handling == 1 then
         set_role(ROLE_ATTACKER);
       end
     end
  end

  if wcm.get_robot_is_confused()==0 then
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
    print("CONFUSION FIXED")
  end
end

function check_confused()

  if flip_correction==0 then 
    wcm.set_robot_is_confused(0);
	  return; 
  end
  goalie_alive =  wcm.get_team_goalie_alive();
  if goalie_alive==0 then 
    wcm.set_robot_is_confused(0);
	  return; 
  end --Goalie's dead, we cannot correct flip

  pose = wcm.get_pose();
  t = Body.get_time();

  --Goalie or reserve players never get confused
  if role==ROLE_GOALIE or role >= ROLE_RESERVE_PLAYER then 
    wcm.set_robot_is_confused(0);
		return; 
	end
  is_confused = wcm.get_robot_is_confused();

  if is_confused>0 then
    --Currently confused
    if gcm.get_game_state() ~= 3 --If game state is not gamePlaying
       or gcm.in_penalty() --Or the robot is penalized
       then 
      --Robot gets out of confused state!
      wcm.set_robot_is_confused(0);
      if role==ROLE_CONFUSED then
        set_role(ROLE_ATTACKER); 
      end
    end
  else

    print(".........................................................................");
    print("Is Fall Down = " .. wcm.get_robot_is_fall_down());
    print("Pose x == " .. math.abs(pose.x))
    print("Pose y = " .. math.abs(pose.y))
    print("Game state = " .. gcm.get_game_state());
    print("confused_threshold_x " .. confused_threshold_x)
    print("confused_threshold_y " .. confused_threshold_y)
    --Should we turn confused?
    if wcm.get_robot_is_fall_down()>0 
       and math.abs(pose.x)<confused_threshold_x 
       and math.abs(pose.y)<confused_threshold_y 
       and gcm.get_game_state() == 3 --Only get confused during playing
			  then
      wcm.set_robot_is_confused(1);
      wcm.set_robot_t_confused(t);
      print("CONFUSED")
      print("CONFUSED")
      print("CONFUSED")
      print("CONFUSED")
      print("CONFUSED")

      if confusion_handling == 1 then
        set_role(ROLE_CONFUSED); --Robot gets confused!
      elseif confusion_handling == 2 then
        --Robot maintains current role
      end
    end
  end
end

--NSL role can be set arbitarily, so use config value
set_role(Config.game.role or 1);
