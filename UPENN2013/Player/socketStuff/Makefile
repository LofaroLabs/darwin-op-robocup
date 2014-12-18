##########################
# Socket Router Makefile
####

all: sock_router 

########
# DEBUG Level Selection
#  This only sets DEBUG from the makefile.  Any individual file-level
#   debug definitions will override this.
#   .. If you add #define DEBUG DEBUG_INFO to any source file, 
#   ..   if will override this for that file only.
#   .. If you add a #define for DEBUG to any header, it will override
#   ..   this for all files with scope for that header.
#   .. Valid #define values for DEBUG are:
#   ..   DEBUG_INFO, DEBUG_ANNOTATE, DEBUG_VERBOSE, DEBUG_NETWORK, DEBUG_FULL
#   .. These may be chained with the | operator, ie. #define DEBUG DEBUG_INFO|DEBUG_NETWORK
# Default Value with no command line arguments:
#   No Debug is Externally Set
# Example Commmand Line Usage:
#   make DEBUG=1
# Valid Values:
#       [NVAF] 
#   0 - (0000) No Debug Information (Default)
#		1 - (0001) Information  (PRINTF) Only
#		2 - (0010) Annotations  (PRINTA) Only
#   4 - (0100) Verbose      (PRINTV) Only
#   8 - (1000) Network Data (PRINTN) Only
#  15 - (1111) All PRINTs are active.
#   3, 5, 6, 7, 9, 10, 11, 12, 13, 14 - Binary Combinations Thereof
#
# The following line says if no DEBUG is set from the command line,
#   then set DEBUG=0
DEBUG ?= 0 

#########
# C Compiler Options
# 	-g  	Adds debugging information (for gdb)
#   -pg 	Adds the gprof profiling tool
#   -pthread  Adds the pthread library
#		-Wall	Enables most compiler warnings
CC = gcc
CFLAGS = -g -DDEBUG=$(DEBUG) -pthread

#########
# C++ Compile Options
# 	-g  	Adds debugging information (for gdb)
#   -pg 	Adds the gprof profiling tool
#   -pthread  Adds the pthread library
#		-Wall	Enables most compiler warnings
CPP = g++
CPFLAGS = -g -DDEBUG=$(DEBUG) -pthread

##########
# Targets
sock_router: sock_router.c
	$(CC) $(CFLAGS) -o $@ $^

sock_test: sock_test.c
	$(CC) $(CFLAGS) -o $@ $^

clean:
	-rm -f sock_router sock_test
