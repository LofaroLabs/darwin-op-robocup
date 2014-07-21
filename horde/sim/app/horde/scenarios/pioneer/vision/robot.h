#ifndef __ROBOT_H__
#define __ROBOT_H__ 

// values taken from ARIA parameter file
#define THPOS_CONVERSION 0.001534
#define CONTROL_CONVERSION  0.001534
#define SONAR_CONVERSION 0.555

#define NUM_SONARS 16

// number of microseconds to wait for sync response before timeout
#define SYNC_WAIT 500000 

// number of microseconds a thread should sleep for
#define THREAD_SLEEP 500000 

static unsigned char SYNC0_RESPONSE[] = { 250, 251, 3, 0, 0, 0 };
static unsigned char SYNC1_RESPONSE[] = { 250, 251, 3, 1, 0, 1 };
static unsigned char SYNC2_RESPONSE[] = { 250, 251 };

#include <iostream> 
#include <fstream> 

#include <sys/time.h>

#include <termios.h>
#include <fcntl.h> 
#include <errno.h> 
#include <string.h> 

#include <boost/thread.hpp>

using namespace std;
using namespace boost;

class Robot {

	public:
		Robot();
		~Robot();
		int connect(const char *);
		void disconnect();

		void print_packet();
		bool enable_motor(bool);
		bool enable_sonar(bool);

		bool vel2(int, int);

		bool drive(int);
		bool head(int);
		bool turn(int);
		bool rotate(int);
		bool move(int);

		bool resetOdometry();
		bool stop();
		bool emergency_stop();

		double getXPos()
		{
			mutex::scoped_lock l(m_mutex);
			return xPos;
		}
		double getYPos()
		{
			mutex::scoped_lock l(m_mutex);
			return yPos;
		}
		double getOrientation()
		{
			mutex::scoped_lock l(m_mutex);
			return orientation;
		}
		double getLeftWheelVelocity()
		{
			mutex::scoped_lock l(m_mutex);
			return leftWheelVelocity;
		}
		double getRightWheelVelocity()
		{
			mutex::scoped_lock l(m_mutex);
			return rightWheelVelocity;
		}
		int getBattery()
		{
			return battery;
		}
		bool getLeftWheelStallIndicator()
		{
			mutex::scoped_lock l(m_mutex);
			return leftWheelStallIndicator;
		}
		bool getRightWheelStallIndicator()
		{
			mutex::scoped_lock l(m_mutex);
			return rightWheelStallIndicator;
		}
		void readSonar(double *s)
		{
			mutex::scoped_lock l(m_mutex);
			for (int i=0; i < NUM_SONARS; i++)
				s[i] = sonars[i];
		}

	private:
		bool sync0();
		bool sync1();
		bool sync2();
		void read_packet();
		int compute_checksum(unsigned char[], const int);
		bool write_to_robot(unsigned char[], const int);

		unsigned char lowByte(int);
		unsigned char highByte(int);
		bool lookFor(unsigned char[], int, int);
		bool robot_open();
		bool robot_close();

		void pulse();

		int makeSerialPort(const char[], int);
		bool readBytes(int, int);
		int robotFd;

		void readString(unsigned char[]);

		char *robotName, *robotClass, *robotSubclass;
		double xPos, yPos, orientation;
		double sonars[NUM_SONARS];
		double leftWheelVelocity; // in mm/sec
		double rightWheelVelocity; // in mm/sec
		int battery;
		bool leftWheelStallIndicator;
		bool rightWheelStallIndicator;
		bool motorStatus;

		bool working;
		fd_set rset;
		timeval tv;
		thread read_thread, pulse_thread;
		mutex m_mutex;

		int oldX, oldY;
		double oldTheta;

};

#endif 

