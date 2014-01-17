#!/bin/bash
CWD=`pwd`
USAGE="./install_nao.sh <path/to/usb/root/partition> <path/to/usb/user/partition>"

# check args
if [ $# -ne 2 ]; then
  echo "usage:"
  echo "$USAGE"
  exit
fi

# create /usr/local/ folder in home
sudo mkdir -p $2/local/
sudo rsync -avr --exclude=".*" dependencies/usr/local/* $2/local/
sudo chown -R 1001 $2/local/
sudo chgrp -R 18 $2/local/


# compile and install code 
cd ../Lib/ && make setup_nao && cd $CWD
sudo rsync -avr --exclude=".*" ../Player $2/nao/
sudo chown -R 1001 $2/nao
sudo chgrp -R 18 $2/nao

# install naoqi module
cd ../Lib/ && make setup_naoqi && cd $CWD
sudo rsync -avr --exclude=".*"  naoqi/ $2/nao/naoqi/
sudo chown -R 1001 $2/nao/naoqi
sudo chgrp -R 18 $2/nao/naoqi

# link /home/local to /usr/local
sudo ln -s /home/local/ $1/usr/local

