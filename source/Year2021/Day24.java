package Year2021;

/*
  ## Analyzing the input
    The MONAD instructions are 14 almost
    idential blocks of 18 instructions each.
    They starts with taking in the next input digit:

         1    inp w
         ^ just a line number to ease the explanation

    All but 3 of the instructions are identical:
      - line 5 is either
        - div z 1
        - div z 26
      - line 6, where the value of A varies:  add x A
      - line 16, where the value of B varies: add y B

         1    inp w
         2    mul x 0
         3    add x z
         4    mod x 26
         5    div z 1    OR   div z 26
         6    add x A    <--- add A
         7    eql x w
         8    eql x 0
         9    mul y 0
        10    add y 25
        11    mul y x
        12    add y 1
        13    mul z y
        14    mul y 0
        15    add y w
        16    add y B    <--- add B
        17    mul y x
        18    add z y

    #### Using a register as a stack

      Before exploring the particulars, it's important to
      understand how two registers can be used as a stack.
      Assuming the values to be put on the stack and lower than 26,
      then 26 can used as the "base":

                                          stack
        start value                         0
        add value 4                         4

      stack % 26 will give 4. Similar to peek.
      stack / 26 will set the value back to zero and effectively empty the stack.

        preparing to add another
        value by mulpiplying by 26         4*26               = 104
        add value 17                       4*26 + 17          = 121

      stack % 26 will give 17. Similar to peek.
      stack / 26 will set the value back to 4, effectively popping 17 off
      stack / 26 twice will empty the stack.

        preparing to add another
        value by mulpiplying by 26         (4*26 + 17)*26      = 3146
        add value 11                       (4*26 + 17)*26 + 11 = 3157

      stack % 26 will give 11. Similar to peek.
      stack / 26 will set the value back to 4*26 + 17 = 121, effectively popping 11 off
      stack / 26 and then stack % 26 will set the value to 17.
      stack / 26 twice will set the value to 4.
      stack / 26 three times will empty the stack.

      Note that this only works for putting
      positive values on the stack. Had the last
      added value been -3 instead of 11, then:
        stack % 26 would give 23
        stack / 26 would give 120
      which is exactly the same as if the stack had been:
        (4*26 + 16)*26 + 23, adding 4, then 16 then 23.

      Since the modulus operation changes the value to
      the last added, another register is required to
      make a peek, which is used in the blocks:

         2    mul x 0             <-- set x to 0
         3    add x z             <-- add z to x (copy over the value of z)
         4    mod x 26            <-- peek at the top value on the stack, z,
                                      but without changing z at all.
                                      Keep the peeked value in x.

      The next instruction in the block determines
      if the value is popped off z or not:

         5    div z 1             <-- Do not change z
         OR
         5    div z 26            <-- pop the last value off.

    #### The if-statement

         6    add x A             <-- add some fixed value A
         7    eql x w
         8    eql x 0

      Line 6 just adds A to the peeked value.
      Line 7 check if peek+A is equal to w, the newest input:
         if ((peek + A) == input) {
            x = 1
         } else {
            x = 0
         }
      Line 8 reverses the value of x:
         if (x == 0) {
            x = 1
         } else {
            x = 0
         }
      The combination of the three becomes:
         if ((peek + A) == input) {
            x = 0
         } else {
            x = 1
         }

    #### Setting up the push of a new value on z

         9    mul y 0             <-- set y to 0
        10    add y 25            <-- set y to 25
        11    mul y x
        12    add y 1
        13    mul z y

      Line 11 uses the value of x,
      which is the result of the if-statement.
      Since it multiplies y with either 0 or 1:
        if ((peek + A) == input) {
          y = 25 * 0 = 0
        } else {
          y = 25 * 1 = 25
        }

      Line 12 adds 1 to y, making it either have
        - value of 1, which is neutral for multiplication
        - value of 26, which is the base used for the stack

      Line 13 either does or doesn't get the stack ready for a new value,
      by multiplying z with either 1, keeping is as is
      or multiplying z by 26, getting it ready for a new value.

    #### Adding a new value onto z

        14    mul y 0             <-- set y to 0
        15    add y w             <-- set y to w, the newest input
        16    add y B             <-- add some fixed value B
        17    mul y x
        18    add z y

      Line 17 uses exactly the same value of x
      as previously. It's the same if-statement:
        if ((peek + A) == input) {
          y = (input + B) * 0 = 0
        } else {
          y = (input + B) * 1 = input + B
        }

      Line 18 then adds either 0 or input + B to the stack.

    #### Enough with the breakdown..! What does it do?

      1. Take a new input
      2. Peek or pop the stack
      3. If the peeked/popped stack value + A == input
         then it does NOT push the new input + B onto the stack.
         But if they are not equal, it pushes
         the new input + B onto the stack

      Since the pushed value is the new input + B,
      it makes the comparison in the if statement:

        some previous input + its corresponding B value + A == new input

      It also makes the B-values directly linked to the input.
      Meaning the first input will be coupled with the B in the first block.
      The second input will be coupled with the B in the second block..

      At first when the stack is empty, the statement is
        if (0 + A == new input) ...

      Since the aim is to get the stack to 0,
      then the goal is to find values for the
      input which will make the sides equal.

    #### Looking at the different instruction blocks

      The 14 instructions blocks seems to be 7 where line 5 is:
        div z 1             <-- Do not change z
      ..and 7 where it's:
        div z 26            <-- pop the last value off
      meaning that at the most, 7 values can be pushed.

    #### Satisfying the if-statement

      If the sum of B and A > 8 (or less then -8),
      specifically when the sum is 9 (or -9),
      then there's noway to satisfy the equality
      without one of the singleDigit integers being 0:

        singleDigit integer + B + A == singleDigit integer

      In this situation, there's no way to
      avoid the new value being pushed onto the stack.

      But if their sum is less than 9, it's easy
      to find two values to satisfy the equality.

    #### Implementation specifics
      Since it's not possible to push a yet unknown value
      onto a stack, the choice is to push the index instead.
      The first input gets the value 0, the next 1, ...

      Keeping an array of B's (called magicNumbers) enables
      any popped value from the stack to be linked to its
      correspondinding B-value, just by its value being
      the index value of the B-array.

      While setting up the input, the peek/pop, the A,
      (and the B values) for each block, are stored in
      arrays in order, so each loop of the index values
      0-13 has direct access to the particulars of that loop.
*/

