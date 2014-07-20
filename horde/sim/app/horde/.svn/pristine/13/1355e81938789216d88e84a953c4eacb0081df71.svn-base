#ifndef __SERIALDAEMON_H__
#define __SERIALDAEMON_H__


#include <termios.h>
#include <stdio.h>
#include <stdlib.h>
#include <iostream>


#define NOTHING 0
#define BUFFER_SIZE 1024

using namespace std;

class Serialdaemon
{
public:
	Serialdaemon(char *serial, int port, char * baud, bool debug);
	~Serialdaemon() {} 
	
	int run();


private: 
	
	char *see_speed(speed_t speed) ; 
	int parseBaudRates(char* baudChars);
	void printDebugString(char* str, int len) ;
	int readline (int fd, char* ptr, int maxlen); 
	int makeSocket(int port);
	int makeSerialPortFd(char port[],int baud);
	int waitOnSocket(int sockfd);
	void auxShiftBaud(char* cmd); 
	void closeAll();
	int max(int a, int b);

	/* File Descriptors */
	int sockfd;
	int auxfd;
	int auxfd_pre;
	int serialfd;
	int sockfd_pre;

	/* Debugging values */
	int INDEBUG ;
	int OUTDEBUG ;

	int SOCKET_PORT;
	int BAUD;
	int STRIPCR;
	int STRIPLF;
	int NONBLOCK;
	int AUX_PORT;
	char SERIAL[100];

};


#endif 

