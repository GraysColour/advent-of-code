package test;

/*
  Note that this test is somewhat convoluted.
  Its aim is to use a parametrized test over a stream<Arguments>.
  The implementation finds implemenations of days
  then maps those to the corresponding tests in result.json files,
  initializes & runs the days and compares the results.


  ### Test data:
    Test are expected to be present inside the folder:
      \advent-of-code\resources\YYYY\_results_\
    where YYYY is the year, like 2021.
    A results.json is expected in this folder, with each day having expected results:
        {
          "Day1": {
            "sample": {
              "part1": 7,
              "part2": 5
            },
            "challenge": {     <-- this folder in resources\YYYY\ is not present in this
              "part1": 1154,       repository, but is here only for demonstration purposes
              "part2": 1127
            },
          },
          "Day2": {
            "sample": {
              "part1": 150,
              "part2": 900
            },
          ...
        }
    Note that "sample" and "challenge" must be folders in \resources\YYYY\
    which contains input files for each day: Day1.txt, Day2.txt, ...

    If the result is some ascII, then the test result must contain the word "ascII":
          "Day13": {
            "sample": {
              "part1": 17,
              "part2": "ascII"
            },
    and a corresponding folder is required, with a file containing the ascII
      \advent-of-code\resources\YYYY\_results_\sample\Day13Part2AscII.txt
    with the name of the text file being
      dayX + part + ascIIFileSuffix
    as seen here for Day13 + Part2 + AscII.txt


  ### The implmentation idea:
    Get the path of the result.json files in the resources
    folder corresponding to the each year of the array variable "years":
      {"2021","2022"}
    assuming the folderstrucure of the array variable "folders".
    Where the replaceFolder litteral is in place of the year litteral value:
      {"resources", DaysTest.replaceFolder, "_results_"}

    Then add that to the map
      packageJsonParentMap
    so it contains mappings of each year to the path to the parent folder:
      key    value
      2021   C:\..snip..\advent-of-code\resources\2021\_results_
      2022   C:\..snip..\advent-of-code\resources\2022\_results_

    Also using the array variable "years",
    but excluding any empty paths from the above packageJsonParentMap,
    map each year to the expected result in that years results.json file
    in the map
      folderResultMap
    since it's a  Map<String, Map<String, Map<String, Map<String, String>>>>
    with values like  2021        Day1        sample      part1   7

    it can be visualised with:
      2021:                 key
            "Day1": {         key
              "sample": {       key
                "part1": 7,       key: value
                "part2": 5        key: value
              },
              "challenge": {    key
                "part1": 1154,    key: value
                "part2": 1127     key: value
              },
            ....

    or visualised with:
      key      value                              <-- value is a new map
      2021     key      value                     <-- value is a new map
               Day1     key        value          <-- value is a new map
                        sample     key     value
                                   part1   7
                                   part2   5
                        challenge  key     value
                                   part1   1154
                                   part2   1127

    Using the year key of folderResultMap, all DayX implementations
    can be found by the packagename of "Year"+year, for example:
      2021 will have all DayX implementations in the package: Year2021.

    Using the mapper(year, day) each day is now mapped to a stream of test Arguments:
      [package, day, dayArgs, Result part1, Result part2]
    for example:
      [Year2021, Day1, ["-d", "sample"],    Result instance of 7,    Result instance of 5]
      [Year2021, Day1, ["-d", "challenge"], Result instance of 1154, Result instance of 1127]
    where the Result instances are the expected results.

    Since each day creates a stream<Auguments>, each with a separate test,
    those are flatmapped into a single stream containing all days.
    The streams are created in a loop of each year,
    so another flatmap is needed to create a single stream<Auguments>.

    The test gets the stream<Arguments> by "@ParameterizedTest"
    and creates an instance of the day using
    the "package" & "day" to isolate the class and the "dayArgs"
    to ensure the day instance will read the corresponding input file.
    Then it runs the daySolver() and compares the Results.

    If a test has been created in the results.json, but
    no folder exists with a corresponding input, the test will fail like so:
        +-- [1] -> 2022 - Day1 :11:13: foldername [X] expected: <NORMAL_LONG> but was: <NO_FILE>

    Based on the folderResultMap a map of year & day to a boolean is created in
      implementationExists
    Each day will be marked as true in this map by the mapper(year, day).
    Once the tests are completed a new test is run and
    any missing implementations will result in a failed test, like so:

      '-- Finding missing implementations... [OK]
        +-- [1] [X] expected: <Not missing> but was: <2021 - Day2 is missing>
        +-- [2] [X] expected: <Not missing> but was: <2021 - Day14 is missing>
      ...

    This can be removed from the test by adding/"commenting in"
    the @Disabled test annotation for
      testMissing(String message)
    at the bottom.
*/

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.Reader;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Comparator;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import base.*;
import util.*;

