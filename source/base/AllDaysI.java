package base;

/**
 * The interface for the all-day runner {@link AllDays} in the unnamed/default package.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public interface AllDaysI {
  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * @param printTime whether to print the runtime.
   * <code>false</code> will not print the runtimes.
   */
  void setPrintTime(boolean printTime);

  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * <p> Sets the command line arguments for running each day.
   * <p> For example:
   * <pre>
   *     setArgs(new String[]{"-d", "challenge"})</pre>
   *
   * @param dayArgs command line arguments for running the days.
   */
  void setArgs(String[] dayArgs);

  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * <p> Sets the package names for discovering YearYYYY packages, where YYYY is a 4 digit year.
   * <p> For example:
   * <pre>
   *     setPackageNames("2021")</pre>
   * or
   * <pre>
   *     setPackageNames("2021, 2022")</pre>
   *
   * @param years a {@link String} of comma separated 4 digit years
   */
  void setPackageNames(String years);
}
