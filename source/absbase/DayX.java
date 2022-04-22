package absbase;

import java.util.Map;
import java.util.List;
import java.util.function.Function;

import base.*;
import util.*;

/**
 * Abstract class that must be extended by all Day implementations.
 *
 * <p> See <a href="../index.html">Overview</a> for how to implement one.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public abstract class DayX implements DayI {

  /**
   * {@link util.FileName} with a {@link String} path to the input.
   */
  protected FileName fileName;

  /**
   * The actual {@link String} data input.
   */
  protected String input;


  /**
   * {@link base.ResultI} for part 1.
   */
  protected ResultI result;

  /**
   * {@link base.ResultI} for part 2.
   */
  protected ResultI resultPart2;


  /**
   * Alternative solvers for part 1.
   *
   * <p>Default set to an empty map: {@link java.util.Map#of()}
   *
   * <p> To enable running alternatives, set the map in the constructor of an implementing class.
   * For example:
   *
   * <pre>
   *    this.alternatives =
   *      Map.of("Fast", (i) -&gt; solvePart1Fast(i)},
   *             "Slow", (i) -&gt; solvePart1Slow(i));</pre>
   *
   * The map key is used to identify the alternatives in the printout.
   */
  protected Map<String, Function<String, Result>> alternatives  = Map.of();

  /**
   * Alternative solvers for part 2.
   *
   * <p> This is identical to {@link #alternatives}, but for part 2.
   */
  protected Map<String, Function<String, Result>> alternatives2 = Map.of();


  /**
   * Calls the solvers and prints their results.
   *
   * <p> The {@link Runnable} must both call the solvers and print their
   * results using the instance varible {@link #printers}.
   *
   * <p>Default set to: <code>() -&gt; solveAndPrint();</code>
   */
  protected Runnable runMe   = () -> solveAndPrint();  // default action

  /**
   * {@link java.util.List} of runnables that prints the results.
   *
   * <p>Default set to: <code>List.of(() -&gt; Printers.printResultTime(this));</code>
   */
  protected List<Runnable> printers = List.of(() -> Printers.printResultTime(this));


  // ----- constructor!
  /**
   * Set the {@link #fileName} and handles command line arguments.
   *
   * <p> Implementing classes must call
   * <pre>    super(args)</pre>
   *
   * with a {@link String} array of arguments. The array may be empty.
   *
   * @param args {@link String} array of arguments for the Day implementation
   */
  public DayX(String[] args) {
    this.fileName = CommandLineOptions.handleOptions(args, this).build();
  }


  /**
   * Call the runnable in {@link #runMe}
   *
   * <p> This method runs the Day implementation and prints the results.
   */
  public void doIt() {
    this.runMe.run();
  }


  /**
   * Responsible for reading the input file and calling the solvers.
   *
   * <p> Uses
   * <ul>
   *  <li> {@link util.DataReader#readFile(String, Consumer) DataReader.readFile(String, Consumer&lt;String&gt;)} to read the input</li>
   *  <li> {@link util.Timers#timeItResult Timers.timeItResult(Function&lt;T,Result&gt;, T)} to time the runs</li>
   * </ul>
   */
  public void daySolver() {
    // String input = DataReader.readFile(this.file);
    State fileStatus = DataReader.readFile(this.fileName.getfileName(),
                                           (in) -> this.setInput(in));

    if (Status.FILE_OK.equals(fileStatus.getStatus())) {

      this.result = Timers.timeItResult((in) -> solve(in), this.input);
      this.resultPart2 = Timers.timeItResult((in) -> solvePart2(in), this.input);

    } else {
      this.result = Result.createFileErrorResult(fileStatus);
    }
  }

  // -----
  /**
   * Responsible for calling the solvers and printing the result.
   *
   * <p> Only when {@link #result} isn't already set will it run the solvers.
   * Always runs the {@link #printers}.
   */
  public void solveAndPrint() {
    if (this.result == null) {
      daySolver();
    }
    printers.forEach(runnable -> runnable.run());
  }

  /**
   * Responsible for the run of alternatives.
   *
   * <p> Alternatives must be given by the instance variables
   * {@link #alternatives} for part 1 solutions and
   * {@link #alternatives2} for part 2 solutions.
   * <p> If the instance variables are empty,
   * no alternatives are run and a message is printed to the console:
   *
   * <pre>
   *     "No alternatives defined"</pre>
   *
   * @param iterations int specifying how many loops to run.
   */
  public void runVersusAlternatives(int iterations) {
    if (alternatives.isEmpty() && alternatives2.isEmpty()) {
      System.out.println("No alternatives defined");
      return;
    }

    State fileStatus = DataReader.readFile(this.fileName.getfileName(),
                                           (in) -> this.setInput(in));

    if (!Status.FILE_OK.equals(fileStatus.getStatus())) {
      this.result = Result.createFileErrorResult(fileStatus);
      Printers.printError(this, false, true);
      return;
    }

    for (int k = 0; k < iterations; k++) {
      for (Map.Entry<String, Function<String, Result>> entry : this.alternatives.entrySet()) {
        this.result = Timers.timeItResult(entry.getValue(), this.input);
        Printers.printResult(entry.getKey(), true, true, this);
      }
      this.result = null; // reset it to not include the last one in prints for Part2.

      for (Map.Entry<String, Function<String, Result>> entry : this.alternatives2.entrySet()) {
        this.resultPart2 = Timers.timeItResult(entry.getValue(), this.input);
        Printers.printResult(entry.getKey(), true, true, this);
      }
      this.resultPart2 = null; // reset it to not include the last one in prints for Part1.
    }
  }


  // ----- Setters
  /**
   * Sets the {@link #runMe} instance variable
   *
   * <p>Usage:
   *
   * <pre>
   *     dayI.setRunMe(() -&gt; dayI.runVersusAlternatives(int))</pre>
   *
   * where dayI is an subclass of {@link DayX}
   *
   * @param runnable that sets the {@link #runMe} instance variable.
   */
  public void setRunMe(Runnable runnable) {
    this.runMe = runnable;
  }

  /**
   * Sets the {@link #printers} instance variable.
   *
   * <p>Usage:
   *
   * <pre>
   *     dayI.setPrinter(() -&gt; Printers.printJustResult(dayI))</pre>
   *
   * where dayI is an subclass of {@link DayX}
   *
   * <p>Note that the {@link #printers} variable is a {@link java.util.List}.
   * Calling this method will set the list to just
   * this Runnable input using <code>List.of(runnable)</code>.
   *
   * <p>To add an ascII printout, add the following to the constructor of an implementing class
   *
   * <pre>
   *     this.printers = new ArrayList&lt;&gt;(printers);
   *     printers.add(() -&gt; Printers.printAscIIResult(this));</pre>
   *
   * @param runnable that sets the {@link #printers} instance variable.
   */
  public void setPrinter(Runnable runnable) {
    this.printers = List.of(runnable);
  }

  /**
   * Sets the {@link #input} instance variable
   *
   * <p> Used when calling
   * {@link util.DataReader#readFile(String, Consumer) DataReader.readFile(String, Consumer&lt;String&gt;)}.
   *
   * @param input that is to be run against the solution.
   */
  public void setInput(String input) {
    this.input = input;
  }


  // ----- Getters
  /**
   * Gets the result for part 1.
   * @return {@link base.ResultI} for part 1.
   */
  public ResultI getResult() {
    return this.result;
  }

  /**
   * Gets the result for part 2.
   * @return {@link base.ResultI} for part 2.
   */
  public ResultI getResultPart2() {
    return this.resultPart2;
  }


  /**
   * Solution to part 1.
   *
   * <p> This method must implement a solution.
   *
   * @param input the entire input text for the puzzle.
   * @return {@link util.Result} for part 1.
   */
  public abstract Result solve(String input);

  /**
   * Solution to part 2.
   *
   * <p> <b>This method is optional.</b>
   *
   * <p> It defaults to {@link util.Result#createDummyResult()}
   * that has a status of {@link base.Status#NO_RESULT}
   *
   * @param input the entire input text for the puzzle.
   * @return {@link util.Result} for part 1.
   */
  public Result solvePart2(String input) {
    return Result.createDummyResult();
  }

}
