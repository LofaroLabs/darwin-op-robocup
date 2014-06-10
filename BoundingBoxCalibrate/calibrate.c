#include "easysocket.h"
#include <errno.h>
#include <string.h>

#define WIDTH 640
#define HEIGHT 480

int main()
	{
	char buffer[WIDTH * HEIGHT];
	
	server_type = SERVER_TYPE_SERIAL;
	int fd;
	while(1)
		{
		fd = wait_on_socket();
		if (fd < 0)
			{
			printf("Can't connect: %s\n", strerror(errno));
			exit(1);
			}
		
		// do the call
		// callDavesFunction(buffer, WIDTH * HEIGHT);
		
		writen(fd, buffer, WIDTH * HEIGHT);
		//writen(fd, "Sorry, I can't do that, Dave\n", strlen("Sorry, I can't do that, Dave\n"));
		close(fd);
		}
	}