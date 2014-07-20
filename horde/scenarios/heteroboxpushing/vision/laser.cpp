#include "laser.h"

#include <iostream>
#include <sstream>
#include <string>
#include <exception>
#include <stdexcept>

#include "boost/date_time/local_time/local_time.hpp"
#include <queue>

using namespace std;

Laser::Laser(char* laser, int baud_rate) :
	lines(),  gVersionInfo(), gData(),  io(), serial(io, laser)
{

	if (!serial.is_open()) {
		cerr << "Failed to open serial port" << endl;
		cerr << strerror(errno) << endl;
		exit(-1);
	}

	serial.set_option(boost::asio::serial_port_base::baud_rate(baud_rate));
	gDataExpireTime = 0;
	gScanTime = 100;
	gAngleOffset = 0;
	max_range = 4095;

	BAUD_RATES[BAUD_019200] = BAUD_019200;
	BAUD_RATES[BAUD_057600] = BAUD_057600;
	BAUD_RATES[BAUD_115200] = BAUD_115200;
	BAUD_RATES[BAUD_250000] = BAUD_250000;
	BAUD_RATES[BAUD_500000] = BAUD_500000;
	BAUD_RATES[BAUD_750000] = BAUD_750000;

	enable();

}

Laser::~Laser()
{
	disable();
	serial.close();
}

/**
 * Enable the laser. Note, an error is generated if you try to enable the laser if it is already enabled, or disable it if it's already disabled.
 * It logs an "Error" warning to stderr, but does not throw an exception.
 */
void Laser::enable()
{
	doLaserCommand(true);
}

/**
 * Disable the laser. Note, an error is generated if you try to enable the laser if it is already enabled, or disable it if it's already disabled.
 * It logs an "Error" warning to stderr, but does not throw an exception.
 */
void Laser::disable()
{
	doLaserCommand(false);
}

/**
 * Set the baud rate using one of the BAUD_n constants. This function has no effect when using USB for communication.
 */
void Laser::setBaud(string baud)
{
	doSettingsCommand(baud);
}

/** Returns static vendor information from laser. */
string Laser::getVendorInfo()
{
	// no need to synchronized this, because doVersionCommand is
	// already synchronized.  It is possible that two version requests
	// could come in with gVersionInfo null, which will result in two
	// (safely) sequential calls to doVersionCommand(), which could over-
	// write gVersionInfo.  But that's okay, because it will point to
	// the old valid one, or the new valid one, but never to an invalid
	// one.
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_VENDOR];
}

/** Returns static product information from laser. */
string Laser::getProductInfo()
{
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_PRODUCT];
}

/** Returns static firmware version from laser. */
string Laser::getFirmwareVersion()
{
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_FIRMWARE];
}

/** Returns static protocol version from laser. */
string Laser::getProtocolVersion()
{
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_PROTOCOL];
}

/** Returns static sensor serial number from laser. */
string Laser::getSensorSerialNumber()
{
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_SERIAL];
}

/** Returns undocumented static status field laser. */
string Laser::getSensorStatus()
{
	if (gVersionInfo.size() == 0)
		gVersionInfo = doVersionCommand();
	return gVersionInfo[KEY_STATUS];
}

/**
 * Returns the range detected at the given angle, measured in degrees, and within the angular range of the sensor, -120 degrees to +120 degrees.
 * Zero degrees is forward, -120 degrees is back and to the right, +120 degrees is back and to the left.
 *
 * According to the Hokuyo data sheet, the scan time is 100ms / scan. That means, we can cache data for up to that long.
 *
 * If you want to collect a whole set of data at a time, call doDistanceCommand() directly.
 */
int Laser::getRange(double angle)
{
	angle -= gAngleOffset; // convert to proper zero-forward format
	//if (angle < -180)
	//	angle += 360; // wrap particularly negative values
	if (angle < -120 || angle > 120) {
		std::ostringstream out;
		out << "Angle out of range: " << angle;
		throw invalid_argument(out.str());
	}
	int slot = (int) ((angle + 135) * 1024 / 360.0) - 44;

	timeval tim;
	gettimeofday(&tim, NULL);

	if (getCurrentTimeMillis() > gDataExpireTime) {
		gData = doDistanceCommand(44, 725, 1);
		gDataExpireTime = getCurrentTimeMillis() + gScanTime;
	}

	if (slot < 0)
		slot = 0;
	if (slot >= (int)gData.size())
		slot = gData.size() - 1;
	return gData[slot];
}

