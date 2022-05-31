package Year2021;

/*
  --- Part 1 ---:
    Think of a graph of the crabs ordered by distance.
      Each crab is represented by an entry on the x-axis
      The point/bar/hight on the y-axis is its distance.

                                  __
                                 |  |
                               __|  |
                              |  |  |
                               …  …
                            __|  |  |
                           |  |  |  |
                           |  |  |  |
                         __|  |  |  |
                        |  |  |  |  |
                __ __ __|  |  |  |  |     median is 2.
          __ __|  |  |  |  |  |  |  |
       __|  |  |  |  |  |  |  |  |  |
       0  1  1  2  2  2  4  7  14 16      each crab is represented by its distance.

    The median, which is the value that divides the crabs into two halfs
    is the optimal position to align the crabs.

    The reason is that one value over the median will have all the crabs to the left
    have to go one more horizontal position, while those to the right will go one less.
    ..with the exception of the crabs that are at the median position
    which will all have to one step more.

    Further away from the median, less crabs will have to go one less step,
    while more crabs will have to go one more step.

  --- Part 2 ---:
    The fuel consumption follows the triangular numbers.
      https://en.wikipedia.org/wiki/Triangular_number

    The formula for them are f(n) = n * (n + 1) / 2
    Example: f(4) = 4 + 3 + 2 + 1 = 4 * (4 + 1) / 2 = 10

    Each crab's fuel consumption will be:
      abs-f(CiPos - BPos) = f(distance) = f(|CiPos - BPos|)
    where |x| is the absolute value of x
    and CiPos is the position of a crab number i, and BPos is the bomb position.

    If it's possible to find a representation for the sum of all the f(|distance|) functions,
    and then find the minimum value, that would lead to a solution.

    Finding the minimal or maximum value of a function can be done by finding where its
    derivative is zero, since at that point it neither increases or decreases.

    The derivate of a sum is the sum of the derivatives,
    so only the derivative of abs-f needs to be found.

    In order to find the derivative of f(n) and abs-f(n), it is extended to the real numbers, R.
    But for the purpose of triangular numbers, f(n) is really only defined on the natural numbers, N.

    Given that f(n) is a "upward smiley face" parabola, meaning it's center is the
    smallest value, the zero-point of the derivative must be the minimal value.

    The derivative of f(n) is d/dn (n * (n + 1) / 2) = (2n + 1) / 2 = n + 1/2
    while the derivative of abs-f(n) = f(|n|) = n + n / (|n| * 2),
    which is undefined on 0 and isn't ever 0:

           |  /
           | /
           |/
       ____|____
           |
          /|
         / |
        /  |
    See https://www.symbolab.com/solver/derivative-calculator/%5Cfrac%7Bd%7D%7Bdx%7D%5Cleft(%5Cfrac%7B%5Cleft%7Cx%5Cright%7C%5Cleft(%5Cleft%7Cx%5Cright%7C%2B1%5Cright)%7D%7B2%7D%5Cright)?or=input

    The intesting part is that the function has a symmetrical offset around the x-asis.
    The part n / (|n| * 2) ensures that its offset is 1/2 on positive values of n
    and -1/2 on negative values of n.
    The effect is that for example two crabs that doesn't have the same position,
    will have optimal BP when they have an equal, but opposite distances to BP.
    Take C1Pos at 2 and C2Pos at 8. They'll have an optimal BP at 5, since they'll each be
    3 positions away from it and both have a cost of 6.
    Moving the BP closer to either one of them will decrease the cost for one by 3
    while increasing it by 4 on the other.

    Hack nr. 1:
    Just remove the offset of ±1/2 and use abs-f'(n) = n as the derivative. Setting it to 0:
      (C1Pos - BP) + (C2Pos - BP) + ... + (CnPos - BP) = 0
                  C1Pos + C2Pos + ... + CnPos - n * BP = 0
                           C1Pos + C2Pos + ... + CnPos = n * BP
                     (C1Pos + C2Pos + ... + CnPos) / n = BP
    Which is the average of the crab positions.

    Hack nr. 2:
    Use another function g(n) to represent the offset:
     abs-f'(n) = n + g(n), where g(n) = 1/2 for n > 0, 0 when n = 0, and -1/2 for n < 0:
      (C1Pos - BP + (g(C1Pos - BP))) + ... + (CnPos - BP + (g(CnPos - BP))) = 0
       C1Pos + ... + CnPos - n * BP + g(C1Pos - BP)) + ... +(g(CnPos - BP)) = 0
                C1Pos + ... + CnPos + g(C1Pos - BP)) + ... +(g(CnPos - BP)) = n * BP
                                               (C1Pos + ... + CnPos)/n ±1/2 = BP
    since each g(C1Pos - BP) is between -1/2 and 1/2, the sum is between -1/2 * n and 1/2 * n.
    Again it's the average of the crab positions.

    Note that the average isn't necessarily an integer.
    Rouding isn't going to give the correct result in all cases.
    For example 9 crabs at position 0 and one at position 8 will have an average of 0.8.
    A BP at 1 will give a total cost of f(7) + 9*f(1) = 28 + 9 = 37
    while at 0 will give a total cost of f(8) + 9*f(0) = 36 + 0 = 36.
    Looking at hack nr. 2, the solution is the average of 0.8 ±1/2, hence between 0.3 and 1.3.
    The rounded range gives the distinct two values of either 0 or 1.
*/

