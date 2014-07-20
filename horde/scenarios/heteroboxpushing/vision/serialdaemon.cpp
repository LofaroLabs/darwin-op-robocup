/*
 Copyright 2010 Sean Luke and George Mason University

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

#include <fcntl.h>
#include <stdio.h>
#include <errno.h>
#include <string.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>

#include "serialdaemon.h" 
#include <boost/thread.hpp>

using namespace std;

Serialdaemon::Serialdaemon(char *serial, int port, char* baud, bool debug)
{
	strcpy(SERIAL, serial);
	SOCKET_PORT = port;
	BAUD = parseBaudRates(baud);
	STRIPCR = 0;
	STRIPLF = 0;
	NONBLOCK = 0;
	AUX_PORT = 0;

	/* File Descriptors */
	sockfd = NOTHING;
	auxfd = NOTHING;
	auxfd_pre = NOTHING;
	serialfd = NOTHING;
	sockfd_pre = NOTHING;

	/* Debugging values */
	INDEBUG = 0;
	OUTDEBUG = 0;

	if (debug) INDEBUG = OUTDEBUG = 1;

}

/* baudrate settings are defined in <asm/termbits.h>, 
 * which is included by <termios.h> 
 */

char* Serialdaemon::see_speed(speed_t speed)
{
	static char SPEED[20];
	switch (speed) {
	case B9600:
		strcpy(SPEED, "B9600");
		break;
	case B19200:
		strcpy(SPEED, "B19200");
		break;
	case B38400:
		strcpy(SPEED, "B38400");
		break;
	case B115200:
		strcpy(SPEED, "B115200");
		break;
	case B230400:
		strcpy(SPEED, "B230400");
		break;
#ifndef __APPLE__
	case B460800:
		strcpy(SPEED, "B460800");
		break;
	case B500000:
		strcpy(SPEED, "B500000");
		break;
#endif
	default:
		sprintf(SPEED, "unknown (%d)", (int) speed);
		break;
	}
	return SPEED;
}

int Serialdaemon::parseBaudRates(char* baudChars)
{
	int result = 0;
	int tmp = atoi(baudChars);
	switch (tmp) {
#ifndef __APPLE__
	case 500000:
		result = B500000;
		break;
	case 460800:
		result = B460800;
		break;
#endif
	case 230400:
		result = B230400;
		break;
	case 115200:
		result = B115200;
		break;
	case 57600:
		result = B57600;
		break;
	case 38400:
		result = B38400;
		break;
	case 19200:
		result = B19200;
		break;
	case 9600:
		result = B9600;
		break;
	default:
		printf("ERROR!: Unknown baud rate.\n");
		break;
	}
	return result;
}

/* Print for Debugging */
void Serialdaemon::printDebugString(char* str, int len)
{
	int x;
	for (x = 0; x < len; x++) {
		if (str[x] >= 32 && str[x] < 127) putchar(str[x]);
		else
			putchar('?');
	}
	printf("\n                 ");
	for (x = 0; x < len; x++) {
		printf("%d ", str[x] >= 0 ? str[x] : str[x] + 256);
	}
	printf("\n");
}

/*  Reads into ptr maxlen bytes from a socket or file descriptor
 *  or until encountering a '\n', whichever comes first.
 *  '\n' is included at the end of ptr, followed by '\0' (of course).
 *
 *  read fills ptr with the data, and returns either
 *  a read error (<0) or the number of bytes that were
 *  actually read (if this differs from maxlen, it's because
 *  an eof was encountered, or because a '\n' was found).
 *
 *  fd is the file or socket descriptor
 *  ptr is the data to write, maxlen bytes long
 *  maxlen is the maximum size of the data
 *
 *  Stolen straight from Stallings 
 */
int Serialdaemon::readline(int fd, char* ptr, int maxlen)
{
	int n, rc;
	char c;
	for (n = 1; n < maxlen; n++) {
		if ((rc = read(fd, &c, 1)) == 1) {
			*ptr++ = c;
			if (c == '\n') break; /*  End Of Line */
		}
		else if (rc == 0) {
			if (n == 1) return 0; /*  EOF, No Data Read */
			else
				break; /*  EOF, Some Data Was Read */
		}
		else
			return -1; /*  Error! */
	}
	*ptr = 0;
	return n;
}

