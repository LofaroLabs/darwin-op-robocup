#include "camera.h" 
#include "tracking.h"

#include <iostream>
#include <boost/thread.hpp>
#include "easysocket.h"

using namespace std;

Camera::Camera(bool dis, int c_port)
{
	display = dis;
	port = c_port;
}

Camera::~Camera()
{

}

void Camera::run()
{

	BlobTracking *bt = new BlobTracking(display);

	cout << "Connected to camera" << endl;

	Easysocket *socket = NULL;

	try {

		if (display) {
			cout << "Starting display " << endl; 
			while (1) { // bt->isVisible()) {
				bt->update();
				bt->display();
				if (cvWaitKey(10) == 27) break;
				//boost::this_thread::interruption_point();
			}
		}
		else {
			// create socket for Java connection
			socket = new Easysocket("localhost", port, SERVER_TYPE_PARALLEL);

			while (1) {
				int fd = socket->wait_on_socket();
				socket->setFD(fd);
				char buffer[1024];

				cout << "Starting to send camera data " << endl; 
				while (1) {
					bt->update();
					boost::this_thread::interruption_point();
					string s = bt->getData();
					sprintf(buffer, "%s\n", s.c_str());
					socket->writen( buffer, strlen(buffer));
				}

				socket->close_socket();
				cout << "Restarting" << endl;
			}
		}
	} catch (const boost::thread_interrupted& ex) {
		if (bt)
			delete bt;
		bt = NULL;
		if (socket)
			socket->close_socket();
		socket = NULL;
	}

	if (bt != NULL)
		delete bt;

	cout << "..Camera exit.." << endl;;

}
