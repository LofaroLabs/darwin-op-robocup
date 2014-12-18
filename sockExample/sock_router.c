#include "sock_router.h"

/* This is a socket mailbox.  It's all example code, but it's designed
 *  to be run like a daemon, always on in the background.
 * Your programs send TCP data to the port for this router and
 *  it will receive the packet, parse it, and store it
 *  for retrieval, based on the rules defined.
 *
 * General Design - 
 *   Main thread opens a socket for listening
 *   Listener thread actively listens for data, processes data,
 *     and stores data for later retrieval, or retrieves data (peek or pop)  
 *   When you enter 2 on the main thread, it will close the listener
 *     and shut down.  When you ctrl-C, same thing.
 *   When you enter 1 on the main thread, it will print the mailbox data
 *
 *   Is this multi-threaded design really needed?  Sort of.  It allows
 *     the program to close the port and make it immediately available.
 *     Killing the program without doing this leaves the port held by
 *     the OS until it times out, which can take a while.
 */

/* Globals */

  /* Sockets, ports, and files in Linux are handled by File Descriptors.
   *  Opening a port will cause Linux to associate the logical port value
   *  with a file descriptor that is accessible by the process.
   *
   * So, why use an int to hold the socket I'm going to open?
   *  http://linux.die.net/man/3/socket
   * Always check the reference to find the return type of the
   *  function you will be calling and look at *ALL* of the possible
   *  return values.
   *
   * fd_ is a common prefix used for variables holding file descriptors.
   */
  int fd_socket = -1;

  /* Threads!  
   *  Ok, so this is a very, very simple example using threads.  Don't worry about 
   *  it too much.  This program is primarily an example for sockets and polling.
   *
   * This creates a thread structure to control one additional thread.
   */
   pthread_t listener_thread;
  /* Used to control when the listener should stop running and close */
   uint8_t is_running = 1;

  /* Yep, I'm handling packet mailboxing through a linked list! */
  PACKET_LIST *mailbox_head;

/* Function Definitions */

int main() 
{
  open_socket();
  
  /* This next line starts up a new thread.  To start a new thread, 
   *  you minimally need the name of the thread to start and the 
   *  name of the function that this thread will start running.
   *  This function is very special, it has to have a return value
   *  of void* and a parameter list of (void *args).  Don't worry about
   *  these for now. If you guys need to get thread-happy, let me know.
   *  and I'll write a thread-example program.
   *
   * http://linux.die.net/man/3/pthread_create
   *
   * The error codes here are all really nasty. No resources left sort of nasty.
   */
  if(pthread_create(&listener_thread, NULL, listener_function, NULL) != 0)
  {
    PRINTE("[E] Could not create a listener thread. Exiting.\n");
    PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));    
    exit(1);
  }
  printf("The server is now running.\n");

  /* Simple local menu to kill the router on command */
  int option = -1;
  do
  {
    printf("\n+---------------------------------\n");
    printf("| Main Menu\n");
    printf("+---------------------------------\n");
    printf("| 1) Print Stored Data\n");
    printf("| 2) Quit\n");
    printf("----------------------------------\n");
    fprintf(stderr, "Please enter your selection: ");
    scanf("%d", &option);
    /* Remove the newline from the input stream */
    while(getchar() != '\n');
    switch(option)
    {
      case 1:
        print_stored_data();
        break;
      case 2:
        printf("Shutdown Requested\n");
        break;
      default:
        printf("\nThat option is not supported.\n");
    }
  } while(option != 2);
  
  /* The router should shutdown, tell the thread to stop running */
  is_running = 0;

  /* Blocks the program until the listener thread had killed itself. */
  pthread_join(listener_thread, NULL);
  close_socket();
  return 0;
}

/* This opens the socket for RECEIVING packets from other
 *  systems or processes on the local system. 
 *
 * Sockets work by registering a port to receive packets on.
 *  There are two fundamental protocols used on sockets: TCP and UDP
 *  While there are many good reasons to use UDP, for our purposes, TCP
 *   is the way to go.  TCP guarantees packet delivery, in order.
 *  TCP is an active connection protocol.  What this means is that for 
 *   communications, a connection must be made.  The other end of this 
 *   connection specifies the destination system and port for outgoing
 *   communications.  
 *
 * This first function only concerns itself with opening the local port 
 *  for receiving all data on.
 */
