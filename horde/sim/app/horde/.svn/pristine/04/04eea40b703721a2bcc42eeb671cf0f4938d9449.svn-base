#include "robot.h" 

#include <stdlib.h> 

using namespace std; 
using namespace boost; 

Robot::Robot()
{
	for (int i=0; i < NUM_SONARS; i++) 
		sonars[i] = -1; 
	working = true; 
	oldX = 0;
	oldY = 0;
	oldTheta = 0;
	xPos = 0;
	yPos = 0;
	orientation = 0;
} 

Robot::~Robot()
{
	disconnect(); 
} 

void Robot::disconnect()
{
	working = false; 
	robot_close(); 
	
	enable_motor(false); 
	enable_sonar(false); 
	
	pulse_thread.join(); 
	read_thread.join(); 
	
	close(robotFd); 
} 

int Robot::connect(const char *device_name)
{
	robotFd = makeSerialPort(device_name, B9600); 
	if (robotFd < 0) { 
		cout << "Error opening " << device_name << endl;
		cout << strerror(errno) << endl;  
		return -1;
	}
	
	FD_ZERO(&rset); 
	FD_SET(robotFd, &rset); 
	
	tv.tv_sec = 0; 
	tv.tv_usec = SYNC_WAIT; 


	// do a sync0, then a sync1, then a sync2
	bool a = true;
	do {
	    // close the controller if it's already open
	    robot_close();
	    if (a) a = false; else usleep(THREAD_SLEEP);
	    bool b = true;
	    do {
		if (b) b = false; else usleep(THREAD_SLEEP);
		bool c = true;
		do {
		    if (c) c = false; else usleep(THREAD_SLEEP);
		} while (!sync0());
	    } while (!sync1());
	} while (!sync2());

	unsigned char name[256], cls[256], subClass[256]; 
	readString(name); 
	readString(cls); 
	readString(subClass); 
	cout << "Name: " << name << "\t" << cls << "\t" << subClass << endl; 
	
	readBytes(2, SYNC_WAIT);  // checksum of string 
	
	robot_open(); 
	
	// start the heartbeat thread 
	thread t(bind(&Robot::pulse, this)); 
	pulse_thread = boost::move(t); 
	
	// start thread to continually read incoming packets 
	thread r(bind(&Robot::read_packet, this)); 
	read_thread = boost::move(r); 	
	
	cout << "Connected!" << endl; 
	return 0;
} 
	
void Robot::pulse()
{
	unsigned char c[] = { 0 }; 
	while (working) { 
		write_to_robot(c, 1);
		sleep(1);
	} 
		 
}

void Robot::print_packet()
{

} 

bool Robot::enable_motor(bool arg)
{
	unsigned char c[] = { 4, 0x3B, 0, 0}; 
	c[2] = (arg) ? 1 : 0; 
	return write_to_robot(c, 4); 
}

bool Robot::enable_sonar(bool arg)
{
	unsigned char c[] = { 28, 0x3B, 0, 0}; 
	c[2] = (arg) ? 1 : 0; 
	return write_to_robot(c, 4); 
} 

