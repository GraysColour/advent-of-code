
:: ---- Go to the directory of the project:
:: cd C:\Users\Me\advent-of-code

:: ---- Run this file:
:: cmdScripts\GraysJar


:: ---- Do this silently
@ECHO off


:: ---- Do not reuse a value from another run
SET "outputdirectory="


:: ---- Find the argument count
SET argCount=0
FOR %%x IN (%*) DO set /A argCount+=1


:: ---- Set the output directory
IF %argCount% EQU 0 (
  SET "outputdirectory=run"
) ELSE IF %argCount% EQU 2 (
  IF "-Drun.dir"=="%1" (
    SET "outputdirectory=%2"
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


:: ---- Check that the execution directory is correct
SETLOCAL
IF NOT EXIST %outputdirectory% (
  SET "fileError="%outputdirectory%""
)
IF NOT EXIST resources (
  IF "%fileError%"=="" (
    SET "fileError="resources""
  ) ELSE (
    SET "fileError=%fileError% and "resources""
  )
)
IF NOT "%fileError%"=="" (
  ECHO error: missing %fileError% folder^(s^). Is this the project root?
  EXIT /b
)
ENDLOCAL


:: ---- Copy the resources samples into the outputdirectory
:: https://ss64.com/nt/for_cmd.html
FOR /f %%f IN ('dir /b /a resources') DO (
  xcopy resources\%%f\sample                 %outputdirectory%\resources\%%f\sample\           /s /e /y /q >nul
  xcopy resources\%%f\_results_\sample       %outputdirectory%\resources\%%f\_results_\sample\ /s /e /y /q >nul
  xcopy resources\%%f\_results_\results.json %outputdirectory%\resources\%%f\_results_\        /y /q >nul
)


:: ---- Reset the temp file, toinclude.txt, to an empty file:
type NUL > toinclude.txt

:: ---- Add the files and directories of the outputdirectory to the temp file, toinclude.txt:
FOR /f %%f IN ('dir /b /a run') DO (
  IF /I NOT "%%f" == "test" ECHO -C run %%f >> toinclude.txt
)


IF NOT EXIST target mkdir target


:: ---- Create the jar:
:: https://docs.oracle.com/en/java/javase/11/tools/jar.html
jar --create --file target/advent-of-code-1.0.jar --main-class=AllDays @toinclude.txt


:: ---- Don't execute the function just because it's at the end of the file
EXIT /b

:: ---- Internal function
:handle_error
ECHO   Error: Wrong arguments.
ECHO   Usage: -Drun.dir=^<output directory^>
ECHO Example: -Drun.dir=run
