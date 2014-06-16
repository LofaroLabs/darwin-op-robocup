#include "easysocket.h"
#include <errno.h>
#include <string.h>
#include <fcntl.h>
#include "lua_OPCam.cpp"

#define WIDTH (640)
#define HEIGHT (480)

#define COMMAND_PICTURE (0)
#define COMMAND_LOOKUP (1)

// This is strictly less than WIDTH * HEIGHT
#define LOOKUP_TABLE_SIZE (262144) 

int main()
	{
	uint32 buffer[WIDTH * HEIGHT];
	
	server_type = SERVER_TYPE_SERIAL;
	int fd;
	turn_on_camera();
	while(1)
		{
		printf("i am trying to wait for a connection\n");
		fd = wait_on_socket();
		printf("i received a connection\n");
			if (fd < 0)
			{
			printf("Can't connect: %s\n", strerror(errno));
			exit(1);
			}

		// get the command
		readn(fd, buffer, 1);
		if (buffer[0] == COMMAND_PICTURE)
			{	
			// do the call
			// callDavesFunction(buffer, WIDTH * HEIGHT);
			lua_take_save_images(buffer);	
		    lua_take_save_images(buffer);	
			lua_take_save_images(buffer);	
			writen(fd, (char*)buffer, WIDTH * HEIGHT*2); // had to half to make faster
			}
		else if (buffer[0] == COMMAND_LOOKUP)
			{
			readn(fd, buffer, LOOKUP_TABLE_SIZE);
			int fd2 = fopen("/home/darwin/dev/merc/darwin/UPENN2013/Player/Data/lut_demoOP.raw", O_WRONLY | O_CREAT | O_TRUNC);
			writen(fd2, buffer, LOOKUP_TABLE_SIZE);
			close(fd2);
			printf("Lookup Table Written\n");
			// All LOOKUP_TABLE_SIZE bytes are now in the buffer.  process them here -- Sean
			// ...
			}
		else
			{
			printf("Unknown byte received: %d\n", (int)buffer[0]);
			}
		close(fd);
		}
	}
