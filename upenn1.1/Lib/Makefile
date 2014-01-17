# Master Makefile to compile all Lua/C++ libraries

CWD= $(shell pwd)

NAODIR= Platform/Nao
NAOQIDIR= NaoQi
WEBOTSDIR= Platform/Webots
OPDIR= Platform/OP
WEBOTSOPDIR= Platform/Webots_OP
WEBOTSHUBODIR= Platform/Webots_Hubo

IMAGEDIR= ImageProc
UTILDIR= Util
MATLABDIR= Matlab 
COLORTABLEDIR= Colortable 

PLAYER=Player

INSTDIR= ../$(PLAYER)/Lib
WEBOTSCONTDIR= ../WebotsController
PLAYERDIR= ../$(PLAYER)
NAODEPDIR= ../Install/dependencies
NAODEPLUA51DIR= $(NAODEPDIR)/usr/local/lib/lua/5.1

ifndef OSTYPE
  OSTYPE = $(shell uname -s|awk '{print tolower($$0)}')
  #export OSTYPE
endif
ifeq ($(OSTYPE),linux)
  SHLIBEXT= so
endif
ifeq ($(OSTYPE),darwin)
  SHLIBEXT= dylib
endif
LUAEXT= lua

all:: nao webots op image util

colortable:
	@echo "Compiling Colortable Mex Files...\n"
	@cd $(COLORTABLEDIR) && make && cd $(CWD)
	@echo "\n"

matlab:
	@echo "Compiling MATLAB Mex Files...\n"
	@cd $(MATLABDIR) && make && cd $(CWD)
	@echo "\n"

nao:
	@echo "Compiling Nao Lua/C++ Libraries...\n"
	@cd $(NAODIR) && make && cd $(CWD)
	@echo "\n"

naoqi:
	@echo "Compiling Custom Naoqi Modules...\n"
	@cd $(NAOQIDIR) && make && cd $(CWD)
	@echo "\n"

op:
	@echo "Compiling Darwin OP Lua/C++ Libraries...\n"
	@cd $(OPDIR) && make && cd $(CWD)
	@echo "\n"

webots:;
	@echo "Compiling Webots Nao Lua/C++ Libraries...\n"
	@cd $(WEBOTSDIR) && make && cd $(CWD)
	@echo "\n"

webots_op:;
	@echo "Compiling Webots OP Lua/C++ Libraries...\n"
	@cd $(WEBOTSOPDIR) && make && cd $(CWD)
	@echo "\n"

webots_hubo:;
	@echo "Compiling Webots OP Lua/C++ Libraries...\n"
	@cd $(WEBOTSHUBODIR) && make && cd $(CWD)
	@echo "\n"

image:
	@echo "Compiling Image Processing Lua/C++ Libraries...\n"
	@cd $(IMAGEDIR) && make && cd $(CWD)
	@echo "\n"

util::
	@echo "Compiling Utility Lua/C++ Libraries...\n"
	@cd $(UTILDIR) && make && cd $(CWD)
	@echo "\n"

	
