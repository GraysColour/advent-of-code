:: ---- Go to the directory of the project:
:: cd C:\Users\Me\advent-of-code

:: ---- Run this file:
:: cmdScripts\GraysPile


:: https://stackoverflow.com/questions/411247/running-a-cmd-or-bat-in-silent-mode
@ECHO off


:: ---- Do not reuse a value from another run
SET "outputdirectory="


:: ---- Check that the execution directory is correct
IF NOT EXIST source (
  ECHO error: "source" directory is missing. Is this the project root?
  EXIT /b
)


:: ---- Find the argument count
SET argCount=0
FOR %%x IN (%*) DO set /A argCount+=1


:: ---- Set the output directory ..and
:: ---- Handle a possible "fromTest" parameter from GraysPileTest.cmd
IF %argCount% EQU 0 (
  SET "outputdirectory=run"

) ELSE IF %argCount% EQU 1 (
  if "%1" == "fromTest" (
    SET "outputdirectory=run"
  ) ELSE (
    GOTO :handle_error
  )

) ELSE IF %argCount% EQU 2 (
  IF "%2" == "fromTest" (
    GOTO :handle_error
  ) ELSE (
    CALL :handle_args %1 %2
  )

) ELSE IF %argCount% EQU 3 (
    IF "%3" == "fromTest" (
      CALL :handle_args %1 %2
    ) ELSE (
      GOTO :handle_error
    )

) ELSE (
  GOTO :handle_error
)


:: ---- outputdirectory wasn't properly set, so exit
IF "%outputdirectory%" == "" (
  EXIT /b
)


:: ---- Create a list of all the java files that needs compiling. Order matters:
dir source\base\*.java /b /s > toinclude.txt
dir source\util\*.java /b /s >> toinclude.txt
dir source\absbase\*.java /b /s >> toinclude.txt
dir source\Year2021\*.java /b /s >> toinclude.txt
dir source\AllDays.java /b /s >> toinclude.txt

:: ---- Compile single file:
:: javac -cp run -d run source/Year2021/Day22.java
:: javac -cp %outputdirectory% -d %outputdirectory% source/Year2021/Day22.java

:: ---- Compile all of them:
:: javac -cp run -d run @toinclude.txt
javac -cp %outputdirectory% -d %outputdirectory% @toinclude.txt


:: ---- Create the properties files:
ECHO run.dir=%outputdirectory% > %outputdirectory%/config.properties


:: ---- Run one on the samples:
:: java -cp run Year2021.Day22
:: java -cp %outputdirectory% Year2021.Day22

:: ---- Run one on the challenge:
:: java -cp run Year2021.Day22 -d challenge
:: java -cp %outputdirectory% Year2021.Day22 -d challenge

:: ---- Run all the days on samples:
:: java -cp run AllDays
:: java -cp %outputdirectory% AllDays

:: ---- Creating the JavaDoc
:: javadoc @source\javaDoc\args.txt


:: ---- Don't execute the functions just because it's at the end of the file
EXIT /b

:: ---- Internal functions
:handle_args
IF "-Drun.dir"=="%1" (
  SET "outputdirectory=%2"
  EXIT /b
)
:: ..fall through..

:handle_error
ECHO   Error: Wrong arguments.
ECHO   Usage: -Drun.dir=^<output directory^>
ECHO Example: -Drun.dir=run
