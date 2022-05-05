package util;

import java.util.List;
import java.util.ArrayList;

import base.*;

/**
 * Handles command line options.
 *
 * <p> Handles both options when running a single {@link absbase.DayX}
 * implementation or when running {@link AllDays}
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class CommandLineOptions {

  /**
   * @hidden
   */
  private CommandLineOptions() {}


  /**
   * Handles arguments for the run of an {@link base.AllDaysI} instance.
   *
   * <p> Arguments
   * <ul>
   *   <li><code>-h</code> or <code>--help</code></li>
   *   <li><code>-hf</code> or <code>--helpFileOptions</code></li>
   * </ul>
   * <br> prints the corresponding help to the console. Then the program halts.
   *
   * <p> Arguments that effects the {@link base.AllDaysI} are handled:
   * <ul>
   *   <li><code>-noTime</code> or <code>--noPrintTime</code> sets
   *       the <code>allDaysI</code> to not print run times</li>
   *   <li><code>-y</code> or <code>--year</code> sets
   *       the package years of <code>allDaysI</code></li>
   *   <li><code>-o</code> or <code>--output</code> set
   *        <code>Printers</code> to print to the specified file</li>
   * </ul>
   *
   * <p> Arguments that effects a {@link base.DayI} that {@link base.AllDaysI} will
   * be running, are collected and later passed to the {@link base.DayI}.
   *
   * <p> Any <code>Exception</code>, like unknown arguments, causes the help to the printed.
   *
   * @param args the original arguments used to call the program.
   * @param allDaysI the instance to be run.
   */
  public static void handleAllDaysOptions(String[] args, AllDaysI allDaysI) {
    boolean help = false;
    boolean helpFile = false;

    List<String> modifiedArgs = new ArrayList<>(args.length);

    try {
      for (int i = 0; i < args.length; i++) {
        switch (args[i]) {
          case "-h"                :
          case "--help"            : help = true;
                                     break;
          case "-hf"               :
          case "--helpFileOptions" : helpFile = true;
                                     break;
          case "-noTime"           :
          case "--noPrintTime"     : allDaysI.setPrintTime(false);
                                     continue;
          case "-y"                :
          case "--year"            : allDaysI.setPackageNames(args[++i]);
                                     continue;
          case "-o"                :
          case "--output"          : Printers.setTarget(args[++i]);
                                     continue;
          case "-f"                :
          case "--file"            :
          case "-p"                :
          case "--path"            :
          case "-dp"               :
          case "--directoryParent" :
          case "-dy"               :
          case "--directoryYear"   :
          case "-d"                :
          case "--directory"       :
          case "-n"                :
          case "--fileName"        : modifiedArgs.add(args[i]);
                                     modifiedArgs.add(args[++i]);
                                     continue;
          default: help = true;
        }
      }
    } catch (Exception ex) {
      help = true;
    }

    checkHelp(help, helpFile);
    allDaysI.setArgs(modifiedArgs.toArray(new String[0]));
  }

  /**
   * Handles arguments for the run of a {@link base.DayI} extending {@link absbase.DayX} instance.
   *
   * <p> Arguments
   * <ul>
   *   <li><code>-h</code> or <code>--help</code></li>
   *   <li><code>-hf</code> or <code>--helpFileOptions</code></li>
   * </ul>
   * <br> prints the corresponding help to the console. Then the program halts.
   *
   * <p> Arguments related to file options sets the appropriate
   * field in the returned {@link FileName.Builder}:
   * <ul>
   *   <li><code>-f</code> or <code>--file</code> calls
   *       {@link util.FileName.Builder#setFullFilename(String)}</li>
   *   <li><code>-p</code> or <code>--path</code> calls
   *       {@link util.FileName.Builder#setPath(String)}</li>
   *   <li><code>-dp</code> or <code>--directoryParent</code> calls
   *       {@link util.FileName.Builder#setParent(String)}</li>
   *   <li><code>-dy</code> or <code>--directoryYear</code> calls
   *       {@link util.FileName.Builder#setYear(String)}</li>
   *   <li><code>-d</code> or <code>--directory</code> calls
   *       {@link util.FileName.Builder#setFolderName(String)}</li>
   *   <li><code>-n</code> or <code>--fileName</code> calls
   *       {@link util.FileName.Builder#setFileName(String)}</li>
   * </ul>
   *
   * <p> Remaining arguments are handled with:
   * <ul>
   *   <li><code>-noTime</code> or <code>--noPrintTime</code> sets
   *       the <code>DayX</code> to not print run times</li>
   *   <li><code>-o</code> or <code>--output</code> set
   *       <code>Printers</code> to print to the specified file</li>
   *   <li><code>-alt</code> or <code>--alternaive</code> calls
   *      {@link base.DayI#setRunMe(Runnable)} with
   *      {@link base.DayI#runVersusAlternatives(int)}</li>
   * </ul>
   *
   * @param args the original arguments used to call the program.
   * @param dayI the instance to be run.
   * @return a {@link FileName.Builder} with specified folder and file names.
  */
  public static FileName.Builder handleOptions(String[] args, DayI dayI) {
    boolean help = false;
    boolean helpFile = false;
    FileName.Builder fileNameBuilder = new FileName.Builder(dayI);

    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "-h"                :
        case "--help"            : help = true;
                                   break;
        case "-hf"               :
        case "--helpFileOptions" : helpFile = true;
                                   break;
        case "-o"                :
        case "--output"          : Printers.setTarget(args[++i]);
                                   continue;
        case "-f"                :
        case "--file"            : fileNameBuilder.setFullFilename(args[++i]);
                                   continue;
        case "-p"                :
        case "--path"            : fileNameBuilder.setPath(args[++i]);
                                   continue;
        case "-dp"               :
        case "--directoryParent" : fileNameBuilder.setParent(args[++i]);
                                   continue;
        case "-dy"               :
        case "--directoryYear"   : fileNameBuilder.setYear(args[++i]);
                                   continue;
        case "-d"                :
        case "--directory"       : fileNameBuilder.setFolderName(args[++i]);
                                   continue;
        case "-n"                :
        case "--fileName"        : fileNameBuilder.setFileName(args[++i]);
                                   continue;
        case "-noTime"           :
        case "--noPrintTime"     : dayI.setPrinter(() -> Printers.printJustResult(dayI));
                                   continue;
        case "-alt":
        case "--alternaive"      : int temp = 0;
                                   try {
                                     temp = Integer.parseInt(args[++i]);
                                   } catch (NumberFormatException | ArrayIndexOutOfBoundsException e){
                                     help = true;
                                   }
                                   int next = temp;
                                   dayI.setRunMe(() -> dayI.runVersusAlternatives(next));
                                   continue;
        default: help = true;
      }
    }

    checkHelp(help, helpFile);
    return fileNameBuilder;
  }

  /**
   * Checks if help should be printed.
   *
   * <p> Only help or file help is printed. Priority is given to help.
   *
   * <p> Halts the program after calling the help or file help printout.
   *
   * @param help <code>true</code> if help is to be printed
   * @param helpFile <code>true</code> if file help is to be printed
   */
  private static void checkHelp(boolean help, boolean helpFile) {
    if (help) {
      usageHelp();
      System.exit(0);
    }

    if (helpFile) {
      usageHelpFiles();
      System.exit(0);
    }
  }

  /**
   * Prints the help.
   *
   * <p> Content of the printout:
   * <pre>
   *Usage:  java -cp run Year2021.Day&lt;1-25&gt; [OPTIONS]
   *   Or:  java -cp run AllDays [OPTIONS]
   *
   *        -h, --help                     Prints this ;)
   *        -hf, --helpFileOptions         Usage on the file options
   *        -noTime, --noPrintTime         Prints only the result, not the execution time
   *                                       This has no effect if using the `-alt` option
   *        -alt, --alternative &lt;INTEGER&gt;  Runs alternative implemenations &lt;INTEGER&gt; times.
   *                                       Note: This option can NOT be used with AllDays!
   *        -y, --year &lt;4 DIGIT INTEGER&gt;   Runs AllDays for that year only.
   *                                       Note: This option can ONLY be used with AllDays!
   *                                       Multiple comma separated years are accepted using
   *                                       for example "2020, 2021" inluding the double quotes
   *        -o, --output &lt;FILE&gt;            Prints output to specified file
   *                                       example -o out\myOutput.txt or -o myOutput.txt
   *                                       Note: The file will be overwritten!</pre>
   */
  public final static void usageHelp() {
    String formatUsage = "%n%6s\t%s%n%6s\t%s%n%n";
    String format      = "\t%-29s  %s%n";

    System.out.printf(formatUsage, "Usage:",
                                   "java -cp run Year2021.Day<1-25> [OPTIONS]",
                                   "Or:",
                                   "java -cp run AllDays [OPTIONS]");

    System.out.printf(format, "-h, --help", "Prints this ;)");

    System.out.printf(format, "-hf, --helpFileOptions", "Usage on the file options");

    System.out.printf(format, "-noTime, --noPrintTime", "Prints only the result, not the execution time");
    System.out.printf(format, "", "This has no effect if using the `-alt` option");

    System.out.printf(format, "-alt, --alternative <INTEGER>",
                              "Runs alternative implemenations <INTEGER> times.");
    System.out.printf(format, "", "Note: This option can NOT be used with AllDays!");

    System.out.printf(format, "-y, --year <4 DIGIT INTEGER>",
                              "Runs AllDays for that year only.");
    System.out.printf(format, "", "Note: This option can ONLY be used with AllDays!");
    System.out.printf(format, "", "Multiple comma separated years are accepted using");
    System.out.printf(format, "", "for example \"2020, 2021\" inluding the double quotes");

    System.out.printf(format, "-o, --output <FILE>", "Prints output to specified file");
    System.out.printf(format, "", "example -o out\\myOutput.txt or -o myOutput.txt");
    System.out.printf(format, "", "Note: The file will be overwritten!");
  }

  /**
   * Prints help for file options.
   *
   * <p> Content of the printout:
   * <pre>
   *FileOptions:
   *
   *        For a class with the fully qualified name of:
   *                Year2021.Day1
   *
   *        the expected location &amp; name of the input file is:
   *                advent-of-code\resources\2021\sample\Day1.txt
   *
   *        divided into:
   *                executionRoot\directoryParent\directoryYear\directory\filename
   *
   *        Options in order of precedence to replace into:
   *                -f
   *                executionRoot\-p\filename
   *                executionRoot\-dp\-dy\-d\-n
   *
   *        -f, --file &lt;FILE&gt;               Input data file.
   *                                        Use the full path or relative path.
   *        -p, --path &lt;PATH&gt;               the path prior to \filename excluding executionRoot
   *        -dp, --directoryParent &lt;NAME&gt;   See above
   *                                        Default is "resources"
   *        -dy, --directoryYear &lt;NAME&gt;     See above
   *                                        Default is "2021" for package "Year2021"
   *        -d, --directory &lt;NAME&gt;          See above
   *                                        Default is "sample"
   *        -n, --fileName &lt;NAME&gt;           The filename of the input file
   *                                        Default is &lt;unqualified ClassName&gt;.txt, like "Day1.txt"
   *                                        Use % in place of class names. "%B.txt" becomes "Day1B.txt"
   *                                        The %-wildcard is especially usefull when running "AllDays"</pre>
   */
  public final static void usageHelpFiles() {
    String formatUsage         = "%n%s%n";
    String formatExplanation   = "%n\t%s%n";
    String formatUsageIndented = "\t\t%s%n";
    String format              = "\t%-30s  %s%n";

    System.out.printf(formatUsage, "FileOptions:");
    System.out.printf(formatExplanation, "For a class with the fully qualified name of:");
    System.out.printf(formatUsageIndented, "Year2021.Day1");
    System.out.printf(formatExplanation, "the expected location & name of the input file is:");
    System.out.printf(formatUsageIndented, "advent-of-code\\resources\\2021\\sample\\Day1.txt");
    System.out.printf(formatExplanation, "divided into:");
    System.out.printf(formatUsageIndented, "executionRoot\\directoryParent\\directoryYear\\directory\\filename");
    System.out.printf(formatExplanation, "Options in order of precedence to replace into:");
    System.out.printf(formatUsageIndented, "-f");
    System.out.printf(formatUsageIndented, "executionRoot\\-p\\filename");
    System.out.printf(formatUsageIndented, "executionRoot\\-dp\\-dy\\-d\\-n");
    System.out.println();

    System.out.printf(format, "-f, --file <FILE>", "Input data file.");
    System.out.printf(format, "", "Use the full path or relative path.");

    System.out.printf(format, "-p, --path <PATH>", "the path prior to \\filename excluding executionRoot");

    System.out.printf(format, "-dp, --directoryParent <NAME>", "See above");
    System.out.printf(format, "", "Default is \"resources\"");

    System.out.printf(format, "-dy, --directoryYear <NAME>", "See above");
    System.out.printf(format, "", "Default is \"2021\" for package \"Year2021\"");

    System.out.printf(format, "-d, --directory <NAME>", "See above");
    System.out.printf(format, "", "Default is \"sample\"");

    System.out.printf(format, "-n, --fileName <NAME>", "The filename of the input file");
    System.out.printf(format, "", "Default is <unqualified ClassName>.txt, like \"Day1.txt\"");
    System.out.printf(format, "", "Use % in place of class names. \"%B.txt\" becomes \"Day1B.txt\"");
    System.out.printf(format, "", "The %-wildcard is especially usefull when running \"AllDays\"");
  }

}
