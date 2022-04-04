import base.*;
import util.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public class AllDays implements AllDaysI {

  private boolean printTime = true;
  private List<String> packageNames;  // plain names like ["Year2016", "Year2021"]
  private Set<Classes.YearPackage> yearPackages;
  private String[] dayArgs;

  public static void main(String[] args) {

    AllDays allDays = new AllDays();
    CommandLineOptions.handleAllDaysOptions(args, allDays);

    allDays.yearPackages = Classes.getYearPackagesOnClassPath();
    allDays.editYearPackages();

    allDays.fetchAndRun();

  }


  public void editYearPackages() {
    if (this.packageNames == null || this.packageNames.size() == 0) {
      return; // nothing to do
    }

    // any packages missing
    for (String packageName : this.packageNames) {
      boolean found = false;
      for (Classes.YearPackage yearPackage : this.yearPackages) {
        if (yearPackage.getPackageName().equals(packageName)){
          found = true;
        }
      }
      if (!found) {
        this.yearPackages.add(new Classes.YearPackage(packageName, Classes.ContainerType.NO_EXIST));
      }
    }

    // too many packages
    Iterator<Classes.YearPackage>	iterator = this.yearPackages.iterator();
    while (iterator.hasNext()) {
      Classes.YearPackage yearPackage = iterator.next();
      boolean found = false;
      for (String packageName : this.packageNames) {
        if (yearPackage.getPackageName().equals(packageName)){
          found = true;
        }
      }
      if (!found) {
        iterator.remove();
      }
    }
  }


  // ---- Setters
  public void setPrintTime(boolean printTime) {
    this.printTime = printTime;
  }
  public void setArgs(String[] dayArgs) {
    this.dayArgs = dayArgs;
  }
  public void setPackageNames(String years) { // from CommandLineOptions
    this.packageNames = Arrays.stream(years.split(","))
                              .map(year -> "Year" + year.trim())
                              .sorted()
                              .collect(Collectors.toList());
  }


  // -----
  private void fetchAndRun() {
    for (Classes.YearPackage yearPackage : this.yearPackages) {

      String packageName = yearPackage.getPackageName();
      Stream<String> base = null;

      switch (yearPackage.getContainerType()) {
        case NO_EXIST:
          System.out.println("\nPackage " + packageName + " does not exist\n");
          continue;
        case JAR:
          base = Classes.classNamesAsStreamJar(packageName, yearPackage.getClassPath());
          break;
        case FILE:
          base = Classes.classNamesAsStreamFile(packageName);
      }

      Printers.printAllDayYear(packageName.substring(4));
      Printers.printAllDayHeader(this.printTime);

      Timers.TimeAccumulator totalTime = new Timers.TimeAccumulator();

      base.map(fileName -> Classes.createClass(fileName, packageName, this.dayArgs))
          .filter(dayX -> dayX != null)
          .forEach(dayX -> runDay(dayX, totalTime));

      Printers.printAllDayTotal(totalTime, this.printTime);
    }
  }


  // -----
  private void runDay(DayI dayI, Timers.TimeAccumulator accumulator) {
      dayI.daySolver();
      if (this.printTime) {
        accumulator.addTime(dayI);
      }
      Printers.printDayByDay(dayI, this.printTime);
  }

}
