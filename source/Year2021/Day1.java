package Year2021;

/*
  There's not really anything to this except noticing that for part 2:
    (d+c+b) - (c+b+a) = d + c + b - c - b - a = d - a
*/

import java.util.stream.IntStream;

import absbase.DayX;
import util.Result;

public class Day1 extends DayX {

  private String[] splitInput;

  // -----
  public static void main(String[] args){
    new Day1(args).doIt();
  }

  public Day1(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    this.splitInput = input.split("\\R");     // Split on newlines
  }

  // -----
  public Result solve(String input) {
    if (this.splitInput == null || this.splitInput.length == 0) {
      setup(input);
    }

    int counter = 0;
    int last = Integer.MAX_VALUE;  // challenge says larger, so this should be fine

    for (String s : this.splitInput) {
      int current = Integer.parseInt(s);
      if (current > last) {
        counter++;
      }
      last = current;
    }

    return Result.createResult(counter);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.splitInput == null || this.splitInput.length == 0) {
      setup(input);
    }

    /*
     ( (arr[i - 2] + arr[i - 1] + arr[i]) - (arr[i - 3] + arr[i - 2] + arr[i - 1]) )
     is the same as ( arr[i] - (arr[i - 3] )
    */

    return Result.createResult(
             IntStream.range(3, this.splitInput.length)
                      .map(i -> Integer.parseInt(this.splitInput[i])
                                - Integer.parseInt(this.splitInput[i - 3]))
                      .filter(i -> i > 0)
                      .count());
  }
}
