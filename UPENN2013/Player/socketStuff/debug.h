/* 
 * debug.h
 *
 * Sockets Example Core
 *
 * This file provides the mechanism for C/C++ debugging in a non-floody way.
 */

#ifndef DEBUG_H
#define DEBUG_H

/* DEBUG Core
 *
 * All project source files should include this header.
 * You can set DEBUG level via the Makefile, or you can 
 *  manually set debug on a per-source file basis:
 *  ie. #define DEBUG DEBUG_VERBOSE
 * If you set up a #define for DEBUG in a header, all source
 *  files using that header will get that DEBUG.
 */

#include <stdio.h>

/* Define the debug levels */
#define DEBUG_NONE      0
#define DEBUG_INFO      1
#define DEBUG_ANNOTATE  2
#define DEBUG_VERBOSE   4
#define DEBUG_NETWORK   8
#define DEBUG_FULL      DEBUG_INFO|DEBUG_ANNOTATE|DEBUG_NETWORK|DEBUG_VERBOSE

/* Error printing.  Always print all errors. */
#define PRINTE(...) fprintf(stderr, __VA_ARGS__)

/* These are MACROS in C. 
 * This is a macro conditional.  It is *only* evaluated
 *  at compile time.  If DEBUG is defined and is given the 
 *  appropriate flag value, then the corresponding version 
 *  of PRINT_ will be defined as a fprintf straight to 
 *  stdout.  This can be changed to stderr if desired.
 *  PRINT_s define here work the same as a normal printf does.
 *  ie. PRINTF("%d %c\n", my_int, my_char);
 * The advantage of using these instead of printf is that when
 *  your DEBUG levels are off, they are not included in your 
 *  program.  So you don't incur the cost or annoyance of undesired
 *  print statements from old debugs.
 */

 /* This one defines PRINTF() for manual debug stuff. */
#if (DEBUG) & DEBUG_INFO
  #define PRINTF(...) fprintf(stderr, __VA_ARGS__)
#else
  #define PRINTF(...)
#endif
/* DEBUG_INFO */

 /* This one defines PRINTA() for common debug stuff
  *  This is stuff like stats, leaving it on shouldn't flood anything.
  */
#if (DEBUG) & DEBUG_ANNOTATE
  #define PRINTA(...) fprintf(stderr, __VA_ARGS__)
#else
  #define PRINTA(...)
#endif
/* DEBUG_ANNOTATE */

 /* This one defines PRINTV() for heavy, floody debug stuff. */
#if (DEBUG) & DEBUG_VERBOSE
  #define PRINTV(...) fprintf(stderr, __VA_ARGS__)
#else
  #define PRINTV(...)
#endif
/* DEBUG_VERBOSE */

 /* This one defines PRINTN() for displaying all packet data. */
#if (DEBUG) & DEBUG_NETWORK
  #define PRINTN(...) fprintf(stderr, __VA_ARGS__)
#else
  #define PRINTN(...)
#endif
/* DEBUG_NETWORK */

#endif 
/* DEBUG_H */
