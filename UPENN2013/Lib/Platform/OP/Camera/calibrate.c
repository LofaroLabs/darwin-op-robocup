#include "easysocket.h"
#include <errno.h>
#include <string.h>
#include "lua_OPCam.cpp"
#define WIDTH 640
#define HEIGHT 480

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
		
		// do the call
		// callDavesFunction(buffer, WIDTH * HEIGHT);
		lua_take_save_images(buffer);	
	        lua_take_save_images(buffer);	
		lua_take_save_images(buffer);	

		
		
		writen(fd, (char*)buffer, WIDTH * HEIGHT*2); // had to half to make faster
		//writen(fd, "Sorry, I can't do that, Dave\n", strlen("Sorry, I can't do that, Dave\n"));
		close(fd);
		}
	}
