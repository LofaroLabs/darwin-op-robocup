CC:=gcc
LD:=gcc
LIBWIIUSE_LDFLAGS:=-lwiiuse

CFLAGS:=-fPIC -O2 -I/home/ksulliv/Robotics/robocup/robocup2012/overo/
LDFLAGS:= -lm -fPIC ${LIBWIIUSE_LDFLAGS}
SRCS:=$(wildcard *.c)
OBJS:=$(SRCS:.c=.o)
DEPS:=$(SRCS:.c=.d)
PROGS:=horde

all: $(PROGS) 

clean:
	rm -rf Dependencies *~
	rm -rf $(PROGS) $(OBJS) $(DEPS)

$(PROGS): horde.o easysocket.o
	$(LD) $(LDFLAGS) -o horde horde.o easysocket.o

%.o : %.c
	$(CC) $(CFLAGS) -MMD -c $< -o $@
	@cat $*.d >> Dependencies
	@rm -f $*.d

-include Dependencies
