package Year2021;

/*
  --- Part 1 ---:
    This is straight forward.

  --- Part 2 ---:
    Once the signals for 1, 4, 7, and 8 have been isolated, there's only 6 unknowns:
      - 2, 3 and 5 that have 5 segments (size-5) each.
      - 0, 6 and 9 that have 6 segments (size-6) each.

    By turning each signal into a set of segments, like Set<Character>
    they can be used to find intersections.

    For example, 9 is the only digits of size-6,
    that has all the 4 segments that 4 also has:

          4:
         ....
        b    c
        b    c
         dddd
        .    f
        .    f
         ....

          0:      6:      9:
         aaaa    aaaa    aaaa
        b    c  b    .  b    c
        b    c  b    .  b    c
         ....    dddd    dddd
        e    f  e    f  .    f
        e    f  e    f  .    f
         gggg    gggg    gggg

    6 doesn't have any c-segment and 0 doesn't have any d-segment.
    So 9 will be the only set of segments where for example "allMatch"
    of the segments of 4 against the size-6 signals that will return true.

    Once 9 has been identified, it can be removed as an unknown size-6.
    Of the remaining 0 and 6, only 0 will have all the segments of 7.
    The last size-6 will obviously be 6.

    The size-5's are:

          2:      3:      5:
         aaaa    aaaa    aaaa
        .    c  .    c  b    .
        .    c  .    c  b    .
         dddd    dddd    dddd
        e    .  .    f  .    f
        e    .  .    f  .    f
         gggg    gggg    gggg

    Only 3 has all the segments of 7.

    Of the remaining 2 and 5, only 5 has all the segments that 6 also has.
    2 has c-segments, which 6 doesn't have.
    Note that this is reversed compared to the previous "allMatch",
    where a known digit was "allMatch" to the set of unknowns.
    Here each of the two remaining size-5 unknowns are "allMatch"'ed against 6.

    Once 5 is identified the last remaining must be 2.

    Now it's relatively trivial to map each string-signal to its corresponding
    numeric value and computing the actual value of the "four digit output".
*/

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import absbase.DayX;
import util.Result;

public class Day8 extends DayX {

  private String[][] inputSetup;  // the input after it's been split by newline -> space

  // -----
  public static void main(String args[]){
    new Day8(args).doIt();
  }

  public Day8(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    // No clue why, but it refused to recognize these pipes: "(?<!\\|)\\R" or "(?<![\\|])\\R".
    // make sure to not split the signals on |\R
    String[] firstSplit = input.split("(?<=[a-g])\\R");

    // this gets [["first ten digits", "first signal"], ["second ten digits", "second signal"], ..]
    this.inputSetup = Arrays.stream(firstSplit)
                            .map(s -> s.split("\\s\\|\\R?\\s?"))
                            .toArray(String[][]::new);
  }

  // -----
  public Result solve(String input) {
    if (this.inputSetup == null) {
      setup(input);
    }

    // this gets "signal1" + "signal2" + .. each having just 4 signals.
    String justTheLast4 = Arrays.stream(inputSetup)
                                .map(s -> s[1])
                                .collect(Collectors.joining(" "));

                                       // strings of length 2, 3, 4 or 7
    Pattern pattern = Pattern.compile("\\b[a-g]{2}(?:[a-g]{5}|[a-g]{1,2})?\\b");
    long result = pattern.matcher(justTheLast4)
                         .results()
                         .count();

    return Result.createResult(result);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.inputSetup == null) {
      setup(input);
    }

    int result = 0;

    for (String[] array : inputSetup) {

      Set<Character> one = null;
      Set<Character> four = null;
      Set<Character> seven = null;
      Set<Character> eight = null;

      Set<Set<Character>> size5 = new HashSet<>();  // 2, 3, 5
      Set<Set<Character>> size6 = new HashSet<>();  // 0, 6, 9

      // first part has "all ten unique signal patterns"
      String[] tenPatterns = array[0].split("\\s");

      for (String codedDigit : tenPatterns) {
        switch (codedDigit.length()) {
          case 2: one = stringToCharSet(codedDigit);
                  break;
          case 3: seven = stringToCharSet(codedDigit);
                  break;
          case 4: four = stringToCharSet(codedDigit);
                  break;
          case 5: size5.add(stringToCharSet(codedDigit));
                  break;
          case 6: size6.add(stringToCharSet(codedDigit));
                  break;
          case 7: eight = stringToCharSet(codedDigit);
                  break;
          default: throw new AssertionError("Got this: " + codedDigit);
        }
      }

      // nine is the only size6, that has all the 4 elements of four:
      Set<Character> nine = getElementIsSubsetOfSet(size6, four);
      size6.remove(nine); // not checking the boolean return value :O

      // with nine out of size6, only zero has all the elements of seven:
      Set<Character> zero = getElementIsSubsetOfSet(size6, seven);
      size6.remove(zero);

      // last element left is the six:
      Set<Character> six = size6.iterator().next();


      // three is the only size5, that has all the 3 elements of seven:
      Set<Character> three = getElementIsSubsetOfSet(size5, seven);
      size5.remove(three);

      // only five is a subset of six. (two has an element that is not in six)
      Set<Character> five = getSetIsSubsetOfElement(size5, six);
      size5.remove(five);

      // last element left is the two:
      Set<Character> two = size5.iterator().next();


      Map<Set<Character>, Integer> digitMap =
        Map.of(zero, 0, one, 1, two, 2, three, 3, four, 4,
               five, 5, six, 6, seven, 7, eight, 8, nine, 9);

      String[] theMessage = array[1].split("\\s");

      // add up all the 4 digit numbers
      int sum = 0;
      for (int i = 0; i < theMessage.length; i++) {
        sum += digitMap.get(stringToCharSet(theMessage[i])) * Math.pow(10, 3-i);
      }

      result += sum;
    }

    return Result.createResult(result);
  }

  // returns the first set in setOfSets where the set is a true subset of element.
  private Set<Character> getSetIsSubsetOfElement(Set<Set<Character>> setOfSets, Set<Character> element) {
      return setOfSets.stream()
                      .filter(set -> set.stream()
                                        .allMatch(elem -> element.contains(elem)))
                      .findFirst()
                      .orElseThrow();
  }


  // returns the first set in setOfSets where element is a true subset of set.
  private Set<Character> getElementIsSubsetOfSet(Set<Set<Character>> setOfSets, Set<Character> element) {
      return setOfSets.stream()
                      .filter(set -> element.stream()
                                              //.filter(elem -> set.contains(elem))
                                              //.count() == 4)
                                              .allMatch(elem -> set.contains(elem)))
                      .findFirst()
                      .orElseThrow();
  }

  // returns the Set of Characters that make up a string.
  private Set<Character> stringToCharSet(String string) {
    return string.chars()
                 .mapToObj(e -> (char) e)
                 .collect(Collectors.toSet());
  }
}