long long Laser::getCurrentTimeMillis()
{
	using namespace boost::gregorian;
	using namespace boost::local_time;
	using namespace boost::posix_time;

	ptime time_t_epoch(date(1970, 1, 1));
	ptime now = microsec_clock::local_time();
	time_duration diff = now - time_t_epoch;
	return diff.total_milliseconds();
}

/**
 * This method specifies the number of milliseconds per scan of the laser. This defaults to 100ms, as specified by the Hokuyo data sheet, however
 * you may have a faster laser (or a slower one), so you can adjust the scan time here.
 */
void Laser::setScanTime(long l)
{
	gScanTime = l;
}

long Laser::getScanTime()
{
	return gScanTime;
}

/**
 * The Hokuyo laser ranger considers forward to be zero degrees, however, some systems, such as the sensor array on the Pioneer robots, consider
 * zero degrees to be in other locations (for the Pioneers, that would be on the right). This method allows you to "set" the zero direction. The
 * argument to this method tells the API what angle from forward you want to consider the zero angle. So to match the Pioneer, you would pass in
 * 90. Default is zero.
 */
void Laser::setAngleOffset(double d)
{
	gAngleOffset = d;
}

/**
 * Retreive the angle offset.
 */
double Laser::getAngleOffset()
{
	return gAngleOffset;
}

// ==== INTERNAL LASER INTERFACE VIA COM FUNCTIONS ====

/**
 * <p>
 * Internal method to return a map of version info from the laser. Most users should access this data via getVendorInfo(), getProductInfo(),
 * getFirmwareVersion(), getProtocolVersion(), and getSensorSerialNumber().
 * </p>
 *
 * <pre>
 * The keys are:
 * KEY_VENDOR   = "Vender Information" // sic
 * KEY_PRODUCT  = "Product Information"
 * KEY_FIRMWARE = "Firmware Version"
 * KEY_PROTOCOL = "Protocol Version"
 * KEY_SERIAL   = "Sensor Serial Number"
 * KEY_STATUS   = "Undocumented Status Field"
 *
 * The values are informational strings.
 * </pre>
 */
map<string, string> Laser::doVersionCommand()
{
	write(VERSION);
	write(LF);
	//flush();

	readCommand(VERSION);
	readLF();
	readStatus("Version"); // consumes LF

	map < string, string > versionMap;
	versionMap[KEY_VENDOR] = readString();
	versionMap[KEY_PRODUCT] = readString();
	versionMap[KEY_FIRMWARE] = readString();
	versionMap[KEY_PROTOCOL] = readString();
	versionMap[KEY_SERIAL] = readString();
	versionMap[KEY_STATUS] = readString();
	readLF(); // end of response

	return versionMap;
}

/**
 * Internal method to enable or disable the laser. Most users should use enable() or disable() methods. Note, an error is generated if you try to
 * enable the laser if it is already enabled, or disable it if it's already disabled. It logs an "Error" warning to stderr, but does not throw an
 * exception.
 */
void Laser::doLaserCommand(bool enable)
{
	write(LASER);
	write(enable ? LASER_ON : LASER_OFF);
	write(LF);
	//flush();

	readCommand(LASER);
	assertByte(readByte(), enable ? LASER_ON : LASER_OFF);
	readLF();
	readStatus("Laser"); // consumes LF
	readLF(); // end of response

}

/**
 * Internal method to configures the baud rate of the device. The argument is one of 019200, 057600, 115200, 250000, 500000, 750000. This method
 * has no effect when communicating via USB. Most users should use the method setBaud() for this function.
 */
void Laser::doSettingsCommand(string baud)
{
	if (BAUD_RATES.find(baud) == BAUD_RATES.end())
		throw new LaserException("Invalid baud rate: " + baud);

	write(SETTINGS);
	write(BAUD_RATES[baud]);
	write(RESERVED);
	write(LF);
	//flush();

	readCommand(SETTINGS);
	assertBytes(readBytes(6), BAUD_RATES[baud]);
	assertBytes(readBytes(7), RESERVED);
	// no LF here according to spec!
	readStatus("Settings"); // consumes LF
	readLF(); // end of response
}

