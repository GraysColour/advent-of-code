package Year2021;

/*
  The image edges partially depend on the "universe" colour.
  The border around the image depends on both "universe" colour and image colour.
  The border after that depends only on the colour of the "universe".

                                      ......... <-- border after
                          border -->  .........
        #..#.                         ..#..#...
        #....                         ..#......
        ##..#                         ..##..#..
        ..#..                         ....#....
        ..###                         ....###..
                                      .........
                                      .........

  As long as the universe stays dark, the "border after" will not change.
  This can only happen if the first bit in the "enhancement algorithm string" is a ".".
  If it's a "#", then any 3-by-3 bit block of "."s, will become a "#",
  effectively changing the "universe" colour.

  The image is converted to include these two borders, before the enhancement runs.
  After the first run, it's only neseccary to include one extra border per run,
  since only two universe borders are needed for each run.

  The colour of the universe after each enhancement is
  determined by the colour of any element on the outermost border.

  Before processing the image, every "." are made into 0s while "#" are 1s.
*/

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.function.Function;

import absbase.DayX;
import util.Result;

public class Day20 extends DayX {

  private int[] enhancements;
  private int[][] initialImage;
  private int universeColor;
  private int repeat = 2;

  // -----
  public static void main(String args[]){
    new Day20(args).doIt();
  }

  public Day20(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    String[] originalNenhancement = input.split("\\R{2}");

    Function<String, int[]> mapStringToIntArray =
        (str) -> IntStream.range(0, str.length())
                          .map(i -> str.charAt(i))
                          .map(c -> c == '.' ? 0 : 1)
                          .toArray();

    this.enhancements =
             mapStringToIntArray
               .apply(originalNenhancement[0].replaceAll("\\R", ""));

    String[] imageAsString = originalNenhancement[1].split("\\R");
    this.initialImage =
             IntStream.range(0, imageAsString.length)
                      .mapToObj(i -> mapStringToIntArray.apply(imageAsString[i]))
                      .toArray(int[][]::new);

    this.universeColor = 0;
  }

  // -----
  public Result solve(String input) {
    if (this.initialImage == null || this.initialImage.length == 0) {
      setup(input);
    }

    int universeColour = this.universeColor;
    // Initialize & expand with one border before the run
    int[][] enhancedWorld = expandWorld(this.initialImage, universeColour);

    for (int i = 0; i < this.repeat; i++) {
      enhancedWorld = enhanceWorld(enhancedWorld, universeColour);
      universeColour = enhancedWorld[0][0];
    }

    return Result.createResult(countLight(enhancedWorld));
  }

  // -----
  private long countLight(int[][] world) {
    return IntStream.range(0, world.length)
                    .mapToObj(y -> IntStream.range(0, world[y].length)
                                            .map(x -> world[x][y]))
                    .flatMapToInt(s -> s)
                    .filter(c -> c == 1)
                    .count();
  }

  // -----
  private int[][] enhanceWorld(int[][] world, int universeColour) {
    int[][] expandedWorld = expandWorld(world, universeColour);
    return changeWorld(expandedWorld, universeColour);
  }

  // -----
  private int[][] changeWorld(int[][] world, int universeColour) {
    // do not manipulate the same image as the values are fetched from.
    int[][] cloneWorld = copyWorld(world);

    IntStream.range(0, world[0].length)
             .forEach(x -> IntStream.range(0, world.length)
                                    .forEach(y -> cloneWorld[y][x]
                                                    = getNewColour(world,
                                                                   universeColour,
                                                                   x, y))
              );
    return cloneWorld;
  }

  // -----
  private int getNewColour(int[][] world, int universeColour, int x, int y) {
    int xLength = world[0].length;
    int yLength = world.length;

    // outer border only depeneds on the universe
    if (x == 0 || x == xLength - 1
        || y == 0 || y == yLength - 1) {
      return this.enhancements[universeColour == 0 ? 0 : 0b111_111_111];
    }

    int grid = 0;
    for (int i = y - 1; i <= y + 1; i++) {
      for (int j = x - 1; j <= x + 1; j++) {
        grid += world[i][j];
        grid <<= 1; // move it
      }
    }
    return this.enhancements[grid >>= 1]; // get back the last shift
  }

  // -----
  private int[][] copyWorld(int[][] world){
    int[][] cloneWorld = new int[world.length][world[0].length];
    IntStream.range(0, cloneWorld.length)
             .forEach(i -> {
                        System.arraycopy(world[i], 0, cloneWorld[i], 0, world[i].length);
              });
    return cloneWorld;
  }

  // -----
  private int[][] expandWorld(int[][] world, int universeColour){
    int[][] newWorld = new int[world.length + 2][world[0].length + 2];
    int newXlength = newWorld[0].length;

    // get a row full of universe colour
    int[] borderRow = new int[newXlength];
    Arrays.fill(borderRow, universeColour);

    // fill the top and bottom rows in the new expanded world.
    newWorld[0] = Arrays.copyOf(borderRow, newXlength);
    newWorld[newWorld.length - 1] = Arrays.copyOf(borderRow, newXlength);

    // fill the rest of the new world containing the original in the middle.
    IntStream.range(0, world.length)
             .forEach(i -> {
                        Arrays.fill(newWorld[i+1], 0, 1, universeColour);
                        System.arraycopy(world[i], 0, newWorld[i+1], 1, world[i].length);
                        Arrays.fill(newWorld[i+1], newXlength - 1, newXlength, universeColour);
              });
    return newWorld;
  }


  // -----
  public Result solvePart2(String input) {
    this.repeat = 50;
    Result result = solve(input);
    this.repeat = 2;
    return result;
  }
}
