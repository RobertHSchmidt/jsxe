@ECHO OFF
set INSTALLDIR=.
set CLASSPATH=%INSTALLDIR%/jsXe.jar;%INSTALLDIR%/lib/xml-apis.jar;%INSTALLDIR%/lib/xercesImpl.jar
set JSXE=net.sourceforge.jsxe.jsXe
set JAVA_HEAP_SIZE=32
java -mx%JAVA_HEAP_SIZE%m -cp %CLASSPATH% %JSXE% %1 %2 %3 %4 %5 %6 %7 %8 %9
if errorlevel 1 pause