/**
 * <p>
 * Internal method to request sensor data from the laser. The getRange() method provides simpler access to range data.
 * </p>
 * <p>
 * Starting and ending point range from 0..768. Cluster count is from 0..99.
 * </p>
 * <p>
 * Sensor measures range of 4095mm with 1mm resolution.
 * </p>
 * <p>
 * Starting and ending point refer to steps in the field of angular detection. This field is a range of 240 degrees, with a dead zone of 120
 * degrees at the rear. Step 0 is at -135 degrees from front (right), Step 384 is at 0 degrees (facing front), and Step 768 is at +135 degrees
 * (left). Step 0 and 768 are both in the deadzone. Measurable range is from Step 44 to Step 725. Each step has a resolution of 360deg/1024 =
 * 0.3515625 deg.
 * </p>
 * <p>
 * I assume that the "cluster" parameter, which refers to the number of neighboring points that are grouped as a cluster would reduce the amount
 * of data returned.
 * </p>
 * <p>
 * For example, if start is at "44" and end is at "725" and cluster is "01", then you would expect some 680 data points.
 * </p>
 * <p>
 * If cluster were "20", then you might expect only 34 data points, each of which is perhaps the average of a 7 degree arc?
 * </p>
 * <p>
 * Sensors will return values between 20mm and 4094mm. Anything less than 20mm will result in an error code at that data point.
 * </p>
 * <p>
 * Error codes for data points are (don't ask me what they mean):
 * </p>
 *
 * <pre>
 *  0    "Possibility of detected object is at 22m"
 *  1-5  "Reflected light has low intensity"
 *  6    "Possibility of detected object is at 5.7m"
 *  7    "Distance data on the preceding and succeeding steps have errors"
 *  8    "Others"
 *  9    "The same step had error in the last two scan"
 * 10-15 "Others"
 * 16    "Possibility of detected object is in the range 4096mm"
 * 17    "Others"
 * 18    "Unspecified"
 * 19    "Non-Measurable Distance"
 * </pre>
 */
vector<int> Laser::doDistanceCommand(int start, int end, int cluster)
{
	if (start < MIN_STEP || start > MAX_STEP || end < MIN_STEP || end > MAX_STEP || end < start || cluster < MIN_CLUSTER || cluster > MAX_CLUSTER) {
		std::ostringstream out;
		out << "Invalid distance parameters: start is " << start << " and end is " << end << ", which must be between " << MIN_STEP << " and "
				<< MAX_STEP << ", and cluster is " << cluster << ", which must be between " << MIN_CLUSTER << " and " << MAX_CLUSTER;
		throw LaserException(out.str());
	}

	write(DISTANCE);
	char ss[3];
	ss[0] = (char) (0x30 + (start / 100) % 10);
	ss[1] = (char) (0x30 + (start / 10) % 10);
	ss[2] = (char) (0x30 + start % 10);
	string ss_str(ss, 3);
	write(ss_str);
	char ee[3];
	ee[0] = (char) (0x30 + (end / 100) % 10);
	ee[1] = (char) (0x30 + (end / 10) % 10);
	ee[2] = (char) (0x30 + end % 10);
	string ee_str(ee, 3);
	write(ee_str);
	char cc[2];
	cc[0] = (char) (0x30 + (cluster / 10) % 10);
	cc[1] = (char) (0x30 + cluster % 10);
	string cc_str(cc, 2);
	write(cc_str);
	write(LF);

	readCommand(DISTANCE);
	assertBytes(readBytes(3), ss_str);
	assertBytes(readBytes(3), ee_str);
	assertBytes(readBytes(2), cc_str);

	readLF();
	readStatus("Distance"); // consumes LF

	// read data
	vector<int> ret;
	int count = 0;
	for (;;) {
		// each data point is encoded as a pair of ascii bytes
		int c1 = readByte();
		count++;
		if (c1 == LF)
			break; // break when you see an LF in the 65th spot
		int c2 = readByte();
		count++;
		int data = ((c1 - 0x30) << 6) | (c2 - 0x30);

		//cout << count << " : " << c1 << " " << c2 << " => " <<  data << endl;

		ret.push_back(data);
		if (count == 64) { // after 64 bytes...
			count = 0;
			readLF(); // consume a LF
		}
	}
	if (count < 64)
		readLF(); // if not on a boundary, we've got an extra LF
	return ret;
}

/**
 * Return the last error code byte generated by the laser and clear the code.
 */
char Laser::getLastError()
{
	char ch = gLastError;
	gLastError = 0;
	return ch;
}

// ==== UTILITY FUNCTIONS =========================

void Laser::write(string s)
{
	boost::asio::write(serial, boost::asio::buffer(s.c_str(), s.size()));
}

