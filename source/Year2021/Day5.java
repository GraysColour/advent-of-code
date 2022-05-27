package Year2021;

/*
  Two different solutions:

    1. Using a two-dimensional array as a grid of all points.
       Each time a [x1, y1, x2, y2] is encountered,
       every matching coordinate is increased by 1.
       Whenever a coordinate is already 1 when increased, a counter is incremented.

       To prepare for part two, all diagonal [x1, y1, x2, y2]
       are put into a separate two-dimensional array.

       The loop counter for diagonals depend on which of x1 or x2 is smaller.
       If x1 is smaller than x2, then the counter is incremented.
       If x1 is larger than x2, then the counter is decremented.
       The same is done with with y.
       Both x's and y's are simultatiously
       initialized and modified by the loop construction.
       Note that x2 and y2 are included, so the loop stop condition must be
       one more than the value of the end coordinate.

    2. Using a map that only holds the coordinates of a crossed vent line.
       The map used is a sorted treemap, where it's possible to identify
       identical arrays with ordered content, where [1, 2] is not equal to [2, 1].

       Each coordinate within [x1, y1, x2, y2]
       is added to the map with an initial value of 1.
       If the map already contains a coordinate, its value is increased by 1.
       Once it's increased, if the value is 2, a counter is incremented.
       It seemed more effecient to use map.merge, than to check
       for the presence of an array in the map & if present the current value.
       map.merge return the merged value, which is 2 only once :)

       Part 2 is handled in a similar way as with the grid solution.
*/

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Arrays;
import java.util.function.BiFunction;

import absbase.DayX;
import util.Result;

public class Day5 extends DayX {

  private int coordinateWidth;              // max grid value // = 1000;
  private int[][] inputCoordinates;         // contains arrays of [x1, y1, x2, y2]
  private int[][] inputCoordinatesDiagonal;

  private int[][] gridOfPoints;             // contains points with their number of vent lines

  private BiFunction<Integer, Integer, Integer> mapping
    = (oldValue, newValue) -> oldValue + newValue;
  private Map<int[], Integer> mapOfPoints;  // contains points with at least one vent line

  private int overlapCountPart1;

  // -----
  public static void main(String args[]){
    Day5 me = new Day5(args);
    me.doIt();
  }

  public Day5(String[] args) {
    super(args);

    this.alternatives =
      Map.of("Grid", (i) -> solveWithGrid(i),
             "Map",  (i) -> solveWithMap(i));

    this.alternatives2 =
      Map.of("Grid", (i) -> solvePart2WithGrid(i),
             "Map",  (i) -> solvePart2WithMap(i));
  }

  // -----
  private void setup(String input) {
    String[] firstSplit = input.split("\\R");     // Split on newlines

    int maxValue = 0;
    int[][] intCoordinates = new int[firstSplit.length][4];

    for (int i = 0; i < firstSplit.length; i++) {
      String[] coordinates = firstSplit[i].split(",|\\s->\\s");

      int x1 = Integer.parseInt(coordinates[0]);
      int y1 = Integer.parseInt(coordinates[1]);
      int x2 = Integer.parseInt(coordinates[2]);
      int y2 = Integer.parseInt(coordinates[3]);

      int[] range = new int[]{x1, y1, x2, y2};
      int maxCoordinate = findMax(range);
      maxValue = maxValue < maxCoordinate ? maxCoordinate : maxValue;

      intCoordinates[i] = range;
    }

    this.coordinateWidth = maxValue + 1;
    this.inputCoordinates = intCoordinates;
  }

  // -----
  private int findMax(int[] values) {
    return Arrays.stream(values)
                 .max()
                 .orElseThrow();
  }

  // -----
  public Result solve(String input) {
    return solveWithGrid(input);
    // return solveWithMap(input);
  }

