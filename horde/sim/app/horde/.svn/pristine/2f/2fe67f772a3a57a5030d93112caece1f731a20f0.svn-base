#include <iostream> 
#include <boost/thread.hpp> 
#include <termios.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/poll.h>
#include <signal.h>
#include <string.h>

#include "camera.h" 
#include "serialdaemon.h"
#include "laserdaemon.h"

using namespace std;
using namespace boost;

boost::thread robot_thread, camera_thread, laser_thread, keyboard_thread;

void death(int sig)
{
	exit(-1);
}

void close_vision()
{
	std::cerr << "Closing threads....";
	keyboard_thread.interrupt();
	camera_thread.interrupt();
	laser_thread.interrupt();
	robot_thread.interrupt();

	sleep(2); // wait for threads to actually close down
	std::cerr << "done!" << endl;
}

void process_keyboard()
{

	int kfd = 0;
	char c;
	struct termios raw, cooked;

	// get the console in raw mode
	tcgetattr(kfd, &cooked);
	memcpy(&raw, &cooked, sizeof(struct termios));
	raw.c_lflag &= ~(ICANON | ECHO);
	raw.c_cc[VEOL] = 1;
	raw.c_cc[VEOF] = 2;
	tcsetattr(kfd, TCSANOW, &raw);

	struct pollfd ufd;
	ufd.fd = kfd;
	ufd.events = POLLIN;

	bool isKeyboardRunning = true;

	try {
		while (isKeyboardRunning) {
			boost::this_thread::interruption_point();

			// get the next event from the keyboard
			int num;

			if ((num = poll(&ufd, 1, 250)) < 0) {
				perror("poll():");
				return;
			}
			else if (num > 0) {
				if (read(kfd, &c, 1) < 0) {
					perror("read():");
					return;
				}
			}

			switch (c) {
				case 0x71: // q or Q
				case 0x51:
					isKeyboardRunning = false;
					break;
			}
		}
	} catch (const boost::thread_interrupted& ex) {
		// ignore exception, but need to catch it so we
		// execute the code below to restore the keyboard
	}
	tcsetattr(kfd, TCSANOW, &cooked);
	cout << "..Keyboard exit..";
	exit(-1);
}

int main(int argc, char **argv)
{
	// register a function to be called at exit.  Also need to
	// register a function handle being Ctrl-C'ed
	atexit(close_vision);
	signal(SIGINT, &death);

	char robotSerial[100], robotBaud[100];
	int robotPort;
	bool robotDebug = false;
	char laserSerial[100];
	int laserPort;
	bool cameraDisplay = false;
	int cameraPort;

	// process command line arguments
	for (int x = 0; x < argc; x++) {
		if (strcmp(argv[x], "-robotserial") == 0) {
			strcpy(robotSerial, argv[x + 1]);
			x++;
		}
		else if (strcmp(argv[x], "-robotport") == 0) {
			robotPort = atoi(argv[x + 1]);
			x++;
		}
		else if (strcmp(argv[x], "-robotbaud") == 0) {
			strcpy(robotBaud, argv[x + 1]);
			x++;
		}
		else if (strcmp(argv[x], "-robotdebug") == 0) {
			robotDebug = true;
		}
		else if (strcmp(argv[x], "-laserserial") == 0) {
			strcpy(laserSerial, argv[x + 1]);
			x++;
		}
		else if (strcmp(argv[x], "-laserport") == 0) {
			laserPort = atoi(argv[x + 1]);
			x++;
		}
		else if (strcmp(argv[x], "-cameradisplay") == 0)
		{
			cameraDisplay = true;
		}
		else if (strcmp(argv[x], "-cameraport") == 0)
		{
			cameraPort = atoi(argv[x+1]);
			x++;
		}
	}

	Serialdaemon robot(robotSerial, robotPort, robotBaud, robotDebug);
	thread r(bind(&Serialdaemon::run, robot));
	robot_thread = boost::move(r);

/*	Camera camera(cameraDisplay, cameraPort);
	thread c(bind(&Camera::run, camera));
	camera_thread = boost::move(c);

	LaserDaemon laser(laserSerial, laserPort);
	thread l(bind(&LaserDaemon::run, laser));
	laser_thread = boost::move(l);

	//thread k(process_keyboard);
	//keyboard_thread = boost::move(k);

	//keyboard_thread.join();
	camera_thread.join();
	robot_thread.join();
	laser_thread.join();
*/
	robot_thread.join(); 
	std::cout << "ALL DONE! " << std::endl;
	return 0;
}
