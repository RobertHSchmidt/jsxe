#!/bin/sh
INSTALLDIR=.
CLASSPATH=$INSTALLDIR/jsXe.jar:$INSTALLDIR/lib/xml-apis.jar:$INSTALLDIR/lib/xercesImpl.jar
JSXE=net.sourceforge.jsxe.jsXe
JAVA_HEAP_SIZE=32
exec java -mx${JAVA_HEAP_SIZE}m -cp $CLASSPATH $JSXE $@
