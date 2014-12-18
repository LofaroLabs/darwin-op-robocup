#ifndef SOCK_ROUTER_H
#define SOCK_ROUTER_H

/* Common C Environment */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
/* Sockets */
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netdb.h>
/* Threads */
#include <pthread.h>
/* Poll */
#include <poll.h>
/* For errors */
#include <errno.h>
/* For Debugs */
#include "debug.h"

/* Magic numbers in source code are bad.
 * Keeping all definitions in headers helps to
 *  organize your code and makes changing
 *  the values very easy.
 */
#define ROUTER_PORT       2247

/* Code Constants */
#define BIND_TIMEOUT      10 /* Seconds */
#define BIND_MAX_ATTEMPTS 5
#define MAX_CONNECTIONS   10
#define POLL_TIMEOUT      500 /* ms */

/* Convenience Constants */
#define ACTION_PUSH       0
#define ACTION_POP        1
#define ACTION_PEEK       2
#define ACTION_PUT        3

#define DATA_POP          0
#define DATA_PEEK         1

/* List Structures */
typedef struct data_list
{
  char *data;
  int length;
  struct data_list *next;
} DATA_LIST;

typedef struct packet_list 
{
  char *code;
  DATA_LIST *data_list;
  struct packet_list *next;
} PACKET_LIST;

/* Prototypes (Function Declarations) */
void open_socket();
void close_socket();
void *listener_function(void *args);
DATA_LIST *store_data(char *data, int length, char *code, int action);
DATA_LIST *retrieve_data(char *code, int action);
void print_stored_data();

#endif //SOCK_ROUTER_H