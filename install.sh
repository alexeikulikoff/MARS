#!/bin/bash

echo "Install Patients Cabinet... "

JAR="mibs-cabinet-1.1.jar"
YML="application.yml"

SRC_CONFIG=$PWD"/src/main/resources/"
DST_CONFIG="/usr/local/etc/mibs-cabinet/"
BUILD_DIR=$PWD"/build/libs/"
INSTALL_DIR="/usr/local/bin/"

SRC_CONFIG_YML=$SRC_CONFIG$YML
DST_CONFIG_YML=$DST_CONFIG$YML

DST_JAR=$INSTALL_DIR$JAR
SRC_JAR=$BUILD_DIR$JAR

ZER=10

copy_file(){ 

echo "Copy  $1 -> $2"	
cp $1 $2

if [ -e $2 ]
then
	echo "Copy sucessfull..."
else
	echo "Copy fail"
	exit 1
fi
}

if [ ! -e $DST_CONFIG  ]
then
	echo "Create directory "$DST_CONFIG
	mkdir $DST_CONFIG
fi
		

copy_file "$SRC_CONFIG_YML" "$DST_CONFIG_YML"
copy_file "$SRC_JAR" "$DST_JAR"



