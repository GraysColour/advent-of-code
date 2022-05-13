package util;

import base.*;

/**
 * Used by {@link util.Result} to determine the state of the result.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class State implements StateI {

  /**
   * A {@link base.Status} of this {@link util.State}.
   */
  private Status status;

  /**
   * Any {@link java.lang.Exception} of this {@link util.State}.
   */
  private Exception exception;

  /**
   * Any {@link java.lang.String} message needed to explain this {@link util.State}.
   */
  private String message;


  /**
   * Creates a {@link util.State} with the given {@link base.Status}.
   *
   * <p> The {@link base.Status} can later be altered by using {@link #setStatus(Status)}
   *
   * @param status Any valid {@link base.Status} reflecting the status of the {@link util.State}.
   */
  public State(Status status){
    this.status = status;
  }


  // ---- Setters
  /**
   * Sets the {@link java.lang.String} message.
   *
   * @param message a {@link java.lang.String} message that the {@link util.State} should contain.
   * @return <code>this</code> instance.
   */
  public State withMessage(String message) {
    this.message = message;
    return this;
  }

  /**
   * Sets the {@link java.lang.Exception}.
   *
   * @param exception an {@link java.lang.Exception} that the {@link util.State} should contain.
   * @return <code>this</code> instance.
   */
  public State withException(Exception exception) {
    this.exception = exception;
    return this;
  }

  /**
   * Sets the {@link base.Status}.
   *
   * @param status the new {@link base.Status} that the {@link util.State} should have.
   */
  public void setStatus(Status status) {
    this.status = status;
  }


  // ---- Getters
  /**
   * Gets the {@link java.lang.String} message.
   *
   * @return A message expected to be printed by {@link util.Printers}.
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * Gets the {@link java.lang.Exception}.
   *
   * @return An {@link java.lang.Exception} expected to be printed by {@link util.Printers}.
   */
  public Exception getException() {
    return this.exception;
  }

  /**
   * Gets the {@link base.Status}.
   *
   * @return {@link base.Status} expected to be evaluated.
   */
  public Status getStatus() {
    return this.status;
  }

}
