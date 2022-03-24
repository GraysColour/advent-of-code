package util;

import java.util.function.Consumer;
import java.util.function.Function;

import base.*;

public class Timers {

  private Timers() {}

  public static <T> Result timeItResult(Function<T, Result> doMe, T input) {
    long startTime = System.nanoTime();
    Result result = doMe.apply(input);
    result.setNanoTime(System.nanoTime() - startTime);
    return result;
  }

  public static <T> long timeItNoreturn(Consumer<T> doMe, T input) {
    long startTime = System.nanoTime();
    doMe.accept(input);
    long elapsedNanos = System.nanoTime() - startTime;
    return elapsedNanos;
  }

  public static <T> long[] timeItReturn(Function<T, Result> doMe, T input) {
    long startTime = System.nanoTime();
    Result result = doMe.apply(input);
    long elapsedNanos = System.nanoTime() - startTime;
    return new long[]{elapsedNanos, result.getResult()};
  }

  public static class TimeAccumulator {

    private final long nanoToMilli = 1000000;
    private long nanoTime1;
    private long nanoTime2;

    public TimeAccumulator() {}


    // ---- Getters
    public long getTime1Milli() {
      return nanoTime1 / nanoToMilli;
    }
    public long getTime2Milli() {
      return nanoTime2 / nanoToMilli;
    }

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
