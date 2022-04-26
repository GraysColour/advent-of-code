package base;

/**
 * The interface for the abstract class {@link absbase.DayX} in the {@link absbase} package.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */


public interface DayI {

  /**
   * Used by {@link util.Printers}.
   *
   * @return {@link base.ResultI} for the solution to part 1.
   */
  ResultI getResult();

  /**
   * Used by {@link util.Printers}.
   *
   * @return {@link base.ResultI} for the solution to part 2.
   */
  ResultI getResultPart2();


  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * <p> Sets the printer to be used.
   * <p> For example:
   * <pre>
   *     setPrinter(() -&gt; Printers.printJustResult(dayI))</pre>
   *
   * @param runnable To be run when printing is to be performed.
   */
  void setPrinter(Runnable runnable);

  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * <p> Sets the runner for the solver.
   * <p> For example:
   * <pre>
   *    setRunMe(() -&gt; dayI.runVersusAlternatives(intValue))</pre>
   *
   * @param runnable Calls and runs the solution.
   * Note that if none of the solvers are called, they will obviosly not be run.
   */
  void setRunMe(Runnable runnable);

  /**
   * Used by {@link util.CommandLineOptions}.
   *
   * <p> Responsible for the run of alternatives.
   *
   * @param iterations int specifying how many loops to run.
   */
  void runVersusAlternatives(int iterations);

  /**
   * Used by {@link test.DaysTest}.
   *
   * <p> Responsible for reading the input file and calling the solvers.
   */
  void daySolver();
}
