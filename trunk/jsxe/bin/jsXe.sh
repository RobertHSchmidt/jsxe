#!/bin/sh
JSXEDIR=.
CLASSPATH=$JSXEDIR/jsXe.jar:$JSXEDIR/lib/xml-apis.jar:$JSXEDIR/lib/xercesImpl.jar:$JSXEDIR/lib/jgraph.jar
JSXE=net.sourceforge.jsxe.jsXe
JAVA_HEAP_SIZE=32
exec java -mx${JAVA_HEAP_SIZE}m -cp $CLASSPATH $JSXE $@
