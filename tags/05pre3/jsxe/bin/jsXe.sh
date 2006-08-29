#!/bin/sh
# Java heap size, in megabytes
JSXEDIR=.
JAVA_HEAP_SIZE=32
exec java -server -mx${JAVA_HEAP_SIZE}m -Djava.endorsed.dirs=${JSXEDIR}/lib ${JSXE} -jar ${JSXEDIR}/jsXe.jar $@