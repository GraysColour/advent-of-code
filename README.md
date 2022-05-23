## Index
- [About the project](#about-the-project)
- [© Copyright](#-copyright)
- [Custom scripts](#custom-scripts)
  - [Linux](#linux)
- [Requirements](#requirements)
  - [JDK (Java Development Kit)](#jdk-java-development-kit)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Compiling the project](#compiling-the-project)
  - ["Manually" on the command line](#manually-on-the-command-line)
  - [Using Maven](#using-maven)
  - [With Gradle](#with-gradle)
  - [Changing the output directory](#changing-the-output-directory)
- [Running the program](#running-the-program)
  - [-h or -hf](#-h-or--hf)
  - [-noTime](#-notime)
  - [-alt \<INTEGER\>](#-alt-integer)
  - [-o \<FILE\>](#-o-file)
  - [-f \<FILE\>](#-f-file)
  - [Other filepath options](#other-filepath-options)
  - [-y <4 DIGIT INTEGER>](#-y-4-digit-integer)
- [Running the test](#running-the-test)
  - ["Manually" on the command line](#manually-on-the-command-line-1)
  - [Using Maven](#using-maven-1)
  - [With Gradle](#with-gradle-1)
- [Package the project into a .jar](#package-the-project-into-a-jar)
  - ["Manually" on the command line](#manually-on-the-command-line-2)
  - [Using Maven](#using-maven-2)
  - [With Gradle](#with-gradle-2)
- [Input](#input)
  - [Test input](#test-input)
- [JavaDoc](#javadoc)
  - ["Manually" on the command line](#manually-on-the-command-line-3)
  - [Using Maven](#using-maven-3)
  - [With Gradle](#with-gradle-3)

<br />

## About the project

The project is composed of individual classes, where each is a solution to a puzzle from [advent of code](https://adventofcode.com/). At present solutions only cover Day 1 to Day 25 of the year 2021.

Each source file, `Day<X>` where `X` is between 1 and 25, has a comment section at the top that describes and explains the solution.

None of the solutions are aimed at being optimal with regard to speed or lines of codes. Some of the solutions are rather verbose. The solution to Day 23 is quite slow talking roughly 4 seconds. The aim is to explain the idea of the solution and the implementation is a proof of concept.

There are occasionally multiple different solutions to the same puzzle. Only one of them is run by default.

Three ways to compile and test the project have been included:
  - "Manually" on the command line.
  - Using Maven
  - With Gradle

The idea is for everyone to have options and pick their preferred tool. Only one of the three is needed to use the project. There's also a diffence in required disk space. "Manually" includes 7 files already in the project. They amount to about 30 KB of space, but requires a manual download of libraries (additionally 3 MB) to run tests. Maven needs around 60 MB and Gradle will take 160 MB.

<br />

## © Copyright

This project is under normal [copyright](https://en.wikipedia.org/wiki/Copyright). No permissive licenses are granted.

You can download the repository and personally play with it, but do *not* distribute it. That includes not distributing modified versions, derivatives and/or adaptations.

<br />

## Custom scripts

The development of the project was done on Windows. The subheaders marked **"Manually" on the command line** uses plain scripts. One set for the Windows command prompt, the "cmd". Another set for Linux bash. The bash scripts have not been tested on Mac.

The scripts in the `cmdScripts` folder for **"Manually" on the command line** only works when calling them from the *root of the project*. Executing them from inside the `cmdScripts` folder will cause them to error. This also applies to the bash scripts located in `bashScripts`.

### Linux

#### Carriage return

Since the project was created on Windows, all files are likely to contain the Windows line terminator, both "carriage return" and "line feed". See [Newline at wikipedia](https://en.wikipedia.org/wiki/Newline) for more information. This can cause issues when running the project as is on Linux. To check the line terminator use the -v flag of cat. For example from *the root of the project* illustrated by `~/advent-of-code $`:

```
~/advent-of-code $ cat -v resources/2021/sample/Day3.txt
```

If the file contains `^M` at the end of each line:

```
00100^M
11110^M
...
```

the extra carriage return used by Windows should be removed from all files.

First remove it from this script:

```
~/advent-of-code $ sed -i 's/\r$//' bashScripts/GraysFixFiles.sh
```

then run the script:

```
~/advent-of-code $ bash bashScripts/GraysFixFiles.sh
```

which will remove the carriage return from all other files in the project.


#### Running the scripts

The scripts can be run using:

```
~/advent-of-code $ bash bashScripts/<FULL FILENAME>
```

Examples for **"Manually" on the command line** will only include how to run the scripts in `cmdScripts` on Windows. The included scripts for Linux in `bashScripts` do the same as those for Windows in `cmdScripts`. They have the same names, except for the extension being `.sh`. **Note** that Linux uses forward slash `/`, not backslash `\`.


It's possible, but not necessary, to run the script without explicitly calling `bash`:

```
~/advent-of-code $ bashScripts/GraysFixFiles.sh
```

but that will require the scripts being executable, adding the "x" to **u**ser:

```
~/advent-of-code $ chmod u+x bashScripts/*.sh
```

<br />

## Requirements

### JDK (Java Development Kit)

Without the JDK, it's not possible to compile the source, so it needs to be installed. This project was compiled and run with Java 11, but since Java is backwards compatible, any version from 11 onwards is likely to work as well.

It can be downloaded from:
- [Oracle](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK](https://jdk.java.net/)
- [OpenJDK archive for older versions](https://jdk.java.net/archive/)

### Maven

Having Maven is not a requirement. It's a build and project life-cycle tool that is optional. See the **"Manually" on the command line** subheaders for how to use the project/program without using Maven.

However, it's quite simple to install. It just needs to be unpacked and added to the `PATH`. It can be downloaded from [Maven at Apache](https://maven.apache.org/download.cgi). Here's the [installation nstructions](https://maven.apache.org/install.html) The installation when unpacked is roughly 25MB.

Maven will fetch all the packages it needs (that it didn't already fetch) from the internet. The first commands can therefore be quite verbose since all the information about what Maven fetches is displayed. For this project it will fetch roughly 35MB. The packages it fetches will all go into the `C:\Users\Me\.m2` directory by default.

### Gradle

This is also optional. Like Maven, it's a build tool. It does the same thing and it's installed the same way. Just unpack it and add it to the `PATH`. This is the official [installation instructions](https://gradle.org/install/). Gradle can be downloaded from their [Releases](https://gradle.org/releases/) page.

Gradle's requirements on disk space is around 125MB for the unpacked installation itself.

Like Maven, it also fetches packages it needs from the internet. Though it does so silently. It saves its fetched libraries into the `C:\Users\Me\.gradle\` folder. More precisely in the `caches\modules-2\` subfolder. The size of fetched libraries for this project is the same as for Maven at roughly 25MB.

<br />

## Compiling the project

For the purpose of the examples here, it is assumed that the project is situated inside a folder called `advent-of-code`, so that the structure if running `tree` from

```
C:\Users\Me\advent-of-code>
```

..only including folders is:

```
C:.
├───libraries             <-- doesn't exist in this repository.
├───resources
│   └───2021
│       ├───challenge
│       ├───sample
│       └───_results_
│           ├───challenge
│           └───sample
└───source
    ├───absbase
    ├───base
    ├───javaDoc
    ├───test
    ├───util
    └───Year2021
```

The program does not depend on the folder name of `advent-of-code`. It can be anything.

### "Manually" on the command line

To compile the project, just run this:

```
C:\Users\Me\advent-of-code> cmdScripts\GraysPile
```

It's a `.cmd` script that first collects all the `.java` files from the source into a file `tocompile.txt`. Then it compiles all of them using `javac -cp run -d run @tocompile.txt`. The resulting `.class` files will go into a `run` directory (since that's specified with the `-d` option) with the exact same structure as the `source` folder.

Using a forward slash `/` instead of a backslash `\` will *not* run the script, but instead produce an error:

```
C:\Users\Me\advent-of-code> cmdScripts/GraysPile
'cmdScripts' is not recognized as an internal or external command,
operable program or batch file.
```

### Using Maven

Just as simple, just run:

```
C:\Users\Me\advent-of-code> mvn clean compile
```

Note that this project does not conform to the default Maven project structure. The pom.xml has been modified to properly reflect the structure of this project.

### With Gradle

Run:

```
C:\Users\Me\advent-of-code> gradle compileJava --console=verbose
```

If the `--console=verbose` is omitted, Gradle will output something along:

```
BUILD SUCCESSFUL in 12s
1 actionable task: 1 executed
```

while the "verbose" option prints out something similar to:

```
> Task :compileJava

BUILD SUCCESSFUL in 14s
1 actionable task: 1 executed
```

### Changing the output directory

All the compiled files can go into a different directoy than `run`.

Note that the extra parameter should be used with every command.
<br />Running a Day or AllDays must also use someOtherDir instead of `run`.

#### Using GraysPile ("Manually"):

```
C:\Users\Me\advent-of-code> cmdScripts\GraysPile -Drun.dir=someOtherDir
```

#### With Maven it's:

```
C:\Users\Me\advent-of-code> mvn clean compile -Drun.dir=someOtherDir
```

#### Gradle will need:

```
C:\Users\Me\advent-of-code> gradle compileJava -Drun.dir=someOtherDir
```

#### PowerShell quirck

The `-Drun.dir` construct causes PowerShell to separate the variable name, so use `-D"run.dir"=someOtherDir` instead.

<br />

## Running the program

Specify which Day to run, like so:

```
C:\Users\Me\advent-of-code> java -cp run Year2021.Day1
```

..that will output:

```
=2021= Day1  - result             :          7   time:     5242400 nano,     5242 micro,     5 milli
=2021= Day1  - result-2           :          5   time:    41310900 nano,    41310 micro,    41 milli
```

Since Java is a little picky on package names, requiring them to be valid identifiers which excludes them starting with a digit, the years are prefixed with "Year", hence 2021 will have the package name "Year2021".

Running a Day with ascii output, will print the output:

```
=2021= Day13 - result             :         17   time:    61201900 nano,    61201 micro,    61 milli
=2021= Day13 - result-2           :        ---   time:    26468300 nano,    26468 micro,    26 milli
=2021= Day13 - ascII-result-2:
#####
#   #
#   #
#   #
#####
```

Running all the days in one go:

```
C:\Users\Me\advent-of-code> java -cp run AllDays
```

..will output:

```
==== Running year: 2021 ====
Day            result-1      time-1 micro          result-2      time-2 micro
-----  ----------------  ----------------  ----------------  ----------------
Day1                  7              1172                 5             14532
Day2                150               377               900                19
Day3                198             14681               230             10485
Day4               4512             16432              1924                46
Day5                  5              8989                12              3584
Day6               5934             23762       26984457539              1423
Day7                 37              2780               168             11213
Day8                 26             18788             61229             30914
Day9                 15              6312              1134             14909
Day10             26397              1312            288957              1373
Day11              1656             17401               195              7336
Day12               226             47650              3509            386867
Day13                17             32045               ---             46789
Day14              1588            115734     2188189693529              2889
Day15                40             17837               315             60699
Day16                12             14345                46             10422
Day17                45               185               112             34515
Day18              4140             25509              3993             20294
Day19                79            210256              3621                52
Day20                35             45460              3351             97659
Day21            739785               742   444356092776315             30825
Day22            474140            104051  2758514936282235             10800
Day23             12521            408756             44169           6730438
Day24    97919997299495              6798    51619131181131               123
Day25                58             10659
-----  ----------------  ----------------  ----------------  ----------------
Total in milli                     1152ms                              7528ms
```

The command line argument `-cp` is for "classpath" and it tells Java where all the class files are to be found. In this case this is in the subfolder "run".

### -h or -hf

To get a small list of options, use the `-h` or `--help`.

```
C:\Users\Me\advent-of-code> java -cp run Year2021.Day1 -h
```

..using `-hf` or `--helpFileOptions` instead will give a list of input file options.

### -noTime

This will not print the runtime:

```
=2021= Day1  - result   :          7
=2021= Day1  - result-2 :          5
```

### -alt \<INTEGER\>

When several solutions have been implemented, they can be run in a loop. The integer value is the loop value:

```
C:\Users\Me\advent-of-code> java -cp run Year2021.Day2 -alt 3
```

Here the alternatives are looped a total of 3 times:

```
=2021= Day2  - result  SetupStream:        150   time:    35472599 nano,    35472 micro,    35 milli
=2021= Day2  - result SetupForLoop:        150   time:      751100 nano,      751 micro,     0 milli
=2021= Day2  - result  SetupStream:        150   time:      170000 nano,      170 micro,     0 milli
=2021= Day2  - result SetupForLoop:        150   time:      345300 nano,      345 micro,     0 milli
=2021= Day2  - result  SetupStream:        150   time:      268599 nano,      268 micro,     0 milli
=2021= Day2  - result SetupForLoop:        150   time:      388200 nano,      388 micro,     0 milli
```

Following Days have alternatives:
- Day2
- Day5
- Day6
- Day7
- Day12
- Day13
- Day15
- Day19 (Doesn't have an alternative, but it can still be looped)
- Day21
- Day22

Note that this options is ignored if running `AllDays`.

### -o \<FILE\>

Does not print to the console, but outputs the result to the specified file **overriding** the content of the file. The file path can be either relative or absolute, meaning

- `-o myOutput.txt` will save the file to `C:\Users\Me\advent-of-code\myOutput.txt`
- `-o out\myOutput.txt` will save the file to `C:\Users\Me\advent-of-code\out\myOutput.txt`
- `-o C:\Users\Me\outputtest.txt` ..you guessed it :)

### -f \<FILE\>

Specifies a relative or absolute path and filename to the inputfile. For example:

```
C:\Users\Me\advent-of-code> java -cp run Year2021.Day2 -f C:\Users\Me\inputDay2_B.txt
```

This option obviously does not require a `resources` folder.


Please see the **Input** section on how the input files are structured.

### Other filepath options

The location of an input file

```
advent-of-code\resources\2021\sample\Day7.txt
```

is broken down into

```
executionRoot\directoryParent\directoryYear\directory\filename
```

the options (and precedence) to override the defaults are:
- `-f`
- `executionRoot\-p\filename`
- `executionRoot\-dp\-dy\-d\-n`

They can be changed with options:

- -p \<PATH\>

  the path prior to `\filename` excluding the executionRoot.

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -p res\21
  ```
  will expect the input file at `advent-of-code\res\21\Day7.txt`

  <br />

- -dp \<NAME\>

   The default is `resources`.

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -dp res
  ```
  will expect the input file at `advent-of-code\res\2021\sample\Day7.txt`

  <br />

- -dy \<NAME\>

   The default is the year of the package, in this case: `2021`.

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -dy theYearOf2021
  ```
  will expect the input file at `advent-of-code\resources\theYearOf2021\sample\Day7.txt`

  <br />

- -d \<NAME\>

   The default is `sample`.

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -d challenge
  ```
  will expect the input file at `advent-of-code\resources\2021\challenge\Day7.txt`.
  This option is very useful when adding a separate subfolder containing a different set of input data.

  <br />

- -n \<NAME\>

   The default is the class name with the surname of ".txt" In this case: `Day7.txt`.

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -n myDay7file.txt
  ```
  will expect the input file at `advent-of-code\resources\2021\sample\myDay7file.txt`.

  This option allows for `%` in place of class names, so

  ```
  C:\Users\Me\advent-of-code> java -cp run Year2021.Day7 -n %_mine.txt
  ```
  will expect the input file at `advent-of-code\resources\2021\sample\Day7_mine.txt`.
  This option is very useful when running `AllDays`.

The options can be combined with the exception of precendence, where for example `-f` will override any other file option.

### -y \<4 DIGIT INTEGER\>

This is only valid when running `AllDays`. It will be run for the specified year only.

```
C:\Users\Me\advent-of-code> java -cp run AllDays -y 2039
```

Years can be specified comma separated, `2021,2039`. If there are spaces, it must be enclosed in quotes: `"2021, 2039"`.

If a year is picked that isn't implemented, a message for that year will be printed:

```
Package Year2039 does not exist
```

`AllDays` "self-discoveres" Day implementations. It will run any Day that it can find that is residing in packages prefixed with "Year".

<br />

## Running the test

### "Manually" on the command line

The test depends on two library `.jar` files:
- [gson-2.8.9.jar](https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.9)
- [junit-platform-console-standalone-1.8.2.jar](https://mvnrepository.com/artifact/org.junit.platform/junit-platform-console-standalone/1.8.2)

Download them by clicking on the `jar`-button on the "Files" line. Create a folder, `libraries`, (marked **<-- doesn't exist in this repository** in the folder structure shown) and place the `.jar`-files in it.

Newer version of the two libraries may work too, but those have obviously not been tested.

First compile the project including the test:

```
C:\Users\Me\advent-of-code> cmdScripts\GraysPileTest
```

then run the test with

```
C:\Users\Me\advent-of-code> java -jar libraries/junit-platform-console-standalone-1.8.2.jar --disable-ansi-colors --class-path run:libraries/gson-2.8.9.jar -c test.DaysTest
```

The result should look like this:

```
.
+-- JUnit Jupiter [OK]
| '-- DaysTest [OK]
|   +-- Testing all DayX against json exptected results [OK]
|   | +-- [1] -> 2021 - Day1 :7:5: sample [OK]
|   | +-- [2] -> 2021 - Day2 :150:900: sample [OK]
|   | +-- [3] -> 2021 - Day3 :198:230: sample [OK]
|   | +-- [4] -> 2021 - Day4 :4512:1924: sample [OK]
|   | +-- [5] -> 2021 - Day5 :5:12: sample [OK]
|   | +-- [6] -> 2021 - Day6 :5934:26984457539: sample [OK]
|   | +-- [7] -> 2021 - Day7 :37:168: sample [OK]
|   | +-- [8] -> 2021 - Day8 :26:61229: sample [OK]
|   | +-- [9] -> 2021 - Day9 :15:1134: sample [OK]
|   | +-- [10] -> 2021 - Day10 :26397:288957: sample [OK]
|   | +-- [11] -> 2021 - Day11 :1656:195: sample [OK]
|   | +-- [12] -> 2021 - Day12 :226:3509: sample [OK]
|   | +-- [13] -> 2021 - Day13 :17:ascII: sample [OK]
|   | +-- [14] -> 2021 - Day14 :1588:2188189693529: sample [OK]
|   | +-- [15] -> 2021 - Day15 :40:315: sample [OK]
|   | +-- [16] -> 2021 - Day16 :12:46: sample [OK]
|   | +-- [17] -> 2021 - Day17 :45:112: sample [OK]
|   | +-- [18] -> 2021 - Day18 :4140:3993: sample [OK]
|   | +-- [19] -> 2021 - Day19 :79:3621: sample [OK]
|   | +-- [20] -> 2021 - Day20 :35:3351: sample [OK]
|   | +-- [21] -> 2021 - Day21 :739785:444356092776315: sample [OK]
|   | +-- [22] -> 2021 - Day22 :474140:2758514936282235: sample [OK]
|   | +-- [23] -> 2021 - Day23 :12521:44169: sample [OK]
|   | +-- [24] -> 2021 - Day24 :97919997299495:51619131181131: sample [OK]
|   | '-- [25] -> 2021 - Day25 :58:null: sample [OK]
|   +-- Checking file related errors... [OK]
|   | '-- [1] [OK]
|   '-- Finding missing implementations... [OK]
|     '-- [1] [OK]
'-- JUnit Vintage [OK]

Test run finished after 9925 ms
[         6 containers found      ]
[         0 containers skipped    ]
[         6 containers started    ]
[         0 containers aborted    ]
[         6 containers successful ]
[         0 containers failed     ]
[        27 tests found           ]
[         0 tests skipped         ]
[        27 tests started         ]
[         0 tests aborted         ]
[        27 tests successful      ]
[         0 tests failed          ]
```

### Using Maven

Since dependencies are specified in the `pom.xml`, Maven will download those automatically. Just run:

```
C:\Users\Me\advent-of-code> mvn clean test
```

Maven is not as verbose and the result of the test is quite compressed in comparison:

```
...
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running test.DaysTest
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.474 s - in test.DaysTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 27, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  15.166 s
[INFO] Finished at: 2022-02-20T02:07:11+01:00
[INFO] ------------------------------------------------------------------------
```

### With Gradle

Dependencies are specified in the `gradle.build` file and are downloaded by Gradle. Just run:

```
C:\Users\Me\advent-of-code> gradle test --console=verbose
```

Gradle is the least verbose and the test results has been added to the `gradle.build` explicitly:

```
...
> Task :test
 --------------------------------------------------------------------
|  Test results: SUCCESS (27 tests, 27 passed, 0 failed, 0 skipped)  |
 --------------------------------------------------------------------

BUILD SUCCESSFUL in 24s
4 actionable tasks: 3 executed, 1 up-to-date
```

<br />

## Package the project into a .jar

Running the project as a `.jar` file will *still require* a `resource` folder external to the `.jar`-file at the root at the running command. The included `resource` folder in the `.jar` file is only meant to show the expected structure, and the folder can be extracted to the root as is.

However, the project is certainly not a library, so there's no point in packaging it.
<br /> For the same reason, the project hasn't been made into a Java module.

### "Manually" on the command line

```
C:\Users\Me\advent-of-code> cmdScripts\GraysJar
```

which will create a `advent-of-code-1.0.jar` in a `target` folder.

It can be run using:

```
C:\Users\Me\advent-of-code> java -jar target/advent-of-code-1.0.jar
```

this will run the `AllDays` program only.

To run a Day from the `.jar` file, use

```
C:\Users\Me\advent-of-code> java -cp target/advent-of-code-1.0.jar Year2021.Day2
```

### Using Maven

```
C:\Users\Me\advent-of-code> mvn clean package
```

which will also create a `advent-of-code-1.0.jar` in a `target` folder.


### With Gradle

```
C:\Users\Me\advent-of-code> gradle jar --console=verbose
```

The `advent-of-code-1.0.jar` will be located in the `build\libs` folder instead.

<br />

## Input

The data is expected in a folder called `resources` at the *location where the project is run*.

```
C:\Users\Me\advent-of-code> java -cp run Year2021.Day2
```

..assuming the class files is in the `run` directory.

E.g. if the project is run from `C:\Users\Me\advent-of-code`, all the input data is expected to be in `C:\Users\Me\advent-of-code\resources`. Each year should be a subfolder, like 2021, and subfolders in `resources/2021/` is expected to have input files, with the name corresponding to the Day, so data for Day3 is expected to be in a file named `Day3.txt`:

```
C:\Users\Me\advent-of-code\resources
└───2021
    ├───challenge
    │       Day1.txt
    │       Day2.txt
    │       Day3.txt
    │       Day4.txt
    |       ...
    │       Day25.txt
    ├───sample
    │       Day1.txt
    │       Day2.txt
    │       Day3.txt
    │       Day4.txt
    |       ...
    │       Day25.txt
    └───_results_
        │   results.json
        │
        ├───challenge
        │       Day13Part2AscII.txt
        │
        └───sample
                Day13Part2AscII.txt
```

Note that if a Day is run with no options, it will default to finding the input in the `sample` folder.

### Test input

The test will run input from the folder as structured above, and compare the results with data in the `results.json` located in `\resources\_results_`. The json file is expected to be be structured like so:

```
{
  "Day1": {
    "sample": {
      "part1": 7,
      "part2": 5
    },
    "challenge": {
      "part1": 1057,
      "part2": 1007
    }
  },
  "Day2": {
    "sample": {
      "part1": 150,
      "part2": 900
    }
  },
  ...
  "Day13": {
    "sample": {
      "part1": 17,
      "part2": "ascII"
    }
  },
  ...
}
```

If the result is some kind of ascii representation, the ascii expected result must be in a subfolder of the same name as the data input, and the file name must comply with `Day<X>` + `Part<Y>` + `AscII.txt`, like: `Day13Part2AscII.txt`.

<br />

## JavaDoc

Three packages contain JavaDoc comments

- base
- absbase
- util

since those packages form the basis for the AllDays, the test and the Day implementations.

JavaDoc is created from the source files directly. Once JavaDoc is created, all its files will appear in a `javadoc` folder. In this case at `C:\Users\Me\advent-of-code\javadoc\`. Open the file named `index.html` in a browser to view the JavaDoc files.

Create the JavaDoc with:

### "Manually" on the command line

```
C:\Users\Me\advent-of-code> javadoc @source\javaDoc\args.txt
```

### Using Maven

```
C:\Users\Me\advent-of-code> mvn javadoc:javadoc
```

### With Gradle

```
C:\Users\Me\advent-of-code> gradle javadoc --console=verbose
```