int Serialdaemon::makeSocket(int port)
{
	int sockfd; //, sd, childpid;
	struct sockaddr_in serv_addr;

	if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
		fprintf(stderr, "Server Error:  Can't open stream socket.\n");
		return -1;
	}

	bzero((char*) &serv_addr, sizeof(serv_addr));
	serv_addr.sin_family = AF_INET;
	serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
	serv_addr.sin_port = htons(port);

	if (bind(sockfd, (struct sockaddr*) &serv_addr, sizeof(serv_addr)) < 0) {
		fprintf(stderr, "Server Error:  Can't bind to local address.\n");
		return -1;
	}

	listen(sockfd, 5);
	return sockfd;
}

int Serialdaemon::makeSerialPortFd(char port[], int baud)
{
	int fd;
	struct termios newtio;
	fd = open(port, O_RDWR); // open up the port on read / write mode
	if (fd == -1) return (-1); // Opps. We just has an error

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

	/* NO FLAGS AT ALL FOR INPUT CONTROL -- Sean */
	newtio.c_iflag = 0;

	/* NO FLAGS AT ALL FOR OUTPUT CONTROL  -- Sean */
	newtio.c_oflag = 0;

	/* NO FLAGS AT ALL FOR LFLAGS  -- Sean*/
	newtio.c_lflag = 0;

	/* Clean the modem line and activate new port settings */
	tcflush(fd, TCIOFLUSH);
	tcsetattr(fd, TCSANOW, &newtio);

	return (fd);
}

int Serialdaemon::waitOnSocket(int sockfd)
{
	struct sockaddr_in cli_addr;
	unsigned int clilen = sizeof(cli_addr);
	int sd;

	sd = accept(sockfd, (struct sockaddr *) &cli_addr, &clilen);

	if (sd < 0) {
		fprintf(stderr, "Server Error:  Accept error.\n");
		return -1;
	}

	return sd;
}

void Serialdaemon::auxShiftBaud(char* cmd)
{
	int newBaud = parseBaudRates(cmd);

	/* Save the current serial port settings */
	struct termios newtio;
	tcgetattr(serialfd, &newtio);

	/* Set the input/output baud rates for this device */
	cfsetispeed(&newtio, newBaud);
	cfsetospeed(&newtio, newBaud);

	/* Clean the modem line and activate new port settings */
	tcflush(serialfd, TCIOFLUSH);
	tcsetattr(serialfd, TCSANOW, &newtio);

	if (tcgetattr(serialfd, &newtio) != 0) {
		printf("ERROR: Bad termoios; Rate change may have failed?\n");
		return;
	}

	if (OUTDEBUG) {
		printf("DEBUG: changed to %s\n", see_speed(cfgetispeed(&newtio)));
	}
}

void Serialdaemon::closeAll()
{
	if (sockfd != NOTHING) close(sockfd);
	if (sockfd_pre != NOTHING) close(sockfd_pre);
	if (auxfd != NOTHING) close(auxfd);
	if (auxfd_pre != NOTHING) close(auxfd_pre);
	if (serialfd != NOTHING) close(serialfd);
}

int Serialdaemon::max(int a, int b)
{
	return (a > b ? a : b);
}