void open_socket()
{
  PRINTV("[S] Socket Startup Sequence...\n");

  /* This opens the local socket under the TCP protocol.
   *  http://linux.die.net/man/3/socket
   * AF_INET specifies the Internet Address Family.
   * SOCK_STREAM specifies Connection-Based (TCP is its default protocol)
   *  SOCK_DGRAM is used for Connectionless (UDP is its default protocol)
   * The final parameter is the protocol argument.
   *  0 specifes the default protocol for the type.
   */
  fd_socket = socket(AF_INET, SOCK_STREAM, 0);

  /* This will seem really verbose and code-heavy, but the rule of thumb
   *  is that every function in C returns something.  You should look at
   *  each function's return values and handle them.
   */
  if(fd_socket == -1) /* Error Condition */
  {
    PRINTE("[E] Could not open the local socket. Exiting.\n");
    PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
    /* Obviously, if this was more complicated code, exiting directly might be really
     *  bad.  You usually want to return a bad code and let the next higher function 
     *  in the call stack clean stuff up.  For this, it's fine to just kill it.
     */
    exit(-1);
  }
  PRINTV("... Local Socket Created.\n");

  /* Create a structure to hold the local side of the connection */
  struct sockaddr_in local_address;
  local_address.sin_family = AF_INET; /* Use Internet Addressing */
  /* Accept connections from any (INADDR_ANY) IP address. */
  local_address.sin_addr.s_addr = htonl(INADDR_ANY);
  /* Specified in sock_router.h, this is our incoming data port,
   *  and what other processes should send data to. 
   *
   * htons() is a function that is critical in networking programming.
   *  Port numbers are stored as short ints (2 bytes on many systems).
   *  The Internet uses big endian ordering.  The local system typically
   *   uses little endian, but it could also be big endian.  htons() is a 
   *   cool function that converts a short int from your Host machine ordering 
   *   to Network ordering.  host to network short (HTONS).
   * Yep, this also means data coming from the internet should be converted back
   *  to host ordering.
   *
   * If you forget to convert your port to network order, then it'll get all messed
   *  up and nobody will be able to send anything to you.
   */
  local_address.sin_port = htons(ROUTER_PORT); 

  /* Now, this gets fun.  We have a port selected and our socket opened, but we 
   *  need to tell the OS to bind our socket with our selected port.
   *
   * http://linux.die.net/man/3/bind
   *
   * bind returns only two values.  0 = good, -1 = bad.
   *  Looking at the errors possible for bad stuff, there are a ton of em.
   *  There is one that will typically cause this though, EADDRINUSE, which
   *  means the port is not available for binding.  Again, usually this means 
   *  your program crashed and you tried to launch it again before the OS finished
   *  cleaning the ports up after it timed out.
   */
  int8_t port_bound = -1;
  uint8_t num_attempts = 0;
  /* Fun note: There are a lot of conditionals in here.  Do we care about optimizing this?
   *  Nope!  It only gets run once on startup. Better to have clear logic than saving 0.5ms once
   */
  do {
    num_attempts++;
    port_bound = bind(fd_socket, (struct sockaddr *)&local_address, sizeof(struct sockaddr)); 
    /* Even if there is an error, let's check to see if it's critical (not EADDRINUSE), 
     *  or if we've tried too many times (> BIND_MAX_ATTEMPTS)
     */
    if(port_bound == -1 && (errno != EADDRINUSE || num_attempts > BIND_MAX_ATTEMPTS))
    {
      PRINTE("[E] Could not bind %d to the local socket. Exiting.\n", ROUTER_PORT);
      PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
      /* Obviously, if this was more complicated code, exiting directly might be really
       *  bad.  You usually want to return a bad code and let the next higher function 
       *  in the call stack clean stuff up.  For this, it's fine to just kill it.
       */
      exit(-1);
    }
    /* If we're still here, then it either worked, or the port is in use. */
    if(port_bound == -1)
    {
      PRINTE("[E] Port %d is presently busy. Trying again in %d seconds.\n", ROUTER_PORT, BIND_TIMEOUT);
      sleep(BIND_TIMEOUT);
    }
  } while(port_bound == -1);
  PRINTV("... Local Port Bound to %d.\n", ROUTER_PORT);

  /* The final step is to mark this socket as available for incoming connections.
   *
   *  http://linux.die.net/man/3/listen
   *
   *  While the backlog argument doesn't directly correlate with max connections to
   *   support, it does very closely mirror that in practice.  This is indirectly, 
   *   but closely, tied to the listen queue.
   *  listen() returns 0 if everything is good, or -1 if bad stuff.  Don't read too much
   *   into this pattern.  A lot of standard C functions do return 0/-1, but the one you
   *   assume does will be the one where 0 is bad.  Always check the documentation first.
   *
   *  The bad stuff here is really pretty bad.  No expected bad thing is recoverable.
   */
  if(listen(fd_socket, MAX_CONNECTIONS) == -1)
  {
    PRINTE("[E] Could not set the local socket to listen. Exiting.\n");
    PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
  }
  PRINTV("... Local Socket is now Listening for Connections.\n");

  PRINTV("[S] Socket Startup Sequence...Complete.\n");
  return;
}

