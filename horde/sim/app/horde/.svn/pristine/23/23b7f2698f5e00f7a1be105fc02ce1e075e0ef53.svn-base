#!/bin/bash 
echo "doing it"
dot -Tpdf $1 -o /tmp/output${2}.pdf
export i=`uname`
if [ $i == "Darwin" ] ; then
	open /tmp/output${2}.pdf 
else
	evince /tmp/output${2}.pdf &
fi
