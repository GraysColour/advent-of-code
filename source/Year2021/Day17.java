package Year2021;

/*
  Note that the progression of the x and the y values are
  somewhat independent, and can be analyzed individually.

  ## First: x-axis
     - This first part can be skipped..
     - TL;DR: Go directly to "But this isn't going into the implementation!"

    Since x + (x - 1) + (x - 2) + ... + 2 + 1 =
              ^ --- x - 1 elements -------- ^

          x + (x - 1) + 1 + (x - 2) + 2 ...   =

          x + (x + (1 - 1) + x + (2 - 2) ...) =

          x + (x + x ...) =
              ^ ------- ^  <-- ( (x - 1) / 2 ) elements
                           (1/2 of an element is when it's represented by half it's value ;-)

          x + (x + x ...)                     =
          ^ ----------- ^  <-- 1     + ( (x - 1) / 2 ) elements
                               2 / 2 + ( (x - 1) / 2 ) elements

          x * (2 / 2 + (x - 1) / 2))          =
          x * ( (2 + x - 1) / 2 )             = x * (x + 1) / 2

    The only valid x's are: minX <= x(x+1)/2 >= maxX where minX & maxX are the target area min & max

    minX = x(x+1)/2 is the same as x(x+1)/2 - minX = 0 which is x^2 + x - minX*2 = 0

    Solving that equation is just a regular polynomial: ax2 + bx + c = 0
    Solving it has two solutions:

      x1 = (-b + √d) / 2a = (-1 + √(1 + 8*minX)) / 2
      x2 = (-b - √d) / 2a = (-1 - √(1 + 8*minX)) / 2

      where d = b^2 - 4ac = 1^2 - 4 * 1 * (-minX)*2 = 1-(-8*minX) = 1 + 8*minX

    The axis of symmetry (the lowest point of the graph) is  -b / 2a = - 1 / 2
    which means that for integer values of x, the lowest of x1 and x2 will be negative.
    Only positive values are of interest, which is x1.

    So the smallest allowed x is (-1 + √(1 + 8*minX)) / 2
    And the largest allowed x is (-1 + √(1 + 8*maxX)) / 2

    Further reading:
      https://en.wikipedia.org/wiki/Triangular_number
      https://stackoverflow.com/questions/2483918/what-is-the-proof-of-of-n-1-n-2-n-3-1-nn-1-2
      https://www.web-formulas.com/Math_Formulas/Algebra_Polynomials_Second_Degree.aspx
      http://www.nabla.hr/FU-QuadEquQuadFun2.htm
      https://www.wikihow.com/Find-an-Axis-of-Symmetry

    ### But this isn't going into the implementation!

    The above only works if the series is exhaused! Meaning the steps >= initial value of x.

    For fewer steps, look at the calculation for Y. Which will explain:
      The valid x values for steps n are: minX < nx - (n(n+1)/2 - n) < maxX.
      Note that this only works as long as the step size is less than or equal to the x-value.
      For more steps, the x-value must be locked in, since further steps has a velocity of 0.


  ## Now: y

    Positive values of y are symmetrical. After (y * 2) + 1 steps, it's back to the 0-position.
    When reaching the zero-line, the increament-value will be negative:
      -y. The next will be (-y - 1) = -(y + 1).

    This also means that for a positive target area, any value for
    y as step one, will also work for step (y * 2).

    If the area crosses the 0-line, there's no maximum initial value for y.
    Any initial value will bring it back to the 0 line.

    ### Maximum values
      So the maximum possible y will always be -(minY+1) for negative minYs & maxYs.
      And the maximum posible Y when the area is above the line is maxY,
      since any higher value will overshoot.
      NOTE: This is the answer to part 1 of the puzzle in the form of n(n+1)/2

    ### Minimum values & ranges.

    The minimum value for y for a positive target area will be
      (y (y + 1) / 2) = minY for exactly y steps.
    Due to the symmetry, the range of y for a positive area will be between minY and maxY.

    The minimum value for y for a negative target area will be minY. But for negative areas, the range will be from minY to -(minY + 1).

    The range of either positive or negative areas becomes:
      steps 1: values between minY and maxY.
      steps 2: minY < y + (y - 1) = 2y - 1 < maxY.
      steps 3: minY < y + (y - 1) + (y - 2) = 3y - 3 < maxY.
      steps 4: minY < y + (y - 1) + (y - 2) + (y - 3) = 4y - 6 < maxY.
        noticing the triangular constant of n-1:
      steps n: minY < ny - (n(n+1)/2 - n) < maxY.

    which for minY gives:
                    minY   = ny - n(n+1)/2 + n
                    minY/n = y - (n+1)/2 + 1
      minY/n + (n+1)/2 - 1 = y

    as long as maxY/n + (n+1)/2 - 1 < y < minY/n + (n+1)/2 - 1 the area will be hit.

    Note that due to the symmetry any destination that can be reached for a negative value of y
    in n steps can also be reached by the equivalent positive value in ((-(y + 1) * 2) + 1 + n) steps.
    This is marked with (Roman numeral / *) and corresponding C-(Roman numeral / *) below:

    ### Examples for values maxY = -5 & minY = -10

      steps 1: -10/1 + (1+1)/2 - 1 <= y <= -5/1 + (1+1)/2 - 1
                               -10 <= y <= -5  (*)

      steps 2: -10/2 + (2+1)/2 - 1 <= y <= -5/2 + (2+1)/2 - 1
                              -4.5 <= y <= -2
        -2 -(-3)-> -5   (I)
        -3 -(-4)-> -7   (II)
        -4 -(-5)-> -9   (III)

      steps 3: -10/3 + (3+1)/2 - 1 <= y <= -5/3 + (3+1)/2 - 1
                             -2.33 <= y <= -0.66
        -1 -(-2)-> -3 -(-3)-> -6    (IV)
        -2 -(-3)-> -5 -(-4)-> -9    (V)

      steps 4: -10/4 + (4+1)/2 - 1 <= y <= -5/4 + (4+1)/2 - 1
                                -1 <= y <= 0.25
        -1 -(-1)-> -2 -(-2)-> -4 -(-3)-> -7  (VI)
        0 -(-1)-> -1 -(-2)-> -3 -(-3)-> -6   <-- C-(IV)

      steps 5: -10/5 + (5+1)/2 - 1 <= y <= -5/5 + (5+1)/2 - 1
                                 0 <= y <= 1
        0 -(-1)-> -1 -(-2)-> -3 -(-3)-> -6 -(-4)-> -10 <-- C-(VI)
        1 -(0)-> 1 -(-1)-> 0 -(-2)-> -2 -(-3)-> -5     <-- C-(I)

      steps 6: -10/6 + (6+1)/2 - 1 <= y <= -5/6 + (6+1)/2 - 1
                               0.83 < y < 1.66
        1 -(0)-> 1 -(-1)-> 0 -(-2)-> -2 -(-3)-> -5 -(-4)-> -9  <-- C-(V)

      steps 7: -10/7 + (7+1)/2 - 1 <= y <= -5/7 + (7+1)/2 - 1
                               1.57 < y < 2.28
        2 -(+1)-> 3 -(0)-> 3 -(-1)-> 2 -(-2)-> 0 -(-3)-> -3 -(-4)-> -7 <-- C-(II)

      steps 8: -10/8 + (8+1)/2 - 1 <= y <= -5/8 + (8+1)/2 - 1
                               2.25 < y < 2.88 <-- NO integer values

      steps 9: -10/9 + (9+1)/2 - 1 <= y <= -5/9 + (9+1)/2 - 1
                               2.88 < y < 3.44
        3 -(+2)-> 5 -(+1)-> 6 -(0)-> 6 -(-1)-> 5 -(-2)-> 3 -(-3)-> 0 -(-4)-> -4 -(-5)-> -9  <-- C-(III)

      steps 10: -10/10 + (10+1)/2 - 1 <= y <= -5/10 + (10+1)/2 - 1
                                   3.5 < y <= 4
        4 -(+3)-> 7 -(+2)-> 9 -(+1)-> 10 -(0)-> 10 -(-1)-> 9 -(-2)-> 7 -(-3)-> 4 -(-4)-> -0 -(-5)-> -5  <-- C-(*)

      ...

      steps 18: -10/18 + (18+1)/2 - 1 <= y <= -5/18 + (18+1)/2 - 1
                                  7.44 < y < 8.22  <-- C-(*)

      steps 19: -10/19 + (19+1)/2 - 1 <= y <= -5/19 + (19+1)/2 - 1
                                  8.47 < y < 8.73  <-- NO integer values

      steps 20: -10/20 + (20+1)/2 - 1 <= y <= -5/20 + (20+1)/2 - 1
                                    9 <= y < 9.25  <-- C-(*)

      steps 21: -10/21 + (21+1)/2 - 1 <= y <= -5/21 + (21+1)/2 - 1
                                  9.52 < y < 9.76  <-- NO integer values and the max has been exeeded.


  ## StepByStep

    The area needs to be reached by both x and y for any given step, so the number of combinations are
    almost the number of option for x at step A multiplied by the number of options for y with the same step A.

    Sometimes, this will be the result:

        Y ----
        ...
        Step 4: [-1, 0]
        Step 5: [0, 1]
        ...
        X ----
        ...
        Step 4: [7, 8, 9]
        Step 5: [6, 7, 8]
        ...

    Steps have unique starting points, but those points can appear in several steps.
    Simply compining them would lead to:

      Step 4: [-1, 7] - [-1, 8] - [-1, 9] - [0, 7] - [0, 8] - [0, 9]
      Step 5:                      [0, 6] - [0, 7] - [0, 8] - [1, 6] - [1, 7] - [1, 8]
                                             ^ duplicates ^
*/

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import absbase.DayX;
import util.Result;

