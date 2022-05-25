package Year2021;

/*
  Both parts can be solved by simple regex:

  --- Part 1 ---:
    Search the string for "1...." to find all string starting with a 1.
    Then search the string for ".1..." to find all string with a 1 as the second character.
    Note that the dots have to account the full length of each string.

  --- Part 2 ---:
    Do the same as part 1, but once a character has been found, then keep it.
    So if "1...." fulfills the requirement of being the most or the least,
    then use "1" as the first character for the next regex to test: "11..."
    If not, then the next test should be: "01..."
*/

import java.util.stream.IntStream;
import java.util.regex.Pattern;
import java.util.function.BiPredicate;

import absbase.DayX;
import util.Result;

public class Day3 extends DayX {

  private String lineTerminator = System.getProperty("line.separator");
  private int theLength;
  private int halfLineCount;

  // -----
  public static void main(String args[]){
    new Day3(args).doIt();
  }

  public Day3(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    this.theLength = input.indexOf(lineTerminator);  // of a line

                        // there's no lineterminator on the last line.
    this.halfLineCount = (input.length() + lineTerminator.length())
                          / (theLength + lineTerminator.length()) // length of a line
                          / 2;
  }

  // -----
  public Result solve(String input) {
    setup(input);

    StringBuilder gammaString = new StringBuilder(this.theLength);
    String[] regexArray = regexCreator(this.theLength);

    for (String regex : regexArray) {
      Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
      long count = pattern.matcher(input)
                          .results()
                          .count();

      gammaString.append(count > this.halfLineCount ? "1" : "0");
    }

    int gamma = Integer.parseInt(gammaString.toString(), 2);  // radix 2 = binary
    int epsilon = getComplement(gamma, this.theLength);

    return Result.createResult(gamma * epsilon);
  }

  // -----
  private int getComplement(int original, int length) {
    // the complement of  10110 is really
    // the complement of   000...00010110
    // which is            111...11101001 <-- a negative number
    // but "and" that with 000...00011111 and we get 01001 :)
    //     -1 << length is 111...11100000 <-- so we need the complement of that.
    return ~original & ~(-1 << length);
  }

  // -----
  private String[] regexCreator(int length) {
    // If the length is total 5, we need a "1" and 4 "." of strings with 1 being rotated.
    // which gives this array: [1...., .1..., ..1.., ...1., ....1]
    return IntStream.range(0, length)
                    .mapToObj(i -> regexOneStringCreator(length, i))
                    .toArray(String[]::new);
  }

  // -----
  private String regexOneStringCreator(int length, int onePosition) {
    StringBuilder regex = new StringBuilder(length);
    regex.append("^");
    regex.append(".".repeat(onePosition));
    regex.append("1");
    regex.append(".".repeat(length - 1 - onePosition));
    regex.append("$");
    return regex.toString();
  }


  // -----
  public Result solvePart2(String input) {
    if (this.theLength == 0 || this.halfLineCount == 0) {
      setup(input);
    }

    int oxygenNumber =
      Integer.parseInt(lastMatch(
                        (count, totalCount) -> count >= totalCount / 2 + totalCount % 2,
                        input,
                        this.theLength),
                       2); // Remember it's binary

    int CO2Number =
      Integer.parseInt(lastMatch(
                        (count, totalCount) -> count < totalCount / 2 + totalCount % 2,
                        input,
                        this.theLength),
                       2);

    return Result.createResult(oxygenNumber * CO2Number);
  }

  // -----
  private String lastMatch(BiPredicate<Long, Long> threshold, String input, int length) {
    StringBuilder searchString = new StringBuilder(length);
    long totalCount = this.halfLineCount * 2;
    String searchResultString = "";

    while (searchResultString.isEmpty()) {
      // The test starts with just "1...."
      // Depending on the predicate and the result
      // ..a fixed search string either gets a "0" or a "1" appended.
      // Then a new test is created appending a "1" to the search string.
      String testRegex = regexExtendStringWithOne(searchString.toString(), length);

      Pattern pattern = Pattern.compile(testRegex, Pattern.MULTILINE);
      long count = pattern.matcher(input)
                          .results()
                          .count();

      if (threshold.test(count, totalCount)) { // this is where the predicate matters.
        searchString.append("1");
        totalCount = count;
      } else {
        searchString.append("0");
        totalCount = totalCount - count;
      }
      if (totalCount == 1) {
        // we're done.. except we may not have the entire string.
        // Note: The testRegex cannot be reused, since it always appends with a "1" for the test.
        String lastRegex = regexExtendString(searchString.toString(), length);
        searchResultString = Pattern.compile(lastRegex, Pattern.MULTILINE)
                                    .matcher(input)
                                    .results()
                                    .findFirst()
                                    .orElseThrow()
                                    .group();
      }
    }
    return searchResultString;
  }

  // -----
  private String regexExtendStringWithOne(String begin, int length) {
    StringBuilder regex = new StringBuilder(length);
    regex.append("^");
    regex.append(begin);
    regex.append("1");
    regex.append(".".repeat(length - 1 - begin.length()));
    regex.append("$");
    return regex.toString();
  }

  // -----
  private String regexExtendString(String begin, int length) {
    StringBuilder regex = new StringBuilder(length);
    regex.append("^");
    regex.append(begin);
    regex.append(".".repeat(length - begin.length()));
    regex.append("$");
    return regex.toString();
  }
}
