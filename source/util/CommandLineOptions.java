package util;

import java.util.List;
import java.util.ArrayList;

import base.*;

public class CommandLineOptions {

  private CommandLineOptions() {}

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
