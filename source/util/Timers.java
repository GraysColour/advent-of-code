package util;

import java.util.function.Consumer;
import java.util.function.Function;

import base.*;

/**
 * Calculate and accumulate run times
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class Timers {

  /**
   * @hidden
   */
  private Timers() {}


  /**
   * Times a {@link java.util.function.Function} using {@link java.lang.System#nanoTime()}
   *
   * <p> Gets the {@link util.Result} of the <code>doMe</code> function.
   * Then updates the {@link util.Result} with the run time.
   *
   * @param <T> the input type.
   * @param input the variable to call the {@link java.util.function.Function} with.
   * @param doMe {@link java.util.function.Function} to be timed.
   * @return {@link util.Result} with the run time in nano seconds.
   */
  public static <T> Result timeItResult(Function<T, Result> doMe, T input) {
    long startTime = System.nanoTime();
    Result result = doMe.apply(input);
    result.setNanoTime(System.nanoTime() - startTime);
    return result;
  }


  /**
   * Times a {@link java.util.function.Consumer} using {@link java.lang.System#nanoTime()}
   *
   * <p> Times the call of the <code>doMe</code> {@link java.util.function.Consumer}.
   *
   * @param <T> the input type.
   * @param input the variable to call the {@link java.util.function.Consumer} with.
   * @param doMe {@link java.util.function.Consumer} to be timed.
   * @return the run time in nano seconds.
   */
  public static <T> long timeItNoreturn(Consumer<T> doMe, T input) {
    long startTime = System.nanoTime();
    doMe.accept(input);
    long elapsedNanos = System.nanoTime() - startTime;
    return elapsedNanos;
  }


  /**
   * Times a {@link java.util.function.Function} using {@link java.lang.System#nanoTime()}
   *
   * <p> Times the call of the <code>doMe</code> {@link java.util.function.Function}.
   *
   * @param <T> the input type.
   * @param input the variable to call the {@link java.util.function.Function} with.
   * @param doMe {@link java.util.function.Function} to be timed.
   * @return a <code>long</code> array of the run time in nano seconds at index 0 and
   * the <code>long</code> result of the {@link java.util.function.Function} at index 1.
   */
  public static <T> long[] timeItReturn(Function<T, Result> doMe, T input) {
    long startTime = System.nanoTime();
    Result result = doMe.apply(input);
    long elapsedNanos = System.nanoTime() - startTime;
    return new long[]{elapsedNanos, result.getResult()};
  }


  /**
   * Accumulates run times.
   *
   * <p> Used by {@link util.Printers} and {@link AllDays}.
   *
   * @author  GraysColour
   * @version 1.0
   * @since   1.0
   */
  public static class TimeAccumulator {

    /**
     * A constant of 1.000.000 for calculating nano to milli seconds.
     */
    private final long nanoToMilli = 1000000;

    /**
     * Nano seconds for running part 1 solutions.
     */
    private long nanoTime1;

    /**
     * Nano seconds for running part 2 solutions.
     */
    private long nanoTime2;

    /**
     * Default constructor
     */
    public TimeAccumulator() {}


    // ---- Getters
    /**
     * Returns the accumulated run time for running <b>part 1</b> solutions.
     *
     * <p><i> Info: There are 1.000.000 nano seconds to a milli second.</i>
     *
     * @return accumulated run time for running part 1 solutions in milli seconds.
     */
    public long getTime1Milli() {
      return nanoTime1 / nanoToMilli;
    }

    /**
     * Returns the accumulated run time for running <b>part 2</b> solutions.
     *
     * <p><i> Info: There are 1.000.000 nano seconds to a milli second.</i>
     *
     * @return accumulated run time for running part 1 solutions in milli seconds.
     */
    public long getTime2Milli() {
      return nanoTime2 / nanoToMilli;
    }


    /**
     * Accumulates run times from the {@link base.ResultI} of a {@link base.DayI}.
     *
     * <p> The {@link base.DayI} must have already been run having a valid {@link base.ResultI}.
     *
     * <p> The accumulator can only accumulate run times where
     * the {@link base.ResultI} is both valid calling {@link base.ResultI#isValid}
     * and has a timed result calling {@link base.ResultI#isTimed}.
     *
     * <p> <b>part 1</b> and <b>part 2</b> are accumulated separately.
     *
     * @param dayI a {@link base.DayI} that's run its solutions.
     */
    public void addTime(DayI dayI) {
      ResultI result = dayI.getResult();
      boolean part1 = result != null && result.isValid() && result.isTimed();
      if (part1) {
        nanoTime1 += result.getNanoTime();
      }

      ResultI result2 = dayI.getResultPart2();
      boolean part2 = result2 != null && result2.isValid() && result2.isTimed();
      if (part2) {
        nanoTime2 += result2.getNanoTime();
      }
    }
  }

}
