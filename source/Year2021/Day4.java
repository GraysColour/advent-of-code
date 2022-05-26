package Year2021;

/*
  The idea:
    Each board is set up with a two dimentional array, bingoNumbers:
    [[boardcolumn, boardrow],[],[]..
    The index into the array is the a bingonumber.
    The [boardcolumn, boardrow] is the placement of that number on the board.

    Each board also has an array for vertical columns and one for horizontal columns.
    When a bingonumber is drawn, its boardcoordinates are fetched from bingoNumbers
    ..and the corresponding bit in the vertical & horizontal columns is set.

    Once a horizontal or vertical column gets the value of 11111, the board wins.

    Note that it's important to play all the boards through for any bingoNumber.
    Else there's a risk that for part 2, some of the boards will have missed that draw.
*/

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

import absbase.DayX;
import util.Result;

public class Day4 extends DayX {

  private int defaultNaN = -1;                  // signal no value
  private int maxNumber = 100;                  // bingo numbers from 0 - 99
  private int boardDimension = 2;               // flat board
  private int boardSize = 5;                    // 5 x 5
  private int boardFull = (1 << boardSize) - 1; // binary: 11111, dec: 31

  private List<Board> boards;                    // boards of this game that didn't win yet
  private int[] bindgoNumberDraws;               // bingonumbers of this game
  private int bingoNumberIndexAt = 0;            // next bingonumber

  // -----
  public static void main(String args[]){
    new Day4(args).doIt();
  }

  public Day4(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input){
    // first element are the drawn numbers
    // the rest are bingo-boards
    String[] firstSplit = input.split("\\R{2}");     // empty lines between boards

    this.boards = new LinkedList<>();
    setUpBoards(this.boards, firstSplit, 1); // the first element is not a board.

    // go through the bingo numbers
    this.bindgoNumberDraws = Arrays.stream(firstSplit[0].trim().split(","))
                                   .mapToInt(str -> Integer.parseInt(str))
                                   .toArray();
  }

  // -----
  public Result solve(String input) {
    setup(input);
    return Result.createResult(play(true));  // firstWin
  }

  // -----
  private long play(boolean firstWin){
    // if the method returns without going through all the board
    // only some of the boards will have marked the current running bingoNumber.
    long result = defaultNaN;

    // go through the bingo numbers. The loop index is needed to know the next draw for part 2.
    for (int i = this.bingoNumberIndexAt;
         i < this.bindgoNumberDraws.length && result == defaultNaN;
         i++) {

      int bingoNumber = bindgoNumberDraws[i];

      // all the boards
      // since the boards will be modified during iteration,
      // a normal loop is going to cause a ConcurrentModificationException
      ListIterator<Board> iterator = this.boards.listIterator(0);
      while (iterator.hasNext()) {

        Board board = iterator.next();
        int[] x_y = board.bingoNumbers[bingoNumber];

        if (x_y[0] == defaultNaN) continue;  // number isn't on the board

        // the horizontal/vertial value is a bitmap and fills up to 11111 for a full set
        board.horzontalBoard[x_y[1]] |= 1 << x_y[0];
        board.vertialBoard[x_y[0]] |= 1 << x_y[1];

        // Keep the sum as of all unmarked bingo numbers.
        board.sum -= bingoNumber;

        // Did we win yet?
        if ((board.horzontalBoard[x_y[1]] == boardFull)
            || (board.vertialBoard[x_y[0]] == boardFull)) {
          iterator.remove(); // no longer playing this board

          // just store the result, but do not return yet!
          // make sure to not override the result of a previous board for this bingoNumber
          if ((firstWin || this.boards.size() == 0) && result == defaultNaN) { // last board
            this.bingoNumberIndexAt = i + 1;  // remember next number
            result = board.sum * bingoNumber;
          }
        }
      }
    }
    return result;
  }

  // -----
  private void setUpBoards(List<Board> boardList, String[] firstSplit, int start) {
    for (int i = start; i < firstSplit.length; i++) {     // Go through the boards
      Board newBoard = new Board(maxNumber, boardDimension, boardSize);
      String[] boardLines = firstSplit[i].split("\\R");

      for (int j = 0; j < boardLines.length; j++) {       // Go through the lines
        String[] boardElements = boardLines[j].trim().split("\\s+");

        for (int k = 0; k < boardElements.length; k++) {  // Go through the elements
          int value = Integer.parseInt(boardElements[k]);

          // set the coordinates of the board on that element
          newBoard.bingoNumbers[value][0] = k;
          newBoard.bingoNumbers[value][1] = j;

          // accumulate the sum on the board
          newBoard.sum += value;
        }
      }

      boardList.add(newBoard);
    }
  }

  // -----
  private class Board {
    int sum = 0;
    int[][] bingoNumbers;
    int[] horzontalBoard;
    int[] vertialBoard;

    Board(int maxNumber, int boardDimension, int boardSize){
      bingoNumbers = getDefaultBingoNumbers(maxNumber, boardDimension);
      horzontalBoard = new int[boardSize];
      vertialBoard = new int[boardSize];
    }
  }

  // -----
  private int[][] getDefaultBingoNumbers(int dimensionOne, int dimensionTwo){
    int[][] defaultBingoNumbers = new int[dimensionOne][dimensionTwo];
    int[] defaultContent = new int[dimensionTwo];
    Arrays.fill(defaultContent, defaultNaN);
    for (int i = 0; i < defaultBingoNumbers.length; i++) {
      defaultBingoNumbers[i] = Arrays.copyOf(defaultContent, dimensionTwo);
    }
    return defaultBingoNumbers;
  }

  // -----
  public Result solvePart2(String input) {
    if (this.bingoNumberIndexAt == 0) {
      play(true);       // firstWin
    }

    return Result.createResult(play(false)); // play until no more boards are left
  }
}
