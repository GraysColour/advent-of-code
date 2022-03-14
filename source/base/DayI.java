package base;

public interface DayI {
  ResultI getResult();
  ResultI getResultPart2();

  void setPrinter(Runnable runnable);
  void setRunMe(Runnable runnable);
  void runVersusAlternatives(int iterations);

  void daySolver();
}
