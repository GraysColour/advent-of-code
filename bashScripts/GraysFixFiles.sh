#!/bin/bash

# If windows carriage returns are present in files..

# See https://support.microfocus.com/kb/doc.php?id=7014821#
# Or https://www.cyberciti.biz/faq/how-to-remove-carriage-return-in-linux-or-unix/

# check a file:
# cat -v resources/2021/sample/Day3.txt

# Using in place substitution of a carriage return just prior to end of line
# NOTE! This script itself needs to be fixed before it works!
# sed -i 's/\r$//' bashScripts/GraysFixFiles.sh

# parts of below from https://stackoverflow.com/a/55817578
# by https://stackoverflow.com/users/1904943/victoria-stuart

# only files (not directories)
# no hidden files
# no .class files
# no .jar files
find $(pwd) -type f \
  -not -path '*/\.*' \
  -not -path '*.class' \
  -not -path '*.jar' \
  -exec sed -i 's/\r$//' {} +