// https://junit.org/junit5/docs/5.7.2/api/index.html
// https://junit.org/junit5/docs/current/user-guide

@TestMethodOrder(OrderAnnotation.class)
public class DaysTest {

  // modify this for selectively include years in the test {"2021","2022"}
  // if left blank, all implemented years will be tested
  private static String[] years;

  private final static String replaceFolder = "dummyReplace";
  private static String[] folders
    = new String[]{"resources", DaysTest.replaceFolder, "_results_"};

  private static String jsonResultFilename = "results.json";

                  // 2021    C:\..snip..\advent-of-code\resources\2021\_results
  private static Map<String, String> packageJsonParentMap;

                  // 2021        Day1        sample      part1   7
  private static Map<String, Map<String, Map<String, Map<String, String>>>> folderResultMap;

                  // 2021        Day1    true/false
  private static Map<String, Map<String, Boolean>> implementationExists;
  private final static String notMissing = "Not missing";

  private final static String ascII = "ascII";
  private final static String ascIIFileSuffix = "AscII.txt";

  private final static String noErrors = "No errors";
  private final static List<String> errors = new ArrayList<>();


  @BeforeAll
  private static void init() {

    if (DaysTest.years == null || DaysTest.years.length == 0) {

      String[] classPaths = getCleanClassPath();
      if (classPaths.length == 0) { // this is true when manually running the test.
        classPaths = new String[]{DataReader.getRunDir()};  // directory containing classes.
      }

      DaysTest.years = Stream.of(classPaths)
                             .map(classPath -> Classes.getYearPackagesFile(classPath))
                             .flatMap(list -> list.stream())
                             .map(str -> str.replace("Year",""))
                             .distinct()
                             .toArray(String[]::new);
    }

    DaysTest.packageJsonParentMap
      = Arrays.stream(DaysTest.years)
              .collect(Collectors.toMap(year -> year,
                                        year -> getResultJsonParentPath
                                                 (
                                                   year,
                                                   DaysTest.folders,
                                                   DaysTest.jsonResultFilename
                                                 )
              ));

    DaysTest.folderResultMap
      = Arrays.stream(DaysTest.years)
              .filter(str -> !DaysTest.packageJsonParentMap.get(str).isEmpty())
              .collect(Collectors.toMap(key -> key,
                                        key -> getDaysFromJson
                                                 (
                                                   Path.of(packageJsonParentMap.get(key),
                                                           DaysTest.jsonResultFilename)
                                                 )
              ));

    // initialise implementationExists to be false for all tests
    // once tests are created, entries will be marked true
    DaysTest.implementationExists
      = DaysTest.folderResultMap
                .entrySet()
                .stream()
                .collect(Collectors
                           .toMap(year -> year.getKey(),      // 2021
                                  year -> year.getValue()
                                              .entrySet()
                                              .stream()      // Day0 is never required
                                              .filter(day -> !"Day0".equals(day.getKey()))
                                              .collect(Collectors      // Day1, false
                                                         .toMap(day -> day.getKey(),
                                                                day -> false))));
  }

  private static String[] getCleanClassPath() {
    return Stream.of(System.getProperty("java.class.path")
                           .split(System.getProperty("path.separator")))
                 .filter(str -> !str.endsWith(".jar"))
                 .distinct()
                 .toArray(String[]::new);
  }


