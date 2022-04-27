package base;

/**
 * The interface for {@link util.Result} which is used to
 * return results to solutions in {@link absbase.DayX} implementations.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public interface ResultI {

  /**
   * Used by {@link util.Printers} and {@link util.Timers}.
   *
   * @return <code>true</code> if the result is valid. Otherwise <code>false</code>.
   */
  boolean isValid();


  /**
   * Used by {@link util.Printers} and {@link test.DaysTest}.
   *
   * @return the {@link base.StateI} of the result.
   */
  StateI getState();


  /**
   * Used by {@link util.Printers} and {@link util.Timers}.
   *
   * @return <code>true</code> if the result contains run time. Otherwise <code>false</code>.
   */
  boolean isTimed();

  /**
   * Used by {@link util.Printers} and {@link util.Timers}.
   *
   * <p><i> Info: There are 1.000.000.000 nano seconds to a second.</i>
   *
   * @return the run time in nano seconds.
   */
  long getNanoTime();

  /**
   * Used by {@link util.Printers}.
   *
   * <p><i> Info: There are 1.000.000 micro seconds to a second.</i>
   *
   * @return the run time in micro seconds.
   */
  long getMicroTime();

  /**
   * Used by {@link util.Printers}.
   *
   * <p><i> Info: There are 1000 milli seconds to a seconds.</i>
   *
   * @return the run time in milli seconds.
   */
  long getMilliTime();


  /**
   * Used by {@link test.DaysTest}.
   *
   * @return the long result.
   */
  long getResult();

  /**
   * Used by {@link util.Printers}.
   *
   * <p> It's expected that the returned result be either
   *   <ul>
   *     <li> a {@link java.lang.String} representation of the long result</li>
   *     <li> a converted representation of a {@link String} array ascII result
   *          to a {@link String} of letters</li>
   *     <li> if none of the above, a literal {@link String} "---" </li>
   *   </ul>
   *
   * @return a printable result.
   */
  String getPrintableResult();


  /**
   * Used by {@link util.Printers}.
   *
   * @return <code>true</code> if the result is an ascII array. Otherwise <code>false</code>.
   */
  boolean hasAscII();

  /**
   * Used by {@link util.Printers}.
   *
   * @return the ascII {@link String} array.
   */
  String[] getAscIIResult();
}
