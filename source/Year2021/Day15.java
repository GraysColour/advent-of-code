package Year2021;

/*
  --- Part 1 ---:
    This can be solved using Dijkstra's shortest path algorithm.
    See https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm

    A coordinate (point) in the carvern will be a vertex.
    The value of a coordinate will be an edge.
    Notice that edges are not bidirectionally uniform:

      A -->  4 6  <-- B

    Going from A to B will have an edge cost of 6.
    But going from B to A will only have an edge cost of 4.

    Dijkstra's shortest path algorithm will be modified slightly to:
      - Each point has information about
        - its coordinate
        - its current cost
        - whether it's been handled already.
        - the previous point on the path
      - Using a priority queue based on cost,
        the starting point is created with a cost of 0 and added to the queue.
      - Looping while polling the queue,
        which will give the point with the lowest cost.
        - if neighbors do not already exsit, create them.
        - for each neighbor:
          - Update its cost to whichever is smaller of:
            - its current cost.
            - a calculate cost based on the cost of the current point + the edge cost.
          - add it to the queue.
        - mark the current point as "handled".

        As long as it's the same exact objects being added to the queue,
        marking the points as "handled" enables adding all neighbors to the queue,
        regardless of them being there already.

    Implementation specific:
      A map is used to map points to their metrics.
      This is much easier than going through the priority queue
      to find
        A. if point has already been created.
        B. getting the exact object.

      There are two separate implementations:

      1. Using int arrays.
        Each point is an [y, x] with metrics:
          [current minimum cost, handled?],[last point on path]
        The priority queue holds for each point the two-dimensional array:
          [[y, x],[cost, 0/1],[yLast, xLast]]
        Easy access to that array comes from a map from the [y, x]
        to that two-dimensional array.

      2. Using a class, Point, that has the same information as instance variables.
        With the exception of the "last point on path",
        since that was only included to enable finding the actual path.
        The priority queue holds Points.
        The map maps Points to themselves for easy access to already created points.
        If map.get(new Point) returns a Point, the retrieved Point is used,
        and the new Point is discarded.

  --- Part 2 ---:
    Two separate solutions have been implemented for this:
    1. Extending the grid to be 5 x 5 of the original
       with each coordinate's weight pre-calculated.
    2. Adding a factor variable, that enables
       calculating the edge-weight on-the-fly based on the coordinates.
       In part 1 the factor is 1, but in part 2 the factor is set to 5.

    The only trick is to realize that the values 1 to 9
    can be seen as 0 to 8 (+1).
    So subtracting 1 from the orginal edge weight before
    adding the additional risklevel, enables % 9 of the sum.
    Then the result is found by mapping the
    value back to the range between 1 to 9 by adding 1.
*/

import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Comparator;
import java.util.Objects;

import absbase.DayX;
import util.Result;

public class Day15 extends DayX {

  private int[][] weights;  // the input
  private int weightsSize;
  private int factor = 1;   // used for scaling up the input grid in part 2

  // -----
  public static void main(String[] args){
    new Day15(args).doIt();
  }

