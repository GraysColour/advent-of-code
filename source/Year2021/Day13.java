package Year2021;

/*
  The idea:
    Start with a set that contain all the #-points.

    Create a new set where symmetrical points relative to the
    foldline are considered comparatively identical.
    For example with "fold along y=7" then points [x, 6] and [x, 8] would be
    identical, since they'd both be on the same spot when the fold is applied.
    This is easy to do with a sorted set and a crafted comparator using,
    for example for y:
      Math.abs(foldline - y-coordinate)
    which is the length from the foldline to the coordinate
    making 6 and 8 identical on a foldline of 7.

    Then clean up the set, so only the lower mirrored points remains.
    From the example, [x, 6] is fine, but if the point in
    the set is [x, 8] after the fold, then it need to become [x, 6]
    for the next iteration of folds.

    If the set isn't clearned up, then the next iteration will not succeed.
    For example "fold along y=5" would have [x, 8] be at a distance of 3,
    when it's actually a distance of 1 from the foldline, since it's at [x, 6].

    Since sorted sets do not apply the sorting algorithm to already inserted items,
    the cleanup is most easily done by creating a new set, and inserting
    all the adjusted points into that, where the adjusted y coordinate =
      foldline - Math.abs(foldline - y-coordinate).


  Alternative, but similar idea:
    Since each fold is just applying: foldValue - Math.abs(foldValue - coordinateValue),
    it's possible to collect all the folds for each coordinate into one
    combined function, and apply that to all the points in one move.
    That would make sorted sets unnecesary, except maybe for counting the unique #-points.
    This method is discussed in details below just before
    the implemention of the alternative approach.
*/

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.Arrays;

import absbase.DayX;
import util.Result;
import util.Printers;

public class Day13 extends DayX {

  private String[][] instructions;

  private TreeSet<long[]> hashCoordinates;  // initial set of arrays of x, y points.
  private TreeSet<long[]> resulthashes;     // result set of arrays of x, y points.

  private long[][] hash2DCoordinates;       // x, y points. Used in the alternative method.

  // -----
  public static void main(String[] args){
    new Day13(args).doIt();
  }

  public Day13(String[] args) {
    super(args);

    // making printers also print the ascII
    this.printers = new ArrayList<>(printers);
    printers.add(() -> Printers.printAscIIResult(this));

    this.alternatives =
      Map.of(
             "TreeSets",    (i) -> solveFirstOption(i),
             "CurryNarray", (i) -> solveAlternative(i)
            );
    this.alternatives2 =
      Map.of(
             "TreeSets",    (i) -> solvePart2FirstOption(i),
             "CurryNarray", (i) -> solvePart2Alternative(i)
            );
  }

  // -----
  public Result solve(String input) {
    // return solveFirstOption(input);
    return solveAlternative(input);
  }
  public Result solvePart2(String input) {
    // return solvePart2FirstOption(input);
    return solvePart2Alternative(input);
  }

  // -----
  private void setup(String input) {
    String[] dataNinstructions = input.split("\\R{2}");

    // Using a set of unique points.
    this.hashCoordinates =
      dataNinstructions[0].lines()
                          .map(string -> {
                                           String[] coord = string.split(",");
                                           return new long[]{Long.parseLong(coord[0]),
                                                             Long.parseLong(coord[1])};
                                         }
                               )
                          .collect(Collectors.toCollection(
                             () -> new TreeSet<>(folderComparators("none", 0)))
                           );

    setupInstructions(dataNinstructions[1]);
  }

  // -----
  private void setupInstructions(String instructions) {
    String foldString = "fold along ";
    int foldStringLength = foldString.length();
    this.instructions =
      instructions.lines()
                  .map(str -> {
                         int stringEquals = str.indexOf("=");
                         String direction = str.substring(foldStringLength, stringEquals);
                         String foldline = str.substring(stringEquals + 1);
                         return new String[]{direction, foldline};
                   })
                  .toArray(String[][]::new);
  }

  // -----
  int sign(long value) {
    return value > 0 ? 1 : (value < 0 ? -1 : 0);
  }

  // -----
  private Comparator<long[]> folderComparators (String coordinate, long z) {
    if ("x".equals(coordinate)) {
      // fold along x = 5 <-- z
      // then (4, y) is the same as (6, y)
      return (a, b) -> {
               long c = Math.abs(z - b[0]) - Math.abs(z - a[0]);
               return sign(c == 0 ? a[1] - b[1] : c);
             };
    } else if ("y".equals(coordinate)) {
      // fold along y = 7 <-- z
      // then (x, 6) is the same as (x, 8)
      return (a, b) -> {
               long c = a[0] - b[0];
               return sign(c == 0 ? Math.abs(z - b[1]) - Math.abs(z - a[1]) : c);
             };
    } else {
      // compare x then y normally. Used for the inital setup:
      return (a, b) -> {
               long c = a[0] - b[0];
               return sign(c == 0 ? a[1] - b[1] : c);
             };
    }
  }

