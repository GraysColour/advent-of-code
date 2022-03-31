package absbase;

import java.util.Map;
import java.util.List;
import java.util.function.Function;

import base.*;
import util.*;

public abstract class DayX implements DayI {

  protected FileName fileName;
  protected String input;

  protected ResultI result;
  protected ResultI resultPart2;

  protected Map<String, Function<String, Result>> alternatives  = Map.of();
  protected Map<String, Function<String, Result>> alternatives2 = Map.of();

  protected Runnable runMe   = () -> solveAndPrint();  // default action
  protected List<Runnable> printers = List.of(() -> Printers.printResultTime(this));


  // ----- constructor!
  public DayX(String[] args) {
    this.fileName = CommandLineOptions.handleOptions(args, this).build();
  }


  public void doIt() {
    this.runMe.run();
  }

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
  public void solveAndPrint() {
    if (this.result == null) {
      daySolver();
    }
    printers.forEach(runnable -> runnable.run());
  }


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
  public void setRunMe(Runnable runnable) {
    this.runMe = runnable;
  }

  public void setPrinter(Runnable runnable) {
    this.printers = List.of(runnable);
  }

  public void setInput(String input) {
    this.input = input;
  }


  // ----- Getters
  public ResultI getResult() {
    return this.result;
  }

  public ResultI getResultPart2() {
    return this.resultPart2;
  }


  public abstract Result solve(String input);

  public Result solvePart2(String input) {
    return Result.createDummyResult();
  }

}
