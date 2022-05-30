package Year2021;

/*
  The idea:
    Start by counting fish by their day values.
    So x many with the value of 0, y many with the value of 1.. up to the value of 8.
    Put the values into a queue of 9 total items.
    For each step, poll the first value (day 0) and
      - Add that value to the now current value of day 6 (the 7th item)
      - Put the value as the last element in the queue as day 8 (the 9th item)

    Note that the values grow quicky (+ valueZeroDay*2), so longs are necessary.
*/

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.LongStream;

import absbase.DayX;
import util.Result;

public class Day6 extends DayX {

  private LinkedList<Long> lanternfish;
  private int daysPart1 = 80;
  private int daysPart2 = 256;
  private int days = daysPart1;

  // -----
  public static void main(String args[]){
    new Day6(args).doIt();
  }

  public Day6(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "Setup",       (i) -> {setup(i);                 return solve(i);},
             "SetupAlt",    (i) -> {setupAlternative(i);      return solve(i);},
             "SetupTwoAlt", (i) -> {setupOtherAlternative(i); return solve(i);}
             );
  }

  // -----
  public Result solve(String input) {
    if (this.lanternfish == null) {
      setup(input);
      // setupAlternative(input);
      // setupOtherAlternative(input);
    }

    for (int i = 0; i < days; i++) {
      long currentZeroes = this.lanternfish.poll();
      this.lanternfish.set(6, this.lanternfish.get(6) + currentZeroes);
      this.lanternfish.offer(currentZeroes);
    }

    return Result.createResult(this.lanternfish
                                   .stream()
                                   .reduce(0L, (a, b) -> a + b));
  }

  // -----
  private void setup(String input) {
    long[] numbers = new long[9];

    input.chars()
         .forEach(ch -> {
            switch (ch) {
              case ',': break;
              case '0': numbers[0]++;
                        break;
              case '1': numbers[1]++;
                        break;
              case '2': numbers[2]++;
                        break;
              case '3': numbers[3]++;
                        break;
              case '4': numbers[4]++;
                        break;
              case '5': numbers[5]++;
                        break;
              case '6': numbers[6]++;
                        break;
              case '7': numbers[7]++;
                        break;
              case '8': numbers[8]++;
                        break;
              default:  throw new AssertionError("found character: " + ch);
            }
          });

    this.lanternfish =
      LongStream.of(numbers)
                .boxed()
                .collect(LinkedList::new,
                         LinkedList::add,
                         LinkedList::addAll);
  }


  // -----
  private void setupAlternative(String input) {

    Long zeros  = Long.valueOf(0);
    Long ones   = Long.valueOf(0);
    Long twos   = Long.valueOf(0);
    Long threes = Long.valueOf(0);
    Long fours  = Long.valueOf(0);
    Long fives  = Long.valueOf(0);
    Long sixes  = Long.valueOf(0);
    Long sevens = Long.valueOf(0);
    Long eights = Long.valueOf(0);

    for (int i = 0; i < input.length(); i++) {
      switch (input.charAt(i)) {
        case '0': zeros++;
                  break;
        case '1': ones++;
                  break;
        case '2': twos++;
                  break;
        case '3': threes++;
                  break;
        case '4': fours++;
                  break;
        case '5': fives++;
                  break;
        case '6': sixes++;
                  break;
        case '7': sevens++;
                  break;
        case '8': eights++;
                  break;
      };
    };

    this.lanternfish =
      new LinkedList<>(List.of(zeros, ones, twos, threes, fours, fives, sixes, sevens, eights));
  }

  // -----
  private void setupOtherAlternative(String input) {

    long[] numbers = new long[9];

    for (int i = 0; i < input.length(); i++) {
      switch (input.charAt(i)) {
        case '0': numbers[0]++;
                  break;
        case '1': numbers[1]++;
                  break;
        case '2': numbers[2]++;
                  break;
        case '3': numbers[3]++;
                  break;
        case '4': numbers[4]++;
                  break;
        case '5': numbers[5]++;
                  break;
        case '6': numbers[6]++;
                  break;
        case '7': numbers[7]++;
                  break;
        case '8': numbers[8]++;
                  break;
      };
    };

    this.lanternfish =
      new LinkedList<>(List.of(numbers[0],
                               numbers[1],
                               numbers[2],
                               numbers[3],
                               numbers[4],
                               numbers[5],
                               numbers[6],
                               numbers[7],
                               numbers[8]));
  }

  // -----
  public Result solvePart2(String input) {
    if (this.lanternfish == null) {
      solve(input);
    }

    this.days = daysPart2 - daysPart1;
    Result result = solve(input);
    this.days = daysPart1;
    return result;
  }
}
