package util;

import java.util.stream.IntStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

import base.*;

public class Printers {

  private static PrintStream target = System.out;
  private static boolean isWindows = System.getProperty("os.name").indexOf("Windows") > -1;

  private Printers() {}

  public static void setTarget(String targetString) {
    try {
      // for "outputFile.txt" input strings, the absolutePath is needed to find the parent.
      Path path = Path.of(targetString).toAbsolutePath();

      if (!Files.exists(path)) {
        Path parent = path.getParent();
        if (!Files.exists(parent)) {
          Files.createDirectories(parent);
        }
      }

      Files.createFile(path);
      Printers.target = new PrintStream(path.toFile());

    } catch (IOException ex) {
      ex.printStackTrace();
      // this requires ending the program
      System.exit(-1);
    }
  }


  public static void printResultTime(DayI dayI) {
    printResult("", true, Printers.isWindows ? false : true, dayI);
  }

  public static void printJustResult(DayI dayI) {
    printResult("", false, Printers.isWindows ? false : true, dayI);
  }

  private static String formatTime   = "%-12s - %-8s%15s: %16s   time: %11d nano, %8d micro, %5d milli%s";
  private static String formatNoTime = "%-12s - %-8s : %16s%s";

  public static void printResult(String extra, boolean time, boolean linebreak, DayI dayI) {
    String name = Classes.getClassNameWithYear(dayI);

    ResultI result = dayI.getResult();
    boolean part1 = result != null && result.isValid();

    // if result2.isValid() is false, then it was set by the default solver.
    ResultI result2 = dayI.getResultPart2();
    boolean part2 = result2 != null && result2.isValid();

    if (part1) {
      if (time) {
        printDayResult(formatTime, name, "result", extra, result, linebreak || part2);
      } else {
        printDayResultNoTime(formatNoTime, name, "result", result, linebreak || part2);
      }
    } else if (!part2) { // it's only really errornous if there's also no part2
      printError(dayI, linebreak, true);
      return;
    }

    if (!part2) {
      return;
    }

    if (time) {
      printDayResult(formatTime, name, "result-2", extra, result2, linebreak);
    } else {
      printDayResultNoTime(formatNoTime, name, "result-2", result2, linebreak);
    }
  }

  private static void printDayResult(String format,
                                     String name,
                                     String part,
                                     String extra,
                                     ResultI result,
                                     boolean linebreak) {
    Printers.target.printf(format,
                           name,
                           part,
                           extra,
                           result.getPrintableResult(),
                           result.getNanoTime(),
                           result.getMicroTime(),
                           result.getMilliTime(),
                           linebreak ? "\n" : "");
  }

  private static void printDayResultNoTime(String format,
                                           String name,
                                           String part,
                                           ResultI result,
                                           boolean linebreak) {
    Printers.target.printf(format,
                           name,
                           part,
                           result.getPrintableResult(),
                           linebreak ? "\n" : "");
  }


  public static void printAscIIResult(DayI dayI) {
    printAscIIResult(dayI, 1);
    printAscIIResult(dayI, 2);
  }

  private static void printAscIIResult(DayI dayI, int part) {
    ResultI result = part == 1 ? dayI.getResult() : dayI.getResultPart2();
    if (result == null || !result.hasAscII()) {
      return;
    }

    String name = Classes.getClassNameWithYear(dayI);
    String[] ascII = result.getAscIIResult();

    Printers.target.printf("%n%-5s - ascII-result-%d:%n",
                           name,
                           part);
    IntStream.range(0, ascII.length)
             .forEach(i -> Printers.target.print(ascII[i] +
                             (i == ascII.length - 1 ? "" : "\n")));
  }


  public static void printAllDayYear(String year) {
    Printers.target.println();  // get some space
    Printers.target.printf("==== Running year: %-4s ====%n", year);
  }

  private static String formatDay       = "%-5s  %16s  %16s  %16s  %16s%n";
  private static String formatDayNoTime = "%-5s  %16s  %16s%n";
  private static String formatTotal     = "%-22s  %17s  %34s%n";

  public static void printAllDayHeader(boolean time) {

    if (time) {
      Printers.target.printf(formatDay,
                             "Day",
                             "result-1",
                             "time-1 micro",
                             "result-2",
                             "time-2 micro");
      printHeaderSeperator();
    } else {
      Printers.target.printf(formatDayNoTime,
                             "Day",
                             "result-1",
                             "result-2");
      Printers.target.printf(formatDayNoTime,
                             "-".repeat(5),
                             "-".repeat(16),
                             "-".repeat(16));
    }
  }

  private static void printHeaderSeperator() {
    Printers.target.printf(formatDay,
                           "-".repeat(5),
                           "-".repeat(16),
                           "-".repeat(16),
                           "-".repeat(16),
                           "-".repeat(16));
  }

  public static void printDayByDay(DayI dayI, boolean time) {
    ResultI result = dayI.getResult();
    boolean part1 = result != null && result.isValid();

    if (!part1) {
      printError(dayI, true, false);
      return;
    }

    String name = Classes.getSimpleClassName(dayI);
    ResultI result2 = dayI.getResultPart2();
    boolean part2 = result2 != null && result2.isValid();

    if (time) {
      Printers.target.printf(formatDay,
                             name,
                             result.getPrintableResult(),
                             result.isTimed() ? result.getMicroTime() : "",
                             part2 ? result2.getPrintableResult() : "",
                             part2 && result2.isTimed() ? result2.getMicroTime() : "");
    } else {
      Printers.target.printf(formatDayNoTime,
                             name,
                             result.getPrintableResult(),
                             part2 ? result2.getPrintableResult() : "");
    }
  }

  public static void printAllDayTotal(Timers.TimeAccumulator accumulator, boolean time) {
    if (time) {
      printHeaderSeperator();
      Printers.target.printf(formatTotal,
                             "Total in milli",
                             accumulator.getTime1Milli() + "ms",
                             accumulator.getTime2Milli() + "ms");
    }
  }


  public static void printError(DayI dayI, boolean linebreak, boolean withYear) {
    ResultI result = dayI.getResult();
    if (result == null) {
      return;
    }
    StateI state = result.getState();
    if (state == null) {
      return;
    }

    String name = withYear
                    ? Classes.getClassNameWithYear(dayI)
                    : Classes.getSimpleClassName(dayI);
    String format = "%-5s  - %s%s";

    switch (state.getStatus()) {
      case NO_FILE:
      case NO_FILE_CONTENT: Printers.target.printf(format,
                                                   name,
                                                   state.getMessage(),
                                                   linebreak ? "\n" : "");
                            break;
      case GOT_EXCEPTION:   Printers.target.printf(format,
                                                   name,
                                                   state.getException(),
                                                   linebreak ? "\n" : "");
      default:
    }
  }

}
