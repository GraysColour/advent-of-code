package base;

/**
 * Enum to determine the status of a {@link base.StateI}.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public enum Status {
  /**
   * Normal long result
   */
  NORMAL_LONG,

  /**
   * AscII result.
   *
   * <p> <i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  ASCII,

  /**
   * There is no result.
   *
   * <p> Signals a dummy {@link base.ResultI}
   *
   * <p> <i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  NO_RESULT,

  /**
   * Reading the file did not cause errors.
   *
   * <p> This is an intermediate {@link Status}.
   * <p> <i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  FILE_OK,

  /**
   * File cannot be found.
   *
   * <p> This is an error {@link Status}.
   * <p> <i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  NO_FILE,

  /**
   * File has been found, but it's empty.
   *
   * <p> This is an error {@link Status}.
   * <p> <i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  NO_FILE_CONTENT,

  /**
   * An exception occurred.
   *
   * <p> This is an error {@link Status}.
   * <p><i>Note: There may be a long of <code>0L</code>, but it's not representative.</i>
   */
  GOT_EXCEPTION;
}
