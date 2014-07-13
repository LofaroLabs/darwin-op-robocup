#asssumg dcm on start up
sleep 1
screen -dm -s /usr/bin/bash -S cog lua ./run_cognition.lua
sleep 3
screen -dm -s /usr/bin/bash -S hor lua ./hoard_connection.lua
echo `screen -ls`


