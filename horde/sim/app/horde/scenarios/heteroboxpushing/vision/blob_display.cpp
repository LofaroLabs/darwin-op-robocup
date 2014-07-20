#include "tracking.h" 

extern "C" {
#include "easysocket.h" 
};
 
using namespace std; 

int main(int argc, char **argv)
{

  BlobTracking *bt = new BlobTracking(false);

  cout << "Connected to camera" << endl; 

  if (argc > 1 && strcmp(argv[1], "-display") == 0) { 
    while (bt->isVisible()) { 
      bt->update(); 
      bt->display(); 
      if (cvWaitKey(10) == 27) 
	break;
    }

  }
  else { 

    // create socket for Java connection 
    server_type = SERVER_TYPE_PARALLEL;

    while (1) { 
      int fd = wait_on_socket();
      char buffer[64]; 
	
      while (bt->isVisible()) {
	bt->update();

	sprintf(buffer, "%d:%d:%d:%d\n", bt->getXmin(), bt->getYmin(), bt->getXmax(), bt->getYmax());
	writen(fd, buffer, strlen(buffer));
      }

      shutdown(fd, SHUT_RDWR); 
      close(fd);
      cout << "Restarting" << endl; 
    }
  }
  delete bt; 

  cout << "All done!" << endl; 
	
  return 0; 
}
