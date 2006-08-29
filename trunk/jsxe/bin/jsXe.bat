@ECHO OFF
set JSXEDIR=.
rem set CLASSPATH=%JSXEDIR%/jsXe.jar;%JSXEDIR%/lib/xml-apis.jar;%JSXEDIR%/lib/xercesImpl.jar;%JSXEDIR%/lib/resolver.jar
rem set JSXE=net.sourceforge.jsxe.jsXe
set JAVA_HEAP_SIZE=32
javaw -server -mx%JAVA_HEAP_SIZE%m -Djava.endorsed.dirs=%JSXEDIR%/lib -jar %JSXEDIR%/jsXe.jar %1 %2 %3 %4 %5 %6 %7 %8 %9
if errorlevel 1 pause