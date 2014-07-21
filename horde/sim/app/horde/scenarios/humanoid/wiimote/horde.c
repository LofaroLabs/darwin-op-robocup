
#include <stdio.h>
#include <stdlib.h>
#include <wiiuse.h>

#include "easysocket.h"

int currently_training = 0; 

#define STOP_BUTTON       0 
#define FORWARD_BUTTON    1
#define BACKWARD_BUTTON   2
#define LEFT_TURN_BUTTON  256
#define RIGHT_TURN_BUTTON 64
#define ONE_BUTTON  128
#define TWO_BUTTON 200
 
int handle_event(struct wiimote_t *wm)
{
  if (IS_JUST_PRESSED(wm, WIIMOTE_BUTTON_A)) { 
    currently_training = 1 - currently_training; 
  }

  int button = STOP_BUTTON; 

  //if (IS_PRESSED(wm, WIIMOTE_BUTTON_UP))
  //  button = FORWARD_BUTTON; 

  if (IS_PRESSED(wm, WIIMOTE_BUTTON_B)) { 
    if (IS_PRESSED(wm, WIIMOTE_BUTTON_LEFT)) { 
      button = FORWARD_BUTTON; 
    }
    else if (IS_PRESSED(wm, WIIMOTE_BUTTON_RIGHT)) { 
      button = FORWARD_BUTTON + 1000; 
    }
    }
  //else if (IS_PRESSED(wm, WIIMOTE_BUTTON_DOWN)) { 
   // button = BACKWARD_BUTTON; 
  //}
  else if (IS_PRESSED(wm, WIIMOTE_BUTTON_LEFT)) { 
    button = LEFT_TURN_BUTTON; 
  }
  else if (IS_PRESSED(wm, WIIMOTE_BUTTON_RIGHT)) { 
    button = RIGHT_TURN_BUTTON; 
  }
  else if (IS_PRESSED(wm, WIIMOTE_BUTTON_ONE)) { 
    button = ONE_BUTTON; 
  }
  else if (IS_PRESSED(wm, WIIMOTE_BUTTON_TWO)) { 
    button = TWO_BUTTON; 
  }

  return button; 
}
	
int main(int argc, char** argv) {
  wiimote** wiimotes;
  int found, connected;

  // init
  printf("[INFO] Looking for wiimotes (5 seconds)...\n");
  wiimotes =  wiiuse_init(1);

  // find wii (wait for 5 seconds)
  found = wiiuse_find(wiimotes, 1, 5);
  if (!found) {
    printf ("[INFO] No wiimotes found.\n");
    return 0;
  }

  // connect
  connected = wiiuse_connect(wiimotes, 1);
  if (connected)
    printf("[INFO] Connected to %i wiimotes (of %i found).\n", connected, found);
  else {
    printf("[ERROR] Failed to connect to any wiimote.\n");
    return 0;
  }

  // rumble and set leds
  wiiuse_set_leds(wiimotes[0], WIIMOTE_LED_1);
  wiiuse_rumble(wiimotes[0], 1);
  usleep(200000);
  wiiuse_rumble(wiimotes[0], 0);
  
  //set up socket to Java code
  server_type = SERVER_TYPE_SERIAL; 
  int fd = wait_on_socket(); 

  char write_buffer[128]; 
 
  int button=0; 
  currently_training = 0; 
  // continuously poll wiimote and handle events
  while (1) {
     if (wiiuse_poll(wiimotes, 1)) {
      switch (wiimotes[0]->event) {
      case WIIUSE_EVENT:
	button = handle_event(wiimotes[0]); 
	break;
      case WIIUSE_DISCONNECT:
      case WIIUSE_UNEXPECTED_DISCONNECT:
	goto exit;
	break;

      default:
	break;
	} 
      }

     usleep(50); 

    // tell Java code info about the wiimote
    sprintf (write_buffer, "%d:%d\n", currently_training, button); 
    writen(fd, write_buffer, strlen(write_buffer)); 
  }
 exit:
  wiiuse_cleanup(wiimotes, 1);
  close(fd); 

  return 0;
}
