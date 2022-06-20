package Year2021;

/*
  --- Part 1 ---:
    ### Player 1:
      Assuming a player starts at position 10.
      Then the first turn will move to 1 + 2 + 3 = 6.
      Since the other player rolled 4 + 5 + 6,
      the next will move the player 7 + 8 + 9 = 24
      The moves are:

        1 + 2 + 3    = 6  = 6 + 0*18  = 6 (mod 10)
        7 + 8 + 9    = 24 = 6 + 1*18  = 4 (mod 10)
        13 + 14 + 15 = 42 = 6 + 2*18  = 2 (mod 10)
        19 + 20 + 21 = 60 = 6 + 3*18  = 0 (mod 10)
        25 + 26 + 27 = 78 = 6 + 4*18  = 8 (mod 10)
        31 + 32 + 33 = 96 = 6 + 5*18  = 6 (mod 10) = 6 + 0*18, the first move.
                            6 + k*18

      Modulus 10 has 10 different values,
      but since it starts at an even number
      and the values are "rotated" by a multiple, k, of an even number,
      there can only be 5 outcomes.
      That means the cycle of "rotation" is at most 5.

      See https://en.wikipedia.org/wiki/Cyclic_group
      and https://en.wikipedia.org/wiki/Modular_arithmetic

      Based on the cycle of moves:
        6,  4,  2, 10,  8, and then 6 again

      The placement on the board will be
        6, 6+4, 6+4+2, 6+4+2+10, 6+4+2+10+8, 6+4+2+10+8 + 6
        6,  10,     2,         2,        10,   repeating: 6, 10, 2, 2, 10, ...

      If the player starts at 1, instead of 10, the cycle is offset by 1:
        7,  1,  3,  3,  1, ...

      Since there are 10 board placements, there are 10 possible rotations:

        Starting at 0:  6, 10,  2,  2, 10, ...
        Starting at 1:  7,  1,  3,  3,  1, ...
        Starting at 2:  8,  2,  4,  4,  2, ...
        Starting at 3:  9,  3,  5,  5,  3, ...
          ...
        Starting at 9:  5,  9,  1,  1,  9, ...

    ### Player 2:
      Again assuming a player starts at position 10.
      Then the first turn will move to 4 + 5 + 6 = 15
      The next will be 10 + 11 + 12 = 33
      The moves are:

        4 + 5 + 6    = 15 = 15 + 0*18  = 5 (mod 10)
        10 + 11 + 12 = 33 = 15 + 1*18  = 3 (mod 10)
        16 + 17 + 18 = 51 = 15 + 2*18  = 1 (mod 10)
        22 + 23 + 24 = 69 = 15 + 3*18  = 9 (mod 10)
        28 + 29 + 30 = 87 = 15 + 4*18  = 7 (mod 10)
        34 + 35 + 36 = 95 = 15 + 5*18  = 5 (mod 10) = 15 + 0*18, the first move.
                            15 + k*18

      However, since the values are odd,
      the moves will switch between odd and even,
      not limiting the "rotation" to 5 of the 10 possible modulus 10 values.
      so the rotation needs to applied twice to get the board placements.

      Based on the cycle of moves:
        5,  3,  1,  9,  7 and then 5 again

      The placement on the board will be
        5, 5+3, 5+3+1, 5+3+1+9, 5+3+1+9+7, 5+3+1+9+7+5, ...
        5,   8,     9,       8,         5,          10,  3,  4,  3, 10, repeating 5, 8 ...

      Including starting positions, the 10 possible rotations becomes:

        Starting at 0:  5,  8,  9,  8,  5, 10,  3,  4,  3, 10, ...
        Starting at 1:  6,  9, 10,  9,  6,  1,  4,  5,  4,  1, ...
        Starting at 2:  7, 10,  1, 10,  7,  2,  5,  6,  5,  2, ...
        Starting at 3:  8,  1,  2,  1,  8,  3,  6,  7,  6,  3, ...
          ...
        Starting at 9:  4,  7,  8,  8,  4,  9,  2,  3,  2,  9, ...

    ### How is that helpful?

      Knowing the rotation of the board placements
      means the sum can be calculated.
      Each time player one has moved 5 times,
      its score is incremented by a fixed value.
      For example, starting at position 2, its score will be
        8 + 2 + 4 + 4 + 2 = 20 after the first 5 moves.
      and 2*20 = 40 after the 10th move.

      For player two the sum has to be calculated for 10 moves,
      since the first 5 moves will not increment the score
      by the same value as the last 5 moves in the cycle.

      When taking the score increments for 10 turns
      for each player, it's easy to see how many total turns lie
      under the winning move, with less than 10 moves each to go.

  --- Part 2 ---:
    Looking at the first roll of the die, there are 3 outcomes.
    Each of those have another three outcomes
    for the second roll, making it 9 outomes.
    When the die has been rolled three times,
    there's a total of 3*3*3 = 27 outcomes:

                  1                        2                        3
          1       2       3        1       2       3        1       2       3
        1 2 3   1 2 3   1 2 3    1 2 3   1 2 3   1 2 3    1 2 3   1 2 3   1 2 3

    The value of the sum of the rolls are:
        3 4 5   4 5 6   5 6 7    4 5 6   5 6 7   6 7 8    5 6 7   6 7 8   7 8 9

    The sum values can be grouped and counted:

        sum : count
          3 : 1
          4 : 3
          5 : 6
          6 : 7
          7 : 6
          8 : 3
          9 : 1

    The next time the player rolls the die 3 times,
    each of those counts will make for 27 new sum outcomes.
    All outcomes are in modulus 10:

      prev 3's :         prev 4's :         prev 5's :         prev 6's :      ...
        sum : count        sum : count        sum : count        sum : count
     3+3= 6 : 1         4+3= 7 : 1*3       5+3= 8 : 1*6       6+3= 9 : 1*7
          7 : 3              8 : 3*3            9 : 3*6           10 : 3*7
          8 : 6              9 : 6*3           10 : 6*6            1 : 6*7
          9 : 7             10 : 7*3            1 : 7*6            2 : 7*7
         10 : 6              1 : 6*3            2 : 6*6            3 : 6*7
          1 : 3              2 : 3*3            3 : 3*6            4 : 3*7
          2 : 1              3 : 1*3            4 : 1*6            5 : 1*7

    Note that adding up all the sums will not be helping,
    since they don't have identical scores.
    For example there are 22 8s total, but they have these scores:
      6 counts of 8s that were previously 3, now have a score of 3 + 8: 11
      9 counts of 8s that were previously 4, now have a score of 4 + 8: 12
      6 counts of 8s that were previously 5, now have a score of 5 + 8: 13
      1 count  of 8  that was  previously 9, how have a score of 9 + 8: 17

    Similarly all identical scores do not have the same board position.

    There is a need to store the score information along with the board positions,
    so either a list/map/array of scores each having counts of board positions,
    or a list/map/array of board positions each having counts of scores.

    When going through the turns using the
    basic "sum : count" of 27 increment-mappings, once any value
    passes the score of 21, the player will have won in the number
    of universes that the other player hasn't won in yet.
    If those wins are then removed from the stored information
    about score & board position, the total count of the universes
    a player hasn't won in, will be its total combination count for that turn.

    For example player1 gets 4 values of 21 or more in a turn.
    Then player1 will have won in 4 * the total count "left" for player2.
*/

