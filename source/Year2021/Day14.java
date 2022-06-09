package Year2021;

/*
  Notice that every letter in the string, except the last letter
  is the first letter in a pair of "double-letters":

      NNCB
      NN     <-- this becomes NCN, which is two doubles: NC and CN
       NC    <-- this becomes NBC, which is two doubles: NB and BC
        CB   <-- this becomes CHB, which is two doubles: CH and HB

    Result:
      NCNBCHB
      NC
       CN
        NB
         BC
          CH
           HB

  The result of this one step can be recreated from the pairs of "double-letters" by
  taking the first letter of each and at the end appending the very last original letter.
  In above example that very last original letter is B.

  The string grows in size by double-letter*2 + 1 (the last letter)
  and there are total-letters - 1 amount of "double-letters" in a string.
  So the string grows by (n-1) * 2^(n) + 1 for n steps.
  That makes it unfeasible to implement a naive solution by manipulating a string.

  The idea:
    Keep a map (mapper) of which two pairs a pair of double-letters maps to.

    Isolate all the possible "double-letters".
    Keep another map (pairCounter) of "double-letters"
    and the original total count of each.

    For every step:
      Update the pairCounter by going through each pair and:
      - Getting the counter for the pair
      - Discarding the counter for this pair,
        since it's no longer part of this extension of the string.
      - Finding the two pairs that the pair maps to
      - Adding the counter to those two pairs.
*/

import java.util.Map;
import java.util.HashMap;

import absbase.DayX;
import util.Result;

public class Day14 extends DayX {

  private String polymerTemplate;
  private String[] insertionRules;

  // holds the current count in the first entry in the array.
  // the second entry is used to add counts on each iteration.
  // Note that the current count of XY cannot go to count on itself in an iteration.
  private Map<String, long[]> pairCounter;
  private Map<String, String[]> mapper;    // maps each pair XY to XZ & ZY
  private String lastLetter;

  private int iterations = 10;

  // -----
  public static void main(String[] args){
    new Day14(args).doIt();
  }

  public Day14(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    String[] dataNinstructions = input.split("\\R{2}");

    this.polymerTemplate = dataNinstructions[0];
    this.insertionRules =
      dataNinstructions[1].lines()
                          .toArray(String[]::new);

    this.pairCounter = new HashMap<>(this.insertionRules.length);
    this.mapper = new HashMap<>(this.insertionRules.length * 2);
    this.lastLetter = this.polymerTemplate.substring(polymerTemplate.length() - 1);

    // a mapping from "XY - > Z" wil become
    //   "XY" : ["XZ", "ZY"] in the mapper
    //   "XY" : [0L, 0L] in the pairCounter
    for (String str : this.insertionRules) {
      String[] mapping = str.split(" -> ");
      String pair = mapping[0];
      String middle = mapping[1];

      this.mapper.put(pair, new String[]{pair.substring(0, 1) + middle,
                                         middle + pair.substring(1)});
      this.pairCounter.put(pair, new long[]{0L,0L});
    }

    // The initial template counts
    for (int i = 0; i < this.polymerTemplate.length() - 1; i++) {
      String key = this.polymerTemplate.substring(i, i + 2);
      this.pairCounter.get(key)[0]++;
    }
  }

  // -----
  public Result solve(String input) {
    if (this.pairCounter == null || this.pairCounter.isEmpty()) {
      setup(input);
    }

    // 1. Look up all the pairs in the counter
    // 2. Find what each maps to
    // 3. Add the counter for each pair to its corresponding mapped elements
    for (int i = 0; i < this.iterations; i++) {
      // every pair XY needs to become two pairs: XZ and ZY.
      pairCounter.entrySet()
                 .stream()
                 .forEach(entry -> {
                    String[] mapsTo = mapper.get(entry.getKey());
                    long valueToAdd = entry.getValue()[0]; // the count of XY

                    for (String str : mapsTo) {
                      long[] currentCounter = pairCounter.get(str);
                      // the updated count of XZ & ZY
                      currentCounter[1] = currentCounter[1] + valueToAdd;
                    }
                  });

      // getting ready for the next iteration by
      // moving the counter to the first element of the counter array
      pairCounter.entrySet()
                 .stream()
                 .forEach(entry -> {
                    long[] currentCounter = entry.getValue();
                    currentCounter[0] = currentCounter[1];
                    currentCounter[1] = 0;
                  });
    }

    // count only the first character of the pairCounter
    // since the second one is the first in another entry
    Map<String, Long> charCounter = new HashMap<>(this.insertionRules.length);
    pairCounter.entrySet()
               .stream()
               .forEach(entry -> {
                  String letter = entry.getKey().substring(0, 1); // first letter
                  long currentCounter = entry.getValue()[0];
                  charCounter.merge(letter, currentCounter, (a, b) -> a + b);
                });
    // the last character didn't get counted yet
    charCounter.merge(lastLetter, 1L, (a, b) -> a + b);

    long max = Long.MIN_VALUE, min = Long.MAX_VALUE;
    for (Map.Entry<String, Long> entry : charCounter.entrySet()) {
      long current = entry.getValue();
      if (max < current) {
        max = current;
      }
      if (min > current) {
        min = current;
      }
    }

    return Result.createResult(max - min);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.pairCounter == null || this.pairCounter.isEmpty()) {
      solve(input);
    }

    this.iterations = 40 - 10;
    Result result = solve(input);
    this.iterations = 10;
    return result;
  }
}
