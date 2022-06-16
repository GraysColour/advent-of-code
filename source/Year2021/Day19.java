package Year2021;

/*
  Given the two scanners in the example:
    --- scanner 0 ---      --- scanner 1 ---
    0,2             A      -1,-1           D
    4,1             B      -5,0            E
    3,3             C      -2,1            F

  These are the flight path distances (^2)
  between probes isolated to the individual scanners:
    scanner 0              scanner 1
    A:                     D:
      B: 4^2+1^1 = 17        E: 4^2+1^2 = 17
      C: 3^2+1^2 = 10        F: 1^2+2^2 = 5
    B:                     E:
      C: 1^2+2^2 = 5         F: 3^2+1^2 = 10
      A:           17        D:           17
    C:                     F:
      A:           10        D:           5
      B:           5         E:           10

  The distances are calculated by squaring each coordinate.
  This avoids 1,3 being identical to 2,2, since 1^1 + 3^2 = 10 != 2^2 + 2^2 = 8

  ## Method 1:
    Since the distance between probeA and probeB of scanner1,
                  and between probeD and probeE of scanner2
    are identical, and assuming they are overlapping then
      either probeA=probeD and probeB=probeE
          or probeA=probeE and probeB=probeD

  ## Method 2:
    However, it's obvious that probeA cannot be probeD,
    since probeD's distance to the last probe isn't 10, as it is for probeA.
    By just comparing the probes set of distances to the other probes,
    probeA must be the same probe as probeE.

    But for the sake of example, both options
      - probeA=probeD and probeB=probeE
      - probeA=probeE and probeB=probeD
    will be tested in the examples.

  ### Finding relative coordinates.
    If a coordinate/vektor from a scanner to a point p (x,y) is (ScanX, ScanY)
    but it's not known which direction the scanner is facing,
    there are 4 different locations where the scanner can be.

    There are 8 different permutations of (+-ScanX, +-ScanY),
    but only 4 of those are rotations.
    The others require that the space is flipped.

        (a) x - ScanX, y - ScanY
        (b) x - ScanX, y + ScanY  <-- flipped !
        (c) x + ScanX, y - ScanY  <-- flipped !
        (d) x + ScanX, y + ScanY
        (e) x - ScanY, y - ScanX  <-- flipped !
        (f) x - ScanY, y + ScanX
        (g) x + ScanY, y - ScanX
        (h) x + ScanY, y + ScanX  <-- flipped !


                                             ! (c)   (d)
                                                S — — S
                                                   |
               — S <-- scanner         (f) S       |       S ! (h)
              |                            |       |       |
              |                              — — — p — — —
              ǀ                            |       |       |
              p <-- probe            ! (e) S       |       S (g)
                                                   |
                                                S — — S
                                               (a)   ! (b)

    With two points it's possible to isolate the scanner's location
    by comparing each of the 4 combinations to see it there's an overlap.

    Assuming the coordinates/vektor from S to q is (ScanXq, ScanYq)

               — q
              |        then S would be here   S — —
              |                                     |
              S                                     q

    using (q_x + ScanYq, q_y - ScanXq), which corresponds to (g).

    If q is as seen here, then S can be isolated to its coordinates:

                             S — — S
                                |
                        S       |       S
                        |       |       |
                          — — — p — — —
                        |       |       |
                        S       |       S — —
                                |            |
                             S — — S         q

    Knowing the placement of S and which of the the 4 calculation used enables any
    point/probe as seen from S to be known with its coordinates recalculated.

    Assuming S will say that
      p is at -1, -3
      q is at  1 , 2

                — q
               |
               |
             — S
            |
            |
            ǀ
            p

    Isolating S to for example 3,5 using (e) x + ScanY, y - ScanX:
      Note that ScanX and ScanY is the vektor from the Scanner to p!
      From p to the Scanner the sign is reversed: x - ScanY, y + ScanX

          (ScanPos_x, ScanPos_y) = (pN_x - pS_y, pN_y + pS_x)

           ScanPos_x = pN_x - pS_y
             => pN_x = ScanPos_x + pS_y
             => pS_y = pN_x - ScanPos_x
           ScanPos_y = pN_y + pS_x
             => pN_y = ScanPos_y - pS_x
             => pS_x = ScanPos_y - pN_y

        where pN is the point in "our" system,
        while pS is the point according to the Scanner

        pN is at (3 + (-3)) , 5 - (-1)) = (0, 6)
        qN is at (3 + 2     , 5 - 1)    = (5, 4)

      Obviously knowing p and q from out perspective,
      from the perspective of S the calulation is reversed:
        pS = (5 - 6 , 0 - 3) = (-1, -3)
        qS = (5 - 4 , 5 - 3) = (1, 2)

  ## Examples:
    ### Testing probeA=probeD and probeB=probeE from scanners mentioned at the top:

      If probeA=probeD then according to Scanner0,
      Scanner1 must be at either of the 4 locations
        0 - -1 , 2 - -1 or 0 + -1 , 2 + -1 or 0 - -1 , 2 + -1 or 0 + -1 , 2 - -1
             1 , 3             -1 , 1              1 , 1             -1 , 3

      and probeB=probeE gives these 4 locations:
        4 - -5 , 1 - 0 or 4 + -5 , 1 + 0 or 4 - 0 , 1 + -5 or 4 + 0 , 1 - -5
             9 , 1            -1 , 1            4 , -4            4 , 6

      There's only 1 overlap, so assume that Scanner1 is at -1,1 according to Scanner0.
      Since the applied addition to find the coordinate was:

        ProbeAx (+) ProbeDx, ProbeAy (+) ProbeDy    Scanner1x, Scanner1y
              0  +       -1,       2  +       -1  =       -1,          1

        ProbeBx (+) ProbeEx, ProbeBy (+) ProbeEy    Scanner1x, Scanner1y
              4  +       -5,       1  +        0  =       -1,          1

      Then finding ProbeA's coordinates from ProbeD's coordinates, must be:

        Scanner1x (-) ProbeDx, Scanner1y (-) ProbeDy = ProbeAx, ProbeAy
              -1   -      -1          1   -       -1         0        2

        Scanner1x (-) ProbeEx, Scanner1y (-) ProbeEy = ProbeBx, ProbeBy
              -1   -      -5          1   -        0         4        1

      Seeing if that fits with the last remaining probe:

        Scanner1x (-) ProbeFx, Scanner1y (-) ProbeFy != ProbeCx, ProbeCy
              -1   -      -2          1   -        1         -3        0

      which isn't the correct coordinate, so probeA != probeD

    ### Testing probeA=probeE and probeB=probeD
      If probeA=probeE then according to Scanner0,

      Scanner1 must be at either of the 4 locations:
        0 - -5 , 2 - 0 or 0 + -5 , 2 + 0 or 0 - 0 , 2 + 5 or 0 + 0 , 2 - 5
             5 , 2            -5 , 2            0 , 7            0 , -3

      and probeB=probeD gives these 4 locations:
        4 - -1 , 1 - -1 or 4 + -1 , 1 + -1 or 4 - -1 , 1 + -1 or 4 + -1 , 1 - -1
             5 , 2              3 , 0              5 , 0              3 , 2

      Again there's only one overlap: 5,2

      There's only 1 overlap, so assume that Scanner1 is at 5,2 according to Scanner0.
      Since the applied addition to find the coordinate was:

        ProbeAx (-) ProbeEx, ProbeAy (-) ProbeEy    Scanner1x, Scanner1y
              0  -       -5,       2  -        0  =        5,          2

        ProbeBx (-) ProbeDx, ProbeBy (-) ProbeDy    Scanner1x, Scanner1y
              4  -       -1,       1  -       -1  =        5,          2

      Then finding ProbeA's coordinates from ProbeD's coordinates, must be:

        Scanner1x (+) ProbeEx, Scanner1y (+) ProbeEy = ProbeAx, ProbeAy
               5   +      -5          2   +        0         0        2

        Scanner1x (+) ProbeDx, Scanner1y (+) ProbeDy = ProbeBx, ProbeBy
               5   +      -1          2   +       -1         4        1

      Seeing if that fits with the last remaining probe:

        Scanner1x (+) ProbeFx, Scanner1y (+) ProbeFy = ProbeCx, ProbeCy
               5   +      -2          2   +        1         3        3

      which fits.

  ### Using matrixes

    Recalling the 4 valid combinations for rotations:

        (1) x - ScanX, y - ScanY
        (2) x + ScanX, y + ScanY
        (3) x - ScanY, y + ScanX
        (4) x + ScanY, y - ScanX

   Those can be expressed using rotation matrixes:

        (1) (x,y)  + | -1  0 |   | ScanX |
                     |  0 -1 | * | ScanY |

        (2) (x,y)  + |  1  0 |   | ScanX |
                     |  0  1 | * | ScanY |

        (3) (x,y)  + |  0 -1 |   | ScanX |
                     |  1  0 | * | ScanY |

        (3) (x,y)  + |  0  1 |   | ScanX |
                     | -1  0 | * | ScanY |

      See https://en.wikipedia.org/wiki/Rotation_matrix

    They can be found by using 0°, 90°, 180° and 270°,
    or using radians 0, π/2, π, 3π/2 for θ:

        |  cos(θ) -sin(θ) |
        |  sin(θ)  cos(θ) |

    Note that the rotation matrix in (1) is the same
    as -1 * the rotation matrix in (2).
    Similar for (3) and (4).


  ### Doing it in 3D
    This is "almost" the same, only there's one more coordinate,
    so the possible combinations for finding a scanner location is
    24 instead of 4.

    There are 3 base rotation matrixes. Each rotating around an axis:

               |    1        0        0    |
      R_x(θ) = |    0      cos(θ)  -sin(θ) |
               |    0      sin(θ)   cos(θ) |

               |  cos(θ)     0      sin(θ) |
      R_y(θ) = |    0        1        0    |
               | -sin(θ)     0      cos(θ) |

               |  cos(θ)  -sin(θ)     0    |
      R_z(θ) = |  sin(θ)   cos(θ)     0    |
               |    0        0        1    |

    With the 4 angles 0, π/2, π, 3π/2 for θ,
    and the rotations in the opposite direction along an axis
    makes it a total of 24 different possible rotations.
    Intuitively it can be visualized by seeing that a cube has 6 sides,
    and each side can be rotatated 4 times by 90°.

    Looking at the possible permutations taking a point p (x,y,z) and subtracting
    the coordinates of a point as seen from a Scanner (ScanX, ScanY, ScanZ)
    yields all permutations of (+-ScanX, +-ScanY, -+ ScanZ):

        (1) x -+ ScanX, y -+ ScanY, z -+ ScanZ
        (2) x -+ ScanX, y -+ ScanZ, z -+ ScanY
        (3) x -+ ScanY, y -+ ScanX, z -+ ScanZ
        (4) x -+ ScanY, y -+ ScanZ, z -+ ScanX
        (5) x -+ ScanZ, y -+ ScanX, z -+ ScanY
        (6) x -+ ScanZ, y -+ ScanY, z -+ ScanX

    Which gives 6  *  8 possible pertumations = 48 in total.
    But as with 2D, half of those are flipped rotations.
    Only 24 are valid.

      These are the 4 rotations around the X-axis:
        (a) x + ScanX, y + ScanY, z + ScanZ  for 0°
        (b) x + ScanX, y + ScanZ, z - ScanY  for π/2  / 90°
        (c) x + ScanX, y - ScanY, z - ScanZ  for π    / 180°
        (d) x + ScanX, y - ScanZ, z + ScanY  for 3π/2 / 270°

      A coordinate (a, b, c) when looking in the X-direction
        - becomes b, -a, c when looking at the Y-direction.
          which is the same as applying the rotation: R_z(π/2).
          (
            b, -a, c can also be seen as the -Y-direction,
            but it doesn't change anything.
            Applying R_z(3π/2) would give (-b, a, c)
            which is the opposite facing Y-direction.
          )
        - and -c, b, a when looking at the Z-direction.
          which is the same as applying the rotation: R_y(π/2).

      To get to one of the 4 coordinates facing in the -X-axis direction,
      the point/vektor can be rotated either by
      180° around the y-axis, R_y(π), giving -a, b, -c
      or by 180° around the z-axis, R_z(π), giving -a, -b, c.

      Applying (b), (c) and (d) to the point -ScanX, -ScanY, ScanZ,
      gives the other 3 coordinates in the -X-axis direction:

        (e) x - ScanX, y - ScanY, z + ScanZ
        (h) x - ScanX, y - ScanZ, z - ScanY
        (g) x - ScanX, y + ScanY, z - ScanZ
        (f) x - ScanX, y + ScanZ, z + ScanY

      To get all possible locations of the Scanner,
      (a), (b), (c) and (d) can be applied to ScanX, ScanY, ScanZ:

       - the       X-direction:  ScanX,  ScanY, ScanZ
       - the minux X-direction: -ScanX, -ScanY, ScanZ
       - the       Y-direction:  ScanY, -ScanX, ScanZ
       - the minus Y-direction: -ScanY,  ScanX, ScanZ
       - the       Z-direction: -ScanZ,  ScanY, ScanX
       - the minus Z-direction:  ScanZ, -ScanY, ScanX

    #### When invalid rotations are valid

      Note that for example the point 1,2,3 from a scanner to a point
      can never be rotated into -1,-2,-3. But from the point,
      that mirroed coordinate is the exact vektor to the scanner.

      In 2D the mirrored coordinates just happen to lie
      within the valid rotations for the scanner.
*/

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.Arrays;