import java.util.Map;
import java.util.stream.IntStream;

import absbase.DayX;
import util.Result;

public class Day21 extends DayX {

  int modulus = 10;
  int win = 1000;
  int winPart2 = 21;
  int player1start;
  int player2start;

  // -----
  public static void main(String[] args){
    new Day21(args).doIt();
  }

  public Day21(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "Cycles",      (i) -> solveCycles(i),
             "BruteForce",  (i) -> solveBrute(i)
            );
  }

  private void setup(String input) {
    String[] players = input.split("\\R");

    String search = "Player X starting position: ";
    int length = search.length();

    for (String str : players) {
      if (str.startsWith(search.replace("X","1"))) {
        this.player1start = Integer.parseInt(str.substring(length));
      }
      if (str.startsWith(search.replace("X","2"))) {
        this.player2start = Integer.parseInt(str.substring(length));
      }
    }
  }

  // -----
  public Result solve(String input) {
    setup(input);
    // return solveCycles(input);
    return solveBrute(input);
  }

  // -----
  private Result solveCycles(String input) {
    setup(input);

    // Get the player's score increment array and sum for 10 turns
    int[] player1Additions = createIncrements(1 + 2 + 3,
                                              2 * (3+3+3),
                                              this.modulus,
                                              this.player1start);
    int player1ModSum = getAdditionsSum(player1Additions);

    int[] player2Additions = createIncrements(4 + 5 + 6,
                                              2 * (3+3+3),
                                              this.modulus,
                                              this.player2start);
    int player2ModSum = getAdditionsSum(player2Additions);

    // find the number of turns each players can make
    // while both staying under score 1000
    int maxCycleSum = Math.max(player1ModSum, player2ModSum);
    // if player1 hits 1000 exactly, then player2 has played
    // one turn too many, if they took an equal amount of turns
    int maxCycleGames = player1ModSum > player2ModSum && win % player1ModSum == 0
                          ? (this.win - 1) / maxCycleSum
                          : this.win / maxCycleSum;

    // calulate their individual score and the total dicerolls
    int player1Score = maxCycleGames * player1ModSum;
    int player2Score = maxCycleGames * player2ModSum;
    int diceRolls = maxCycleGames * 2 * 3 * this.modulus;

    // play the rest of the game
    for (int i = 0;; i++) {
      if (player1Score >= this.win || player2Score >= this.win) {
        break;
      }

      if (i % 2 == 0) {
        player1Score += player1Additions[(i/2 % 10)]; // for i = 0, 2, 4, 6, ..
      } else {
        player2Score += player2Additions[(i/2 % 10)]; // for i = 1, 3, 5, 7, ..
      }
      diceRolls += 3;
    }

    return Result.createResult(diceRolls * Math.min(player1Score, player2Score));
  }

  // -----
  private int[] createIncrements(int base, int pairDeltaIncrements, int modulus, int start) {
    int[] baseDeltaFirst                    // [6, 4, 2, 10, 8, 6, 4, 2, 10, 8]
      = IntStream.range(0, modulus)
                 .map(i -> getModulus(base + i * pairDeltaIncrements, modulus))
                 .toArray();

    int[] baseAdditionsFirst                // [6, 10, 2, 2, 10, 6, 10, 2, 2, 10]
      = IntStream.range(0, modulus)
                 .map(i -> getModulus(IntStream.rangeClosed(0,i)
                                               .map(j -> baseDeltaFirst[j])
                                               .sum(),
                                    modulus))
                 .toArray();

    return IntStream.of(baseAdditionsFirst)  // [10, 4, 6, 6, 4, 10, 4, 6, 6, 4]
                    .map(i -> getModulus(i + start, modulus))
                    .toArray();
  }

  // -----
  private int getModulus(int value, int modulus) {
    value %= modulus;
    return value == 0 ? modulus : value;
  }

  // -----
  private int getAdditionsSum(int[] additions) {
    return IntStream.of(additions).sum();
  }

  // -----
  private Result solveBrute(String input) {
    setup(input);

    int player1Spot = this.player1start;
    int player2Spot = this.player2start;
    int player1Score = 0;
    int player2Score = 0;
    int die = 0;
    int diceRolls = 0;

    for (int i = 0;; i++) {
      if (player1Score >= this.win || player2Score >= this.win) {
        break;
      }

      int delta = ++die + ++die + ++die;
      diceRolls += 3;
      if (i % 2 == 0) {
        player1Spot = getModulus(player1Spot + delta, this.modulus);
        player1Score += player1Spot;
      } else {
        player2Spot = getModulus(player2Spot + delta, this.modulus);
        player2Score += player2Spot;
      }
    }

    return Result.createResult(diceRolls * Math.min(player1Score, player2Score));
  }


  // -----
  public Result solvePart2(String input) {
    setup(input);

    // the base map of 3 diceroll outcomes
    // index is the diceroll value & array value is the count
    int[] spawnMapArray = new int[this.modulus];
    for (int i = 1; i < 4; i++) {
      for (int j = 1; j < 4; j++) {
        for (int k = 1; k < 4; k++) {
          spawnMapArray[i+j+k] += 1;
        }
      }
    }

    // initialise with the first turn based on initial position
    // [board position][score] = count of the scores with that board position
    int[][] player1 = firstTurn(this.player1start, spawnMapArray);
    int[][] player2 = firstTurn(this.player2start, spawnMapArray);

    long otherPlayerLosses = 0;
    long player1wins = 0;
    long player2wins = 0;

    do {

      player1 = play(player1, spawnMapArray);
      player1wins += getAndResetWins(player1) * getLosses(player2);

      player2 = play(player2, spawnMapArray);
      otherPlayerLosses = getLosses(player1);
      player2wins += getAndResetWins(player2) * otherPlayerLosses;

    } while (otherPlayerLosses != 0);

    return Result.createResult(Math.max(player1wins, player2wins));
  }

  // -----
  private long getLosses(int[][] player) {
    return IntStream.range(0, player.length)
                    .map(i -> IntStream.of(player[i]).sum())
                    .sum();
  }

  // -----
  private int getAndResetWins(int[][] player) {
    return IntStream.range(0, player.length)
                    .map(i -> {
                               int count = player[i][player[i].length - 1];
                               player[i][player[i].length - 1] = 0;
                               return count;
                     })
                    .sum();
  }

  // -----
  private int[][] firstTurn(int startPosition, int[] spawnMapArray) {

                                // [board position][score counts]
    int[][] playerFirstTurn = new int[this.modulus][winPart2 + 1];

    for (int i = 0; i < playerFirstTurn.length; i++) {
      int count = spawnMapArray[i];
      int boardPos = (startPosition + i) % this.modulus;
      int newScore = getModulus(boardPos, this.modulus);
      playerFirstTurn[boardPos][newScore] = count;
    }

    return playerFirstTurn;
  }

  // -----
  private int[][] play(int[][] playerBoardScore, int[] spawnMapArray) {

    int[][] playerNewTurn = new int[this.modulus][this.winPart2 + 1];

    for (int i = 0; i < playerBoardScore.length; i++) {      // board index
      for (int j = 0; j < playerBoardScore[0].length; j++) { // score index

        int scoreCount = playerBoardScore[i][j];
        for (int k = 0; k < spawnMapArray.length; k++) {
          int count = scoreCount * spawnMapArray[k];
          int boardPos = (k + i) % this.modulus;
          int newScore = j + getModulus(boardPos, this.modulus);
          if (newScore > this.winPart2 - 1) {    // anything greater than 20
            newScore = this.winPart2;
          }
          playerNewTurn[boardPos][newScore] += count;
        }
      }
    }

    return playerNewTurn;
  }
}
