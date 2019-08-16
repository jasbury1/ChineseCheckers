#!/bin/bash
cd `dirname $0`
if [ "$MATERIALS_DIR_305" = "" ] ; then
    # You must set MATERIALS_DIR_305 in you .mybashrc or
    # .bashrc or wherever it is for your OS.
    echo "Error:  The environment variable MATERIaLS_DIR_305 is not set."
    exit 1
fi
LIBS=$MATERIALS_DIR_305/lib/testy.jar

#
# Compile the program:
#
rm -rf out
mkdir out
javac -Xlint:unchecked -Xmaxerrs 5 -sourcepath src  \
	-cp $LIBS -d out `find src -name *.java -print`
if [ $? != 0 ] ; then
    exit 1
fi


#
# Run the program with assertions enabled, feeding it any 
# command-line arguments:
#
java -cp out:$LIBS -ea Main $*

#
# Clean up
#
rm -rf out
