CC=g++
INCLUDES=-I. -I/usr/local/include
LIBS=`pkg-config opencv --libs` -lcvblob -lboost_thread-mt  -lboost_date_time-mt -lboost_system-mt
CFLAGS=`pkg-config opencv --cflags` -g -Wall

DEPS:=tracking.o easysocket.o camera.o laser.o serialdaemon.o laserdaemon.o
PROGS:= main visualizer display

all: $(DEPS) $(PROGS) 

.PHONY: clean 

clean:
	rm -f *.o *~ $(PROGS) *.class SWIGTYPE_*

main: main.o ${DEPS}

visualizer: visualizer.o ${DEPS}

tracking.o: tracking.cpp tracking.h

easysocket.o: easysocket.cpp easysocket.h 

display: display.o ${DEPS} 

serialdaemon.o: serialdaemon.cpp serialdaemon.h 

laserdaemon.o: laserdaemon.cpp laserdaemon.h 

camera.o: camera.cpp camera.h 

$(PROGS) : % : %.o
	$(CC) -o $@ $@.o $(DEPS) $(LIBS) 

%.o : %.cpp
	g++ $(CFLAGS) $(INCLUDES) -c $< -o $@

%.o : %.c
	gcc -c $< -o $@

