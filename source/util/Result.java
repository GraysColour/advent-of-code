package util;

import base.*;

public class Result implements ResultI {

  private State state;
  private long result;
  private long nanoTime = -1;
  private final String SEEASCII = "---";
  private String[] ascIIResult;
  private String ascIIConverted;

  private Result(long result){
    this.result = result;
    this.state = new State(Status.NORMAL_LONG);
  }

  private Result(State state){
    this.state = state;
  }

  private Result(String[] ascIIResult){
    this.ascIIResult = ascIIResult;
    this.state = new State(Status.ASCII);
    this.ascIIConverted = ConvertAscII.findLetters(ascIIResult);
  }


  public boolean isValid() {
    Status status = state.getStatus();
    return status.equals(Status.NORMAL_LONG) || status.equals(Status.ASCII);
  }

  public boolean hasAscII() {
    return ascIIResult != null && ascIIResult.length > 0;
  }


  // ---- Setters
  public void setNanoTime(long nanoTime) {
    this.nanoTime = nanoTime;
  }

  public void setAscIIResult(String[] ascIIResult) {
    this.ascIIResult = ascIIResult;
    this.state.setStatus(Status.ASCII);
    this.ascIIConverted = ConvertAscII.findLetters(ascIIResult);
  }


  public boolean isTimed() {
    return nanoTime != -1;
  }
  public long getNanoTime() {
    return nanoTime;
  }
  public long getMicroTime() {
    return (long) (nanoTime / 1e3);
  }
  public long getMilliTime() {
    return (long) (nanoTime / 1e6);
  }


  // ---- Getters
  public long getResult() {
    return result;
  }

  public String getPrintableResult() {
    return !hasAscII()
             ? "" + result
             : this.ascIIConverted == null || this.ascIIConverted.isEmpty()
                 ? SEEASCII
                 : ascIIConverted;
  }

  public String[] getAscIIResult() {
    return ascIIResult;
  }

  public State getState() {
    return state;
  }


  public static Result createResult(long result) {
    return new Result(result);
  }

  public static Result createAscIIResult(String[] ascII) {
    return new Result(ascII);
  }

  public static Result createFileErrorResult(State state) {
    return new Result(state);
  }

  public static Result createDummyResult() {
    return new Result(new State(Status.NO_RESULT));
  }


  @Override
  public String toString() {
    return "Result - state: " + state.getStatus() + ", result: " + result;
  }

}
