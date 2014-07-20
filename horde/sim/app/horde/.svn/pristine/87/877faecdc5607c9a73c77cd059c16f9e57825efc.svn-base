#ifndef __LASER_H__
#define __LASER_H__

#include <string>
#include <vector>
#include <map>

#include <exception>
#include <boost/asio.hpp>

using namespace std;

/** Minimum step value for laser that corresponds with -135 degrees to the right of forward. */
#define MIN_STEP 0

/** Maximum step value for laser that corresponds with 135 degrees to the left of forward. */
#define MAX_STEP 768

/** Smallest number of clusters permitted. */
#define MIN_CLUSTER  0

/** Largest number of clusters permitted. */
#define MAX_CLUSTER 99

/** Vendor Information. */
#define KEY_VENDOR "Vender Information" // sic
/** Product Information. */
#define KEY_PRODUCT  "Product Information"
/** Firmware Version. */
#define KEY_FIRMWARE  "Firmware Version"
/** Protocol Version. */
#define KEY_PROTOCOL  "Protocol Version"
/** Sensor Serial Number. */
#define KEY_SERIAL  "Sensor Serial Number"
/** Undocumented Status Field. */
#define KEY_STATUS  "Undocumented Status Field"

/** 19.2kbps baud rate. */
#define BAUD_019200  "019200"
/** 57.6kbps baud rate. */
#define BAUD_057600  "057600"
/** 115.2kbps baud rate. */
#define BAUD_115200  "115200"
/** 250kbps baud rate. */
#define BAUD_250000  "250000"
/** 500kbps baud rate. */
#define BAUD_500000  "500000"
/** 750kbps baud rate. */
#define BAUD_750000  "750000"

#define RESERVED "0000000"

#define VERSION  'V'
#define LASER  'L'
#define SETTINGS  'S'
#define DISTANCE 'G'
#define LF  (char) 0x0a
#define LASER_ON (char) 0x31
#define LASER_OFF  (char) 0x30

// Known statuses so far...
// OK
#define STATUS_OK (char) 0x30
// Trying to enable a laser that is already enabled.
// private static final char STATUS_02 = (char)0x32;
// Trying to get data from a disabled laser.
// private static final char STATUS_06 = (char)0x36;


class LaserException: public exception
{
public:
	LaserException()
	{
		LaserException("");
	}

	LaserException(string msg)
	{
		message = msg;
	}

	~LaserException() throw ()
	{

	}

	const char * what() const throw ()
	{
		string m = "LaserException: " + message;
		return m.c_str();
	}

private:
	string message;
};

class Point2D
{
public:
	Point2D()
	{
		x = 0;
		y = 0;
	}
	Point2D(double t_x, double t_y)
	{
		x = t_x;
		y = t_y;
	}
	double x, y;

	friend ostream &operator<<(ostream &out, Point2D p) //output
	{
		out << "(" << p.x << ", " << p.y << ")";
		return out;
	}

	void scale(double scale)
	{
		x *= scale;
		y *= scale;
	}

	void shift(double s_x, double s_y)
	{
		x += s_x;
		y += s_y;
	}

};

class Line
{
public:
	Point2D start, end;

	friend ostream &operator<<(ostream &out, Line l) //output
	{
		out << l.start << " -> " << l.end;
		return out;
	}

	void scale (double scale)
		{
			start.scale(scale);
			end.scale(scale);
		}

	void shift(double x, double y)
	{
		start.shift(x,y);
		end.shift(x,y);
	}

	double length()
	{
		double deltaX = end.x - start.x;
		double deltaY = end.y - start.y;
		return sqrt(deltaX*deltaX + deltaY*deltaY);
	}

	double angle()
	{
		return atan2(end.y - start.y, end.x - start.x);
	}

};

class Laser
{
public:
	Laser(char*, int);
	~Laser();

	void disable();
	void enable();
	void setBaud(string);
	string getVendorInfo();
	string getProductInfo();
	string getFirmwareVersion();
	string getProtocolVersion();
	string getSensorSerialNumber();
	string getSensorStatus();
	int getRange(double);
	void setScanTime(long);
	long getScanTime();
	void setAngleOffset(double);
	double getAngleOffset();

	string getErrorFromData(int);

	vector<int> doDistanceCommand(int, int, int);

	void setMaxRange(int i)
	{
		max_range = i;
	}
	int getMaxRange()
	{
		return max_range;
	}

	void computeLines();
	vector<Line> lines;

	double getAngleLongestLine();
	double getDistanceLongestLine();

private:
	void doLaserCommand(bool);
	void doSettingsCommand(string);
	map<string, string> doVersionCommand();
	char getLastError();

	// utility functions
	void write(char);
	void write(string);
	void flush();
	void readCommand(char);
	void readStatus(string);
	void readLF();
	void assertByte(char, char);
	void assertBytes(string, string);
	char readByte();
	string readBytes(int);
	bool checkStatus(char);
	string readString();
	void error(string, char);
	long long getCurrentTimeMillis();

	map<string, string> gVersionInfo;
	vector<int> gData;
	long gDataExpireTime;
	long gScanTime;
	double gAngleOffset;
	char gLastError;

	int max_range;

	double distanceToLongestLine;

	boost::asio::io_service io;
	boost::asio::serial_port serial;

	map<string, string> BAUD_RATES;

};

#endif
