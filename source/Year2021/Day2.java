package Year2021;

/*
  As with the first Day, the naive straight forward approach seems to work fine.
*/

import java.util.Map;

import absbase.DayX;
import util.Result;

public class Day2 extends DayX {

  private String[][] instructions;

  // -----
  public static void main(String args[]){
    new Day2(args).doIt();
  }

  public Day2(String[] args) {
    super(args);

    this.alternatives =
      Map.of("SetupStream",  (i) -> {setup(i);            return solve(i);},
             "SetupForLoop", (i) -> {setupAlternative(i); return solve(i);});
  }

  // -----
  private void setup(String input) {
    this.instructions =
      input.lines()
           .map(str -> {
                  int theSpace = str.indexOf(" ");
                  String command = str.substring(0, theSpace);
                  String theRange = str.substring(theSpace + 1);
                  return new String[]{command, theRange};
            })
           .toArray(String[][]::new);
  }

  // -----
  private void setupAlternative(String input) {
    String[] firstSplit = input.split("\\R");     // Split on newlines
    this.instructions = new String[firstSplit.length][2];

    for (int i = 0; i < firstSplit.length; i++) {
      String str = firstSplit[i];

      int theSpace = str.indexOf(" ");
      String command = str.substring(0, theSpace);
      String theRange = str.substring(theSpace + 1);

      this.instructions[i] = new String[]{command, theRange};
    }
  }

  // -----
  public Result solve(String input) {
    if (this.instructions == null || this.instructions.length == 0) {
      // setup(input);
      setupAlternative(input);
    }

    int horizontal = 0;
    int depth = 0;

    for (String[] instruction : instructions) {
      String command = instruction[0];
      int theRange = Integer.parseInt(instruction[1]);

      switch (command) {
        case "forward": horizontal += theRange;
                        break;
        case "down":    depth += theRange;
                        break;
        case "up":      depth -= theRange;
                        break;
        default:        throw new AssertionError("command: " + command);
      }
    }

    return Result.createResult(horizontal * depth);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.instructions == null || this.instructions.length == 0) {
      setupAlternative(input);
    }

    int horizontal = 0;
    int depth = 0;
    int aim = 0;

    for (String[] instruction : instructions) {
      String command = instruction[0];
      int theRange = Integer.parseInt(instruction[1]);

      switch (command) {
        case "forward": horizontal += theRange;
                        depth += aim * theRange;
                        break;
        case "down":    aim += theRange;
                        break;
        case "up":      aim -= theRange;
                        break;
        default:        throw new AssertionError("command: " + command);
      }
    }

    return Result.createResult(horizontal * depth);
  }
}
