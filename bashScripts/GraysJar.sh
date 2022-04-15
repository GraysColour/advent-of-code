#!/bin/bash

# ---- Go to the directory of the project:
# cd ~/advent-of-code

# ---- Run this file:
# bash bashScripts/GraysJar.sh


# ---- Handle a directory argument
# at least one argument
if [[ -n $1 ]]; then  # split the argument on =
  arg=($(echo $1 | tr "=" " "))
  outputdirectory=${arg[1]}

  # more than one argument
  # more than 2 parts in the argument
  # first part is not -Drun.dir
  # second part is empty
  # more than one =
  if [ $# -gt 1 ] || \
     [ ${#arg[@]} -gt 2 ] || \
     [ "${arg[0]}" != "-Drun.dir" ] || \
     [[ -z $outputdirectory ]] || \
     [ $(expr length ${1//[^=]}) -gt 1 ]
  then
    echo "  Error: Wrong arguments."
    echo "  Usage: -Drun.dir=<output directory>."
    echo "Example: -Drun.dir=run"
    exit 1
  fi
else
  outputdirectory="run"
fi


# ---- Check that the execution directory is correct
if [ ! -d "$outputdirectory" ]; then
  fileError=\"$outputdirectory\"
fi
if [ ! -d "resources" ]; then
  if [[ -z $fileError ]]; then
    fileError=\"resources\"
  else
    fileError="$fileError and \"resources\""
  fi
fi
if [[ -n $fileError ]]; then
  echo Error: missing $fileError folder\(s\). Is this the project root?
  exit 1
fi


# ---- Copy the resources samples into the outputdirectory
cp -R --parents resources/*/sample $outputdirectory
cp -R --parents resources/*/_results_/sample $outputdirectory
cp --parents resources/*/_results_/results.json $outputdirectory

# ---- Add the files and directories of the outputdirectory to the temp file, toinclude.txt:
find $outputdirectory -maxdepth 1 ! -name test ! -name $outputdirectory -printf "-C $outputdirectory %f\n" > toinclude.txt

# ---- Create the target directory. Do not error if it alredy exists.
mkdir -p target

# ---- Create the jar:
jar --create --file target/advent-of-code-1.0.jar --main-class=AllDays @toinclude.txt
