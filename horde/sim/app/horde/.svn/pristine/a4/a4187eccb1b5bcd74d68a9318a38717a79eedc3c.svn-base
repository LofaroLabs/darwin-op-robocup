#include "laserdaemon.h"
#include "laser.h"
#include <iostream>
#include <boost/thread.hpp>

#include "easysocket.h"

using namespace std;

LaserDaemon::LaserDaemon(char *dev, int p)
{
	strcpy(device, dev);
	port = p;
}

LaserDaemon::~LaserDaemon()
{

}

void LaserDaemon::run()
{

	cout << "Connecting to laser" << endl;

	int fd = -1;
	Easysocket socket("localhost", port, SERVER_TYPE_PARALLEL);

	Laser *laser = new Laser(device, 115200);

	try {
		while (1) {
			fd = socket.wait_on_socket();
			socket.setFD(fd);
			char buffer[64];

			cout << "Starting to send laser data..." << endl;

			while (true)
			{
				boost::this_thread::interruption_point();
				sprintf(buffer, "%.2f:%.2f\n", laser->getAngleLongestLine(), laser->getDistanceLongestLine());
				socket.writen(buffer, strlen(buffer));
			}
			socket.close_socket();
			cout << "Restarting laser..." << endl;
		}
	} catch (const boost::thread_interrupted& ex) {
		if (laser)
			delete laser;
		laser = NULL;
		socket.close_socket();

	}

}