void Laser::write(char c)
{
	string s(1, c);
	write(s);
}

/*void Laser::write(char b)
 {
 try {
 gOutput.write(b);
 } catch (exception e) {
 throw new LaserException(e.what());
 }
 }

 void Laser::write(string b)
 {
 try {
 gOutput.write(b);
 } catch (exception e) {
 throw new LaserException(e.what());
 }
 }*/

/*void Laser::flush()
 {
 try {
 gOutput.flush();
 } catch (exception e) {
 throw new LaserException(e.what());
 }
 }*/

/**
 * Reads command from input, and assert that it is correct
 */
void Laser::readCommand(char expected)
{
	char got = readByte();
	assertByte(got, expected);
}

/**
 * Reads status from input, followed by LF, returns byte of status.
 */
void Laser::readStatus(string cmd)
{
	char ch = readByte();
	if (!checkStatus(ch)) {
		error(cmd, ch);
		gLastError = ch;
	}
	readLF();
}

/**
 * Consumes a byte, and asserts that it is an LF.
 */
void Laser::readLF()
{
	char lf = readByte();
	assertByte(lf, LF);
}

void Laser::assertByte(char got, char expected)
{
	if (got != expected) {
		//std::ostringstream out;
		//out << "Unexpected character input, received " << got << " instead of " << expected << ".";
		//throw LaserException(out.str());
		char buffer[128];
		sprintf(buffer, "Unexpected character input; received %d instead of %x. \n", got, expected);
		throw LaserException(string(buffer));
	}
}

void Laser::assertBytes(string got, string expected)
{
	if (got != expected) {
		std::ostringstream out;
		out << "Unexpected string input, received " << got << " instead of " << expected << ".";
		throw LaserException(out.str());
	}
}

/**
 * Reads a single byte of input.
 */
char Laser::readByte()
{

	try {
		char c;
		int ret = boost::asio::read(serial, boost::asio::buffer(&c, 1));
		if (ret != 1 || c < 0) {
			char buffer[128];
			sprintf(buffer, "Failed to read byte: %d %d", ret, c);
			throw LaserException(string(buffer));
			//ostringstream out;
			//out << "Failed to read a single byte: " << ret << " " << c;
			//throw LaserException(out.str());
		}

		return c;

	} catch (exception &e) {
		throw LaserException(e.what());
	}
}

/**
 * Reads the specified number of bytes of input.
 */
string Laser::readBytes(int n)
{
	try {
		string str = "";
		for (int i = 0; i < n; i++)
			str += readByte();
		return str;
	} catch (exception e) {
		throw new LaserException(e.what());
	}
}

/**
 * Returns true of status is okay (0x30), and false otherwise.
 */
bool Laser::checkStatus(char status)
{
	return status == STATUS_OK;
}

/**
 * Reads an ascii string (LF terminated).
 */
string Laser::readString()
{
	string str = "";
	char ch;
	for (;;) {
		ch = readByte();
		if (ch == LF)
			break;
		str += ch;
	}

	return str;
}

void Laser::error(string cmdName, char status)
{
	cerr << "Error: " << cmdName << " received status " << status << endl;
}

/**
 * If a data point less than 20 is returned, it corresponds to an error code as documented. This method returns the string associated with that
 * error code.
 */
string Laser::getErrorFromData(int c)
{
	switch (c) {
		case 0:
			return "Possibility of detected object is at 22m";
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return "Reflected light has low intensity";
		case 6:
			return "Possibility of detected object is at 5.7m";
		case 7:
			return "Distance data on the preceding and succeeding steps have errors";
		case 8:
			return "Others";
		case 9:
			return "The same step had error in the last two scan";
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
			return "Others";
		case 16:
			return "Possibility of detected object is in the range 4096mm";
		case 17:
			return "Others";
		case 18:
			return "Unspecified";
		case 19:
			return "Non-Measurable Distance"; // i.e. -/+135..-/+121
		default:
			return NULL;
	}
}