public class Day17 extends DayX {

  private int xMin;
  private int xMax;
  private int yMin;
  private int yMax;
  private long stopAtY;

  // -----
  public static void main(String args[]){
    new Day17(args).doIt();
  }

  public Day17(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    int xStart = input.indexOf("x=");
    int comma  = input.indexOf(",");
    int yStart = input.indexOf("y=");
    int middle = 0;

    String xRange = input.substring(xStart + 2, comma);
    middle = xRange.indexOf("..");
    this.xMin = Integer.parseInt(xRange.substring(0,middle));
    this.xMax = Integer.parseInt(xRange.substring(middle + 2));

    String yRange = input.substring(yStart + 2);
    middle = yRange.indexOf("..");
    this.yMin = Integer.parseInt(yRange.substring(0,middle));
    this.yMax = Integer.parseInt(yRange.substring(middle + 2));
  }


  // -----
  public Result solve(String input) {
    setup(input);

    int initialYvelocity = 0;

    if (sign(this.yMax) != sign(this.yMin)) {
      return Result.createResult(Long.MAX_VALUE);
    } else if (this.yMin < 0) {
      initialYvelocity = (this.yMin * -1) - 1;
    } else {
      initialYvelocity = this.yMax;
    }

    this.stopAtY = initialYvelocity;
    return Result.createResult(initialYvelocity * (initialYvelocity + 1) / 2);
  }

