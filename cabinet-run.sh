#!/bin/bash
#
# /etc/init.d/cabinet-run
#
# Startup script for cabinet-run
#
# chkconfig: 2345 80 20
# description: Starts and stops cabinet-run
# pidfile: /var/run/callboard.pid

### BEGIN INIT INFO
# Provides:          cabinet-run
# Required-Start:    
# Required-Stop:     
# Should-Start:      
# Should-Stop:       
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: distributed storage system for structured data
# Description:       cabinet-run is a distributed (peer-to-peer) system for
#                    the management call-centers
### END INIT INFO

JAVA_LIBS=/home/admin2/dcm4che/dcm4che-assembly/target/dcm4che-5.13.0-bin/dcm4che-5.13.0/lib/linux-x86_64
JAR="mibs-cabinet-1.1.jar"
YML="application.yml"
CONFIG="/usr/local/etc/mibs-cabinet/"$YML

INSTALL_DIR="/usr/local/bin/"
JAR=$INSTALL_DIR$JAR


PID="/var/run/mibs-cabinet.pid"

LOG="/var/log/mibs-cabinet.log"

# If JAVA_HOME has not been set, try to determine it.
if [ -z "$JAVA_HOME" ]; then
    # If java is in PATH, use a JAVA_HOME that corresponds to that. This is
    # both consistent with how the upstream startup script works, and with
    # the use of alternatives to set a system JVM (as is done on Debian and
    # Red Hat derivatives).
    java="`/usr/bin/which java 2>/dev/null`"
    if [ -n "$java" ]; then
        java=`readlink --canonicalize "$java"`
        JAVA_HOME=`dirname "\`dirname \$java\`"`
    else
        # No JAVA_HOME set and no java found in PATH; search for a JVM.
        for jdir in $JVM_SEARCH_DIRS; do
            if [ -x "$jdir/bin/java" ]; then
                JAVA_HOME="$jdir"
                break
            fi
        done
        # if JAVA_HOME is still empty here, punt.
    fi
fi
JAVA="$JAVA_HOME/bin/java"
export JAVA_HOME JAVA

case "$1" in
    start)
   	 	$JAVA -Djava.library.path=$JAVA_LIBS  -jar $JAR --spring.config.location=$CONFIG 2>&1 >>$LOG &
		RES=$!
		echo $RES > $PID
    	;;
   stop)
   		kill -9 `cat $PID`
   		;;
    *)
     echo "Usage: `basename $0` start|stop"
     exit 1
esac

exit 0       
   