/* Attempts to close the local socket.
 *  http://linux.die.net/man/3/close
 * The return code is either 0 for Good or -1 for bad.
 *  Errors here are going to be really bad.
 */
void close_socket()
{
  PRINTV("[F] Socket Shutdown Sequence...\n");
  if(close(fd_socket) == -1)
  {
    PRINTE("[E] Could not close the local socket. Returning to the shutdown procedure.\n");
    PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
    exit(1);
  }
  PRINTV("[F] Socket Shutdown Sequence...Complete\n");
  return;
}

void *listener_function(void *args)
{
  /* Woah, there's no synchronized here or any mutexes?!?!
   *  There are only 2 shared variables: fd_socket, which is not physically
   *   possible for this to be touched by the main thread while this thread
   *   exists.  The other is is_running; this function is read-only for that one.
   *  Therefore, no need for mutexes.  Also, this isn't a thread
   *   demo program, so I'm trying to avoid them.
   */

  /* To begin with, we need to set up the file descriptor framework */
  struct pollfd fd_list[MAX_CONNECTIONS];   
  int fds_active;
  int max_fd = 0;

  /* Set up [0] to be the incoming socket port */
  fd_list[0].fd = fd_socket;
  fd_list[0].events = POLLIN;

  max_fd = 0;

  char buffer[1024];

  while(is_running == 1)
  {
    /* This is where it gets really cool.
     *
     * This loop should:
     *   0) block until an action occurs (totally off CPU)
     *   1) accept any and all connections
     *   2) accept data from all connections
     *   3) check the data for disposition
     *    3a) if data should be stored, do so
     *    3b) if data is a request for stored data, fetch and deliver
     *   4) if no data is received, check is_running and then block for new data again.
     *
     * Now, I'm not designing this for RoboCup or any other real application.  This is just
     *   example code on how to work with sockets.
     *   For Storing:   "action,code,length_of_data,data"
     *   For Accessing: "action,code,"
     *   Where action is one of the following:
     *     0 - Push (Store in the <code> buffer)
     *     1 - Pop (Pop FIFO buffer that matches <code> and reply with <data>)
     *     2 - Peek (Retrieve without destroying the top of the stack)
     *     3 - Put (Overwrite the top of the stack)
     */ 

   /* We'll start with the poll framework.
    * 
    * There are two basic models for managing a listener: thread and multiplexing
    *  1) Thread: This is very simple.  Every time you accept a connection, spawn a 
    *       brand new thread to manage it.  Have a dozen connections? 12 threads, plus
    *       the listener thread.  Plus the main thread.
    *  2) Multiplexing: This is also pretty simple.  You have one thread running the 
    *       listener.  When none of the watched sockets have activity, it blocks and
    *       removes itself from the CPU.  When any of the sockets has activity, it 
    *       unblocks, receives, processes, handles, and resumes blocking until new
    *       data arrives.  Can handle hundreds of simultaneous connections without
    *       breaking a sweat, all on one thread.  We're doing this one.
    *
    * The multiplexer I like is poll().  select() is an older, very common one too.  
    *  A newer, event-driven one is epoll, which is good beyond 1024 connections, but
    *    it's pretty complicated and is really only ideal for large numbers of connections.
    *
    * Poll takes 3 arguments.  The first is the set of file descriptors for connections you
    *   want to watch, then it takes the number of file descriptors to watch, finally there
    *   is a timeout for routine event handling during lulls in activity.
    *
    * If it works, it will return a non-negative integer for the number of file descriptors
    *   with pending activity.  0 is valid (used for timeout).  < 0 is bad.
    */
    fds_active = poll(fd_list, max_fd + 1, POLL_TIMEOUT);
    if(fds_active < 0)
    {
      PRINTE("[E] Poll returned an error. Exiting.\n");
      PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
      exit(1);      
    }
    else if(fds_active == 0)
    {
//      PRINTV("[V] Poll Timed Out... is_running == %d\n", is_running);
    }

    /* Received connection request on the incoming socket port */
    if(fd_list[0].revents != 0)
    {
      PRINTA("[A] New Client Request\n");
      struct sockaddr_in new_client;
      socklen_t sock_len = sizeof(struct sockaddr_in);
      int client_sock = accept(fd_list[0].fd, (struct sockaddr *)&new_client, &sock_len);
      if(client_sock != -1)
      {
        PRINTA("[A] Client Added Successfully on Socket FD: %d\n", client_sock);
        int i;
        /* Find the first open fd_list index to add the connection to.
         * This is needed because if you have, for example [0][1][2][3][4] and max is 4, then
         *   [3] disconnects, you have a gap at [3], but max is [4].  If you have
         *   a lot of connect/disconnect events, then max would otherwise have to
         *   keep expanding and your array would be sparse and each check would waste
         *   a lot of cycles searching nothing.
         */
        int index = -1;
        for(i = 1; i <= max_fd; i++)
        {
          if(fd_list[i].fd == -1)
          {
            index = i;
          }
        }
        if(index == -1)
        {
          max_fd++;
          index = max_fd;
        }
        fd_list[index].fd = client_sock;
        fd_list[index].events = POLLIN;
      } /* End of valid accept */
    } /* End of new connection */
    /* Now we're having fun.  Data is from an existing connection */
    else  
    {
      int i;
      int action, code, length;
      char *data;
      char *packet;
      DATA_LIST *retrieved_data = NULL;
      /* Check all possible sockets in the list to see which have data */
      for(i = 1; i <= max_fd; ++i)
      {
        /* This socket has activity on it */
        if(fd_list[i].revents != 0)
        {
          PRINTV("[V] Socket %d has data on it\n", fd_list[i].fd);
          /* There are two sources of activity.  We need to check to see
           *   if this is data incoming or a disconnect event.
           *
           * Check for disconnect first. 
           *  read returns the number of bytes successfully read.
           *  If the number of bytes is <= 0, there was a connection error (disconnect)
           * 
           *  This line has the side effect of reading the first byte into action.
           */
          if(read(fd_list[i].fd, (char *)&buffer[0], 1) <= 0)
          {
            PRINTA("[D] The process at %d had disconnected.\n", fd_list[i].fd);
            if(close(fd_list[i].fd) == -1)
            {
              PRINTE("[E] Could not close socket %d. Continuing.\n", fd_list[i].fd);
              PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
            }
            fd_list[i].fd = -1;
            /* Admin cleanup. If this was the max, then walk backwards until the first valid
             *  socket is hit.  This one will be the new max.
             */
            if(max_fd == i)
            {
              while(fd_list[max_fd].fd == -1)
                --max_fd;
            }
          } /* End disconnect event */
          /* This has real data AND action is already set to the action. */
          else
          {
            PRINTA("Real data detected\n");
            int index = 0;
            /* Step 1: Parse the action */
            buffer[1] = '\0';
            action = atoi(buffer);
            PRINTN("[N] Read Action == %d\n", action);
            /* Toss out the comma separater */
            read(fd_list[i].fd, (char *)&buffer[0], 1);
            /* Step 2: Read and parse the code */
            index = 0;
            do{
              read(fd_list[i].fd, (char *)&buffer[index], 1);
            } while(buffer[index++] != ',');
            buffer[index - 1] = '\0';
            code = atoi(buffer);
            PRINTN("[N] Read Code == %d\n", code);
            if(action == ACTION_PUSH || action == ACTION_PUT)
            {
              /* Step 3: Read and parse the code */
              index = 0;
              do{
                read(fd_list[i].fd, (char *)&buffer[index], 1);
              } while(buffer[index++] != ',');
              buffer[index - 1] = '\0';
              length = atoi(buffer);
              /* Step 4: Read the data */
              read(fd_list[i].fd, (char *)&buffer[0], length);
              buffer[length] = '\0';
              data = buffer;
  
              PRINTN("[N] Received Packet from %d\n", fd_list[i].fd);
              PRINTN("... Action: %2d\n... Code: %2d\n... Length: %2d\n... Data:%s\n", action, code, length, data);
            }
            else
            {
              PRINTN("[N] Received Packet from %d\n", fd_list[i].fd);
              PRINTN("... Action: %2d\n... Code: %2d\n", action, code);
            }

            /* Decide what to do based on the action of the received message */
            switch(action)
            {
              /* We're storing, so don't need any data sent back. */
              case ACTION_PUSH:
              case ACTION_PUT:
                PRINTN("[N] Request to Store data in code %d\n", code);
                store_data(data, length, code, action);
                break;                
              /* In either case, the same post-processing is done. */
              case ACTION_POP:
                /* Intentional Fallthrough */
              case ACTION_PEEK:
                retrieved_data = retrieve_data(code, action);
                /* Data was not found, prep 0,0 for sendback */
                if(retrieved_data == NULL)
                {
                  PRINTA(" (No Data Found)\n");
                  packet = calloc(1, sizeof(char));
                  packet[0] = 0;
                  length = 1;
                }
                else
                {
                  PRINTA(" %s\n", retrieved_data->data);
                  packet = retrieved_data->data;
                  length = retrieved_data->length;
                  PRINTN("Sending %s\n", packet);
                }
                if(write(fd_list[i].fd, (char *)packet, length) != length)
                {
                  PRINTE("Attempted to write %d bytes to %d, but failed. Continuing.\n", length, fd_list[i].fd);
                  PRINTE("[E] Error Code: %d (%s)\n", errno, strerror(errno));
                } 
                else  
                  PRINTN("[O] Just Wrote %d bytes to socket %d\n", length, fd_list[i].fd); 
                break;
            }
          } /* End of received data */
        } /* End of socket has activity on it */
      } /* End of for loop iterating over each FD */
    } /* End of data from existing connection */
  } /* End of while loop */

  return NULL;
}


