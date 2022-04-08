:: ---- Go to the directory of the project:
:: cd C:\Users\Me\advent-of-code

:: ---- Run this file:
:: cmdScripts\GraysPileTest


:: ---- Do this silently
@ECHO off


:: ---- GraysPile.cmd handles the arguments and compiling everything but test
CALL cmdScripts/GraysPile %* fromTest


:: ---- outputdirectory wasn't properly set, so exit
IF "%outputdirectory%" == "" (
  EXIT /b
)


:: ---- Create a list of all the java test files that needs compiling.
dir source\test\*.java /b /s > toinclude.txt

:: ---- Compile single file:
:: javac -cp run;libraries/junit-platform-console-standalone-1.8.2.jar;libraries/gson-2.8.9.jar -d run source/test/DaysTest.java
:: javac -cp %outputdirectory%;libraries/junit-platform-console-standalone-1.8.2.jar;libraries/gson-2.8.9.jar -d %outputdirectory% source/test/DaysTest.java

:: ---- Compile all of them. Including all project classes:
:: javac -cp run;libraries/junit-platform-console-standalone-1.8.2.jar;libraries/gson-2.8.9.jar -d run @toinclude.txt
javac -cp %outputdirectory%;libraries/junit-platform-console-standalone-1.8.2.jar;libraries/gson-2.8.9.jar -d %outputdirectory% @toinclude.txt


:: ---- Remove any resources folder in the outputdirectory directory:
IF EXIST %outputdirectory%\resources rmdir /s /q %outputdirectory%\resources


:: ---- Run one of the tests:
:: java -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path run:libraries/gson-2.8.9.jar -c test.DaysTest
:: java -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path %outputdirectory%:libraries/gson-2.8.9.jar -c test.DaysTest
:: ---- Other options for PowerShell:
:: java --% -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path run;libraries/gson-2.8.9.jar -c test.DaysTest
:: java @('-jar', 'libraries/junit-platform-console-standalone-1.8.2.jar', '--disable-ansi-colors', '--class-path', 'run:libraries/gson-2.8.9.jar', '-c', 'test.DaysTest')

:: ---- Run all tests (not neeeded. There's only one):
:: java -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path run:libraries/gson-2.8.9.jar --scan-classpath
:: java -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path %outputdirectory%:libraries/gson-2.8.9.jar --scan-classpath
