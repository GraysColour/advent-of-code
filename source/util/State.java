package util;

import base.*;

public class State implements StateI {

  private Status status;
  private Exception exception;
  private String message;


  public State(Status status){
    this.status = status;
  }

  // ---- Setters
  public State withMessage(String message) {
    this.message = message;
    return this;
  }
  public State withException(Exception exception) {
    this.exception = exception;
    return this;
  }
  public void setStatus(Status status) {
    this.status = status;
  }

  // ---- Getters
  public String getMessage() {
    return this.message;
  }
  public Exception getException() {
    return this.exception;
  }
  public Status getStatus() {
    return this.status;
  }
}