  @Order(1)
  @DisplayName("Testing all DayX against json exptected results")
  @ParameterizedTest(name = "[{index}] -> {0}")
  @MethodSource("dayTests")
  void testDayXs(Object[] dayExpected) {
    DayI dayI = Classes.createClass((String) dayExpected[1],    // Day2
                                    (String) dayExpected[0],    // Year2021
                                    (String[]) dayExpected[2]); // args[]
    dayI.daySolver();

    testIt((Result) dayExpected[3], dayI.getResult());          // expected Result part1
    testIt((Result) dayExpected[4], dayI.getResultPart2());     // expected Result part2
  }

  private void testIt(ResultI expected, ResultI actual) {
    if (expected != null) {
      if (actual.isValid()) {
        assertEquals(expected.getResult(),
                     actual.getResult());

        assertArrayEquals(expected.getAscIIResult(),
                          actual.getAscIIResult());
      } else {
        assertEquals(expected.getState().getStatus(), actual.getState().getStatus());
      }
    } else {
      assertEquals(Status.NO_RESULT, actual.getState().getStatus());
    }
  }

  // ----
  private static Stream<Arguments> dayTests() {
    return
      DaysTest.folderResultMap // holds only years with a json file
              .entrySet()
              .stream()
              .sorted(Comparator.comparing(entry -> entry.getKey()))
              .map(entry -> {
                    String year = entry.getKey(); // 2021
                    return
                              // Day1, Day2, Day3, ....
                      Classes.classNamesAsStreamFile(getPackageName(year))
                               // map each day to its tests
                              .map(day -> mapper(year, day))
                               // each day may have severals tests, and they are streamed
                              .flatMap(stream -> stream);
               })
             // each year is a stream
            .flatMap(stream -> stream);
  }

  // ----
  private static Stream<Arguments> mapper(String year, String day) {
    Map<String, Map<String, String>> dayResults
      = DaysTest.folderResultMap.get(year).get(day);
    if (dayResults == null) {
      DaysTest.errors.add(String.format("No tests found for: %s - %s", year, day));
      return Stream.empty();
    }

    // mark the day as implemented
    DaysTest.implementationExists.get(year).put(day, true);

    return
      dayResults.entrySet()
                .stream()
                .map(test -> {
                       // [package, day, dayArgs, Result part1, Result part2]
                       Object[] mapping = new Object[5];
                       mapping[0] = getPackageName(year);
                       mapping[1] = day;

                       String directory = test.getKey();
                       String[] dayArgs = new String[]{"-d", directory};
                       mapping[2] = dayArgs;

                       Map<String, String> testResults = test.getValue();
                       String part1result = testResults.get("part1");
                       String part2result = testResults.get("part2");
                       mapping[3] = createResult(year, day, directory, part1result, "Part1");
                       mapping[4] = createResult(year, day, directory, part2result, "Part2");

                       // Stream<Objects[]> causes an error
                       // See https://github.com/junit-team/junit5/issues/2708
                       return Arguments.of(Named.of(createTestName(year,
                                                                   day,
                                                                   directory,
                                                                   part1result,
                                                                   part2result),
                                                   (Object) mapping));
                 });
  }

  // ----
  private static String getPackageName(String year){
    return "Year" + year;
  }

  // ----
  private static String createTestName(String packageName,
                                       String dayX,
                                       String directory,
                                       String part1,
                                       String part2) {
      // Day1 :7:5: sample
      StringBuilder testname = new StringBuilder();
      testname.append(packageName);
      testname.append(" - ");
      testname.append(dayX);
      testname.append(" :");
      testname.append(part1);
      testname.append(":");
      testname.append(part2);
      testname.append(":");
      testname.append(" ");
      testname.append(directory);
      return testname.toString();
  }

  // create a Result from the exptected value of the json file
  private static ResultI createResult(String year,
                                      String dayX,
                                      String directory,
                                      String result,
                                      String part) {
    if (result != null) {
      if (!DaysTest.ascII.equals(result)) {
        return Result.createResult(Long.valueOf(result));
      } else {
        return DataReader.readAscIIFile(Path.of(packageJsonParentMap.get(year),
                                        directory,
                                        dayX + part + ascIIFileSuffix));
      }
    }
    return null;
  }