  // -----
  private Result solveWithGrid(String input) {
    setup(input);
    List<int[]> diagonals = new LinkedList<>();

    int overlapCount = 0;

    int[][] grid = new int[coordinateWidth][coordinateWidth];
    for (int[] coordinates : inputCoordinates) {

      int x1 = coordinates[0];
      int y1 = coordinates[1];
      int x2 = coordinates[2];
      int y2 = coordinates[3];

      if (x1 == x2) {
        for (int i = Math.min(y1,y2); i <= Math.max(y1,y2); i++) {
          int count = grid[x1][i];
          if (count == 1) overlapCount++;
          grid[x1][i] = ++count;
        }
      } else if (y1 == y2){
        for (int i = Math.min(x1,x2); i <= Math.max(x1,x2); i++) {
          int count = grid[i][y1];
          if (count == 1) overlapCount++;
          grid[i][y1] = ++count;
        }
      } else {
        // collect the point for part 2
        diagonals.add(coordinates);
      }
    }

    // prepare for part 2
    this.gridOfPoints = grid;
    this.inputCoordinatesDiagonal = diagonals.stream().toArray(int[][]::new);

    this.overlapCountPart1 = overlapCount;
    return Result.createResult(overlapCount);
  }

  // -----
  private Result solveWithMap(String input) {
    setup(input);
    List<int[]> diagonals = new LinkedList<>();

    int overlapCount = 0;

    Map<int[], Integer> map = new TreeMap<>(Arrays::compare);

    for (int[] coordinates : inputCoordinates) {
      int x1 = coordinates[0];
      int y1 = coordinates[1];
      int x2 = coordinates[2];
      int y2 = coordinates[3];

      if (x1 == x2) {
        for (int i = Math.min(y1,y2); i <= Math.max(y1,y2); i++) {
          int accumulation = map.merge(new int[]{x1,i}, 1, this.mapping);
          if (accumulation == 2) overlapCount++;
        }
      } else if (y1 == y2) {
        for (int i = Math.min(x1,x2); i <= Math.max(x1,x2); i++) {
          int accumulation = map.merge(new int[]{i,y1}, 1, this.mapping);
          if (accumulation == 2) overlapCount++;
        }
      } else {
        // collect the point for part 2
        diagonals.add(coordinates);
      }
    }

    // prepare for part 2
    this.mapOfPoints = map;
    this.inputCoordinatesDiagonal = diagonals.stream().toArray(int[][]::new);

    this.overlapCountPart1 = overlapCount;
    return Result.createResult(overlapCount);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.mapOfPoints == null || this.mapOfPoints.isEmpty()) {
      solve(input);
    }

    return solvePart2WithGrid(input);
    // return solvePart2WithMap(input);
  }

  // -----
  private Result solvePart2WithGrid(String input) {
    if (this.gridOfPoints == null || this.gridOfPoints.length == 0) {
      solveWithGrid(input);
    }

    int overlapCount = 0;

    int[][] grid = this.gridOfPoints;
    for (int[] coordinates : inputCoordinatesDiagonal) {
      int x1 = coordinates[0];
      int y1 = coordinates[1];
      int x2 = coordinates[2];
      int y2 = coordinates[3];

      // if x1 is larger then x2, the loop increments should be negative
      int offsetX = x1 < x2 ? 1 : -1;
      int offsetY = y1 < y2 ? 1 : -1;

      for (int i = x1, j = y1;
           i != x2 + offsetX;  // promise of diagonals is that this is reached exactly
           i += offsetX, j += offsetY) {
        int count = grid[i][j];
        if (count == 1) overlapCount++;
        grid[i][j] = ++count;
      }
    }

    return Result.createResult(this.overlapCountPart1 + overlapCount);
  }

  // -----
  private Result solvePart2WithMap(String input) {
    if (this.mapOfPoints == null || this.mapOfPoints.isEmpty()) {
      solveWithMap(input);
    }

    Map<int[], Integer> map = this.mapOfPoints;

    int overlapCount = 0;

    for (int[] coordinates : inputCoordinatesDiagonal) {
      int x1 = coordinates[0];
      int y1 = coordinates[1];
      int x2 = coordinates[2];
      int y2 = coordinates[3];

      int offsetX = x1 < x2 ? 1 : -1;
      int offsetY = y1 < y2 ? 1 : -1;

      for (int i = x1, j = y1;
           i != x2 + offsetX;
           i += offsetX, j += offsetY) {

        int accumulation = map.merge(new int[]{i,j}, 1, this.mapping);
        if (accumulation == 2) overlapCount++;
      }
    }

    return Result.createResult(this.overlapCountPart1 + overlapCount);
  }
}
