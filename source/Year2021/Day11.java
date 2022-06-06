package Year2021;

/*
  --- Part 1 ---:
    After setting up the octopuses into a int array grid,
    and creating a flashing queue,
    each step can be implemented with two parts:
    1:
      - Every octopus's that has a level above 9, has it's level set to 0,
        because that means it flashed in the step before this one.
      * If an octopus's level is exactly 9:
        - a flashcounter is incremented
        - it's put into the flashing queue.
      - increment all levels by one.
    2:
      - The queue is polled until it's empty, and for every octopus polled:
        - for each neighbor, do * above
        - increament its level.

    The test for exactly 9 will ensure that no octopus can flash more than once per step.
    Octopuses that have already contributed to
    the flash counter has a level of 10 when polled from the queue.
    That also means that a polled octopus can be handled as if it's its own neighbor.

    Setting all those with levels creater than 9 to 0 during the first "level up"-part
    means that the octopuses can be incremented any number of times
    without keeping a record of whether they already flashed in any given step.

  --- Part 2 ---:
    It's assumed that not all octopuses flashed in one go during part 1.
    Part 2 picks up the int array grid left at part 1,
    and continues until they all flash.
*/

import java.util.LinkedList;

import absbase.DayX;
import util.Result;

public class Day11 extends DayX {

  private int[][] octopuses;  // the input grid
  private int iterations = 100;
  private int energyLimit = 9;
  private int part = 1;

  // -----
  public static void main(String[] args){
    new Day11(args).doIt();
  }

  public Day11(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    this.octopuses = input.lines()
                          .map(string -> string.chars()
                                               .map(e -> e - '0')
                                               .toArray())
                          .toArray(int[][]::new);
  }

  // -----
  public Result solve(String input) {
    if (this.octopuses == null) {
      setup(input);
    }
    int localIterations = this.part == 2 ? Integer.MAX_VALUE : this.iterations;

    LinkedList<int[]> queue = new LinkedList<>();
    int ySize = octopuses.length;
    int xSize = octopuses[0].length;

    int totalFlashcounter = 0;
    int flashcounter = 0;
    int step = 0;

    for (int k = 0; k < localIterations; k++) {
      flashcounter = 0;
      step++;
      for (int j = 0; j < xSize; j++) {
        for (int i = 0; i < ySize; i++) {
          int check = octopuses[i][j];
          if (check == this.energyLimit) {
            flashcounter++;
            queue.add(new int[]{i, j});
          } else if (check > this.energyLimit) {
            octopuses[i][j] = 0;
          }
          octopuses[i][j]++;
        }
      }

      int[] point = new int[2];
      while ((point = queue.poll()) != null) {
        int x = point[1];
        int y = point[0];

        for (int i = Math.max(0, y - 1); i <= Math.min(y + 1, ySize - 1); i++) {
          for (int j = Math.max(0, x - 1); j <= Math.min(x + 1, xSize - 1); j++) {
            int check = octopuses[i][j];
            if (check == this.energyLimit) {
              flashcounter++;
              queue.add(new int[]{i, j});
            }
            octopuses[i][j]++;
          }
        }
      }
      if (this.part == 2 && flashcounter == ySize * xSize) {
        return Result.createResult(step);
      }
      totalFlashcounter += flashcounter;
    }

    return Result.createResult(totalFlashcounter);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.octopuses == null) {
      this.part = 2;
      return solve(input);
    }

    this.part = 2;
    long result = iterations + solve(input).getResult();
    this.part = 1;
    return Result.createResult(result);
  }
}