  // https://www.javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/module-summary.html
  private static Map<String, Map<String, Map<String, String>>> getDaysFromJson(Path jsonPath) {

    try (Reader reader = Files.newBufferedReader(jsonPath)) {
      Gson gson = new Gson();
      Type type = new TypeToken<Map<String, Map<String, Map<String, String>>>>(){}.getType();
      /*
            "Day1": {         key
              "sample": {       key
                "part1": 7,       key: value
                "part2": 5        key: value
              },
              "challenge": {    key
                "part1": 1154,    key: value
                "part2": 1127     key: value
              },
            ....
      */
      //  Day1        sample      part1   7
      Map<String, Map<String, Map<String, String>>> map = gson.fromJson(reader, type);

      if (map != null) {
        return map;
      } else {
        DaysTest.errors.add("Empty file: " + jsonPath);
        return Map.of("dummy", Map.of());
      }

    } catch (JsonSyntaxException | JsonIOException | IOException e) {
      e.printStackTrace();
    }

    return Map.of("dummy", Map.of());
  }

  // ----
  private static String getResultJsonParentPath(String packageName,
                                                String[] directories,
                                                String jsonFile) {

    // Path.of needs one vararg, so the arrays must be exted with the jsonFile
    String[] fullPath = Arrays.copyOf(directories, directories.length + 1);
    for (int i = 0; i < directories.length; i++) {
      fullPath[i] = fullPath[i].replace(DaysTest.replaceFolder, packageName);
    }
    fullPath[directories.length] = jsonFile;

    try {

      URL current =
        new Classes().getClass()
                     .getClassLoader()
                     .getResource("."); // current classpath directory

      Path thePath = Paths.get(current.toURI());
      while (Files.exists(thePath)) {
        // see if current\directories\jsonFile exists
        Path test = Path.of(thePath.toString(), fullPath);
        if (Files.exists(test)) {
          // C:\..snip..\advent-of-code\resources\2021\_results
          return Path.of(thePath.toString(),
                         Arrays.copyOf(fullPath, directories.length)).toString();
        }
        thePath = thePath.getParent(); // move up one directory
      }

    } catch (URISyntaxException e) {
      e.printStackTrace();
    } catch (NullPointerException e) {
      // happens when reaching: test = C:\resources\YYYY\_results_\results.json
      // since "C:", the root directory, doesn't have a parent
      DaysTest.errors.add(
        String.format("No file found for: %s",
                      Arrays.stream(fullPath)
                            .collect(Collectors
                                       .joining(System.getProperty("file.separator")))));
    }

    return "";
  }


  @Order(2)
  @DisplayName("Checking file related errors...")
  @ParameterizedTest(name = "[{index}]")
  @MethodSource("errorStream")
  void testError(String message) {
    assertEquals(DaysTest.noErrors, message);
  }

  // ----
  static Stream<String> errorStream() {
    if (errors.size() == 0) {
      return Stream.of(DaysTest.noErrors);  // making the test pass
    } else {
      return errors.stream();
    }
  }


  // @Disabled("Currently disabled!..")
  @Order(3)
  @DisplayName("Finding missing implementations...")
  @ParameterizedTest(name = "[{index}]")
  @MethodSource("missingImplStream")
  void testMissing(String message) {
    assertEquals(DaysTest.notMissing, message);
  }

  // ----
  static Stream<String> missingImplStream() {
    String missing = " is missing";

    Comparator<String> nameCompare =
      Comparator.comparingInt(str -> Integer.parseInt(str.substring(0,4)) * 100
                                       + Integer.parseInt(str.substring(10,
                                                                        str.indexOf(missing))));

    List<String> missimgImplList
      = DaysTest.implementationExists
                .entrySet()
                .stream()
                .map(yearEntry -> yearEntry.getValue()
                                           .entrySet()
                                           .stream()
                                           .filter(entry -> !entry.getValue())
                                           .map(entry -> yearEntry.getKey()
                                                         + " - "
                                                         + entry.getKey()
                                                         + missing))
                .flatMap(stream -> stream)
                .sorted(nameCompare)
                .collect(Collectors.toList());

    if (missimgImplList.size() == 0) {
      return Stream.of(DaysTest.notMissing);  // making the test pass
    } else {
      return missimgImplList.stream();
    }
  }

}
