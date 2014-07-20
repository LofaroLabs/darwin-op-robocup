#ifndef __LASERDAEMON_H__
#define __LASERDAEMON_H__

class LaserDaemon
{
public:
	LaserDaemon(char *, int);
	~LaserDaemon();

	void run();

private:
	char device[128];
	int port;
};

#endif