DATA_LIST *store_data(char *data, int length, int code, int action)
{
  PACKET_LIST *walker;
  /* Everyone always asks why I check 'x != NULL' instead of 'x'.  It's because
   *  NULL is system defined and NOT ALWAYS ZERO.  That's why.
   *
   * And yep, there is a semicolon at the end of this for statement.  It's so cool, it doesn't
   *  need a code block.
   */
  for(walker = mailbox_head; walker != NULL && walker->code != code && walker->next != NULL; walker = walker->next);
  
  /* Code hasn't been seen before and is not in the mailbox */
  if(mailbox_head == NULL || walker->code != code)
  {
    /* Add a new code to the mailbox */
    PACKET_LIST *new_packet = calloc(1, sizeof(PACKET_LIST));
    /* If calloc/malloc fails, it returns NULL */
    if(new_packet == NULL)
    {
      return NULL;
    }

    new_packet->code = code;
    new_packet->next = mailbox_head;
    new_packet->data_list = NULL;
    mailbox_head = new_packet;
    walker = new_packet;
  }
  /* At this point, walker will always point to the right code queue */

  if(walker->data_list == NULL || action == ACTION_PUSH)
  { 
    /* Push the new data to the code queue */
    DATA_LIST *new_data = calloc(1, sizeof(DATA_LIST));
    new_data->data = calloc(length, sizeof(char));
    memcpy(new_data->data,data, length);
    new_data->next = walker->data_list;
    new_data->length = length;
    walker->data_list = new_data;

    /* Return the new data pointer. */
    return new_data;
  }
  else if(action == ACTION_PUT)
  {
    free(walker->data_list->data);
    walker->data_list->data = calloc(length, sizeof(char));
    memcpy(walker->data_list->data, data, length);
    walker->data_list->length = length;
    return walker->data_list;      
  }
  return NULL;
}

