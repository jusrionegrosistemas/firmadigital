@echo off
c:
cd %HOMEPATH%\bin
echo ingresando con %1 >> OpenArgs.log

start /MIN %HOMEPATH%\bin\jre\bin\java.exe -jar firma-0.0.1-SNAPSHOT-jar-with-dependencies.jar %1
exit