int Robot::makeSerialPort(const char port[],int baud)
{
    int fd; 
    struct termios newtio; 
    fd = open(port,O_RDWR); // open up the port on read / write mode
    if (fd == -1)
        return(-1); // Opps. We just has an error
        
    /* Save the current serial port settings */
    tcgetattr(fd, &newtio);
        
    /* Set the input/output baud rates for this device */
    cfsetispeed(&newtio, baud);
    cfsetospeed(&newtio, baud);

    /* CLOCAL:      Local connection (no modem control) */
    /* CREAD:       Enable the receiver */
    newtio.c_cflag |= (CLOCAL | CREAD);

    /* PARENB:      Use NO parity */
    /* CSTOPB:      Use 1 stop bit */
    /* CSIZE:       Next two constants: */
    /* CS8:         Use 8 data bits */
    newtio.c_cflag &= ~PARENB;
    newtio.c_cflag &= ~CSTOPB;
    newtio.c_cflag &= ~CSIZE;
    newtio.c_cflag |= CS8;

    /* Disable hardware flow control */
    // BAD:  newtio.c_cflag &= ~(CRTSCTS);

    /* ICANON:      Disable Canonical mode */
    /* ECHO:        Disable echoing of input characters */
    /* ECHOE:       Echo erase characters as BS-SP-BS */
    /* ISIG:        Disable status signals */
    // BAD: newtio.c_lflag = (ECHOK);

    /* IGNPAR:      Ignore bytes with parity errors */
    /* ICRNL:       Map CR to NL (otherwise a CR input on the other computer will not terminate input) */
    // BAD:  newtio.c_iflag |= (IGNPAR | ICRNL);
    newtio.c_iflag |= (IGNPAR | IGNBRK); 
        
    /* NO FLAGS AT ALL FOR OUTPUT CONTROL  -- Sean */
    newtio.c_oflag = 0;

    /* IXON:        Disable software flow control (incoming) */
    /* IXOFF:       Disable software flow control (outgoing) */
    /* IXANY:       Disable software flow control (any character can start flow control */
    newtio.c_iflag &= ~(IXON | IXOFF | IXANY);

    /* NO FLAGS AT ALL FOR LFLAGS  -- Sean*/
    newtio.c_lflag = 0;

    /*** The following settings are deprecated and we are no longer using them (~Peter) ****/
    // cam_data.newtio.c_lflag &= ~(ICANON && ECHO && ECHOE && ISIG); 
    // cam_data.newtio.c_lflag = (ECHO);
    // cam_data.newtio.c_iflag = (IXON | IXOFF);
    /* Raw output */
    // cam_data.newtio.c_oflag &= ~OPOST;

    /* Clean the modem line and activate new port settings */
    tcflush(fd, TCIOFLUSH);
    tcsetattr(fd, TCSANOW, &newtio);

    return(fd);
}

	
bool Robot::lookFor(unsigned char expected[], int length, int timeout)
{
	long starttime = clock(); 
	int count=0;
	unsigned char c[1]; 

	FD_SET(robotFd, &rset); 
	tv.tv_sec = 0; 
	tv.tv_usec = SYNC_WAIT; 
	
	while (count < length) {
		select(robotFd+1, &rset, NULL, NULL, &tv); 
		if (FD_ISSET(robotFd, &rset)) { 
			read(robotFd, c, 1); 
			if (c[0] == expected[count]) count++; 
			else count=0; 
		}  
		
		if (clock() - starttime >= timeout) { 
		  cout << "TIMEOUT" << endl; 
			return false; 
		}
	}

	return true; 
}

bool Robot::readBytes(int numBytes, int timeout) 
{
	long starttime = clock(); 
	int count=0;
	unsigned char c[1]; 
	int actual;

	FD_SET(robotFd, &rset); 
	tv.tv_sec = 0; 
	tv.tv_usec = SYNC_WAIT; 
	
	for (int i=0; i < numBytes; i++) { 
		select(robotFd+1, &rset, NULL, NULL, &tv); 
		if (FD_ISSET(robotFd, &rset)) 
			read(robotFd, c, 1); 
		if (clock() - starttime >= timeout) 
			return false; 
	}

	return true; 
}

void Robot::readString(unsigned char str[])
{
	unsigned char buf[1]; 
	int i=0; 
	
	FD_SET(robotFd, &rset); 
	tv.tv_sec = 0; 
	tv.tv_usec = SYNC_WAIT;
	
	while (true) { 
		select(robotFd+1, &rset, NULL, NULL, &tv);
		read(robotFd, buf, 1); 
		str[i] = buf[0]; 
		i++;
		if (buf[0] == 0) break;
	}
}
	
bool Robot::sync0()
{
	cout << "Waiting for SYNC0..." << endl; 
	unsigned char c[] = { 0 }; 
	bool b = write_to_robot(c, 1); 
	if (!b) return false; 
	return lookFor(SYNC0_RESPONSE, 6, SYNC_WAIT); 
} 

bool Robot::sync1()
{
	cout << "Waiting for SYNC1..." << endl; 
	unsigned char c[] = { 1 }; 
	bool b = write_to_robot(c, 1); 
	if (!b) return false; 
	return lookFor(SYNC1_RESPONSE, 6, SYNC_WAIT); 
} 

