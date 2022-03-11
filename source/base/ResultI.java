package base;

public interface ResultI {
  boolean isValid();

  StateI getState();

  boolean isTimed();
  long getNanoTime();
  long getMicroTime();
  long getMilliTime();

  long getResult();
  String getPrintableResult();

  boolean hasAscII();
  String[] getAscIIResult();
}
