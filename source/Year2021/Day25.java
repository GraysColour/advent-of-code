package Year2021;

/*
  The important trick is to not update/write to the same grid
  as is being read. If the grid is mutating while
  being read, it's impossible to tell if what is being read
  is the value in the old step or the changed value in the current step.

  When using a new grid to write to, "overlapping" also becomes
  easier to handle. For example a row can look like:

    ....v..v.>
    ^           <-- starting here

  If choosing to update any elements that's '.' having a '>'
  before it, and simply copying everything that doesn't
  fit that, a new copied row will start out with:

    >       .

  having anything but the two updated elements empty.
  When reaching the end of the read row:

    ....v..v.>
             ^  <-- here.

  the default is to just copy that over, since it doesn't
  comply with being a '.' with a '>' behind it.
  However, since the element has already been written,
  it can be ignored completely, and the result is:

    >...v..v..
*/

import absbase.DayX;
import util.Result;

public class Day25 extends DayX {

  private char[][] initialSeacucumbers;

  // -----
  public static void main(String[] args){
    new Day25(args).doIt();
  }

  public Day25(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    this.initialSeacucumbers = input.lines()
                                    .map(str -> str.toCharArray())
                                    .toArray(char[][]::new);
  }

  // -----
  public Result solve(String input) {
    if (initialSeacucumbers == null || initialSeacucumbers.length == 0) {
      setup(input);
    }

    int height = initialSeacucumbers.length;
    int width  = initialSeacucumbers[0].length;

    int moves = 0;
    int steps = 0;

    char[][] lastLocationSeacucumbers = null;
    char[][] movedSeacucumbers        = initialSeacucumbers;

    do {
      moves = 0;
      steps++;

      // set the original to the last
      lastLocationSeacucumbers = movedSeacucumbers;
      // do not work on the original
      movedSeacucumbers        = new char[height][width];

      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {

          if (movedSeacucumbers[i][j] != 0) {
            continue;  // edge case when the last element already moved.
          }

          if (lastLocationSeacucumbers[i][j] == '.'
              && lastLocationSeacucumbers[i][(j + width - 1) % width] == '>') {

            movedSeacucumbers[i][j] = '>';
            movedSeacucumbers[i][(j + width - 1) % width] = '.';
            moves++;

          } else {
            movedSeacucumbers[i][j] = lastLocationSeacucumbers[i][j];
          }
        }
      }

      lastLocationSeacucumbers = movedSeacucumbers;
      movedSeacucumbers        = new char[height][width];

      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {

          if (movedSeacucumbers[i][j] != 0) {
            continue;  // edge case when the last element already moved.
          }

          if (lastLocationSeacucumbers[i][j] == '.'
              && lastLocationSeacucumbers[(i + height - 1) % height][j] == 'v') {

            movedSeacucumbers[i][j] = 'v';
            movedSeacucumbers[(i + height - 1) % height][j] = '.';
            moves++;

          } else {
            movedSeacucumbers[i][j] = lastLocationSeacucumbers[i][j];
          }
        }
      }

    } while (moves != 0);

    return Result.createResult(steps);
  }
}