bool Robot::sync2()
{	
	cout << "Waiting for SYNC2..." << endl ;
	unsigned char c[] = { 2 };
	bool b = write_to_robot(c, 1); 
	if (!b) return false; 
	if (!lookFor(SYNC2_RESPONSE, 2, SYNC_WAIT)) return false; 
	
	// now read the two bytes automatically sent back by the SYNC2 command 
	return readBytes(2, SYNC_WAIT); 
} 

bool Robot::robot_close()
{
	unsigned char c[] = { 2 }; 
	return write_to_robot(c, 1); 
}

bool Robot::robot_open()
{
	unsigned char c[] = { 1 }; 
	return write_to_robot(c, 1); 
}

void Robot::read_packet()
{
	FD_SET(robotFd, &rset); 
	tv.tv_sec = 0; 
	tv.tv_usec = 1000000;
	select(robotFd, &rset, NULL, NULL, &tv); 
	
	while (working) { 
		if (lookFor(SYNC2_RESPONSE, 2, SYNC_WAIT)) { 
			// get the packet size 
			unsigned char temp[1];  
			read(robotFd, temp, 1); 
	
			int packet_length = temp[0]; 
			if (temp[0] < 0) 
				packet_length += 256;

			unsigned char packet[packet_length]; 
			unsigned char tmp[packet_length]; 
			int p=0, t; 

			while (p < packet_length) { 
				t = read(robotFd, tmp, packet_length); 
				for (int i=0; i < t; i++) 
					packet[p+i] = tmp[i]; 
				p += t; 
				if (p < packet_length) usleep(20000);
			}

			int computed_checksum = packet[packet_length-2]; 
			if (packet[packet_length-2] < 0) computed_checksum += 256; 
			computed_checksum *= 256; 
			computed_checksum += packet[packet_length-1]; 
			if (packet[packet_length-1] < 0) computed_checksum += 256; 
			 
			// do we have a valid packet? 
			if (compute_checksum(packet, packet_length-2) == computed_checksum) { 
				// motor status 
				if (packet[0] == 0x32) 
					motorStatus = false; 
				else if (packet[0] == 0x33)
					motorStatus = true; 
				else 
					cout << "Invalid motor status: " << packet[0] << endl; 
				 
				// xPos 
				int alpha = packet[2]; 
				if (packet[2] < 0) 
					alpha += 256;  
				alpha &= 0x7F; 
				alpha *= 256; 
				alpha += packet[1]; 
				if (packet[1] < 0) 
					alpha += 256; 
				if (alpha >= 16384) alpha -= 32768; 
				
				xPos += alpha - oldX;
				oldX = alpha;

				// yPos 
				alpha = packet[4]; 
				if (packet[4] < 0) 
					alpha += 256;  
				alpha &= 0x7F; 
				alpha *= 256; 
				alpha += packet[3]; 
				if (packet[3] < 0) 
					alpha += 256; 
				if (alpha >= 16384) alpha -= 32768; 
				yPos += alpha - oldY;
				oldY = alpha;
				 
				// orientation 
				alpha = packet[6]; 
				if (packet[6] < 0) alpha += 256;  
				alpha *= 256; 
				alpha += packet[5]; 
				if (packet[5] < 0) alpha += 256;
				if (alpha >= 2048) alpha -= 4096;
				orientation += THPOS_CONVERSION * alpha - oldTheta;
				oldTheta = THPOS_CONVERSION * alpha;
				 
				// left wheel velocity 
				alpha = packet[8]; 
				if (packet[8] < 0) alpha += 256;  
				alpha *= 256; 
				alpha += packet[7]; 
				if (packet[7] < 0) alpha += 256;
				if (alpha >= 32768) alpha -= 65536;  
				leftWheelVelocity = alpha; 
				
				// right wheel velocity 
				alpha = packet[10]; 
				if (packet[10] < 0) alpha += 256;  
				alpha *= 256; 
				alpha += packet[9]; 
				if (packet[9] < 0) alpha += 256;
				if (alpha >= 32768) alpha -= 65536;  
				rightWheelVelocity = alpha; 
				
				// wheel stalls 
				leftWheelStallIndicator = (packet[12]&0x80 != 0); 
				rightWheelStallIndicator = (packet[13]&0x80 != 0);
				
				 // read sonars 
				 int numSonars = packet[19]; 
				 for (int i=0; i < numSonars; i++) {
				 	int n_sonar = packet[20+3*i]; 
				 	alpha = packet[20+3*i+2]; 
				 	if (packet[20+3*i+2] < 0) alpha += 256; 
				 	alpha *= 256; 
				 	alpha += packet[20+3*i+1]; 
				 	if (packet[20+3*i+1] < 0) alpha += 256; 
				 	sonars[n_sonar] = alpha;
				 }
			}
			else 
				cout << "Invalid packet: " << compute_checksum(packet, packet_length-2) << " vs " << computed_checksum << endl;
		}
	}
}

