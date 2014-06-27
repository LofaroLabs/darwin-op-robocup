cwd = os.getenv('PWD')
require('init')

require('wcm')
require('unix')
require('Config')
require('shm')
require('vector')
require('mcm')
require('Speak')
require('getch')
require('Body')
require('Motion')
require('dive')
require ('UltraSound')
require('grip')
Motion.entry();
darwin = false;
webots = false;


wcm.set_horde_dummyTraining(0);

