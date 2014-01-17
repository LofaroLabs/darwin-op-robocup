TARGETS = all clean
.PHONY: $(TARGETS)

BOOST_INCLUDE_DIRS=-I/usr/local/boost -I/usr/include/ -I/usr/local/include

ifndef OSTYPE
  OSTYPE = $(shell uname -s|awk '{print tolower($$0)}')
  #export OSTYPE
endif

ifeq ($(OSTYPE),linux)
  SHLIBEXT=so
  MEXEXT=mexglx
  LIBRT=-lrt
endif
ifeq ($(OSTYPE),darwin)
  SHLIBEXT=dylib
  MEXEXT=mexmaci
endif

all: dcmVision8 dcmVision32

dcmVision8: dcmVision8.cpp
	mex -O $^ $(BOOST_INCLUDE_DIRS) $(LIBRT)

dcmVision32: dcmVision32.cpp
	mex -O $^ $(BOOST_INCLUDE_DIRS) $(LIBRT)

clean:
	rm -f *.o
	rm -f *.$(MEXEXT)
