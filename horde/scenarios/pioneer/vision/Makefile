CC=g++
INCLUDES=-I. -I/usr/local/include
LIBS=`pkg-config opencv --libs` -lcvblob -lboost_thread-mt
CFLAGS=`pkg-config opencv --cflags` -g

DEPS:=tracking.o easysocket.o robot.o
PROGS:=blob_display evader

all: $(DEPS) $(PROGS) 

.PHONY: clean 

clean:
	rm -f *.o *~ $(PROGS) *.class SWIGTYPE_*

blob_display: blob_display.cpp tracking.o 

evader: tracking.o robot.o 

tracking.o: tracking.cpp tracking.h

easysocket.o: easysocket.c easysocket.h 

robot.o: robot.cpp robot.h

$(PROGS) : % : %.o
	$(CC) -o $@ $@.o $(DEPS) $(LIBS) 

%.o : %.cpp
	$(CC) $(CFLAGS) $(INCLUDES) -c $< -o $@

%.o : %.c
	gcc -c $< -o $@