  public Day15(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "Arrays", (i) -> solveArrays(i),
             "Class",  (i) -> solveClass(i)
            );
    this.alternatives2 =
      Map.of(
             "NewFactor", (i) -> solvePart2newFactor(i),
             "BigGrid",   (i) -> solvePart2BigGrid(i)
            );
  }

  // -----
  private void setup(String input) {
    this.weights = input.lines()
                        .map(string -> string.chars()
                                             .map(e -> e - '0')
                                             .toArray())
                        .toArray(int[][]::new);

    if (this.weights.length != this.weights[0].length) {
      throw new AssertionError("width of " + this.weights[0].length +
                               " and height of " + this.weights.length +
                               " in input is not square");
    }

    this.weightsSize = this.weights.length;
  }

  // -----
  public Result solve(String input) {
    // return solveArrays(input);
    return solveClass(input);
  }

  // -----
  private Result solveArrays(String input) {
    if (this.weights == null || this.weights.length == 0) {
      setup(input);
    }

    Comparator<int[]> pointComparator =
      (a, b) -> {
          int c = a[0] - b[0];
          return c == 0 ? a[1] - b[1] : c;
      };

    Comparator<int[][]> pointPriorityComparator =
      (a, b) -> a[1][0] - b[1][0];

    // [point],[current minimum cost, handled?],[last point on path]
    // represented by [[y, x],[cost, 0/1],[yLast, xLast]]
    PriorityQueue<int[][]> checkOut = new PriorityQueue<>(pointPriorityComparator);

    // contains [y, x] -> [[y, x][cost, 0/1],[yLast, xLast]]
    Map<int[], int[][]> points = new TreeMap<>(pointComparator);

    // create and insert the starting point
    int[] start = new int[]{0, 0};
    int[][] startMetric = new int[][]{start, new int[]{0, 0}, start};
    points.put(start, startMetric);
    checkOut.add(startMetric);

    // destination point:
    int endPoint = this.weightsSize * this.factor - 1;

    long result = 0L;
    // loop the queue
    int[][] current = null;
    while ((current = checkOut.poll()) != null) {

      // with this "hack" elements can be added even if they are in the queue already
      int handled = current[1][1];
      if (handled == 1) {  // oddly this never seems to happen :O
        System.out.println("  already handled");
        continue;
      }

      int[] point = current[0];
      int pointX = point[1];
      int pointY = point[0];

      int[] metric = current[1];
      int cost = current[1][0];

      if (pointX == endPoint && pointY == endPoint) {
        result = cost;
        break;
      }

      checkNeighborPoint(pointX - 1, pointY,     cost, point, checkOut, points);
      checkNeighborPoint(pointX + 1, pointY,     cost, point, checkOut, points);
      checkNeighborPoint(pointX,     pointY - 1, cost, point, checkOut, points);
      checkNeighborPoint(pointX,     pointY + 1, cost, point, checkOut, points);

      metric[1] = 1; // handled
    }

    return Result.createResult(result);
  }

  // -----
  private void checkNeighborPoint(int x, int y,
                                  int initialCost,
                                  int[] currentPoint,
                                  PriorityQueue<int[][]> checkOut,
                                  Map<int[], int[][]> points) {

    if (isOutOfBounds(x,y)) {
      return;
    }

    int[] neighborPoint = new int[]{y, x};
    int[][] pointMetric = points.get(neighborPoint);

    int newCost = initialCost + getWeight(x, y, this.factor);

    if (pointMetric == null) {
      // neighbor isn't in the map. Create one and add it
      pointMetric = new int[][]{neighborPoint, new int[]{newCost , 0}, currentPoint};
      points.put(neighborPoint, pointMetric);
      checkOut.add(pointMetric);
    } else {
      if (pointMetric[1][1] != 1) {  // yet unhandled
        int cost = pointMetric[1][0];
        if (cost > newCost) { // this never actually happens.. ?!?
          pointMetric[1][0] = newCost;
          pointMetric[2] = currentPoint;
          checkOut.add(pointMetric); // add it regardless if it's already there
        }
      }
    }
  }

  // -----
  private Result solveClass(String input) {
    if (this.weights == null || this.weights.length == 0) {
      setup(input);
    }

    Comparator<Point> pointPriorityComparator =
      (a, b) -> a.cost - b.cost;

    PriorityQueue<Point> checkOut = new PriorityQueue<>(pointPriorityComparator);
    Map<Point, Point> points = new HashMap<>();

    // create and insert the starting point
    Point start = new Point(0, 0, 0, false);
    points.put(start, start);
    checkOut.add(start);

    // destination point:
    int endPoint = this.weightsSize * this.factor - 1;
    Point end = new Point(endPoint, endPoint, Integer.MAX_VALUE, false);
    points.put(end, end);

    long result = 0L;
    // loop the queue
    Point current = null;
    while ((current = checkOut.poll()) != null) {
      if (current.handled) {  // this never happens :O
        continue;
      }

      if (current.equals(end)) {
        result = current.cost;
        break;
      }

      checkNeighborPointClass(current.x - 1, current.y,     current, checkOut, points);
      checkNeighborPointClass(current.x + 1, current.y,     current, checkOut, points);
      checkNeighborPointClass(current.x,     current.y - 1, current, checkOut, points);
      checkNeighborPointClass(current.x,     current.y + 1, current, checkOut, points);

      current.handled = true;
    }

    return Result.createResult(result);
  }

  // -----
  private void checkNeighborPointClass(int x, int y,
                                       Point current,
                                       PriorityQueue<Point> checkOut,
                                       Map<Point, Point> points) {
    if (isOutOfBounds(x,y)) {
      return;
    }

    int newCost = current.cost + getWeight(x, y, this.factor);

    Point neighborPoint = new Point(x, y, newCost, false);
    Point neighborDouble = points.get(neighborPoint);

    if (neighborDouble == null) {
      // neighbor isn't in the map
      points.put(neighborPoint, neighborPoint);
      checkOut.add(neighborPoint);
    } else {
      if (!neighborDouble.handled) {  // yet unhandled
        if (neighborDouble.cost > newCost) {
          neighborDouble.cost = newCost;
          checkOut.add(neighborDouble); // add it regardless if it's already there
        }
      }
    }
  }

  // -----
  private class Point {
    int x;
    int y;
    int cost;
    boolean handled;

    Point(int x, int y, int cost, boolean handled) {
      this.x = x;
      this.y = y;
      this.cost = cost;
      this.handled = handled;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }
    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || this.getClass() != o.getClass()) {
        return false;
      }
      Point point = (Point) o;
      return this.x == point.x
        && this.y == point.y;
    }
  }

  // -----
  private boolean isOutOfBounds(int x, int y) {
    // is x, y within bounds of the grid?
    if ((x < 0 || x > this.weightsSize * this.factor - 1)
        || (y < 0 || y > this.weightsSize * this.factor - 1)) {
      return true;
    }
    return false;
  }

  // -----
  private int getWeight(int x, int y, int factor) {
    int edgeWeight = this.weights[y % this.weightsSize][x % this.weightsSize];
    if (factor != 1) {
      // 1-9 is the same as 0-8 offset by one. Making % 9 work.
      // (x / this.weightsSize) is integer division
      // so it will be 0 if it's within the original grid, and 1 in the adjecent..
      return (edgeWeight - 1 + x / this.weightsSize + y / this.weightsSize) % 9 + 1;
    }
    return edgeWeight;
  }

  // -----
  public Result solvePart2(String input) {
    return solvePart2newFactor(input);
    // return solvePart2BigGrid(input);
  }

  // -----
  private Result solvePart2newFactor(String input) {
    if (this.weights == null || this.weights.length == 0) {
      setup(input);
    }

    this.factor = 5;
    Result result = solve(input);
    this.factor = 1;
    return result;
  }

  // -----
  private Result solvePart2BigGrid(String input) {
    if (this.weights == null || this.weights.length == 0) {
      setup(input);
    }

    int factor = 5;
    int upfactoredSize = this.weightsSize * factor;
    int[][] upfactoredWeights = new int[upfactoredSize][upfactoredSize];

    for (int x = 0; x < upfactoredSize; x++) {
      for (int y = 0; y < upfactoredSize; y++) {
        upfactoredWeights[y][x] = getWeight(x, y, factor);
      }
    }

    this.weights = upfactoredWeights;
    this.weightsSize = upfactoredSize;
    Result result = solve(input);
    setup(input);
    return result;
  }
}