import java.util.Arrays;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.function.BiFunction;

import absbase.DayX;
import util.Result;

public class Day7 extends DayX {

  private int[] crabs;
  private int minPosition = Integer.MAX_VALUE;
  private int maxPosition = Integer.MIN_VALUE;

  private BiFunction<Integer, Integer, Integer> distance
    = (crabPos, BombPos) -> Math.abs(crabPos - BombPos);

  private BiFunction<Integer, Integer, Integer> triangularDistance
    = (crabPos, BombPos) -> Math.abs(crabPos - BombPos) * (Math.abs(crabPos - BombPos) + 1) / 2;

  // -----
  public static void main(String[] args){
    new Day7(args).doIt();
  }

  public Day7(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "Brute",          (i) -> {setup(i);            return solveBrute(i, this.distance);},
             "BruteAltSetup",  (i) -> {setupAlternative(i); return solveBrute(i, this.distance);},
             "Median",         (i) -> {setup(i);            return solveWithMedian(i);},
             "MedianAltSetup", (i) -> {setupAlternative(i); return solveWithMedian(i);}
            );

    this.alternatives2 =
      Map.of(
             "WithAverage",    (i) -> solvePart2WithAverage(i),
             "Brute",          (i) -> solveBrute(i, this.triangularDistance)
            );
  }

  // -----
  private void setup(String input) {
    String[] positionString = input.split(",");
    this.crabs = Arrays.stream(positionString)
                       .mapToInt(s -> Integer.parseInt(s))
                       .toArray();
  }

  // ----- Note: since this isn't using streams, it's faster
  private void setupAlternative(String input) {
    String[] positionString = input.split(",");
    int[] position = new int[positionString.length];
    for (int i = 0; i < position.length; i++) {
      position[i] = Integer.parseInt(positionString[i]);
    }
    this.crabs = position;
  }

  // -----
  public Result solve(String input) {
    return solveWithMedian(input);
    // return solveBrute(input, this.distance);
  }

  // -----
  // https://www.mathsisfun.com/median.html
  private Result solveWithMedian(String input) {
    if (this.crabs == null) {
      setup(input);
    }

    int[] crabs = Arrays.copyOf(this.crabs, this.crabs.length);
    Arrays.sort(crabs);

    int arrayLength = crabs.length;
    int medianIndex = (arrayLength % 2 == 0 ? arrayLength / 2 : arrayLength + 1 / 2);
    return Result.createResult(calculateFuel(crabs, crabs[medianIndex], this.distance));
  }

  // -----
  private void setMinMax(){
    for (int distance : this.crabs) {
      this.minPosition = this.minPosition > distance ? distance : this.minPosition;
      this.maxPosition = this.maxPosition < distance ? distance : this.maxPosition;
    }
  }

  // -----
  private Result solveBrute(String input, BiFunction<Integer, Integer, Integer> cost) {
    if (this.crabs == null) {
      setup(input);
    }

    if (this.minPosition == Integer.MAX_VALUE) {
      setMinMax();
    }

    return Result.createResult(
                    IntStream.rangeClosed(this.minPosition, this.maxPosition)
                             .map(position -> calculateFuel(this.crabs,
                                                            position,
                                                            cost))
                             .min()
                             .orElseThrow(() -> new AssertionError("Empty array")));
  }

  // -----
  private int calculateFuel(int[] array,
                            int position,
                            BiFunction<Integer, Integer, Integer> cost) {
    // calculate the fuel consumption if all
    // the crabs needs to go to this position
    int sum = 0;
    for (int j : array) {
      sum += cost.apply(j, position);
    }
    return sum;
  }

  // -----
  public Result solvePart2(String input) {
    if (this.crabs == null || this.crabs.length == 0) {
      setupAlternative(input);
    }

    return solvePart2WithAverage(input);
    // return solveBrute(input, this.triangularDistance);
  }

  // -----
  private Result solvePart2WithAverage(String input) {
    if (this.crabs == null) {
      setup(input);
    }

    double averageDouble = Arrays.stream(crabs)
                                 .average()
                                 .orElseThrow();
    int averageFloor = (int) averageDouble;

    return Result.createResult(
           Math.min(calculateFuel(crabs, averageFloor, this.triangularDistance),
                    calculateFuel(crabs, averageFloor + 1, this.triangularDistance)));
  }
}