setup_nao: nao setup_util setup_image
	@echo "Setting up Nao Lua/C++ Libraries...\n"
	find $(NAODIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;
	cd $(PLAYERDIR)/Config && rm Config.lua && ln -s Config_Nao.lua Config.lua && cd $(CWD)
	@echo "\n"

setup_webots: webots setup_util setup_image
	@echo "Setting up Webots Lua/C++ Libraries...\n"
	find $(WEBOTSDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;
# For the macs
	find Platform/Webots -regex '.*lua' -exec cp -v {} $(INSTDIR) \;
	find Platform/Webots -regex '.*dylib' -exec cp -v {} $(INSTDIR) \;
#
	cd $(PLAYERDIR)/Config && rm Config.lua && ln -s Config_Webots.lua Config.lua && cd $(CWD)
	cd $(WEBOTSCONTDIR) && make && cd $(CWD)
	@echo "\n"

setup_webots_op: webots_op setup_util setup_image
	@echo "Setting up Webots Lua/C++ Libraries...\n"
	find $(WEBOTSOPDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;

# For the macs
	find $(WEBOTSOPDIR) -regex '.*lua' -exec cp -v {} $(INSTDIR) \;
	find $(WEBOTSOPDIR) -regex '.*dylib' -exec cp -v {} $(INSTDIR) \;
#
	cd $(PLAYERDIR)/Config && rm Config.lua && ln -s Config_Webots_OP.lua Config.lua && cd $(CWD)
	cd $(WEBOTSCONTDIR) && make && cd $(CWD)
	cd $(WEBOTSCONTDIR) && touch Player && cd $(CWD)  
	cd $(WEBOTSCONTDIR) && rm Player && ln -s ../Player Player && cd $(CWD)
	@echo "\n"

setup_webots_hubo: webots_hubo setup_util setup_image
	@echo "Setting up Webots Hubo Lua/C++ Libraries...\n"
	find $(WEBOTSHUBODIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;

# For the macs
	find $(WEBOTSHUBODIR) -regex '.*lua' -exec cp -v {} $(INSTDIR) \;
	find $(WEBOTSHUBODIR) -regex '.*dylib' -exec cp -v {} $(INSTDIR) \;
#
	cd $(PLAYERDIR)/Config && rm Config.lua && ln -s Config_Webots_Hubo.lua Config.lua && cd $(CWD)
	cd $(WEBOTSCONTDIR) && make && cd $(CWD)
	cd $(WEBOTSCONTDIR) && touch Player && cd $(CWD)  
	cd $(WEBOTSCONTDIR) && rm Player && ln -s ../PlayerHMC Player && cd $(CWD)
  

	@echo "\n"

setup_op: op setup_util setup_image
	@echo "Setting up Darwin OP Lua/C++ Libraries...\n"
	find $(OPDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;
	touch $(PLAYERDIR)/Config/Config.lua
	cd $(PLAYERDIR)/Config && rm Config.lua && ln -s Config_OP.lua Config.lua && cd $(CWD)
	cp $(INSTDIR)/unix.so $(PLAYERDIR)/
	cp $(INSTDIR)/unix.so $(PLAYERDIR)/Vision/
	@echo "\n"

setup_image: image
	@echo "Setting up Image Processing Lua/C++ Libraries...\n"
	find $(IMAGEDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;
	# For the macs
	find $(IMAGEDIR) -regex '.*lua' -exec cp -v {} $(INSTDIR) \;
	find $(IMAGEDIR) -regex '.*dylib' -exec cp -v {} $(INSTDIR) \;

	@echo "\n"

setup_util: util
	@echo "Setting up Utility Lua/C++ Libraries...\n"
	find $(UTILDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(INSTDIR) \;
	find $(UTILDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(WEBOTSCONTDIR) \;
	find $(UTILDIR) -regex '.*\($(SHLIBEXT)\|$(LUAEXT)\)' -exec cp -v {} $(NAODEPLUA51DIR) \;
	# For the macs
	find $(UTILDIR) -regex '.*lua' -exec cp -v {} $(INSTDIR) \;
	find $(UTILDIR) -regex '.*dylib' -exec cp -v {} $(INSTDIR) \;
	find $(UTILDIR) -regex '.*lua' -exec cp -v {} $(WEBOTSCONTDIR) \;
	find $(UTILDIR) -regex '.*dylib' -exec cp -v {} $(WEBOTSCONTDIR) \;
	#
	@echo "\n"

setup_naoqi: naoqi setup_util
	@echo "Setting up NaoQi module...\n"
	rsync -avr --exclude=".*" $(NAOQIDIR)/build/sdk/lib/naoqi/* ../Install/naoqi/lib/naoqi/
	@echo "\n"

clean: 
	cd $(IMAGEDIR) && make clean && cd $(CWD)
	cd $(UTILDIR) && make clean && cd $(CWD)
	cd $(NAODIR) && make clean && cd $(CWD)
	cd $(NAOQIDIR) && make clean && cd $(CWD)
	cd $(WEBOTSDIR) && make clean && cd $(CWD)
	cd $(WEBOTSOPDIR) && make clean && cd $(CWD)
	cd $(WEBOTSHUBODIR) && make clean && cd $(CWD)
	cd $(OPDIR) && make clean && cd $(CWD)
	cd $(WEBOTSCONTDIR) && make clean && cd $(CWD)

  