int Serialdaemon::run()
{
	fd_set rset;
	//struct timeval timeout;
	char c[BUFFER_SIZE];
	int csize;

	//int mode;
	int x;
	int y;
	//int args = 0;
	//int tmp;
	int result;
	int blockerror = 0;

	if (INDEBUG || OUTDEBUG) printf("DEBUG: debug mode on!\n");

	sockfd_pre = makeSocket(SOCKET_PORT);
	if (sockfd_pre <= 0) {
		printf("ERROR: couldn't make TCP/IP socket!\n");
		closeAll();
		return -1;
	}

	if (AUX_PORT != 0) {
		auxfd_pre = makeSocket(AUX_PORT);
		if (auxfd_pre <= 0) {
			printf("ERROR: couldn't make TCP/IP socket!\n");
			closeAll();
			return -1;
		}
	}

	serialfd = makeSerialPortFd(SERIAL, BAUD);
	if (serialfd <= 0) {
		printf("ERROR: couldn't open serial port!\n");
		closeAll();
		return -1;
	}

	printf("Listening for data connections on port: %i\n", SOCKET_PORT);
	if (AUX_PORT != 0) {
		printf("Listening for aux  connections on port: %i\n", AUX_PORT);
	}

	try {

		while (1) {
			boost::this_thread::interruption_point();

			/* Wait for connection on data socket */
			sockfd = waitOnSocket(sockfd_pre);
			if (INDEBUG || OUTDEBUG) printf("DEBUG: New data socket opened.\n");
			if (sockfd < 0) {
				closeAll();
				return -1;
			}

			/* Set data socket to non-blocking */
			if (NONBLOCK) {
				if (fcntl(sockfd, F_SETFL, O_NONBLOCK) != 0) {
					printf("ERROR: couldn't make TCP/IP socket non-blocking!\n");
					closeAll();
					return -1;
				}
			}

			/* Wait for connection on AUX socket (if specified) */
			if (auxfd_pre != NOTHING) {
				auxfd = waitOnSocket(auxfd_pre);
				if (INDEBUG || OUTDEBUG) printf("DEBUG: New aux  socket opened.\n");
				if (auxfd < 0) {
					closeAll();
					return -1;
				}
			}

			/* Must have a serial file descriptor (else, why are we running?) */
			if (serialfd < 0) {
				closeAll();
				return -1;
			}

			FD_ZERO(&rset);

			/* Main Loop */
			while (1) {
				boost::this_thread::interruption_point();

				/* Add connections to set */
				FD_SET(sockfd, &rset);
				if (auxfd != NOTHING)
				FD_SET(auxfd, &rset);
				FD_SET(serialfd, &rset);

				/* Select on connection */
				select(max(max(sockfd, auxfd), serialfd) + 1, &rset, NULL, NULL, NULL);

				/* There's stuff to read on AUX */
				if (FD_ISSET(auxfd,&rset)) {
					if ((csize = readline(auxfd, c, BUFFER_SIZE)) >= 1) {
						c[csize] = '\0'; // after length, so no problem
						char cmd = c[0];
						if (c[1] != ' ') printf("ERROR!: Malformed AUX command; ignoring\n");
						char* data = &c[2];
						switch (cmd) {
						case 'B':
							if (INDEBUG) {
								printf("DEBUG: AUX baud change\n");
							}
							auxShiftBaud(data);
							break;
						default:
							printf("ERROR!: Unknown AUX command; ignoring\n");
							break;
						}
					}
					else
						break; /* Failed */
				}

				/* There's stuff to read on SOCKET */
				if (FD_ISSET(sockfd,&rset)) {
					if ((csize = read(sockfd, c, BUFFER_SIZE)) >= 1) {
						y = csize;
						if (STRIPCR == 1) {
							for (x = 0, y = 0; x < csize; x++, y++) {
								if (c[x] == 13) { // get rid of it
									y--;
									if (OUTDEBUG) printf("DEBUG: **STRIPPED CR**\n");
								}
								else
									c[y] = c[x];
							}
						}
						if (STRIPLF == 1) {
							for (x = 0, y = 0; x < csize; x++, y++) {
								if (c[x] == 10) { // get rid of it
									y--;
									if (OUTDEBUG) printf("DEBUG: **STRIPPED LF\n");
								}
								else
									c[y] = c[x];
							}
						}
						if (OUTDEBUG) {
							c[y] = '\0'; // after length, so no problem
							printf("DEBUG: serial <==");
							printDebugString(c, y);
						}
						result = write(serialfd, c, y);
						if (OUTDEBUG) {
							printf("DEBUG: wrote %d/%d\n", result, y);
						}
					}
					else
						break; /* Failed */
				}

				/* There's stuff to read on SERIAL */
				if (FD_ISSET(serialfd,&rset)) {
					if ((csize = read(serialfd, c, BUFFER_SIZE)) >= 1) {
						if (STRIPCR == 1) {
							for (x = 0; x < csize; x++) {
								if (c[x] == 13) { // get rid of it
									c[x] = 10;
									if (OUTDEBUG) printf("DEBUG: **STRIPPED CR**\n");
								}
								else
									c[y] = c[x];
							}
						}
						if (STRIPLF == 1) {
							for (x = 0; x < csize; x++) {
								if (c[x] == 10) { // get rid of it
									c[x] = 13;
									if (OUTDEBUG) printf("DEBUG: STRIPPED LF\n");
								}
								else
									c[y] = c[x];
							}
						}
						if (INDEBUG) {
							c[csize] = '\0'; // after length, so no problem
							printf("DEBUG: serial ==>");
							printDebugString(c, csize);
						}
						result = write(sockfd, c, csize);
						if (result == EWOULDBLOCK) {
							if (!blockerror) {
								blockerror = 1;
								printf("ERROR: dropping bytes writing to socket\n");
							}
						}
						else if (INDEBUG) {
							printf("DEBUG: read %d/%d\n", result, csize);
						}
					}
					else
						break; /* Failed */
				}
			}

			/* Restart connection-wait loop */
			printf("Restarting\n");
			close(sockfd); /* clean up */
		}
	} catch (const boost::thread_interrupted& ex) {
		closeAll();
	}

	std::cout << "..Serialdaemon (" << SERIAL << ") exit..";

	return 0;
}
