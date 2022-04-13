#!/bin/bash

# ---- Go to the directory of the project:
# cd ~/advent-of-code

# ---- Run this file:
# bash bashScripts/GraysPile.sh


# ---- Check that the execution directory is correct
if [ ! -d "source" ]; then
  echo "  Error: \"source\" directory is missing. Is this the project root?"
  exit 1
fi


# ---- Default parameter error message and exit code
error_state () {
  echo "  Error: Wrong arguments."
  echo "  Usage: -Drun.dir=<output directory>"
  echo "Example: -Drun.dir=run"
  exit 1
}


# ---- Handle a possible "fromTest" parameter from GraysPileTest.sh
if [ $# -eq 0 ]; then
  outputdirectory="run"
elif [ $# -eq 1 ]; then
  if [ "$1" == "fromTest" ]; then
    outputdirectory="run"
    fromtest=$1
  else
    dirarg=$1
  fi
elif [ $# -eq 2 ] && [ "$2" == "fromTest" ]; then
  dirarg=$1
  fromtest=$2
else
  error_state
fi


# ---- Handle an argument for the output directory
if [[ -n $dirarg ]]; then

  # split the argument on =
  arg=($(echo $dirarg | tr "=" " "))
  outputdirectory=${arg[1]}

  # more than 2 parts in the argument
  # first part is not -Drun.dir
  # second part is empty
  # more than one =
  if [ ${#arg[@]} -gt 2 ] || \
     [ "${arg[0]}" != "-Drun.dir" ] || \
     [[ -z $outputdirectory ]] || \
     [ $(expr length ${dirarg//[^=]}) -gt 1 ]
  then
    error_state
  fi
fi


# ---- Create a list of all the java files that needs compiling. Order matters:
find source/base/ -mindepth 1 -name *.java > toinclude.txt
find source/util/ -mindepth 1 -name *.java >> toinclude.txt
find source/absbase/ -mindepth 1 -name *.java >> toinclude.txt
find source/Year2021/ -mindepth 1 -name *.java >> toinclude.txt
find source/AllDays.java >> toinclude.txt

# ---- Compile single file:
# javac -cp run -d run source/Year2021/Day22.java

# ---- Compile all of them:
javac -cp $outputdirectory -d $outputdirectory @toinclude.txt


# ---- Create the properties files:
echo run.dir=$outputdirectory > $outputdirectory/config.properties


# ---- Run one on the samples:
# java -cp run Year2021.Day22

# ---- Run one on the challenge:
# java -cp run Year2021.Day22 -d challenge

# ---- Run all the days on samples:
# java -cp run AllDays


# ---- Creating the JavaDoc
# javadoc @source/javaDoc/args.txt

# ---- Return the outputdirectory to GraysPileTest.sh
if [[ -n $fromtest ]]; then
  echo $outputdirectory
fi