import absbase.DayX;
import util.Result;

public class Day19 extends DayX {

  // 12 overlapping probes means each probe-pair must overlap with 11 distances
  private int probeDistanceOverlaps = 11;
  private int scannerDistanceOverlaps = 66;    // 11+10+9+8+7+6+5+4+3+2+1 unique distances

  private Map<Integer, Scanner> scanners;      // scanner, probe coordinates
  private List<Overlap> overlaps;              // unique scanner overlaps
  private TreeSet<int[]> allProbeCoordinates;  // from scanner 0's perspective
  private Map<Integer,int[]> scannerLocations; // from scanner 0's perspective

  // -----
  public static void main(String args[]){
    new Day19(args).doIt();
  }

  public Day19(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "JustTheOne",  (i) -> solve(i)
            );
  }

  // -----
  private void setup(String input) {
    String[] scannerInput = input.split("\\R{2}");
    this.scanners = new HashMap<>(scannerInput.length);

    for (String str : scannerInput) {
      String[] scanner = str.split("\\R");

      String header = scanner[0];
      int endPos = header.indexOf(" ---");
      Integer key = Integer.parseInt(header.substring("--- scanner ".length(), endPos));

      int[][] probeCoordinates
        = Stream.of(scanner)
                .skip(1) // the header
                .map(st -> {
                            String[] strCoord = st.split(",");
                            return new int[]{
                                              Integer.parseInt(strCoord[0]),
                                              Integer.parseInt(strCoord[1]),
                                              Integer.parseInt(strCoord[2]),
                                            };
                 })
                .toArray(int[][]::new);

      this.scanners.put(key, new Scanner(key, probeCoordinates));
    }
  }

  // -----
  public Result solve(String input) {
    if (this.scanners == null || this.scanners.size() == 0) {
      setup(input);
    }

    //  scanner    distances    count
    Map<Integer, Map<Integer, Integer>> scannerCompareDistances
       = new HashMap<>(this.scanners.size());

    computeProbeDistances(scannerCompareDistances);
    findOverlappingScanners(scannerCompareDistances);
    matchProbes();
    findTransitions();
    accumulateLocations();

    return Result.createResult(this.allProbeCoordinates.size());
  }


  // ----
  private void computeProbeDistances(
      Map<Integer, Map<Integer, Integer>> scannerCompareDistances) {

    // For each scanner compute the distances between probes
    for (Map.Entry<Integer, Scanner> entry : this.scanners.entrySet()) {
      int scannerNo = entry.getKey();
      Scanner scanner = entry.getValue();
      int[][] probeCoordinates = scanner.getprobeCoordinates();

      // distances  count
      Map<Integer, Integer> distances = new HashMap<>();

      // From probe   distance  To probes
      Map<Integer, Map<Integer, Set<Integer>>> fromProbeDistancesToProbes = new HashMap<>();

      IntStream
        .range(0, probeCoordinates.length)
        .forEach(i -> {
          Map<Integer, Set<Integer>> distanceToProbes_i
            = getDistanceToProbeMap(i, fromProbeDistancesToProbes);

          IntStream
            .range(i + 1, probeCoordinates.length)
            .forEach(j -> {int dist =
                            calculateDistance(probeCoordinates[i],
                                              probeCoordinates[j]);

                           // insert into the distance list for the scanner
                           distances.merge(dist, 1, (a,b) -> a + b);

                           Map<Integer, Set<Integer>> distanceToProbes_j
                             = getDistanceToProbeMap(j, fromProbeDistancesToProbes);

                           insertDistanceProbes(distanceToProbes_i, dist, j);
                           insertDistanceProbes(distanceToProbes_j, dist, i);
            });
         });

      scanner.setProbeDistances(fromProbeDistancesToProbes);
      scannerCompareDistances.put(scannerNo, distances);
    }
  }

  // ----
  private Map<Integer, Set<Integer>> getDistanceToProbeMap(
      int probe,
      Map<Integer,Map<Integer, Set<Integer>>> fromProbeDistancesToProbes) {

    Map<Integer, Set<Integer>> distanceToProbes
      = fromProbeDistancesToProbes.get(probe);

    if (distanceToProbes == null) {
      distanceToProbes = new HashMap<>();
      fromProbeDistancesToProbes.put(probe, distanceToProbes);
    }
    return distanceToProbes;
  }

  // ----
  private int calculateDistance(int[] a, int[] b) {
    int result = 0;
    for (int i = 0; i < a.length ; i++) {
      result += Math.pow(Math.abs(a[i] - b[i]), 2);
    }
    return result;
  }

  // ----
  private void insertDistanceProbes(
      Map<Integer, Set<Integer>> distanceToProbes,
      int distance,
      int probe) {

    Set<Integer> probeSet = new HashSet<>();
    probeSet.add(probe);

    distanceToProbes.merge(distance,
                           probeSet,
                           (a,b) -> {
                              a.addAll(b);
                              return a;
                           });
  }


  // ----
  private void findOverlappingScanners(
      Map<Integer, Map<Integer, Integer>> scannerCompareDistances) {

    this.overlaps = new ArrayList<>();

    // find which scanners have overlapping probes by looking at their relative distances
    for (Map.Entry<Integer, Map<Integer, Integer>> outer : scannerCompareDistances.entrySet()) {
      int ScannerNoOuter = outer.getKey();
      Map<Integer, Integer> distancesOuter = outer.getValue();

      for (Map.Entry<Integer, Map<Integer, Integer>> inner : scannerCompareDistances.entrySet()) {
        int ScannerNoInner = inner.getKey();
        Map<Integer, Integer> distancesInner = inner.getValue();

        if (ScannerNoOuter >= ScannerNoInner) {
          // Don't compare a scanner to itself,
          // and don't repeat the work already done.
          continue;
        }

        Set<Integer> distanceIntersection = new HashSet<>(distancesOuter.keySet());
        distanceIntersection.retainAll(distancesInner.keySet());

        int count = distanceIntersection.size();
        // we need (scannerDistanceOverlaps) 66: 11+10+9+8+7+6+5+4+3+2+1
        // unless one of the distances is there more than once.
        Map<Integer, Integer> doubleEntries = null;
        if (count < this.scannerDistanceOverlaps) {

          doubleEntries = findDoubledistances(distanceIntersection,
                                              distancesOuter,
                                              distancesInner);
          count += IntStream.range(0, doubleDistanceCount(doubleEntries))
                            .map(i -> this.probeDistanceOverlaps - i)
                            .sum();
        }

        if (count >= this.scannerDistanceOverlaps) { // there may be an overlap
          Scanner ScannerOuter = this.scanners.get(ScannerNoOuter);
          Scanner ScannerInner = this.scanners.get(ScannerNoInner);
          Overlap overlap = new Overlap(ScannerOuter,
                                        ScannerInner,
                                        distanceIntersection,
                                        doubleEntries);
          ScannerOuter.addOverlap(overlap);
          ScannerInner.addOverlap(overlap);
          this.overlaps.add(overlap);
        }
      }
    }
  }

  // ----
  private  Map<Integer, Integer> findDoubledistances(Set<Integer> distances,
                                                     Map<Integer, Integer> distanceCounts1,
                                                     Map<Integer, Integer> distanceCounts2) {
    return distances.stream()
                    .filter(dist -> distanceCounts1.get(dist) > 1
                                    && distanceCounts2.get(dist) > 1)
                    .collect(Collectors.toMap(dist -> dist,
                                              dist ->
                                                Math.min(distanceCounts1.get(dist),
                                                         distanceCounts2.get(dist))));
  }

  // ----
  private int doubleDistanceCount(Map<Integer, Integer> distanceCounts) {
    if (distanceCounts == null) {
      return 0;
    }
    return distanceCounts.entrySet()      // don't count the entry
                         .stream()        // just the amount it's doubled
                         .map(distance -> distance.getValue() - 1)
                         .reduce(0, (a,b) -> a+b);
  }


  // ----
  private void matchProbes() {
    // find which probes of overlapping scanners, that may be identical
    for (Map.Entry<Integer, Scanner> entry : this.scanners.entrySet()) {

      int scannerNo = entry.getKey();
      Scanner scanner = entry.getValue();
      List<Overlap> overlaps = scanner.getOverlaps();

      for (Overlap overlap : overlaps) {
        if (overlap.probeMatchesDone()) {
          continue;
        }
        List<int[]> matches = new ArrayList<>(12);
        overlap.setMatches(matches);

        boolean reversed = false;
        if (scannerNo != overlap.getScanner().getId()) {
          reversed = true;
        }

        Set<Integer> firstOverlapDistances = overlap.getDistances();

        findMathingProbes(overlap.getOtherScanner(scannerNo),
                          scanner.getProbeDistances(),
                          firstOverlapDistances,
                          matches,
                          null,
                          overlap.getdoubleEntries(),
                          reversed);
      }
    }
  }

  // ----
  private void findMathingProbes(
      Scanner scannerOther,
      Map<Integer, Map<Integer, Set<Integer>>> probedistances,
      Set<Integer> overlapDistances,
      List<int[]> matches,
      Integer probeNoOther,
      int doubleCounts,
      boolean reversed) {

    //             probe       distance  other probes
    for (Map.Entry<Integer, Map<Integer, Set<Integer>>> probedistance : probedistances.entrySet()) {

      Set<Integer> originalDistances = probedistance.getValue().keySet();
      Set<Integer> distanceIntersections = new HashSet<>(originalDistances);
      distanceIntersections.retainAll(overlapDistances);

      if (distanceIntersections.size() >= (this.probeDistanceOverlaps - doubleCounts)) {
        if (probeNoOther != null) { // this is the recursive second call
          int[] probes = reversed
                          ? new int[]{probedistance.getKey(), probeNoOther}
                          : new int[]{probeNoOther, probedistance.getKey()};
          matches.add(probes);
        } else if (scannerOther != null) {
          findMathingProbes(null,
                            scannerOther.getProbeDistances(),
                            distanceIntersections,
                            matches,
                            probedistance.getKey(),
                            doubleCounts,
                            reversed);
        }
      }
    }
  }


  // ----
  private void findTransitions() {
    // find transitions for each overlap
    for (Overlap overlap : this.overlaps) {

      Scanner scanner = overlap.getScanner();
      Scanner scannerOther = overlap.getOtherScanner(scanner.getId());

      List<int[]> probeMatches = overlap.getProbeMatches();

      int[] firstPair = probeMatches.get(0);
      int[] scannerP = scanner.getprobeCoordinate(firstPair[0]);
      int[] pVector = scannerOther.getprobeCoordinate(firstPair[1]);

      int[] secondPair = probeMatches.get(1);
      int[] scannerQ = scanner.getprobeCoordinate(secondPair[0]);
      int[] qVector = scannerOther.getprobeCoordinate(secondPair[1]);

      Transition fromOther = null;
      Transition fromScanner = null;
      for (Map.Entry<String, BiFunction<int[], int[], int[]>> rotation : Transitions.reversedRotations.entrySet()) {
        for (Map.Entry<String, Function<int[], int[]>> direction : Transitions.directions.entrySet()) {
          for (Map.Entry<String, Function<int[], int[]>> sign : Transitions.signs.entrySet()) {

            BiFunction<int[], int[], int[]> rotationF = rotation.getValue();
            Function<int[], int[]> directionF = direction.getValue();
            Function<int[], int[]> signF = sign.getValue();

            // from otherScanner to Scanner
            if (fromOther == null) {
              int[] ScannerCood = rotationF.apply(scannerP,
                                                  signF.apply(directionF.apply(pVector)));
              int[] ScannerCoodOther = rotationF.apply(scannerQ,
                                                       signF.apply(directionF.apply(qVector)));

              if (Arrays.compare(ScannerCood, ScannerCoodOther) == 0) {
                fromOther = new Transition(ScannerCood,
                                           rotation.getKey(),
                                           sign.getKey(),
                                           direction.getKey());
              }
            }

            // from scanner to otherScanner
            if (fromScanner == null) {
              int[] ScannerCoodRev = rotationF.apply(pVector,
                                                     signF.apply(directionF.apply(scannerP)));
              int[] ScannerCoodOtherRev = rotationF.apply(qVector,
                                                          signF.apply(directionF.apply(scannerQ)));

              if (Arrays.compare(ScannerCoodRev, ScannerCoodOtherRev) == 0) {
                fromScanner = new Transition(ScannerCoodRev,
                                             rotation.getKey(),
                                             sign.getKey(),
                                             direction.getKey());
              }
            }
          }
        }
      }

      overlap.setTransitions(fromOther, fromScanner);
    }
  }


  // ----
  private void accumulateLocations() {
    // start with scanner 0. From the perspective of scanner 0,
    // using overlaps and transitions, accumulate all probes and scanner locations.
    this.scannerLocations = new HashMap<>(this.scanners.size());
    this.scannerLocations.put(0, new int[]{0,0,0});

    this.allProbeCoordinates = new TreeSet<>(Arrays::compare);
    this.allProbeCoordinates.addAll(Arrays.asList(this.scanners.get(0).getprobeCoordinates()));

    Map<Integer, Function<int[], int[]>> curries = new HashMap<>(Map.of(0, i -> i));

    LinkedList<Integer> queue = new LinkedList<>(List.of(0));
    Integer id;

    while ((id = queue.poll()) != null) {
      Scanner currentScanner = this.scanners.get(id);

      for (Overlap overlap : currentScanner.getOverlaps()) {
        Integer toScanner = overlap.getOtherScanner(id).getId();
        if (curries.get(toScanner) == null) { // this scanner wasn't added yet

          Transition transition = overlap.getTransition(id);
          Function<int[], int[]> curry = curries.get(id);
          this.scannerLocations.put(toScanner, curry.apply(transition.scannerLocation));

          curry = transition.curryTransition(curries.get(id));

          for (int[] coordinate : overlap.getOtherScanner(id).getprobeCoordinates()) {
            this.allProbeCoordinates.add(curry.apply(coordinate));
          }
          queue.add(toScanner);
          curries.put(toScanner, curry);
        }
      }
    }
  }


  // ----
  private class Scanner {
    // From Probe   distance     To probe
    Map<Integer, Map<Integer, Set<Integer>>> ProbeDistances;
    int id;
    int[][] probeCoordinates;
    List<Overlap> overlaps = new ArrayList<>();

    Scanner(int id, int[][] probeCoordinates) {
      this.id = id;
      this.probeCoordinates = probeCoordinates;
    }

    void setProbeDistances(Map<Integer, Map<Integer, Set<Integer>>> ProbeDistances) {
      this.ProbeDistances = ProbeDistances;
    }
    void addOverlap(Overlap overlap) {
      this.overlaps.add(overlap);
    }

    int getId() {
      return id;
    }
    int[][] getprobeCoordinates() {
      return probeCoordinates;
    }
    int[] getprobeCoordinate(int index) {
      return probeCoordinates[index];
    }
    Map<Integer, Map<Integer, Set<Integer>>> getProbeDistances() {
      return this.ProbeDistances;
    }
    List<Overlap> getOverlaps() {
      return this.overlaps;
    }
  }

  // ----
  private class Overlap {
    Scanner scanner;
    Scanner scannerOther;
    Set<Integer> overlapDistances;
    // if the same distance appear more than once.
    Map<Integer, Integer> doubleEntries;

    List<int[]> probeMatches;
    Transition fromOther;     // from scannerOther to Scanner
    Transition fromScanner;   // from scanner from scannerOther

    Overlap(Scanner scanner,
            Scanner scannerOther,
            Set<Integer> overlaps,
            Map<Integer, Integer> doubleEntries){
      this.scanner = scanner;
      this.scannerOther = scannerOther;
      this.overlapDistances = overlaps;
      this.doubleEntries = doubleEntries;
    }

    Scanner getScanner() {
      return scanner;
    }
    Scanner getOtherScanner(int otherThan) {
      if (scannerOther.getId() != otherThan) {
        return scannerOther;
      } else {
        return scanner;
      }
    }

    void setTransitions(Transition fromOther, Transition fromScanner) {
      this.fromOther = fromOther;
      this.fromScanner = fromScanner;
    }

    void setMatches(List<int[]> matches) {
      this.probeMatches = matches;
    }

    Set<Integer> getDistances() {
      return overlapDistances;
    }
    List<int[]> getProbeMatches() {
      return probeMatches;
    }
    int getdoubleEntries() {
      return doubleDistanceCount(doubleEntries);
    }
    Transition getTransition(int scannerId) {
      if (scanner.getId() == scannerId) {
        return fromOther;
      } else {
        return fromScanner;
      }
    }
    boolean probeMatchesDone() {
      return !(probeMatches == null || probeMatches.size() == 0);
    }
  }

  private class Transition {
    int[] scannerLocation; // as seen from the perspective of one scanner
    String rotation;
    String sign;
    String direction;

    Transition(int[] scannerLocation, String rotation, String sign, String direction){
      this.scannerLocation = scannerLocation;
      this.rotation = rotation;
      this.sign = sign;
      this.direction = direction;
    }

    Function<int[], int[]> curryTransition(Function<int[], int[]> function) {
      // function contains transitions from scanner X to scanner 0
      // this addition add from a new Y to X, and needs to be
      // applied first, so we have from Y to X and then from X to 0
      return (coord) -> function.apply(
              Transitions.rotations
                .get(this.rotation)
                .apply(this.scannerLocation,
                       Transitions.signs
                                  .get(this.sign)
                                  .apply(Transitions.directions
                                          .get(this.direction)
                                          .apply(coord))));
    }
  }


  private static class Transitions {

    /*  Recalling:

        (a) x + ScanX, y + ScanY, z + ScanZ  for 0°
        (b) x + ScanX, y + ScanZ, z - ScanY  for π/2  / 90°
        (c) x + ScanX, y - ScanY, z - ScanZ  for π    / 180°
        (d) x + ScanX, y - ScanZ, z + ScanY  for 3π/2 / 270°

       - the       X-direction:  ScanX,  ScanY, ScanZ
       - the minux X-direction: -ScanX, -ScanY, ScanZ
       - the       Y-direction:  ScanY, -ScanX, ScanZ
       - the minus Y-direction: -ScanY,  ScanX, ScanZ
       - the       Z-direction: -ScanZ,  ScanY, ScanX
       - the minus Z-direction:  ScanZ, -ScanY, ScanX

       Note: First get the direction, THEN do the minus.
       If not, the Z-directions end up with -ScanX at the z-coordinate
    */

    private static final Function<Integer, Function<Integer, Integer>> add
        = (w) -> (ScanW) -> w + ScanW;
    private static final Function<Integer, Function<Integer, Integer>> subtract
        = (w) -> (ScanW) -> w - ScanW;

    static final BiFunction<int[], int[], int[]> zero
        = (base, coord) -> new int[]{add.apply(base[0]).apply(coord[0]),
                                     add.apply(base[1]).apply(coord[1]),
                                     add.apply(base[2]).apply(coord[2])};
    static final BiFunction<int[], int[], int[]> zeroReverse
        = (base, coord) -> new int[]{subtract.apply(base[0]).apply(coord[0]),
                                     subtract.apply(base[1]).apply(coord[1]),
                                     subtract.apply(base[2]).apply(coord[2])};

    static final BiFunction<int[], int[], int[]> ninety
        = (base, coord) -> new int[]{add.apply(base[0]).apply(coord[0]),
                                     add.apply(base[1]).apply(coord[2]),
                                     subtract.apply(base[2]).apply(coord[1])};
    static final BiFunction<int[], int[], int[]> ninetyReverse
        = (base, coord) -> new int[]{subtract.apply(base[0]).apply(coord[0]),
                                     subtract.apply(base[1]).apply(coord[2]),
                                     add.apply(base[2]).apply(coord[1])};

    static final BiFunction<int[], int[], int[]> oneEighty
        = (base, coord) -> new int[]{add.apply(base[0]).apply(coord[0]),
                                     subtract.apply(base[1]).apply(coord[1]),
                                     subtract.apply(base[2]).apply(coord[2])};
    static final BiFunction<int[], int[], int[]> oneEightyReverse
        = (base, coord) -> new int[]{subtract.apply(base[0]).apply(coord[0]),
                                     add.apply(base[1]).apply(coord[1]),
                                     add.apply(base[2]).apply(coord[2])};

    static final BiFunction<int[], int[], int[]> twoSeventy
        = (base, coord) -> new int[]{add.apply(base[0]).apply(coord[0]),
                                     subtract.apply(base[1]).apply(coord[2]),
                                     add.apply(base[2]).apply(coord[1])};
    static final BiFunction<int[], int[], int[]> twoSeventyReverse
        = (base, coord) -> new int[]{subtract.apply(base[0]).apply(coord[0]),
                                     add.apply(base[1]).apply(coord[2]),
                                     subtract.apply(base[2]).apply(coord[1])};

    static final Map<String, BiFunction<int[], int[], int[]>> rotations
        = Map.of("zero", zero,
                 "ninety", ninety,
                 "oneEighty", oneEighty,
                 "twoSeventy", twoSeventy);
    static final Map<String, BiFunction<int[], int[], int[]>> reversedRotations
        = Map.of("zero", zeroReverse,
                 "ninety", ninetyReverse,
                 "oneEighty", oneEightyReverse,
                 "twoSeventy", twoSeventyReverse);

    static final Function<int[], int[]> Xdirection // do nothing
        = (coord) -> new int[]{coord[0], coord[1], coord[2]};
    static final Function<int[], int[]> Ydirection
        = (coord) -> new int[]{coord[1], -coord[0], coord[2]};
    static final Function<int[], int[]> Zdirection
        = (coord) -> new int[]{-coord[2], coord[1], coord[0]};

    static final Map<String, Function<int[], int[]>> directions
        = Map.of("X", Xdirection,
                 "Y", Ydirection,
                 "Z", Zdirection);

    static final Function<int[], int[]> minusDirection
        = (coord) -> new int[]{-coord[0], -coord[1], coord[2]};

    static final Map<String, Function<int[], int[]>> signs
        = Map.of("plus", Xdirection,
                 "minus", minusDirection);
  }


  // -----
  public Result solvePart2(String input) {
    if (this.scannerLocations == null || this.scannerLocations.size() == 0) {
      solve(input);
    }

    long result = 0;
    for (int i = 0; i < this.scannerLocations.size() - 1; i++) {
      for (int j = i + 1; j < this.scannerLocations.size(); j++) {

        long newManhattenDistance =
          Math.abs(this.scannerLocations.get(i)[0] - this.scannerLocations.get(j)[0])
          + Math.abs(this.scannerLocations.get(i)[1] - this.scannerLocations.get(j)[1])
          + Math.abs(this.scannerLocations.get(i)[2] - this.scannerLocations.get(j)[2]);

        result = result > newManhattenDistance ? result : newManhattenDistance;
      }
    }

    return Result.createResult(result);
  }
}
