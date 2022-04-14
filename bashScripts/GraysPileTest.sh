#!/bin/bash

# ---- Go to the directory of the project:
# cd ~/advent-of-code

# ---- Run this file:
# bash bashScripts/GraysPileTest.sh


# ---- Get a "result" from GraysPile.sh
outputdirectory=$(bash bashScripts/GraysPile.sh $@ fromTest)


# ---- Compile the test(s)
if [ "$?" -ne "0" ]; then
  # there was an error exit from GraysPile.sh
  printf "$outputdirectory\n"
else
  # ---- Make a list of files to compile
  find source/test/ -mindepth 1 -name *.java > toinclude.txt

  # ---- Compile the list
  javac \
  -cp $outputdirectory:libraries/junit-platform-console-standalone-1.8.2.jar:libraries/gson-2.8.9.jar \
  -d $outputdirectory @toinclude.txt

  # ---- Remove any resources folder in the outputdirectory directory:
  rm -rf $outputdirectory/resources
fi


# ---- Run the test:
# java -jar libraries/junit-platform-console-standalone-1.8.2.jar --class-path run:libraries/gson-2.8.9.jar -c test.DaysTest
