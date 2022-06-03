package Year2021;

/*
  --- Part 1 ---:
    The idea:
      Go through the string one character at a time building a new string.
      Every time an end bracket appears, it has to match a begin bracket,
      which must be the last character in the new string.
      Once a match is made the matched begin bracket is removed.

      If an end bracket does not match the last character in
      the new string, its points is added to an overall result.

      All strings that do not have a mis-mathing end bracket is
      put into a list for part 2.

  --- Part 2 ---:
    Having all the imcomplete strings with all the end bracket
    removed, all there is to do is matching begin brackets.

    Since the calculation requires the missing brackets to be added
    to a result that is multiplied by 5 before the addition,
    it's most easily done by iterating over the strings in reverse order.
    Each begin bracket can then be matched directly to its points.
*/

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.Comparator;

import absbase.DayX;
import util.Result;

public class Day10 extends DayX {

  private List<StringBuilder> incomplete = new ArrayList<>();

  // -----
  public static void main(String args[]){
    new Day10(args).doIt();
  }

  public Day10(String[] args) {
    super(args);
  }

  // -----
  public Result solve(String input) {
    String[] lines = input.split("\\R");

    Set<Character> begins = Set.of('[','{','(','<');

    Map<Character, Character> match = Map.of(']','[',
                                             '}','{',
                                             ')','(',
                                             '>','<');

    Map<Character, Integer> cost = Map.of(')', 3,
                                          ']', 57,
                                          '}', 1197,
                                          '>', 25137);
    long result = 0;

    for (String str : lines) {
      StringBuilder builder = new StringBuilder();

      // Since the loop needs to end prematurely, using a stream isn't feasible
      for (char character : str.toCharArray()) {
        if (begins.contains(character)) {
          builder.append(character);
        } else {
          char matchFor = match.get(character);
          int previousIndex = builder.length() - 1;
          char previousCharacter = builder.charAt(previousIndex);

          if (previousCharacter == matchFor) {
            builder.deleteCharAt(previousIndex);
          } else {
            // mismatch!
            builder = null;
            result += cost.get(character);
            break;
          }
        }
      }

      if (builder != null)
        this.incomplete.add(builder);
    }

    return Result.createResult(result);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.incomplete.isEmpty()) {
      solve(input);
    }

    Map<Character, Integer> cost = Map.of('(', 1,
                                          '[', 2,
                                          '{', 3,
                                          '<', 4);

    List<Long> results = new ArrayList<>();
    for (StringBuilder builder : this.incomplete) {
      long total = 0;
      for (int i = builder.length() - 1; i >= 0; i--) {
        total *= 5;
        total += cost.get(builder.charAt(i));
      }
      results.add(total);
    }

    results.sort(Comparator.naturalOrder());

    // integer division is always the floor :)
    return Result.createResult(results.get((results.size()) / 2));
  }
}