  // -----
  private int sign(long value) {
    return value > 0 ? 1 : (value < 0 ? -1 : 0);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.stopAtY == 0) {
      solve(input);
    }

    // is it still 0?
    if (this.stopAtY == 0) {
      return Result.createResult(Long.MAX_VALUE);
    }

    // the y map must be sorted for the locked-in x on the steps to work.
    Map<Integer, int[]> ySteps = new TreeMap<>();
    Map<Integer, int[]> xSteps = new HashMap<>();

    BiFunction<Integer, Function<Double, Double>, Function<Integer, Integer>> curry
      = (base, function) ->
          (step) -> function.apply(base / (double) step + ( (double) step + 1 ) / 2 - 1).intValue();


    // All the Y mappings
    Function<Integer, Integer> lessThanY = curry.apply(this.yMin, Math::ceil);
    Function<Integer, Integer> greaterThanY = curry.apply(this.yMax, Math::floor);

    for (int i = 1;;i++) {
      int[] range = IntStream.rangeClosed(lessThanY.apply(i), greaterThanY.apply(i))
                             .toArray();
      if (range.length != 0) {
        ySteps.put(i, range);
      }
      if (range.length == 1 && range[0] == this.stopAtY) {
        break;
      }
    }

    // All the X mappings. Note that there's only a need to map steps that's valid for Y too
    Function<Integer, Integer> lessThanX = curry.apply(this.xMin, Math::ceil);
    Function<Integer, Integer> greaterThanX = curry.apply(this.xMax, Math::floor);

    boolean[] xLocked = new boolean[]{false, false};
    int[] xValues = new int[]{0, 0};

    for (Map.Entry<Integer, int[]> entry : ySteps.entrySet()) {
      int stepEntry = entry.getKey();

      if (xLocked[0] && xLocked[1]) { // Don't calculate, just reuse.
        if (xValues.length != 0) {
          xSteps.put(stepEntry, xValues);
        }
        continue;
      }

      if (!xLocked[0]) {
        int currentLessX = lessThanX.apply(stepEntry);
        xValues[0] = currentLessX;
        // once the step count is greater than the result, lock the value.
        if (stepEntry >= currentLessX) {
          xLocked[0] = true;
        }
      }
      if (!xLocked[1]) {
        int currentGreaterX = greaterThanX.apply(stepEntry);
        xValues[1] = currentGreaterX;
        if (stepEntry >= currentGreaterX) {
          xLocked[1] = true;
        }
      }

      int[] range = IntStream.rangeClosed(xValues[0], xValues[1])
                             .toArray();
      if (xLocked[0] && xLocked[1]) {
        xValues = range;  // Reusing the array.
      }
      if (range.length != 0) {
        xSteps.put(stepEntry, range);
      }
    }

    // Eleminate duplicates by inserting the combinations into a map.
    // values of x are shifted 32 to the left, so the combinations are represented as
    // first 32 bits is the x, last 32 bits is the y.
    Set<Long> combinedEntries = new HashSet<>(xSteps.size() * ySteps.size());
    xSteps.entrySet()
          .stream()
          .forEach(entry ->
                     IntStream.of(entry.getValue())
                              .mapToObj(x -> IntStream.of(ySteps.get(entry.getKey()))
                                                      .mapToLong(y -> (((long) x) << 32) + y))
                              .flatMapToLong(combStream -> combStream)
                              .forEach(combination -> combinedEntries.add(combination))
    );

    return Result.createResult(combinedEntries.size());
  }
}