  // ----- Reinsert the values with their coordinates mirrored properly
  private TreeSet<long[]> cleanup (TreeSet<long[]> foldedHashes,
                                   String direction,
                                   long foldline) {

    // reuse the comparator
    TreeSet<long[]> temp = new TreeSet<>(foldedHashes.comparator());

    if ("x".equals(direction)) {
      foldedHashes.forEach(
        element -> {
          temp.add(new long[]{foldline - Math.abs(foldline - element[0]),
                              element[1]});
      });
    } else if ("y".equals(direction)) {
      foldedHashes.forEach(
        element -> {
          temp.add(new long[]{element[0],
                              foldline - Math.abs(foldline - element[1])});
      });
    }
    return temp;
  }

  // -----
  private Result solveFirstOption(String input) {
    setup(input);

    TreeSet<long[]> foldedHashes = new TreeSet<>();
    long result = 0;

    for (String[] instruction : this.instructions) {
      // Get the instruction:
      String direction = instruction[0];
      long foldline = Long.parseLong(instruction[1]);

      // Insert all the coordinates into a new TreeSet with a corresponding comparator:
      TreeSet<long[]> newFold = new TreeSet<>(folderComparators(direction, foldline));
      if (foldedHashes.isEmpty()) {
        newFold.addAll(hashCoordinates);
        result = newFold.size();
      } else {
        newFold.addAll(foldedHashes);
      }

      // Mirror the coordinates to be less/lower/under the foldline
      foldedHashes = cleanup(newFold, direction, foldline);
    }

    this.resulthashes = foldedHashes;
    return Result.createResult(result);
  }

  // ----- This just enable printing the thing to the console.
  private Result solvePart2FirstOption(String input) {
    if (this.resulthashes == null || this.resulthashes.isEmpty()) {
      setup(input);
    }

    int maxX = (int) resulthashes.stream()
                                 .mapToLong(arr -> arr[0])
                                 .max()
                                 .orElse(0);

    int maxY = (int) resulthashes.stream()
                                 .mapToLong(arr -> arr[1])
                                 .max()
                                 .orElse(0);

    StringBuilder[] ascIIimage = new StringBuilder[maxY + 1];

    IntStream.rangeClosed(0, maxY)
              .forEach(i -> ascIIimage[i] = new StringBuilder(" ".repeat(maxX + 1)));

    resulthashes.stream()
                .forEach(arr -> ascIIimage[(int) arr[1]].setCharAt((int) arr[0], '#'));

    return Result.createAscIIResult(
             IntStream.rangeClosed(0, maxY)
                      .mapToObj(i -> ascIIimage[i].toString())
                      .toArray(String[]::new));
  }

/* -------------------------------------------------------------------------------------

  Alternative implementation using function currying.

  ## The section explains the curry function
    BiFunction<Function<Integer, Integer>, Integer, Function<Integer, Integer>>
      curry = (function, foldValue) ->
                (coord) -> foldValue - Math.abs(foldValue - function.apply(coord));

  ## The idea
    Each fold is just applying: foldValue - Math.abs(foldValue - coordinateValue)
    Doing it twice results in:
      coordinateValue2 = foldValue1 - Math.abs(foldValue1 - coordinateValue1);
      coordinateValue3 = foldValue2 - Math.abs(foldValue2 - coordinateValue2);
                       = foldValue2 - Math.abs(foldValue2 -
                                              (foldValue1 - Math.abs(foldValue1 - coordinateValue1)));

    With this folding function: (coord, foldV) -> foldV - Math.abs(foldV - coord),
      foldV can be made a constant with this:
        (coord, foldV) -> (coord) -> foldV - Math.abs(foldV - coord)
      The next coordinate is the result of function.apply(foldV).apply(coord)
      since the first apply(foldV) returns the function:
        (coord) -> foldV - Math.abs(foldV - coord)
      where foldV is a constant.

    Defining this: curry = (function, foldV) -> (coord) -> foldV - Math.abs(foldV - function.apply(coord))
      the folding can be done any number of times:
           firstFold     = initialFunction.apply(foldV1);
           second        = curry.apply(setFirstFold, foldV2);
           third         = curry.apply(second, foldV3);
           ...
           last          = curry.apply(secondLast, foldVLast);
           coordinateResult = last.apply(coord);

    The first function can be defined as:
      Function<Integer, Function<Integer, Integer>>
        initialFunction = (foldV) -> (coord) -> foldV - Math.abs(foldV - coord)

    While the remaining ones with the definition as the "curry" above.
      BiFunction<Function<Integer, Integer>, Integer, Function<Integer, Integer>>

    But the "initialFunction" is just an application of the indentity function coord -> coord on the curry:
      firstFold = curry.apply(coord -> coord, foldV);
     or
      firstFold = curry.apply(Function.identity(), foldV);

    So the folding can be done as so instead:
           firstFold     = curry.apply(Function.identity(), foldV1);
           second        = curry.apply(firstFold, foldV2);
           third         = curry.apply(second, foldV3);
           ...
           last          = curry.apply(secondLast, foldVLast);
           coordinateResult = last.apply(coord);
*/

