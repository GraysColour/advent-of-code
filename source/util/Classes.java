package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.jar.JarFile;

import base.DayI;

public class Classes {

  private static Map<String, String[]> jarCache;

  public Classes() {}

  public static String[] checkClassName(DayI dayI) {
    // Returns if class has a valid fully qualified name in two parts: Year2021.Day<1-25>

    String dayXfullclassName = dayI.getClass().getName();
    String[] split = dayXfullclassName.split("\\.");
    if (split.length != 2) {
      throw new AssertionError("Invalid package structure! Use Year<YYYY>.<classname>");
    }

    return split;
  }

  public static String getSimpleClassName(DayI dayI) {
    // From: Year2021.Day1
    // To:   Day1
    return dayI.getClass()
               .getName()
               .replaceAll("Year\\d{4}\\.","");
  }

  public static String getClassNameWithYear(DayI dayI) {
    // From: Year2021.Day1
    // To:   =2021= Day1
    return dayI.getClass()
               .getName()
               .replaceAll("Year(\\d{4})\\.","=$1= ");
  }


  private static Comparator<String> fileCompare =
           Comparator.comparing(
              fileName -> Integer.parseInt(fileName.replaceAll("Day","")));

  public static Set<YearPackage> getYearPackagesOnClassPath() {
    String classpath = System.getProperty("java.class.path");
    String[] classpathEntries = classpath.split(System.getProperty("path.separator"));

    Set<YearPackage> allYearPackages = new TreeSet<>(YearPackageCompare);

    for (String classPathEntry : classpathEntries) {

      if (classPathEntry.endsWith(".jar")) {

        allYearPackages.addAll(
          getYearPackagesJar(classPathEntry)
            .stream()
            .map(year -> new YearPackage(year, classPathEntry, ContainerType.JAR))
            .collect(Collectors.toList())
        );

      } else {

        // find packages on filesystem
        allYearPackages.addAll(
          getYearPackagesFile(classPathEntry)
            .stream()
            .map(year -> new YearPackage(year, classPathEntry, ContainerType.FILE))
            .collect(Collectors.toList())
        );

      }
    }

    return allYearPackages;
  }

  public static List<String> getYearPackagesFile(String asClassPath) {
    try {
      Path thePath = Path.of(asClassPath);

      return Files.list(thePath)
                  .filter(Files::isDirectory)
                  .map(Path::getFileName)
                  .map(Path::toString)
                  .filter(str -> str.startsWith("Year"))
                  .collect(Collectors.toList());

    } catch (IOException e) {
      e.printStackTrace();
    }

    return List.of();
  }

  public static List<String> getYearPackagesJar(String asClassPath) {
    if (jarCache == null) {
      jarCache = new HashMap<>();
    }

    try (JarFile thisJar = new JarFile(asClassPath)) {
      String[] cache = thisJar.stream()
                              .map(jarEntry -> jarEntry.getName())
                              .toArray(String[]::new);

      jarCache.put(asClassPath, cache);

      return Stream.of(cache)
                   .filter(str -> str.startsWith("Year") && str.endsWith("/"))
                   .map(str -> str.substring(0, str.length() - 1))
                   .collect(Collectors.toList());

    } catch (IOException e) {
      // Not sure what to do about this.. :(
      e.printStackTrace();
    }

    return List.of();
  }

  public static Stream<String> classNamesAsStreamFile(String packageName) {

    InputStream stream =
      new Classes().getClass()
                   .getClassLoader()
                   .getResourceAsStream(packageName.replaceAll("[.]", "/"));

    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

    // Day1, Day2, Day3, ....
    return reader.lines()
                 .filter(fileName -> fileName.matches("^Day(?!0)\\d+\\.class$"))
                 .map(fileName -> fileName.replace(".class",""))
                 .sorted(fileCompare);
  }

  public static Stream<String> classNamesAsStreamJar(String packageName, String asClassPath) {
    String separator = "/"; // fixed in jar files

    return Stream.of(jarCache.get(asClassPath))
                  .filter(fileName -> fileName.matches("^" + packageName + separator
                                                        + "Day(?!0)\\d+\\.class$"))
                  .map(fileName -> fileName.replace(packageName + separator,""))
                  .map(fileName -> fileName.replace(".class",""))
                  .sorted(fileCompare);
  }

  public static DayI createClass(String className, String packageName, String[] dayArgs) {
    try {
      return getClass(className, packageName)
                 .getDeclaredConstructor(String[].class)
                 .newInstance(new Object[]{dayArgs});

    } catch (Exception e) {
      // Not sure what to do about this.. :(
      e.printStackTrace();
    }
    return null;
  }


  @SuppressWarnings(value = "unchecked")
  private static Class<DayI> getClass(String className, String packageName) throws ClassNotFoundException {
    String fullName = packageName + "." + className;
    return (Class<DayI>) Class.forName(fullName);
  }

  public static enum ContainerType {
    JAR,
    FILE,
    NO_EXIST;
  }

  private static Comparator<YearPackage> YearPackageCompare =
    Comparator.comparing(YearPackage -> YearPackage.getPackageName());

  public static class YearPackage {
    private String packageName;
    private String classPath;
    private ContainerType containerType;

    public YearPackage(String packageName, ContainerType containerType) {
      this.packageName = packageName;
      this.containerType = containerType;
    }
    public YearPackage(String packageName, String classPath, ContainerType containerType) {
      this.packageName = packageName;
      this.classPath = classPath;
      this.containerType = containerType;
    }

    public String getPackageName() {
      return packageName;
    }

    public String getClassPath() {
      return classPath;
    }

    public ContainerType getContainerType() {
      return containerType;
    }

    @Override
    public String toString() {
      return packageName + " - part of " + containerType + " on: " + classPath;
    }
  }

}