import java.util.List;
import java.util.LinkedList;
import java.util.Deque;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.IntStream;
import java.util.function.Function;

import absbase.DayX;
import util.Result;

public class Day24 extends DayX {

  private StackOperation[] operations;  // either PEEK or POP
  private int[] magicNumbers;           // B-values
  private int[] equalizers;             // A-values

  private List<Function<Integer, Integer>> calculator;
  private List<Function<Integer, Integer>> maxCalculator
    = List.of(
                difference -> difference < 0 ? 9 + difference : 9, // current
                difference -> difference < 0 ? 9 : 9 - difference  // stackValue
             );
  private List<Function<Integer, Integer>> minCalculator
    = List.of(
                difference -> difference < 0 ? 1 : 1 + difference, // current
                difference -> difference < 0 ? 1 - difference: 1   // stackValue
             );

  // -----
  public static void main(String[] args){
    new Day24(args).doIt();
  }

  public Day24(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    operations   = new StackOperation[14];
    magicNumbers = new int[14];
    equalizers   = new int[14];

    String regex = "inp w" + "\\R" +
                   "mul x 0" + "\\R" +
                   "add x z" + "\\R" +
                   "mod x 26" + "\\R" +
                   "div z (1|26)" + "\\R" +
                   "add x (-?\\d+)" + "\\R" +
                   "eql x w" + "\\R" +
                   "eql x 0" + "\\R" +
                   "mul y 0" + "\\R" +
                   "add y 25" + "\\R" +
                   "mul y x" + "\\R" +
                   "add y 1" + "\\R" +
                   "mul z y" + "\\R" +
                   "mul y 0" + "\\R" +
                   "add y w" + "\\R" +
                   "add y (\\d+)" + "\\R" +
                   "mul y x" + "\\R" +
                   "add z y";

    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(input);
    IntStream
      .range(0, 14)
      .forEach(i -> {
                      if (matcher.find()) {
                        operations[i] = "26".equals(matcher.group(1))
                                          ? StackOperation.POP
                                          : StackOperation.PEEK;
                        magicNumbers[i] = Integer.parseInt(matcher.group(3));
                        equalizers[i]   = Integer.parseInt(matcher.group(2));
                      } else {
                        throw new AssertionError("Unexpected input. Cannot find instructions");
                      }
       });

    this.calculator = this.maxCalculator;
  }

  enum StackOperation {
    PEEK, POP;
  }

  // -----
  public Result solve(String input) {
    if (this.operations == null || this.operations.length == 0) {
      setup(input);
    }

    int[] result = new int[14];
    Deque<Integer> stack = new LinkedList<Integer>();

    for (int i = 0; i < 14; i++) {  // loop over the 14 value indexs
      int difference = 0;
      Integer stackValue = operations[i].equals(StackOperation.PEEK)
                             ? stack.peek()
                             : stack.poll();
      if (stackValue != null) { // something on the stack
        difference = magicNumbers[stackValue]; // the B-value
      }
      difference += equalizers[i];             // the A-value

      // impossible for two single digit results, since it has to be x = y + difference
      if (Math.abs(difference) > 8) {
        stack.addFirst(i);                     // push the index
      } else {
        result[i]          = this.calculator.get(0).apply(difference);
        result[stackValue] = this.calculator.get(1).apply(difference);
      }
    }

    if (stack.size() != 0) {
      throw new AssertionError("Stack isn't empty! Values of register z is not 0!");
    }

    return Result.createResult(makeLongResult(result));
  }

  // ----
  private long makeLongResult (int[] resultArray) {
    StringBuilder sb = new StringBuilder(14);
    for (int i : resultArray) {
      sb.append(i);
    }
    return Long.valueOf(sb.toString());
  }


  // -----
  public Result solvePart2(String input) {
    if (this.operations == null || this.operations.length == 0) {
      setup(input);
    }

    this.calculator = this.minCalculator;
    Result result = solve(input);
    this.calculator = this.maxCalculator;

    return result;
  }
}
