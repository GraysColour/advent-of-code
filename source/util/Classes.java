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

/**
 * Finds DayZ classes and "YearYYYY" packages.
 * <br> Note that "Z" denotes an integer, like 7 for Day7. "YYYY" denotes a year, like 2021.
 *
 * <p> Instantiates DayZ classes.
 *
 * <p> Checks the fully qualified name for a DayZ class. Also creates printable names.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class Classes {

  /**
   * Maps a <code>.jar</code> class path to all the file names of its contained content.
   */
  private static Map<String, String[]> jarCache;


  /**
   * Empty constructor meant to <b>only be used</b> to get to the {@link ClassLoader}.
   */
  public Classes() {}


  /**
   * Checks a class' fully qualified name.
   *
   * <p> If it doesn't have the fully qualified name of
   * <code>Year&lt;YYYY&gt;.&lt;classname&gt;</code>
   * a {@link AssertionError} is raised with the message:
   * <br><pre>    Invalid package structure! Use Year&lt;YYYY&gt;.&lt;classname&gt;</pre>
   *
   * <p> If it does, the fully qualified name is split at the dot
   * and returned in a {@link String} array of two elements.
   *
   * @param dayI the {@link base.DayI} to be checked
   * @return a {@link String} array of 2 elements from the the fully qualified
   * class name with the package name at index 0 and the class name at index 1
   */
  public static String[] checkClassName(DayI dayI) {
    // Returns if class has a valid fully qualified name in two parts: Year2021.Day<1-25>

    String dayXfullclassName = dayI.getClass().getName();
    String[] split = dayXfullclassName.split("\\.");
    if (split.length != 2) {
      throw new AssertionError("Invalid package structure! Use Year<YYYY>.<classname>");
    }

    return split;
  }


  /**
   * Gets the class name of a {@link base.DayI}.
   *
   * <p> From a fully qualified class name of for example <code>Year2021.Day1</code>
   * <br> it returns <pre>    <code>Day1</code></pre>
   * by replacing the package name.
   *
   * @param dayI the {@link base.DayI}.
   * @return the name of the class.
   */
  public static String getSimpleClassName(DayI dayI) {
    // From: Year2021.Day1
    // To:   Day1
    return dayI.getClass()
               .getName()
               .replaceAll("Year\\d{4}\\.","");
  }


  /**
   * Gets the class name of a {@link base.DayI} prefixed with the package year.
   *
   * <p> From a fully qualified class name of for example <code>Year2021.Day1</code>
   * <br> it returns <pre>    <code>=2021= Day1</code></pre>
   * by replacing the package name.
   *
   * @param dayI the {@link base.DayI}.
   * @return the name of the class prefixed with the package year.
   */
  public static String getClassNameWithYear(DayI dayI) {
    // From: Year2021.Day1
    // To:   =2021= Day1
    return dayI.getClass()
               .getName()
               .replaceAll("Year(\\d{4})\\.","=$1= ");
  }


  /**
   * Compares the integer part of the class name of a {@link absbase.DayX} implementation.
   *
   * <p> For example: An implementation named <code>Day7</code> will be compared to other
   * {@link absbase.DayX} implementations by its integer value <code>7</code>.
   */
  private static Comparator<String> fileCompare =
           Comparator.comparing(
              fileName -> Integer.parseInt(fileName.replaceAll("Day","")));


  /**
   * Finds all package names with the "YearYYYY" name structure on the system class path.
   *
   * <p> For every system folder or <code>.jar</code> file on the class path,
   * it finds and collects any packages with the "YearYYYY" name structure,
   * where "YYYY" are the 4 digits of a year.
   *
   * <p> Uses both {@link #getYearPackagesFile(String)} and {@link #getYearPackagesJar(String)}
   * to collect package names.
   *
   * @return a {@link java.util.Set} of year packages having the name "YearYYYY",
   * where "YYYY" are the 4 digits of a year.
   */
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


  /**
   * Finds package names with the "YearYYYY" name structure in a system folder.
   *
   * <p> Does not walk the folder paths, but looks at the files directly in the folder.
   *
   * <p> Returns any folders inside with the "YearYYYY" name structure,
   * where "YYYY" are the 4 digits of a year.
   *
   * <p> It would have been convenient to use
   * <pre>    Package[] packages = Package.getPackages()</pre>
   *
   * but the {@link ClassLoader} does not know of packages until they've already been loaded.
   *
   * @param asClassPath the {@link String} class path of a folder
   * @return a {@link java.util.List} of {@link String} package names
   */
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


  /**
   * Finds package names with the "YearYYYY" name structure in <code>.jar</code> files.
   *
   * <p> "YYYY" must be 4 digits representing a year.
   *
   * <p> Uses {@link java.util.jar.JarFile} to open the <code>.jar</code> file.
   * Then caches the entire content into a map with
   * the key being the class path to the <code>.jar</code> file.
   *
   * <p> Returns a filtered {@link java.util.List} of package names.
   * If none is found, returns an empty {@link java.util.List}.
   *
   * <p> Due to this bug, it's <b>not</b> possible to reopen the jarfile due to caching:
   * <a class="linkColour"
   * href="https://stackoverflow.com/questions/19123887/jetty-java-lang-illegalstateexception-zip-file-closed">
   * Jetty - java.lang.IllegalStateException: zip file closed</a>
   *
   * <br> This bug also presents if the JVM loads the <code>.jar</code> file prior to any method
   * opening it, as in checking to see if a package exists blocks opening the file, e.g:
   * <pre>    ClassLoader.getSystemClassLoader()
   *               .getResource(packageName)</pre>
   *
   * For this reason, the content of the <code>.jar</code> file is cached in a map,
   * since a call to {@link #classNamesAsStreamJar(String, String)} has to be made possible.
   *
   * @param asClassPath the {@link String} class path of the <code>.jar</code>
   * @return a {@link java.util.List} of {@link String} package names
   */
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


  /**
   * Finds and returns all {@link absbase.DayX} implementations in a package on the file system.
   *
   * <p> Using this {@link Classes}' {@link ClassLoader}
   * and {@link ClassLoader#getResourceAsStream getResourceAsStream}
   * it finds the package by name.
   * <br>Then reads and filters the class implementations in the package.
   *
   * <p> Also see <a class="linkColour"
   * href="https://www.baeldung.com/java-find-all-classes-in-package">
   * Finding All Classes in a Java Package</a>
   *
   * <p> Note that since this must also work when running tests using
   * <pre>    java -jar junit-platform-console-standalone-x.x.x.jar ...</pre>
   * it's not feasible to get the package resource from calling:
   * <pre>    ClassLoader.getSystemClassLoader()
   *               .getResourceAsStream(..)</pre>
   * since that relies on the class path.
   * <br> In the case of running tests like above,
   * the junit <code>.jar</code> file initially <b>is</b> the entire class path.
   *
   * <p> The returned {@link java.util.stream.Stream} is expected to contain:
   * "Day1", "Day2", "Day3", ...
   *
   * @param packageName the package name to find implementations in
   * @return a {@link java.util.stream.Stream} of {@link String} class names
   */
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


  /**
   * Finds and returns all {@link absbase.DayX} implementations in a package in a <code>.jar</code>.
   *
   * <p> Is uses a previously populated map of a <code>.jar</code> class path to all
   * the file names of the <code>.jar</code>'s contained content.
   * <br> Looking up the <code>.jar</code> original class path, it filters and finds
   * the {@link absbase.DayX} implementations in the given package name,
   * and returns their {@link String} class names.
   *
   * <p> The returned {@link java.util.stream.Stream} is expected to contain:
   * "Day1", "Day2", "Day3", ...
   *
   * @param packageName the package name to find implementations in
   * @param asClassPath the original class path of the <code>.jar</code> file
   * @return a {@link java.util.stream.Stream} of {@link String} class names
   */
  public static Stream<String> classNamesAsStreamJar(String packageName, String asClassPath) {
    String separator = "/"; // fixed in jar files

    return Stream.of(jarCache.get(asClassPath))
                  .filter(fileName -> fileName.matches("^" + packageName + separator
                                                        + "Day(?!0)\\d+\\.class$"))
                  .map(fileName -> fileName.replace(packageName + separator,""))
                  .map(fileName -> fileName.replace(".class",""))
                  .sorted(fileCompare);
  }


  /**
   * Instantiates an object of a {@link base.DayI} class.
   *
   * <p> Given the packageName and className, it will
   * <ul>
   *   <li>find the class</li>
   *   <li>get the constructor taking a {@link String} array</li>
   *   <li>instantiate a {@link base.DayI} object with the given dayArgs</li>
   * </ul>
   *
   * <p> Returns <code>null</code> if an {@link Exception} occurs.
   *
   * @param className the class name of the object.
   * @param packageName the package name of the object.
   * @param dayArgs the arguments to instantiate the object with.
   * @return the {@link base.DayI} instantiated object
   * or <code>null</code> if an {@link Exception} occurs.
   */
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


  /**
   * Loads the specified class into the current {@link ClassLoader}.
   *
   * <p> Uses {@link Class#forName(String)} to load the class.
   *
   * <p><i> Note: Contains a </i><pre>    <code>@SuppressWarnings(value = "unchecked")</code></pre>
   * <i>to enable casting the class to a {@link base.DayI} without showing compiler warnings.</i>
   *
   * @param className the class name.
   * @param packageName the package name.
   * @throws ClassNotFoundException if the class cannot be found.
   * @return {@link Class} of type {@link base.DayI}.
   */
  @SuppressWarnings(value = "unchecked")
  private static Class<DayI> getClass(String className, String packageName) throws ClassNotFoundException {
    String fullName = packageName + "." + className;
    return (Class<DayI>) Class.forName(fullName);
  }


  /**
   * Enum to determine the file or folder type of a class path.
   *
   * @author  GraysColour
   * @version 1.0
   * @since   1.0
   */
  public static enum ContainerType {
    /**
     * A <code>.jar</code> file.
     */
    JAR,
    /**
     * A regular file or folder on the system. Not a <code>.jar</code> file.
     */
    FILE,
    /**
     * File or folder doesn't exist.
     */
    NO_EXIST;
  }


  /**
   * Compares package name {@link YearPackage#packageName packageName}.
   */
  private static Comparator<YearPackage> YearPackageCompare =
    Comparator.comparing(YearPackage -> YearPackage.getPackageName());


  /**
   * Connects a package name with its class path and type.
   *
   * @author  GraysColour
   * @version 1.0
   * @since   1.0
   */
  public static class YearPackage {

    /**
     * Full package name as in "Year2021".
     */
    private String packageName;

    /**
     * Absolute or relative class path to the package.
     */
    private String classPath;

    /**
     * Type of the container. For example a jar or a regular file.
     */
    private ContainerType containerType;


    /**
     * Initializes and sets the {@link YearPackage#packageName packageName}
     * and {@link YearPackage#containerType containerType}.
     *
     * @param packageName the full package name, for example "Year20210".
     * @param containerType type container. If not a <code>.jar</code> file,
     * and it exists, set it to {@link ContainerType#FILE}
     */
    public YearPackage(String packageName, ContainerType containerType) {
      this.packageName = packageName;
      this.containerType = containerType;
    }

    /**
     * Initializes and sets the {@link YearPackage#packageName packageName},
     * {@link YearPackage#classPath classPath} and
     * {@link YearPackage#containerType containerType}.
     *
     * @param packageName the full package name, for example "Year20210".
     * @param classPath the absolute or relative path.
     * @param containerType type container. If not a <code>.jar</code> file,
     * and it exists, set it to {@link ContainerType#FILE}
     */
    public YearPackage(String packageName, String classPath, ContainerType containerType) {
      this.packageName = packageName;
      this.classPath = classPath;
      this.containerType = containerType;
    }


    /**
     * Returns the {@link YearPackage#packageName packageName}
     *
     * @return the {@link String} package name.
     */
    public String getPackageName() {
      return packageName;
    }

    /**
     * Returns the {@link YearPackage#classPath classPath}
     *
     * @return the {@link String} class name.
     */
    public String getClassPath() {
      return classPath;
    }

    /**
     * Returns the {@link YearPackage#containerType containerType}
     *
     * @return the {@link ContainerType} type.
     */
    public ContainerType getContainerType() {
      return containerType;
    }


    /**
     * The details of this {@link YearPackage YearPackage}.
     *
     * <p> This only exists to aid with debugging.
     */
    @Override
    public String toString() {
      return packageName + " - part of " + containerType + " on: " + classPath;
    }
  }

}