  // -----
  private void setupAlternative(String input) {
    String[] dataNinstructions = input.split("\\R{2}");
    this.hash2DCoordinates =
      dataNinstructions[0].lines()
                          .map(string -> {
                                           String[] coord = string.split(",");
                                           return new long[]{Long.parseLong(coord[0]),
                                                             Long.parseLong(coord[1])};
                                         }
                               )
                          .toArray(long[][]::new);

    setupInstructions(dataNinstructions[1]);
  }

  // -----
  private Result solveAlternative(String input) {
    setupAlternative(input);

    BiFunction<Function<Long, Long>, Long, Function<Long, Long>>
      curry = (function, foldValue) ->
                (coord) -> foldValue - Math.abs(foldValue - function.apply(coord));

    char first = '\07'; // the bell character to signal if the first has been stored
    Function<Long, Long> foldsFirst = null; // the very first fold
    Function<Long, Long> Xfolds = null;
    Function<Long, Long> Yfolds = null;

    for (String[] instruction : this.instructions) {
      // get the instruction:
      String direction = instruction[0];
      long foldline = Long.parseLong(instruction[1]);

      // To count the "dots"/"hashes" after the first step
      // split up the curry function into the first and the rest.
      if ("x".equals(direction)) {
        if (first == '\07') {
          first = 'x';
          foldsFirst = curry.apply(Function.identity(), foldline);
        } else {
          Xfolds = (Xfolds == null)
                     ? curry.apply(Function.identity(), foldline)
                     : curry.apply(Xfolds, foldline);
        }
      } else {
        if (first == '\07') {
          first = 'y';
          foldsFirst = curry.apply(Function.identity(), foldline);
        } else {
          Yfolds = (Yfolds == null)
                     ? curry.apply(Function.identity(), foldline)
                     : curry.apply(Yfolds, foldline);
        }
      }
    }

    // Fold just the once
    for (long[] arr : hash2DCoordinates) {
      if (first == 'x') {
        arr[0] = foldsFirst.apply(arr[0]);
      } else {
        arr[1] = foldsFirst.apply(arr[1]);
      }
    }

    // To count "dots"/"hashes" of the first fold, let the Java SE library work its magic
    TreeSet<long[]> unique = new TreeSet<>(folderComparators("none", 0));
    unique.addAll(Arrays.asList(hash2DCoordinates));
    long result = unique.size();

    // Run the rest of the folds.
    // This is really part of part 2, but makes sense to just do this now.
    for (long[] arr : hash2DCoordinates) {
      if (Xfolds != null) {
        arr[0] = Xfolds.apply(arr[0]);
      }
      if (Yfolds != null) {
        arr[1] = Yfolds.apply(arr[1]);
      }
    }

    return Result.createResult(result);
  }

  // ----- This just enables printing the thing to the console.
  private Result solvePart2Alternative(String input) {
    if (this.hash2DCoordinates == null || this.hash2DCoordinates.length == 0) {
      setupAlternative(input);
    }

    int maxX = 0, maxY = 0;
    for (long[] arr : hash2DCoordinates) {
      maxX = (int) (maxX < arr[0] ? arr[0] : maxX);
      maxY = (int) (maxY < arr[1] ? arr[1] : maxY);
    }

    StringBuilder[] ascIIimage = new StringBuilder[maxY + 1];

    String blanks = " ".repeat(maxX + 1);
    IntStream.rangeClosed(0, maxY)
              .forEach(i -> ascIIimage[i] = new StringBuilder(blanks));

    for (long[] arr : hash2DCoordinates) {
      ascIIimage[(int) arr[1]].setCharAt((int) arr[0], '#');
    }

    return Result.createAscIIResult(
             IntStream.rangeClosed(0, maxY)
                      .mapToObj(i -> ascIIimage[i].toString())
                      .toArray(String[]::new));
  }
}