void Laser::computeLines()
{
	// use the split-merge algorithm to perform line extraction

	/*
	 Algorithm 1: Split-and-Merge
	 1 Initial: set s1 consists of N points. Put s1 in a list L
	 2 Fit a line to the next set si in L
	 3 Detect point P with maximum distance dP to the line
	 4 If dP is less than a threshold, continue (go to 2).  Otherwise, split si at P into si1 and si2, replace si in L by si1 and si2, continue (go to 2)
	 5 When all sets (segments) in L have been checked, merge collinear segments.
	 */

	lines.clear();

	queue < vector<Point2D> > candidates;

	// compute (x,y) points of ranges
	vector<Point2D> tmpPts;

	double delta = 100;  // in millimeters
	double last_range = -1;

	// figure out groups of points which are valid and consecutive points are within delta of each other
	for (int i = -120; i <= 120; i++) {
		double range = getRange(i);
		bool valid_range = (range > 20 && range < max_range);
		bool has_last = (last_range < 0 || (fabs(range - last_range) < delta));

		Point2D pt;
		if ( valid_range  && has_last)
		{
			double angle = (i - 90) * M_PI/180;
			Point2D pt(range * cos(angle), range * sin(angle));
			tmpPts.push_back(pt);
			last_range = range;
		}
		else if (!tmpPts.empty()) {
			if (tmpPts.size() > 1)
				candidates.push(tmpPts);
			tmpPts.clear();
			last_range = -1;
		}
	}

	if (!tmpPts.empty() && tmpPts.size() > 1)
		candidates.push(tmpPts);

	int loop_iter = 0;
	double billion = 1000000000;

	while (!candidates.empty() && loop_iter < 500) {
		loop_iter++;
		vector<Point2D> tmpPoints = candidates.front();
		candidates.pop();

		// fit line to tmpPoints using least squares
		double xSum = 0, ySum = 0, xSumSquared = 0, xySum = 0;
		double xMin = billion, xMax = -billion;
		int numPoints = tmpPoints.size();
		for (int i = 0; i < numPoints; i++) {
			Point2D pt = tmpPoints[i];
			xSum += pt.x;
			ySum += pt.y;
			xSumSquared += pt.x * pt.x;
			xySum += pt.x * pt.y;

			if (pt.x < xMin)
				xMin = pt.x;

			if (pt.x > xMax)
				xMax = pt.x;
		}

		double n = tmpPoints.size();
		double xAverage = xSum / n;
		double yAverage = ySum / n;
		double denom = xSumSquared - n * xAverage * xAverage;
		double a = (yAverage * xSumSquared - xAverage * xySum) / denom;
		double b = (xySum - n * xAverage * yAverage) / denom;

		Line tmpLine;
		tmpLine.start.x = xMin;
		tmpLine.start.y = b * xMin + a;
		tmpLine.end.x = xMax;
		tmpLine.end.y = b * xMax + a;

		// compute maximal distance of points in tmpPoints to computed line
		double deltaX = tmpLine.end.x - tmpLine.start.x;
		double deltaY = tmpLine.end.y - tmpLine.start.y;
		double distanceDenom = sqrt(deltaX * deltaX + deltaY * deltaY);

		double DISTANCE_THRESHOLD = 1500;
		double max_distance = DISTANCE_THRESHOLD;
		int max_index = -1;

		// find max distance from line
		for (int i = 0; i < (int) tmpPoints.size(); i++) {
			Point2D pt = tmpPoints[i];

			double numer = (deltaX * (tmpLine.start.y - pt.y) - deltaY * (tmpLine.start.x - pt.x));
			double distance = fabs(numer) / distanceDenom;

			if (distance > max_distance) {
				max_distance = distance;
				max_index = i;
			}
		}

		// split points around max distance from line
		if (max_index >= 0) {
			std::vector<Point2D> tmpPoints1;

			tmpPoints1.insert(tmpPoints1.end(), tmpPoints.begin(), tmpPoints.begin() + max_index);
			tmpPoints.erase(tmpPoints.begin(), tmpPoints.begin() + max_index);

			if (tmpPoints.size() > 1)
				candidates.push(tmpPoints);
			if (tmpPoints1.size() > 1)
				candidates.push(tmpPoints1);
		}
		else
			// otherwise, no split, so save line
			lines.push_back(tmpLine);
	}

	//if (loop_iter > 400)
	//	std::cout << loop_iter << std::endl;

}


double Laser::getAngleLongestLine()
{
	computeLines();

	int longest_idx = -1;
	double longest = 0;

	int numLines = lines.size();
	for (int i=0; i < numLines; i++)
	{
		double len = lines[i].length();
		if (len > longest)
		{
			longest_idx = i;
			longest = len;
		}
	}

	if (longest_idx >= 0) {
		distanceToLongestLine = longest;
		return lines[longest_idx].angle();
	}
	else
		return -100000;
}

double Laser::getDistanceLongestLine()
{
	return distanceToLongestLine;
}


