module(..., package.seeall);

require('Body')
require('fsm')
require('gcm')
require('Config')

require('bodyIdle')
require('bodyStart')
require('bodyStop')
require('bodyReady')
require('bodySearch')
require('bodyApproach')
require('bodyDribble')
require('bodyKick')
require('bodyWalkKick')
require('bodyOrbit')
require('bodyGotoCenter')
require('bodyPosition')
require('bodyObstacle')
require('bodyObstacleAvoid')
require('bodyDribble')
require('bodyWalkForward')
require('bodyKickGMU')
require('bodyGotoBall')
require('bodyNull')

require('bodyPositionGoalie')
require('bodyAnticipate')
require('bodyChase')
require('bodyDive')


require('bodyUnpenalized')



require('bodyReadyMove')



sm = fsm.new(bodyIdle);
sm:add_state(bodyStart);
sm:add_state(bodyStop);
sm:add_state(bodyReady);
sm:add_state(bodySearch);
sm:add_state(bodyApproach);
sm:add_state(bodyDribble);
sm:add_state(bodyKick);
sm:add_state(bodyWalkKick);
sm:add_state(bodyOrbit);
sm:add_state(bodyGotoCenter);
sm:add_state(bodyPosition);
sm:add_state(bodyObstacle);
sm:add_state(bodyObstacleAvoid);
sm:add_state(bodyDribble);
sm:add_state(bodyWalkForward);
sm:add_state(bodyKickGMU);
sm:add_state(bodyGotoBall);
sm:add_state(bodyNull);

sm:add_state(bodyPositionGoalie);
sm:add_state(bodyAnticipate);
sm:add_state(bodyDive);
sm:add_state(bodyChase);

sm:add_state(bodyReadyMove);
sm:add_state(bodyUnpenalized);

function entry()
  sm:entry()
end

function update()
  sm:update();
end

function exit()
  sm:exit();
end
