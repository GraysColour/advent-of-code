package base;

/**
 * The interface for {@link util.State} which is used
 * to determine the {@link util.Result#state state} of a {@link util.Result}
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public interface StateI {

  /**
   * Used by {@link util.Printers}.
   *
   * @return A {@link String} message expected to be printed.
   */
  String getMessage();

  /**
   * Used by {@link util.Printers}.
   *
   * @return An {@link Exception} expected to be printed.
   */
  Exception getException();

  /**
   * Used by {@link util.Printers}.
   *
   * @return {@link base.Status} expected to be evaluated.
   */
  Status getStatus();
}
