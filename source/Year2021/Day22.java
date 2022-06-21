package Year2021;

/*
  Two options to solve this:
    1. Keep only cuboid that are "on".
       Making sure that any overlapping cuboids are cut at the
       overlap and discarding one of the fully overlapping parts.

    2. Subtracting overlaps.
       If two cuboid have an overlap, then subtract the overlap.

  Checking for overlaps between two cuboids is fairly easy:
    If one of their coordinates do not overlap, then they do not.
    Here the cuboids fully overlap in the y-direction,
    but there's no overlap in the x-direction:
             _______         _______
            |       |       |       |
            |   A   |       |   B   |
            |_______|       |_______|

  ## Option 1: Keep only "on"-cubes
    First find out how they overlap by counting the
    corners of one cuboid inside the other.

    8-corner overlaps will be either:
     - Both have 8 corners inside the other. Then they are identical.
     - 8 corners of one is inside the other,
       but 0 corners of the other are inside the one.
       That means the other cuboid is smaller.
       Depending on their on/off state:
         - The smaller can be discarded
         - The bigger has have to be cut into 7 parts, keeping 6.

    4-corner overlaps:

         as seen from x/y:      as seen from z/y:
              _______            _________
             |       |___       |    ___  |
             |       |   |      |   |   | |
             |   B   | A |      | B | A | |
             |       |___|      |   |___| |
             |_______|          |_________|


    2-corner overlaps:

         as seen from x/y:      as seen from z/y:
              _______            _________
             |     __|___      _|______   |
             |    |      |    |        |  |
             |  B |    A |    |      A |B |
             |    |__ ___|    |_ ______|  |
             |_______|          |_________|


    1-corner overlaps:

         as seen from x/y:      as seen from z/y:
              _______            _________
             |     __|___      _|______   |
             |  B |      |    |        |B |
             |____|    A |    |      A |__|
                  |______|    |________|


    0-corner overlaps:

         as seen from x/y:      as seen from z/y:
              _______            _________
          ___|       |___       |    ___  |
         |   |       |   |      |   |   | |
         | A |   B   |   |      | B | A | |
         |___|       |___|      |   |___| |
             |_______|          |_________|


    For each of the overlap types,
    there will be one identifyable coordinate
    where the cut can be made.
    The coordinate can be identified by the unique amount of coordinate
    values, where the inner cuboid has a corner inside the other cuboid.

    For example for 4-corner overlaps there will be
    only one unique x value of A's corners inside B.
    The other x value is outside B.
    For the other coordinates (y and z) there will not be just one,
    but 2 different values for both for the corners of A inside B
    (assuming the cuboid is wider than 1 in all dimensions):

         _______
        |       |___
        |       |   |
        |   B   | A |
        |       |___|
        |_______|
           ^        ^
           x1      x2

    The cut of the A cuboid can them be made on the x dimension
    at the edge where B has a corner that's between A's corners:

        |       |___|
        |_______|
           ^    ^   ^
           x1  cut  x2

    The other corners of B does not lie within A,
    so it's safe to just check the interval
    limits for B against the interval for A.

    However, if A is an "off"-cuboid, then B must be cut
    at the edges of A, keeping 5 parts, discarding only the
    middle that's completely covered inside A.

    Cutting B starts the same as cutting A:
    Find the coordinate inside B where A
    has one unique value at the corners.
    Then cut where the end of the cuboid interval for A
    is inside the interval of B.
    Next, cut in a coordinate/dimension the corners of A have
    2 separate values for corners inside B.
    In the example above both the y and z coordindate will have that.

    To keep things simple, once a cut has made a cuboid
    into two parts, each part can be checked.
    One part will be kept.
    The other will need to either be discarded or cut again,
    and this is handled by recursion with the part and the uncut cuboid.

    ### Priority:
      Some types of overlaps will have 2 corners from cuboid A
      inside cuboid B, while only one corner from B is inside A

      For example:

         as seen from x/y:          as seen from z/y:
              _______                _________
             |       |              |         |
             |  B    |              |    B    |
             |_______|______        |______ __|
                     |      |       |      |
                     |    A |       |    A |
                     |______|       |______|

      Priority is given to
      - first check to see if a cuboid can be discarded. 8-corners overlap.
      - 0-corners overlap.
      - 4-corners overlap.
      - 2-corners overlap.
      - finally 1-corner overlap.

    ### Messy implemention
      In this above example neither corner is fully inside the other.
      They're not strictly inside the interval of the dimensions
      of the other cuboid.
      To account for this, several if-statement for edge cases are
      required and increases the code-"messiness".

      Cuboids of size one in any dimension are also
      an edge case, since they only have one unique
      value on their range in that dimension.

      The amount of many "normal" cases also adds to
      the implemention being somewhat messy and unweildy.


  ## Option 2: Subtracting overlaps
    The Inclusion–exclusion principle says that the total
    number of elements (the union) of any sets, S,
    can be found by alternating between adding and subtracting
    intersections of "increasingly sized" subsets of S.
    "increasingly sized" subsets meaning first
    finding the intersections of two sets. Then three sets. Then four..

    See https://en.wikipedia.org/wiki/Inclusion–exclusion_principle

    For example, at 5 cuboids the total number of elements can be found by:

      Add       A, B, C, D, E
      Subtract  All pair-wise intersections:
                A ∩ B, A ∩ C, ..., D ∩ E
      Add       All triple-wise intersections:
                A ∩ B ∩ C, A ∩ B ∩ D, ..., C ∩ D ∩ E
      Subtract  All quadruple-wise intersections:
                A ∩ B ∩ C ∩ D, A ∩ B ∩ C ∩ E, A ∩ C ∩ D ∩ E, B ∩ C ∩ D ∩ E
      Add       All quintuple-wise intersections:
                A ∩ B ∩ C ∩ D ∩ E

    ### Going through examples for two to four cuboids:

      With 2 cuboids, the only thing necessary would be to:

        Add       A, B
        Subtract  The pair-wise intersection:
                  A ∩ B

             add            | subtract
            --------------- + ---------------
              A             |
              B             |
                            |  A ∩ B

      With 3 cuboids, the total number of cubes would be:

        Add       A, B and C
        Subtract  The pair-wise intersection:
                  A ∩ B and A ∩ C, B ∩ C
        Add       A ∩ B ∩ C

             add            | subtract
            --------------- + ---------------
              A             |
              B             |
                            |  A ∩ B
            --------------- + ---------------   <-- at 2 cuboids
              C             |
                            |  A ∩ C
                            |  B ∩ C
              A ∩ B ∩ C     |


      With 4 cuboids, the total number of cubes would be:

        Add       A, B, C and D
        Subtract  The pair-wise intersection:
                  A ∩ B, A ∩ C, B ∩ C and A ∩ D, B ∩ D, C ∩ D
        Add       A ∩ B ∩ C and A ∩ B ∩ D, A ∩ C ∩ D, B ∩ C ∩ D
        Subtract  A ∩ B ∩ C ∩ D

             add            | subtract
            --------------- + ---------------
              A             |
              B             |
                            |  A ∩ B
            --------------- + ---------------  <-- at 2 cuboids
              C             |
                            |  A ∩ C
                            |  B ∩ C
              A ∩ B ∩ C     |
            --------------- + ---------------  <-- at 3 cuboids
              D             |
                            |  A ∩ D
                            |  B ∩ D
                            |  C ∩ D
              A ∩ B ∩ D     |
              A ∩ C ∩ D     |
              B ∩ C ∩ D     |
                            |  A ∩ B ∩ C ∩ D


    ### Notice the pattern
     - Adding a cuboid doesn't change the first part upto the marked lines.
     - The addition of a cuboid will result in:
        - subtracing the intersections of the new cuboid
          with everything that was already in the "add"
        - adding the intersections of the new cuboid
          with everything that was already in the "subtract"

      With this in mind the implementation can make use of just two lists
        - Positive cuboids, where the volume is added to the result.
        - Negative cuboids, where the volume is subtracted from the result.

    ### "off"-cuboids.
      The only complication now are "off"-cuboids.
      While their intersections with other cuboids
      will still need to be subtracted, they should
      themselves obviously not be added to the positive list.
*/

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.Arrays;

