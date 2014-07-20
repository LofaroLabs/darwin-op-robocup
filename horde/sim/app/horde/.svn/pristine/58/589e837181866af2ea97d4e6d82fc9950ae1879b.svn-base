#include <iostream>

#include "robot.h"
#include "tracking.h"

double angles[] = {-90,-50,-30,-10,10,30,50,90,90,130,150,170,-170,-150,-130,-90};

#define SPEED 10
#define SAFE_DISTANCE 250
#define EMPTY_SPACE_DIST 2000

 double min(double a, double b, double c)
 {
	if (a < b && a < c) return a;
	if (b < c) return b;
	return c;
}

int avoidObstacles(Robot *robot)
{
	double sonars[NUM_SONARS];
	double min_dist = 100000, min_angle = 0;

	// update view of world
	robot->readSonar(sonars);

	// find closest object, and angle to it
	for (int i=0; i < NUM_SONARS; i++) {
		if (sonars[i] < min_dist) {
			min_dist = sonars[i];
			min_angle = angles[i];
		}
	}

	// something is really close, so stop and wait until object moves
	// don't check the camera information
	if (min_dist < SAFE_DISTANCE) {
		robot->stop();
		return 1;
	}

	// nothing around me, so drive forward
	// and check the camera
	if (min_dist > EMPTY_SPACE_DIST) {
		robot->vel2(SPEED, SPEED);
		return 0;
	}

	// ignore obstacles right behind me by driving forward
	if (abs(min_angle) == 130 ||
		abs(min_angle) == 150 ||
		abs(min_angle) == 170) {
		robot->vel2(SPEED, SPEED);
		return 0;
	}

	// turn away from close object
	if (min_angle < 0)
		robot->vel2(SPEED, -SPEED);
	else
		robot->vel2(-SPEED, SPEED);

	return 1;
}

int avoidPink(BlobTracking *bt, Robot *robot)
{
	if (bt->getBiggestBlob() > 0) { // saw something pink
		if (bt->getXCoord() < 320)
			robot->rotate(-10);
		else
			robot->rotate(10);

		return 1;
	}

	return 0;
}

int main(int argc, char **argv)
{
	// setup the robot
	Robot *robot = new Robot();
	robot->connect(argv[1]);
	sleep(6);
	robot->enable_sonar(true);
	robot->enable_motor(true);

	// setup the camera to track pink
	BlobTracking *bt = new BlobTracking(true, 141, 100, 225);

	while (1) {
		bt->update();
		bt->display();

		// break on ESC key press
		if (cvWaitKey(10) == 27)
			break;

		// avoid any obstacles first
		if (avoidObstacles(robot))
			continue;

		if (avoidPink(bt, robot))
			continue;
	}

	robot->disconnect();
	delete bt;

	return 0;
}