/* This returns a structure.  Remember to free it after a call */
DATA_LIST *retrieve_data(int code, int action) 
{
  PACKET_LIST *walker;
  DATA_LIST *found_data;
  for(walker = mailbox_head; walker != NULL && walker->code != code; walker = walker->next);

  /* We got a request for data from code, but no data is pending */
  if(walker == NULL || walker->code != code || walker->data_list == NULL)
  {
    return NULL;
  }
  /* If pop, then return the pointer to the first element in the appropriate
   *  list and remove it from the list.
   */
  if(action == ACTION_POP)
  {
    found_data = walker->data_list;
    walker->data_list = found_data->next;
    PRINTA("(POP) Returning %s\n", found_data->data);
  }
  /* If peek, then make a copy of the first element in the list and return that. */
  else if(action == ACTION_PEEK)
  {
    found_data = calloc(1, sizeof(DATA_LIST));
    found_data->data = calloc(walker->data_list->length, sizeof(char));
    memcpy(found_data->data, walker->data_list->data, walker->data_list->length);
    found_data->length = walker->data_list->length;
    found_data->next = NULL;
    PRINTA("(PEEK) Returning %s\n", found_data->data);
  }
  return found_data;
}

void print_stored_data()
{
  PACKET_LIST *pwalker;
  DATA_LIST *dwalker;
  printf("Printing all Mailbox Codes\n\n");
  for(pwalker = mailbox_head; pwalker != NULL; pwalker = pwalker->next)
  {
    printf("Mailbox for Code %d\n", pwalker->code);
    for(dwalker = pwalker->data_list; dwalker != NULL; dwalker = dwalker->next)
    {
      printf("...[%d] %s\n", dwalker->length, dwalker->data);
    }
  }
  printf("\n");
}