import absbase.DayX;
import util.Result;

public class Day22 extends DayX {

  private List<Cuboid> cuboids;
  private List<Cuboid> onCuboids; // the net cuboids that have on-cuboids only.

  // alternative solution using the "inclusion–exclusion principle"
  private List<Cuboid> inclusion; // cuboids that are to be added to the volume.
  private List<Cuboid> exclusion; // cuboids that are to be subtracted from the volume.

  // -----
  public static void main(String[] args){
    new Day22(args).doIt();
  }

  public Day22(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "On-Cuboids",       (i) -> solveOnlyOncuboids(i),
             "Include-exclude",  (i) -> solveInclusionExclusion(i)
            );
  }

  // -----
  private void setup(String input) {
    this.cuboids = new ArrayList<>();

    input.lines()
         .forEach(str -> {
                           Cuboid cuboid = makeCuboid(str);
                           if (cuboid == null) {
                             throw new AssertionError("Cuboid is null: " + str);
                           } else {
                             this.cuboids.add(cuboid);
                           }
          });
  }

  // ----
  private Cuboid makeCuboid(String str) {
    Pattern pattern
      = Pattern.compile("(on|off)\\sx=(-?\\d+)\\.\\.(-?\\d+)"+
                                  ",y=(-?\\d+)\\.\\.(-?\\d+)"+
                                  ",z=(-?\\d+)\\.\\.(-?\\d+)");
    Matcher matcher = pattern.matcher(str);

    if (matcher.find() && matcher.groupCount() == 7) {
      String onf = matcher.group(1);
      int[] x = new int[]{Integer.parseInt(matcher.group(2)),
                          Integer.parseInt(matcher.group(3))};
      int[] y = new int[]{Integer.parseInt(matcher.group(4)),
                          Integer.parseInt(matcher.group(5))};
      int[] z = new int[]{Integer.parseInt(matcher.group(6)),
                          Integer.parseInt(matcher.group(7))};
      return new Cuboid(onf, x, y, z);
    }
    return null;
  }

  // -----
  public Result solve(String input) {
    if (this.cuboids == null || this.cuboids.size() == 0) {
      setup(input);
    }

    // return solveOnlyOncuboids(input);
    return solveInclusionExclusion(input);
  }

  // -----
  Result solveInclusionExclusion(String input) {
    if (this.cuboids == null || this.cuboids.size() == 0) {
      setup(input);
    }

    this.inclusion = new LinkedList<>();
    this.exclusion = new LinkedList<>();

    for (Cuboid newCuboid : this.cuboids) {
      List<Cuboid> inclusionTemp = new LinkedList<>();
      List<Cuboid> exclusionTemp = new LinkedList<>();

      // make intersections of the new cuboid with everything
      // already in the inclusion. Add those to be excluded.
      for (Cuboid cuboid : inclusion) {
        if (existsOverlap(newCuboid, cuboid)) {
          exclusionTemp.add(intersection(newCuboid, cuboid));
        }
      }

      // same, but opposite.
      for (Cuboid cuboid : exclusion) {
        if (existsOverlap(newCuboid, cuboid)) {
          inclusionTemp.add(intersection(newCuboid, cuboid));
        }
      }

      this.inclusion.addAll(inclusionTemp);
      this.exclusion.addAll(exclusionTemp);

      // Do NOT add off-cuboids.
      if ("on".equals(newCuboid.onf)) {
        this.inclusion.add(newCuboid);
      }
    }

    long add = this.inclusion
                   .stream()
                   .mapToLong(cuboid -> cuboid.countInitCubes())
                   .sum();
    long subtract = this.exclusion
                        .stream()
                        .mapToLong(cuboid -> cuboid.countInitCubes())
                        .sum();

    return Result.createResult(add - subtract);
  }

  private Cuboid intersection(Cuboid cuboidOne, Cuboid cuboidTwo){
    return new Cuboid("intersect",
                      new int[]{Math.max(cuboidOne.x[0], cuboidTwo.x[0]),
                                Math.min(cuboidOne.x[1], cuboidTwo.x[1])},
                      new int[]{Math.max(cuboidOne.y[0], cuboidTwo.y[0]),
                                Math.min(cuboidOne.y[1], cuboidTwo.y[1])},
                      new int[]{Math.max(cuboidOne.z[0], cuboidTwo.z[0]),
                                Math.min(cuboidOne.z[1], cuboidTwo.z[1])});
  }

  // -----
  Result solveOnlyOncuboids(String input) {
    if (this.cuboids == null || this.cuboids.size() == 0) {
      setup(input);
    }

    // yet net on-cuboids. The ones here are all on and all valid.
    this.onCuboids = new LinkedList<>();

    CuboidTracker tracker = new CuboidTracker(); // keep track of cut cuboids

    // when a new cuboid is cut, the parts need to be
    // compared to the list of yet net on-cuboids.
    // this is used to queue both the original list and their parts.
    LinkedList<Cuboid> cuboidQueue = new LinkedList<>(this.cuboids);

    Cuboid newCuboid = null;
    while ((newCuboid = cuboidQueue.poll()) != null) {
      tracker.clearAll();  // start fresh

      ListIterator<Cuboid> onCuboidIterator = this.onCuboids.listIterator();
      while (onCuboidIterator.hasNext()) {

        Cuboid onCuboid = onCuboidIterator.next();

        if (existsOverlap(newCuboid, onCuboid)) {
          reduceEliminate(newCuboid, onCuboid, tracker);

          // the onCuboid was cut, so remove it. Keep its parts and add them
          // when the loop is over to avoid ConcurrentModificationException
          if (tracker.cutCuboids.contains(onCuboid)) {
            onCuboidIterator.remove();
          }
        }
      }

      // when an onCuboid is cut, it's parts can be
      // safely added to onCuboids once the iteration is done.
      // no reason to add them earlier,
      // since that cuboid has already been handled for other overlaps
      this.onCuboids.addAll(tracker.safeAdds);

      if (tracker.cutCuboids.contains(newCuboid)) {
        // the parts needs to be handled before the next
        // cuboid, else an off-cuboid may not catch them.
        cuboidQueue.addAll(0, tracker.newAdds);
      } else if ("on".equals(newCuboid.onf)) {
        this.onCuboids.add(newCuboid);
      }
    }

    return Result.createResult(this.onCuboids
                                   .stream()
                                   .mapToLong(cuboid -> cuboid.countInitCubes())
                                   .sum());
  }


  // ----
  private boolean existsOverlap(Cuboid cuboid1, Cuboid cuboid2) {
    boolean overlap = true;
        //    max of cuboid1 < min of cuboid2: |--cuboid1--|  |--cuboid2--|
        // or min of cuboid1 > max of cuboid2: |--cuboid2--|  |--cuboid1--|
    if (cuboid1.x[1] < cuboid2.x[0] || cuboid1.x[0] > cuboid2.x[1]
        || cuboid1.y[1] < cuboid2.y[0] || cuboid1.y[0] > cuboid2.y[1]
        || cuboid1.z[1] < cuboid2.z[0] || cuboid1.z[0] > cuboid2.z[1]) {
      overlap = false;
    }

    return overlap;
  }

  // ----
  private int[][] findCorners(Cuboid cornersFrom, Cuboid in){
    int[] Xs = getPointsinInterval(cornersFrom.x, in.x);
    int[] Ys = getPointsinInterval(cornersFrom.y, in.y);
    int[] Zs = getPointsinInterval(cornersFrom.z, in.z);

    return new int[][]{Xs, Ys, Zs};
  }

  private int[] getPointsinInterval(int[] points, int[] interval) {
    return IntStream.of(points)
                    .filter(w -> inInterval(w, interval))
                    .toArray();
  }

  // ----
  private boolean inInterval(int point, int[] interval) {
    return interval[0] <= point && point <= interval[1];
  }

  private boolean inIntervalStrict(int point, int[] interval) {
    return interval[0] < point && point < interval[1];
  }

  private boolean inIntervalSoft(int point, int[] interval, int[] other) {
    if ((interval[1] == other[0] || other[1] == interval[0])
        && other[0] != other[1]) {
      return inInterval(point, interval);
    }
    return inIntervalStrict(point, interval);
  }

  // ----
  private int countCorners(int[][] corners){ // [[Xs][Ys][Zs]], like: [[10, 12], [12], [12]]
    return Stream.of(corners)
                 .mapToInt(array -> array.length)
                 .reduce(1, (a,b) -> a * b);
  }

  // ----
  private void reduceEliminate(Cuboid newCuboid, Cuboid onCuboid, CuboidTracker tracker) {

    boolean isOn = "on".equals(newCuboid.onf);

    int[][] insideOn = findCorners(newCuboid, onCuboid);   // from cuboid, inside cuboid
    int countInsideOn = countCorners(insideOn);

    int[][] insideNew = findCorners(onCuboid, newCuboid);
    int countInsideNew = countCorners(insideNew);

    List<Cuboid> parts = null;  // the result of the cut in two parts.

    // the new cuboid is completely enclosed in the original cuboid
    if (countInsideOn == 8) {
      if (isOn) {
        // Keep the old cuboid. Discard the new.
        tracker.addCut(newCuboid); // hack to signal not to add it
        return;
      } else if (countInsideNew != 8) { // they're still not identical

                           // toCut, other, cutOverlap cutDimensionLength
        parts = prepareAndCut(onCuboid, newCuboid, insideOn, 2);
        handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
        return;
      }
    }

    // the original cuboid is completely enclosed in the new cuboid
    if (countInsideNew == 8) {
      tracker.addCut(onCuboid); // hack to signal to remove it
      return;
    }

    // --- there are partial overlaps ----

    // -- the cuboids are overlapping but the corners are outside eachother
    if (countInsideOn == 0 && countInsideNew == 0 && isOn) {

      // pick the one with the fewest cuts
      Cuboid toCut = null;
      Cuboid other = null;
      int[][] cutOverlap;

      int insideOnSum  = Stream.of(insideOn).mapToInt(array -> array.length).sum();
      int insideNewSum = Stream.of(insideNew).mapToInt(array -> array.length).sum();

      // if the sum of insideOn is smaller,
      // then the onCuboid only sticks out on two sides (one dimension)
      // while the other new cuboid sticks out on four sides.
      toCut      = insideOnSum < insideNewSum ? onCuboid : newCuboid;
      other      = insideOnSum < insideNewSum ? newCuboid : onCuboid;
      cutOverlap = insideOnSum < insideNewSum ? insideOn : insideNew;

      parts = prepareAndCut(toCut, other, cutOverlap, 2);
      handleCutResult(parts, toCut, onCuboid, newCuboid, tracker);
      return;
    }

    if (countInsideOn == 0 && countInsideNew == 0 && !isOn) {

      // find a dimension where both the corner coordinates is inside the
      // interval of the other cuboid. It will be either 0 or 2 coordinates.
      // Pick any with 2 coordinates. Cut on the edge of either corner.

      parts = prepareAndCut(onCuboid, newCuboid, insideOn, 2);
      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    // -- one of the cuboids is inside the other, but sticks out on one side
    if (countInsideOn == 4 && isOn) {
      // cut the new cuboid where it's sticking out.
      // find the dimension where only one coordinate is inside the other cuboid
      // cut on that coordinate, but where the other cuboid ends.

      parts = prepareAndCut(newCuboid, onCuboid, insideOn, 1);
      handleCutResult(parts, newCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    if (countInsideNew == 4) {
      // cut the on-cuboid where is sticking out.

      parts = prepareAndCut(onCuboid, newCuboid, insideNew, 1);
      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    if (countInsideOn == 4 && !isOn) {
      // cut the original cuboid into smaller (upto 5) pieces

      parts = prepareAndCut(onCuboid, newCuboid, insideOn, 1);
      if (parts == null) {
        // cut off the "end"
        parts = prepareAndCut(onCuboid, newCuboid, insideOn, 2);
      }

      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    // -- one of the cuboids is inside the other, but sticks out on two sides
    if (countInsideOn == 2 && isOn) {
      parts = prepareAndCut(newCuboid, onCuboid, insideOn, 1);
      handleCutResult(parts, newCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    if (countInsideNew == 2 && isOn) {
      parts = prepareAndCut(onCuboid, newCuboid, insideNew, 1);
      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    // cut around the off-cuboid
    if (countInsideOn == 2 && !isOn) {
      // cut the onCuboid into pieces.

      parts = prepareAndCut(onCuboid, newCuboid, insideOn, 1);
      if (parts == null) {
        // when one of the two corners aligns with the outer cuboid
        parts = prepareAndCut(onCuboid, newCuboid, insideOn, 2);
      }

      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    if (countInsideNew == 2 && !isOn) {
        parts = prepareAndCut(onCuboid, newCuboid, insideNew, 1);
        handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
        return;
    }

    // -- one of the cuboids is inside the other, but sticks out on three sides
    if ((countInsideOn == 1 || countInsideNew == 1) && isOn) {
      parts = prepareAndCut(newCuboid, onCuboid, insideOn, 1);
      handleCutResult(parts, newCuboid, onCuboid, newCuboid, tracker);
      return;
    }

    // -- one of the cuboids is inside the other, but sticks out on three sides
    if ((countInsideOn == 1 || countInsideNew == 1) && !isOn) {
      parts = prepareAndCut(onCuboid, newCuboid, insideNew, 1);
      handleCutResult(parts, onCuboid, onCuboid, newCuboid, tracker);
      return;
    }
  }

  // ----
  private void handleCutResult(List<Cuboid> parts,
                               Cuboid toCut,
                               Cuboid onCuboid,
                               Cuboid newCuboid,
                               CuboidTracker tracker) {

    for (Cuboid part : parts) {
      // don't compare parts to the origial. Using == due to reference comparison.
      if (existsOverlap(part, toCut == newCuboid ? onCuboid : newCuboid)) {

        if (toCut == newCuboid) { // keep the onCuboid as the right parameter.
          reduceEliminate(part, onCuboid, tracker);
        } else {
          reduceEliminate(newCuboid, part, tracker);
        }

      } else {
        if (toCut == newCuboid) {
          tracker.addNew(part);
          tracker.addCut(newCuboid);
        } else {
          tracker.addSafe(part);
          tracker.addCut(onCuboid);
        }
      }
    }
  }

  // ----
  private List<Cuboid> prepareAndCut(Cuboid toCut,
                                     Cuboid other,
                                     int[][] cutOverlap,
                                     int cutDimLength) {

    List<Cuboid> parts = null;

    for (int i = 0; i < cutOverlap.length; i++) {

      if (cutOverlap[i].length == cutDimLength) {

        // depending on i, it's either x, y or z.
        char dimension = i == 0 ? 'x' : i == 1 ? 'y' : 'z';

        // this is the interval that determines the cut.
        // since the cut needs to be at the edge of the other cuboid.
        int[] otherInterval = i == 0
                                ? other.x
                                : i == 1 ? other.y : other.z;

        // this is the interval that determines
        // which end of the other interval to cut on
        int[] intervalLimit = i == 0
                                ? toCut.x
                                : i == 1 ? toCut.y : toCut.z;

        if (inIntervalSoft(otherInterval[0], intervalLimit, otherInterval)) {
          parts = cut(toCut,
                      dimension,
                      new int[]{otherInterval[0] - 1, otherInterval[0]});
          break;
        } else if (inIntervalSoft(otherInterval[1], intervalLimit, otherInterval)) {
          parts = cut(toCut,
                      dimension,
                      new int[]{otherInterval[1], otherInterval[1] + 1});
          break;
        }

        // corner case where the other interval is only one unique
        // value, which it at the end of the interval to cut.
        if (parts == null
            && otherInterval[0] == otherInterval[1]) {
          if (intervalLimit[0] == otherInterval[0]){
            parts = cut(toCut,
                        dimension,
                        new int[]{otherInterval[0], otherInterval[0] + 1});
            break;
          } else if (intervalLimit[1] == otherInterval[0]) {
            parts = cut(toCut,
                        dimension,
                        new int[]{otherInterval[1] - 1, otherInterval[1]});
            break;
          }
        }
      }
    }

    return parts;
  }

  // ----
  private List<Cuboid> cut(Cuboid cutCuboid, char dimension, int[] coordinate) {
    if (coordinate.length != 2) {              // for example: "y", [45,46]
      throw new AssertionError("Cannot split on " + Arrays.toString(coordinate));
    }
    boolean xSplit = dimension == 'x';
    boolean ySplit = dimension == 'y';
    boolean zSplit = dimension == 'z';

    Cuboid part1 = new Cuboid(cutCuboid.onf,
                              (xSplit
                                 ? new int[]{cutCuboid.x[0],
                                             // coordinate[0] may be cutCuboid.x[0]-1
                                             Math.max(coordinate[0], cutCuboid.x[0])}
                                 : cutCuboid.x),
                              (ySplit
                                 ? new int[]{cutCuboid.y[0],
                                             Math.max(coordinate[0], cutCuboid.y[0])}
                                 : cutCuboid.y),
                              (zSplit
                                 ? new int[]{cutCuboid.z[0],
                                             Math.max(coordinate[0], cutCuboid.z[0])}
                                 : cutCuboid.z));

    Cuboid part2 = new Cuboid(cutCuboid.onf,
                              (xSplit        // coordinate[1] may be cutCuboid.x[1]+1
                                 ? new int[]{Math.min(coordinate[1], cutCuboid.x[1]),
                                             cutCuboid.x[1]}
                                 : cutCuboid.x),
                              (ySplit
                                 ? new int[]{Math.min(coordinate[1], cutCuboid.y[1]),
                                             cutCuboid.y[1]}
                                 : cutCuboid.y),
                              (zSplit
                                 ? new int[]{Math.min(coordinate[1], cutCuboid.z[1]),
                                             cutCuboid.z[1]}
                                 : cutCuboid.z));

    return List.of(part1, part2);
  }

  // ----
  static int CuboidIdCounter = 0;
  private class Cuboid {
    // id is needed since an old cuboid should not be
    // removed if a new cuboid has the exact same properties.
    int id;
    String onf;
    int[] x;
    int[] y;
    int[] z;

    Cuboid(String onf, int[] x, int[] y, int[] z) {
      id = ++CuboidIdCounter;
      this.onf = onf;
      this.x = x;
      this.y = y;
      this.z = z;
    }

    long countCubes() {
      return ((long) Math.abs(x[1] - x[0]) + 1)
             * (Math.abs(y[1] - y[0]) + 1)
             * (Math.abs(z[1] - z[0]) + 1);
    }

    long countInitCubes() {
      return (long) limit(x) * limit(y) * limit(z);
    }

    long limit(int[] w){
      if (w[0] > 50 || w[1] < -50) {
        return 0;
      }
      return Math.abs((Math.min(w[1],50) - Math.max(w[0],-50))) + 1;
    }

    @Override
    public boolean equals(Object object) {
      if (object == this)
        return true;
      if (!(object instanceof Cuboid))
        return false;

      Cuboid other = (Cuboid) object;
      return this.id == other.id;
    }

    @Override
    public final int hashCode() {
      return id;  // since it's already unique
    }

    @Override
    public String toString() {
      return onf +
             " x=[" + x[0] + "," + x[1] + "]" +
             " y=[" + y[0] + "," + y[1] + "]" +
             " z=[" + z[0] + "," + z[1] + "]";
    }
  }

  // ----
  private class CuboidTracker {
    Set<Cuboid> cutCuboids = new HashSet<>();
    List<Cuboid> safeAdds = new ArrayList<>();
    List<Cuboid> newAdds = new ArrayList<>();

    void addSafe(Cuboid cuboid){
      safeAdds.add(cuboid);
    }
    void addNew(Cuboid cuboid){
      newAdds.add(cuboid);
    }
    void addCut(Cuboid cuboid){
      cutCuboids.add(cuboid);
    }
    void clearAll(){
      cutCuboids.clear();
      safeAdds.clear();
      newAdds.clear();
    }
  }


  // -----
  public Result solvePart2(String input) {
    // return solvePart2OnlyOncuboids(input);
    return solvePart2InclusionExclusion(input);
  }

  Result solvePart2OnlyOncuboids(String input) {
    if (this.onCuboids == null || this.onCuboids.size() == 0) {
      solveOnlyOncuboids(input);
    }

    return Result.createResult(this.onCuboids
                                   .stream()
                                   .mapToLong(cuboid -> cuboid.countCubes())
                                   .sum());
  }

  Result solvePart2InclusionExclusion(String input) {
    if (this.inclusion == null || this.inclusion.size() == 0) {
      solveInclusionExclusion(input);
    }

    long add = this.inclusion
                   .stream()
                   .mapToLong(cuboid -> cuboid.countCubes())
                   .sum();
    long subtract = this.exclusion
                        .stream()
                        .mapToLong(cuboid -> cuboid.countCubes())
                        .sum();

    return Result.createResult(add - subtract);
  }
}
