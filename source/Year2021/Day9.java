package Year2021;

/*
  --- Part 1 ---:
    This is straight forward.
    Go through all the points and compare to its neighbors.
    A small trick is that if a point is on an edge,
    make the comrison to the non-existing neighbor to be true.
    For example if a point has index 0, it's smaller than the neighbor at index "-1".

  --- Part 2 ---:
    It's not possible to have more basins than lowpoints.
    However two or more lowpoints may be contained in one basin.

    The idea:
      Each basin becomes a set of points contained in the basin.

      Looping over lowpoints found in part 1:
      - If the lowpoint is already in an existing basin, then skip it.
        It and all its (recursively) neighboring points are already in that basin.
      - If not then create a new basin and add the point to an empty queue.
      - Loop over polling points from the queue until the queue is empty, and:
        - If the point is already in the basin, skip it. Else:
        - Find its neighbors.
        - Each neighbor that is both:
            A. not a 9
            B. isn't already in the basin
          is add it to the queue.
        - Add the point to the basin.

      ## Effectiveness of this approach

        Assuming A is a lowpoint, the points will start with A
        and be handled in following "alphabetical" order:

            First step:    Second step:    Third step:
                               F               F
                B             GBH             GBH
               CAE            CAE            ICAE
                D              D              JD


        ### First step:
          A's neightbors are B, C, D and E.
          They'll all be added to the queue and A will be the first point in a new basin.

        ### Second step:
          Next B will be pulled from the queue. Its neighbors are F, G, A and H.
          Since A is already in a basin, it's not added to the queue.
          B is added to the basin, and the queue is now: C, D, E, F, G, H

        ### Third step:
          Now C is polled from the queue. Its neighbors are G, I, J and A.
          Since G isn't already in the basin, it's added to the queue.
          Once C has been added to the basin, the queue will look like this:
            D, E, F, G, H, G, I, J
          So G is in the queue twice.
          However G will only be handled once.
          The second time it's polled, it will already be in the basin.
*/

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.Comparator;

import absbase.DayX;
import util.Result;

public class Day9 extends DayX {

  private int[][] floor;  // the input after it's been split by newline -> space
  private Set<int[]> lowpoints = new HashSet<>();

  // -----
  public static void main(String args[]){
    new Day9(args).doIt();
  }

  public Day9(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    this.floor = input.lines()
                      .map(string -> string.chars()
                                            // map the 0-9 characters to their integer values.
                                           .map(e -> e - '0')
                                           .toArray())
                      .toArray(int[][]::new);
  }

  // -----
  public Result solve(String input) {
    if (this.floor == null) {
      setup(input);
    }

    //      <-- floorLength -->
    //    ^
    //    |
    // floorWidth
    //    |

    int floorWidth = floor.length;
    int floorLength = floor[0].length;
    int result = 0;

    for (int i = 0; i < floorLength; i++) {
      for (int j = 0; j < floorWidth; j++) {
        int currentHeight = floor[j][i];

        //            north
        //
        //    east   current   west
        //
        //            south

        // the "or" will short-circuit on edges resulting in true
        boolean east  = i == 0               || currentHeight < floor[j][i-1];
        boolean west  = i == floorLength - 1 || currentHeight < floor[j][i+1];
        boolean north = j == 0               || currentHeight < floor[j-1][i];
        boolean south = j == floorWidth - 1  || currentHeight < floor[j+1][i];

        if (east && west && north && south) {
          this.lowpoints.add(new int[]{i, j});
          result += currentHeight + 1;
        }
      }
    }

    return Result.createResult(result);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.lowpoints.isEmpty()) {
      solve(input);
    }

    List<TreeSet<int[]>> basins = new ArrayList<>(this.lowpoints.size());
    LinkedList<int[]> queue = new LinkedList<int[]>();

    for (int[] basinLowpoint : this.lowpoints) {
      // if this point is already in a basin, skip it
      if (basins.stream().anyMatch(basin -> basin.contains(basinLowpoint)))
        continue;

      queue.add(basinLowpoint);

      TreeSet<int[]> currentBasin = new TreeSet<>(Arrays::compare);

      int[] next = null;
      while ((next = queue.poll()) != null) {
        if (currentBasin.contains(next)) { // it's already been handled
          continue;
        }

        int x = next[0];
        int y = next[1];

        // neighbors beyond the edges are nulls
        int[] north = y == 0 ? null : new int[]{x, y - 1};
        int[] east  = x == this.floor[0].length - 1 ? null : new int[]{x + 1, y};
        int[] south = y == this.floor.length - 1 ? null : new int[]{x, y + 1};
        int[] west  = x == 0 ? null : new int[]{x - 1, y};

        Arrays.asList(north, east, south, west)
              .stream()
              .forEach(point -> {
                 if (point == null)
                   return;
                 if (floor[point[1]][point[0]] == 9)
                   return;
                 if (currentBasin.contains(point))
                   return;
                 queue.add(point);
               });
        currentBasin.add(next);
      }

      basins.add(currentBasin);
    }

  return Result.createResult(basins.stream()
                                   .map(set -> set.size())
                                   .sorted(Comparator.reverseOrder())
                                   .limit(3)
                                   .reduce(1, (a, b) -> a * b));
  }
}