int Robot::compute_checksum(unsigned char data[], const int length)
{
	int c=0, data1, data2;
	for(int x=0;x<length-1;x+=2)
	    {
	    data1 = data[x];  if (data1 < 0) data1 += 256;
	    data2 = data[x+1];  if (data2 < 0) data2 += 256;
	    c += ( (data1 << 8) | data2);
	    c = c & 0xffff;
	    }
	if ((length & 0x1) == 0x1)  // odd
	    {
	    data1 = data[length-1];  if (data1 < 0) data1 += 256;
	    c = c ^ data1;
	    }
	return c;
} 
	
bool Robot::write_to_robot(unsigned char data[], const int length)
{
	int actual; 
	// setup the actual packet to be writen 
	unsigned char temp[length + 5]; 
	int checksum = compute_checksum(data, length); 
	temp[0] = 0xFA; // Default header values for Pioneer 
	temp[1] = 0xFB; 
	temp[2] = length + 2;
	for (int x=0; x < length; x++) 
		temp[x+3] = data[x]; 
	temp[length + 3] = (checksum >> 8); 
	temp[length + 4] = (checksum & 0x00FF); 
	
	// write the data 
	if ((actual = write(robotFd, temp, length+5)) < 0) { 
		cout << "Error in write_to_robot: " << strerror(errno) << endl; 
		return false;
	}
	if (actual != length + 5) { 
		cout << "Wrote only " << actual << " bytes instead of "<< length +5 << " bytes" << endl;
		return false; 
	}
	
	return true; 
	
}
	
unsigned char Robot::lowByte(int b)
{
	return (b & 0xFF); 
} 

unsigned char Robot::highByte(int b)
{
	return ((b >> 8) & 0xFF); 
} 

bool Robot::move(int arg)
{
	unsigned char c[] = {8, 0x3B, lowByte(arg), highByte(arg) }; 
	if (arg < 0) { 
		c[1] = 0x1B; 
		c[2] = lowByte(-arg); 
		c[3] = highByte(-arg); 
	}
	
	return write_to_robot(c, 4); 
}

bool Robot::rotate(int arg)
{
	unsigned char c[] = {9, 0x3B, lowByte(arg), highByte(arg) }; 
	return write_to_robot(c, 4); 
}

bool Robot::drive(int arg)
{
	unsigned char c[] = {11, 0x3B, lowByte(arg), highByte(arg) }; 
	return write_to_robot(c, 4); 
}

/* turn to absolute heading in robot's belief, (+) counterclockwise, (-) clockwise */ 
bool Robot::head(int arg)
{
	unsigned char c[] = {12, 0x3B, lowByte(arg), highByte(arg) }; 
	return write_to_robot(c, 4); 
}

/* turn relative to current heading, in degrees (+) counterclockwise, (-) clockwise*/ 
bool Robot::turn(int arg)
{
	unsigned char c[] = {13, 0x3B, lowByte(arg), highByte(arg) }; 
	return write_to_robot(c, 4); 
}

bool Robot::vel2(int lspeed, int rspeed) 
{
	unsigned char c[] = {32, 0x3B, 0, 0}; 
	int l = lspeed; 
	int r = rspeed; 
	if (l < 0) l += 256; 
	if (r < 0) r += 256; 
	int speed = l << 8 | r; 
	c[2] = lowByte(speed); 
	c[3] = highByte(speed); 

	return write_to_robot(c, 4); 
}

bool Robot::resetOdometry()
{
	xPos = 0;
	yPos = 0;
	orientation = 0;
	return true;
}

bool Robot::stop()
{
	unsigned char c[] = { 29 };
	return write_to_robot(c,1);
}

bool Robot::emergency_stop()
{
	unsigned char c[] = { 55 };
	return write_to_robot(c,1);
}



