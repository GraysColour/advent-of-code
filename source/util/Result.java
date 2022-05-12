package util;

import base.*;

/**
 * Used by {@link absbase.DayX} to represent results
 *
 * <p> Either:
 * <ul>
 *   <li> a long result</li>
 *   <li> an ascII result</li>
 *   <li> an error</li>
 * </ul>
 * <br> when running a DayZ solution, where Z is the digit for the Day, for example: Day7.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class Result implements ResultI {

  /**
   * The {@link util.State} of this Result.
   */
  private State state;

  /**
   * A long result. This is the most common result.
   */
  private long result;

  /**
   * The run time in nano seconds. Default set to -1.
   */
  private long nanoTime = -1;


  /**
   * A fixed default {@link String} result that is set to the literal "---".
   *
   * <p> This is used when there's an ascII result, but no conversion to
   * letters are possible.
   * <br> It's assumed no conversion was possible if {@link #ascIIConverted} is empty.
   */
  private final String SEEASCII = "---";

  /**
   * An ascII {@link String} array result.
   */
  private String[] ascIIResult;

  /**
   * If possible, the ascII {@link String} array result
   * {@link #ascIIResult} converted to a {@link String} of letters.
   */
  private String ascIIConverted;


  /**
   * Creates a {@link Result} with the given <code>long</code> value.
   *
   * <p> Sets the {@link util.State} to {@link base.Status#NORMAL_LONG}.
   *
   * @param result the <code>long</code> result.
   */
  private Result(long result){
    this.result = result;
    this.state = new State(Status.NORMAL_LONG);
  }

  /**
   * Creates a {@link Result} with the given {@link util.State}.
   *
   * @param state to initialize the Result with.
   */
  private Result(State state){
    this.state = state;
  }


  /**
   * Creates a {@link Result} with the given {@link String} array result {@link #ascIIResult}.
   *
   * <p> Sets the
   *   <ul>
   *     <li>{@link util.State} to {@link base.Status#ASCII}.</li>
   *     <li>{@link #ascIIConverted} attribute to a {@link String} letter representation
   *         of the given ascII result using {@link util.ConvertAscII#findLetters(String[])}</li>
   *    </ul>
   *
   * @param ascIIResult the {@link String} array result.
   */
  private Result(String[] ascIIResult){
    this.ascIIResult = ascIIResult;
    this.state = new State(Status.ASCII);
    this.ascIIConverted = ConvertAscII.findLetters(ascIIResult);
  }


  /**
   * Returns <code>true</code> if the result is valid.
   *
   * <p> Checks if the {@link base.Status} of the result's {@link #state} is either
   * {@link base.Status#NORMAL_LONG} or {@link base.Status#ASCII}.
   * If it's not, it returns <code>false</code>.
   *
   * @return <code>true</code> if the result is valid. Otherwise <code>false</code>.
   */
  public boolean isValid() {
    Status status = state.getStatus();
    return status.equals(Status.NORMAL_LONG) || status.equals(Status.ASCII);
  }

  /**
   * Returns <code>true</code> if the result has
   * a {@link String} array result {@link #ascIIResult}.
   *
   * <p> Checks if the {@link #ascIIResult} is present
   * and composed of at least one {@link String} element.
   * If it doesn't, it returns <code>false</code>.
   *
   * @return <code>true</code> if the result has a {@link String}
   * array result {@link #ascIIResult}. Otherwise <code>false</code>.
   */
  public boolean hasAscII() {
    return ascIIResult != null && ascIIResult.length > 0;
  }


  // ---- Setters
  /**
   * Sets the {@link #nanoTime}.
   *
   * <p><i> Info: There are 1.000.000.000 nano seconds to a second.</i>
   *
   * @param nanoTime the time in nano seconds of the run time.
   */
  public void setNanoTime(long nanoTime) {
    this.nanoTime = nanoTime;
  }

  /**
   * Sets the {@link String} array result {@link #ascIIResult}.
   *
   * <p> Also set the
   *   <ul>
   *     <li>{@link util.State} to {@link base.Status#ASCII}.</li>
   *     <li>the {@link #ascIIConverted} to a {@link String} letter representation
   *         of the ascII result using {@link util.ConvertAscII#findLetters(String[])}</li>
   *   </ul>
   *
   * @param ascIIResult the {@link String} array result.
   */
  public void setAscIIResult(String[] ascIIResult) {
    this.ascIIResult = ascIIResult;
    this.state.setStatus(Status.ASCII);
    this.ascIIConverted = ConvertAscII.findLetters(ascIIResult);
  }


  /**
   * Returns <code>true</code> if the result has been timed.
   *
   * It checks if the {@link #nanoTime} is set to -1. If it is, <code>false</code> is returned.
   *
   * @return <code>true</code> if the result contains run time. Otherwise <code>false</code>.
   */
  public boolean isTimed() {
    return nanoTime != -1;
  }

  /**
   * Returns the run time in nano seconds.
   *
   * <p><i> Info: There are 1.000.000.000 nano seconds to a second.</i>
   *
   * @return the run time in nano seconds.
   */
  public long getNanoTime() {
    return nanoTime;
  }

  /**
   * Returns the run time in micro seconds.
   *
   * <p><i> Info: There are 1.000.000 micro seconds to a second.</i>
   *
   * @return the run time in micro seconds.
   */
  public long getMicroTime() {
    return (long) (nanoTime / 1e3);
  }

  /**
   * Returns the run time in milli seconds.
   *
   * <p><i> Info: There are 1000 milli seconds to a second.</i>
   *
   * @return the run time in milli seconds.
   */
  public long getMilliTime() {
    return (long) (nanoTime / 1e6);
  }


  // ---- Getters
  /**
   * Returns the long {@link #result}
   *
   * <p><i> Note: the result may not be accurate as it defaults to <code>0L</code></i>.
   * Call {@link #isValid} and {@link #hasAscII} to check the result.
   *
   * @return the long result.
   */
  public long getResult() {
    return result;
  }

  /**
   * A printable {@link java.lang.String} result.
   *
   * <p> The returned result is either
   *   <ul>
   *     <li> a {@link java.lang.String} representation of the long result</li>
   *     <li> a converted representation of a {@link String} array ascII result
   *          to a {@link String} of letters</li>
   *     <li> if none of the above, the literal {@link String} "---" </li>
   *   </ul>
   *
   * @return a printable result.
   */
  public String getPrintableResult() {
    return !hasAscII()
             ? "" + result
             : this.ascIIConverted == null || this.ascIIConverted.isEmpty()
                 ? SEEASCII
                 : ascIIConverted;
  }

  /**
   * Returns the ascII {@link String} array {@link ascIIResult}.
   *
   * @return the ascII {@link String} array.
   */
  public String[] getAscIIResult() {
    return ascIIResult;
  }

  /**
   * Returns the {@link util.State} of the result, {@link #state}
   *
   * @return the {@link util.State} of the result.
   */
  public State getState() {
    return state;
  }


  /**
   * Creates a {@link Result} with the given <code>long</code> value.
   *
   * <p> Sets the {@link util.State} to {@link base.Status#NORMAL_LONG}.
   *
   * @param result the <code>long</code> result.
   * @return the created {@link Result}.
   */
  public static Result createResult(long result) {
    return new Result(result);
  }

  /**
   * Creates a {@link Result} with the given {@link String} array result {@link #ascIIResult}.
   *
   * <p> Sets the
   *   <ul>
   *     <li>{@link util.State} to {@link base.Status#ASCII}.</li>
   *     <li>{@link #ascIIConverted} attribute to a {@link String} letter representation
   *         of the ascII result using {@link util.ConvertAscII#findLetters(String[])}</li>
   *    </ul>
   *
   * @param ascII the {@link String} array result.
   * @return the created {@link Result}.
   */
  public static Result createAscIIResult(String[] ascII) {
    return new Result(ascII);
  }

  /**
   * Creates a {@link Result} with the given {@link util.State}.
   *
   * @param state to initialize the Result with.
   * @return the created {@link Result}.
   */
  public static Result createFileErrorResult(State state) {
    return new Result(state);
  }

  /**
   * Creates a {@link Result} with a {@link util.State} set to {@link base.Status#NO_RESULT}.
   *
   * @return the created {@link Result}.
   */
  public static Result createDummyResult() {
    return new Result(new State(Status.NO_RESULT));
  }


  /**
   * Returns the result as a simple {@link String}
   *
   * <p> It will not return the {@link ascIIResult} {@link String} array.
   * But simply the {@link base.Status} of the {@link #state} and the long {@link #result};
   *
   * @return a representation of the result.
   */
  @Override
  public String toString() {
    return "Result - state: " + state.getStatus() + ", result: " + result;
  }

}
