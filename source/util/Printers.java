package util;

import java.util.stream.IntStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;

import base.*;

/**
 * Prints content of {@link base.ResultI} in a {@link base.DayI}.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class Printers {

  /**
   * A {@link java.io.PrintStream} of where to print.
   *
   * <p> Default set to {@link java.lang.System#out}
   */
  private static PrintStream target = System.out;

  /**
   * <code>true</code> if "Windows" appears in the Operating System name.
   */
  private static boolean isWindows = System.getProperty("os.name").indexOf("Windows") > -1;


  /**
   * @hidden
   */
  private Printers() {}


  /**
   * Sets the {@link java.io.PrintStream} {@link #target} of where to print.
   *
   * <p> Default set to {@link java.lang.System#out}
   *
   *
   * <p>Usage:
   *
   * <pre>
   *     setTarget("output.txt")</pre>
   * or
   * <pre>
   *     setTarget("out\output.txt")</pre>
   *
   * Will create directories in the spcified path if they don't already exist.
   *
   * @param targetString a {@link String} path to a file.
   */
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


  /**
   * Prints the {@link base.ResultI} for both <b>part 1</b>
   * and <b>part 2</b> of a {@link base.DayI} with run times.
   *
   * <p> The same as calling:
   * <pre>
   *     printResult("", true, false, dayI)</pre>
   *
   * Except on non-Windows systems where the
   * boolean <code>linebreak</code> is set to <code>true</code>.
   *
   * @param dayI the {@link base.DayI} of the {@link base.ResultI}.
   */
  public static void printResultTime(DayI dayI) {
    printResult("", true, Printers.isWindows ? false : true, dayI);
  }

  /**
   * Prints the {@link base.ResultI} for both <b>part 1</b>
   * and <b>part 2</b> of a {@link base.DayI} without run times.
   *
   * <p> The same as calling:
   * <pre>
   *     printResult("", false, false, dayI)</pre>
   *
   * Except on non-Windows systems where the
   * boolean <code>linebreak</code> is set to <code>true</code>.
   *
   * @param dayI the {@link base.DayI} of the {@link base.ResultI}.
   */
  public static void printJustResult(DayI dayI) {
    printResult("", false, Printers.isWindows ? false : true, dayI);
  }

  /**
   * A {@link String} formatter for printing a part of a
   * {@link base.DayI} {@link base.ResultI} with run time.
   */
  private static String formatTime   = "%-12s - %-8s%15s: %16s   time: %11d nano, %8d micro, %5d milli%s";

  /**
   * A {@link String} formatter for printing a part of a
   * {@link base.DayI} {@link base.ResultI} without run time.
   */
  private static String formatNoTime = "%-12s - %-8s : %16s%s";

  /**
   * Prints the {@link base.ResultI} for both <b>part 1</b>
   * and <b>part 2</b> of a {@link base.DayI}.
   *
   * <p> Contains two predefined formats for printing the result with or without the run time.
   *
   * <p>Usage for printing with run time:
   * <pre>
   *     printResult("", true, false, dayI)</pre>
   *
   * <p>Usage for printing without run time:
   * <pre>
   *     printResult("", false, false, dayI)</pre>
   *
   * Choosing <code>false</code> for the linebreak parameter
   * only affects the last printed line. There will always
   * be a linebreak between printing of <b>part 1</b> and <b>part 2</b>
   *
   * <p>{@link base.ResultI} is checked for validity calling {@link base.ResultI#isValid}.
   * Only if both <b>part 1</b> and <b>part 2</b> results are invalid, will the error contained
   * in the <b>part 1</b> {@link base.ResultI} be printed using {@link #printError(DayI, boolean, boolean)}.
   * The <b>part 2</b> {@link base.ResultI} are never printed in case of errors.
   *
   * <p> Example of print without errors:
   * <pre>=2021= Day13 - result                 :               17   time:    42514300 nano,    42514 micro,    42 milli
   *=2021= Day13 - result-2               :              ---   time:    17657700 nano,    17657 micro,    17 milli</pre>
   *
   * @param extra an addition {@link String} to be printed after the literal
   * "result" or "result2". Usefull when running {@link base.DayI#runVersusAlternatives}
   * @param time <code>true</code> if run times are to be printed.
   * @param linebreak <code>true</code> if line breaks are to be printed after the last line.
   * @param dayI the {@link base.DayI} of the {@link base.ResultI}.
   */
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

  /**
   * Prints the {@link base.ResultI} with run time.
   *
   * @param format a {@link java.util.Formatter} formatted
   * {@link String} for a <code>printf</code> call.
   * @param name the {@link String} presenting this print, like "=2021= Day2"
   * @param part which part of the solution, like "result" or "result-2"
   * @param extra additional identifier on the print, like "SlowSolution" or "FastSolution"
   * @param result the {@link base.ResultI} with the result and run time.
   * @param linebreak <code>true</code> if the print should add a line break.
   */
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

  /**
   * Prints the {@link base.ResultI} without run time.
   *
   * @param format a {@link java.util.Formatter} formatted
   * {@link String} for a <code>printf</code> call.
   * @param name the {@link String} presenting this print, like "=2021= Day2"
   * @param part which part of the solution, like "result" or "result-2"
   * @param result the {@link base.ResultI} with the result and run time.
   * @param linebreak <code>true</code> if the print should add a line break.
   */
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


  /**
   * Prints the ascII result of a {@link base.DayI}.
   *
   * <p> Prints the ascII result for both <b>part 1</b> and <b>part 2</b>
   * by first calling {@link base.ResultI#hasAscII}. Only if there is an ascII result
   * will it be printed.
   *
   * <p> The print will have a line with the name of the {@link base.DayI} that
   * includes the year, followed by " - ascII-result-" and the part. Then the ascII.
   *
   * <p> Example of print: <pre>=2021= Day13 - ascII-result-2:
   *#####
   *#   #
   *#   #
   *#   #
   *#####</pre>
   *
   * @param dayI a {@link base.DayI} with ascII result.
   */
  public static void printAscIIResult(DayI dayI) {
    printAscIIResult(dayI, 1);
    printAscIIResult(dayI, 2);
  }

  /**
   * Prints the ascII result of a {@link base.DayI} for <b>the given part</b>.
   *
   * <p> Prints the ascII result for the given part by first calling
   * {@link base.ResultI#hasAscII}. Only if there is an ascII result will it be printed.
   *
   * <p> The print will have a line with the name of the {@link base.DayI} that
   * includes the year, followed by " - ascII-result-" and the part. Then the ascII.
   * <p> For example: <pre>=2021= Day13 - ascII-result-2:
   *#####
   *#   #
   *#   #
   *#   #
   *#####</pre>
   *
   * @param dayI a {@link base.DayI} with ascII result.
   * @param part 1 for part 1, 2 for part 2.
   */
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



  /**
   * Prints a year header. Meant to be used for an <b>{@link base.AllDaysI}</b> printout.
   *
   * <p> The print will have a line identifying the year.
   *
   * <p> Example of print: <pre>==== Running year: 2021 ====</pre>
   *
   * @param year the year to appear in the header.
   */
  public static void printAllDayYear(String year) {
    Printers.target.println();  // get some space
    Printers.target.printf("==== Running year: %-4s ====%n", year);
  }

  /**
   * A {@link String} formatter for printing both parts
   * of a {@link base.DayI} {@link base.ResultI} with run time.
   */
  private static String formatDay       = "%-5s  %16s  %16s  %16s  %16s%n";

  /**
   * A {@link String} formatter for printing both parts
   * of a {@link base.DayI} {@link base.ResultI} without run time.
   */
  private static String formatDayNoTime = "%-5s  %16s  %16s%n";

  /**
   * A {@link String} formatter for printing the accumulated runtime
   * of {@link base.DayI} {@link base.ResultI} within a year.
   */
  private static String formatTotal     = "%-22s  %17s  %34s%n";

  /**
   * Prints a day header. Meant to be used for an <b>{@link base.AllDaysI}</b> printout.
   *
   * <p> Prints a header line for the columns of the {@link base.DayI} {@link base.ResultI}.
   *
   * <p> Example of print with run times: <pre>Day            result-1      time-1 micro          result-2      time-2 micro
   *-----  ----------------  ----------------  ----------------  ----------------</pre>
   *
   * <p> Example of print without run times: <pre>Day            result-1          result-2
   *-----  ----------------  ----------------</pre>
   *
   * @param time <code>true</code> if run times are included.
   */
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

  /**
   * Prints separator line.
   *
   * <p> Meant to be called only from <b>{@link printAllDayHeader}</b>
   * or <b>{@link printAllDayTotal(Timers.TimeAccumulator, boolean) printAllDayTotal(Timers.TimeAccumulator, boolean)}</b>.
   *
   * <p> Prints a header separator line for the columns of the {@link base.DayI} {@link base.ResultI}.
   *
   * <p> Example of print:
   * <pre>-----  ----------------  ----------------  ----------------  ----------------</pre>
   */
  private static void printHeaderSeperator() {
    Printers.target.printf(formatDay,
                           "-".repeat(5),
                           "-".repeat(16),
                           "-".repeat(16),
                           "-".repeat(16),
                           "-".repeat(16));
  }

  /**
   * Prints <b>part 1</b> and <b>part 2</b> of a {@link base.DayI} {@link base.ResultI} on a single line.
   *
   * <p> If there is no valid result for <b>part 1</b>, the assumed error contained
   * in the {@link base.DayI} {@link base.ResultI} of <b>part 1</b> will be printed instead.
   *
   * <p> Example of print with run times:
   * <pre>Day3                198             15367               230             11915</pre>
   *
   * <p> Example of print without run times:
   * <pre>Day3                198               230</pre>
   *
   * @param dayI a {@link base.DayI} that contains {@link base.ResultI} for one or both parts to be printed.
   * @param time <code>true</code> if run times are included.
   */
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

  /**
   * Prints the accumulated run times in milli seconds.
   *
   * <p> Prints the accumulated run times for
   * <ul>
   *   <li><b>part 1</b> by calling {@link util.Timers.TimeAccumulator#getTime1Milli getTime1Milli()}</li>
   *   <li><b>part 2</b> by calling {@link util.Timers.TimeAccumulator#getTime2Milli getTime2Milli()}</li>
   * </ul>
   * <br> on the accumulator.
   *
   * <p><i> Note: calling this with the <code>time</code> parameter
   * set to <code>false</code> will not print anything at all.</i>
   *
   * <p> Example of print:
   * <pre>Total in milli                     1219ms                              7885ms</pre>
   * @param accumulator that contains the accumulated run times.
   * @param time <code>true</code> if this whould print at all.
   */
  public static void printAllDayTotal(Timers.TimeAccumulator accumulator, boolean time) {
    if (time) {
      printHeaderSeperator();
      Printers.target.printf(formatTotal,
                             "Total in milli",
                             accumulator.getTime1Milli() + "ms",
                             accumulator.getTime2Milli() + "ms");
    }
  }


  /**
   * Prints the error in a {@link base.ResultI} of a {@link base.DayI}.
   *
   * <p> Prints the contained error for the <b>part 1</b> result by first finding the
   * {@link base.Status} of result. If the {@link base.Status} is either:
   * <ul>
   *   <li>{@link base.Status#NO_FILE}</li>
   *   <li>{@link base.Status#NO_FILE_CONTENT}</li>
   * </ul>
   * <br> the {@link String} message from calling {@link base.StateI#getMessage()} is printed
   *
   * <p> If the {@link base.Status} is {@link base.Status#GOT_EXCEPTION}, the exception
   * from calling {@link base.StateI#getException()} is printed instead.
   *
   * <p> Printing is <b>only</b> done, if the {@link base.Status} is one of the three mentioned.
   *
   * <p> The print will have a line with the name of the {@link base.DayI} that
   * includes the year depending on the given paramter, followed by the message or error.
   *
   * <p> Example: <pre>=2021= Day13  - file resources\2021\sample\Day13.txt does not exist</pre>
   *
   * @param dayI a {@link base.DayI} with ascII result.
   * @param linebreak <code>true</code> if the print should add a line break.
   * @param withYear <code>true</code> if the print should include the year.
   */
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
