package Year2021;

/*
  The idea:
    Start with a path at "start".
    For each cave connected to "start", keep only
    newly create paths, each with that connecting cave added to "start".
    For each of those paths, repeat keeping only new paths
    adding connecting caves of the last visited cave on the path.
    Once "end" is reached, a path is complete.
    If there are no legal connections from a path, drop it.
    Legal connections are: Small caves not already in the path and big caves.

  More implementation specific:
    Setting up the graph is done with a map of caves (keys)
    and their direct connecting caves as a set (values).

    "start" can only be a key. Not a connection.
    That ensures that start cannot be revisited.
    "end" can only be a connection. Not a key.
    That ensures that a path to "end" must stop there.

    --- Part 1 ---:
      A class Path is used to hold a set of visited nodes.
      The path has a method visit(cave):
        - The method adds the cave to a instance variable set and return true if
          - The cave is not already in the set.
          - The cave is big.
        - If the cave is already in the set and it's a small cave
          the method returns false.
      It also keeps a record of the last visited cave in the path.

      Using a queue, a Path only containing "start" is added.
      Looping while polling Paths from the queue:
        - The connections of the last visited cave of the Path is found from the map.
        - For each connection:
          - If a connection contains "end", a counter is incremented.
            and the rest of the loop is skipped.
          - The Path is copied and if visit(cave) is true,
            The copied Path is added to the queue.
      This way every option is explored and "impossible" paths are just dropped.

    --- Part 2 ---:
      This is almost identical to part 1.
      The only exception is that Path holds a "twice allowed",
      which enables a repeating small cave just once.
*/

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.function.BiConsumer;

import absbase.DayX;
import util.Result;

public class Day12 extends DayX {

  private Map<String, Set<String>> graph = new HashMap<>();  // the input graph
  private String start = "start";
  private String end = "end";
  private int part = 1;

  private BiConsumer<String, String> simpleAddToGraph
    = (first, second) -> addBothToGraph(first, second);
  private BiConsumer<String, String> toGraphAlternative
    = (first, second) -> addBothToGraphAlternative(first, second);

  // -----
  public static void main(String[] args){
    new Day12(args).doIt();
  }

  public Day12(String[] args) {
    super(args);

    this.alternatives =
      Map.of(
             "Setup",          (i) -> {setup(i, simpleAddToGraph);   return solve(i);},
             "SetupAlt",       (i) -> {setup(i, toGraphAlternative); return solve(i);}
            );
  }

  // -----
  private void setup(String input, BiConsumer<String, String> intoGraph) {
    input.lines()
         .forEach(line -> {
                   String[] connection = line.split("-");
                   intoGraph.accept(connection[0], connection[1]);
          });
  }

  // -----
  private void addBothToGraph(String first, String second) {
     addToGraph(this.graph, first, second);
     addToGraph(this.graph, second, first);
  }

  private void addToGraph(Map<String, Set<String>> graph, String key, String value) {
    if (this.start.equals(value)) {
      return;
    }

    Set<String> oldValue = graph.get(key);
    if (oldValue == null) {
      HashSet<String> newSet = new HashSet<>();
      if (!this.end.equals(key)) {
        newSet.add(value);
      }
      graph.put(key, newSet);
    } else {
      if (!this.end.equals(key)) {
        oldValue.add(value);
      }
    }
  }

  // -----
  private void addBothToGraphAlternative(String first, String second) {
     addToGraphAlternative(this.graph, first, second);
     addToGraphAlternative(this.graph, second, first);
  }

  private void addToGraphAlternative(Map<String, Set<String>> graph, String key, String value) {
    if (this.start.equals(value)) {
      return;
    }

    graph.compute(key,
                  (k, v) -> {
                    if (v == null) {
                      HashSet<String> newSet = new HashSet<>();
                      if (!this.end.equals(key)) {
                        newSet.add(value);
                      }
                      return newSet;
                    } else {
                      if (!this.end.equals(key)) {
                        v.add(value);
                      }
                      return v;
                    }
                  }
                );
  }

  // -----
  public Result solve(String input) {
    if (this.graph == null || this.graph.isEmpty()) {
      setup(input, simpleAddToGraph);
    }

    LinkedList<Path> queue = new LinkedList<>();
    int counter = 0;

    queue.add(new Path(start, this.part == 1 ? false : true));

    Path path = null;
    while ((path = queue.poll()) != null) {
      String last = path.last;
      for (String connection : this.graph.get(last)) {
        if (end.equals(connection)) {
          counter++;
          continue;
        }
        Path copy = path.clone();
        if (copy.visit(connection)) {
          queue.add(copy);
        }
      }
    }

    return Result.createResult(counter);
  }

  // -----
  public Result solvePart2(String input) {
    if (this.graph == null || this.graph.isEmpty()) {
      setup(input, simpleAddToGraph);
    }
    this.part = 2;
    Result result = solve(input);
    this.part = 1; // cleanup;
    return result;
  }

  // -----
  private class Path {
    Set<String> visited = new HashSet<>();
    String last;
    boolean twiceAllowed = false;

    // --
    Path(String first, boolean twiceAllowed) {
      this.last = first;
      this.twiceAllowed = twiceAllowed;
    }
    Path(Set<String> visited, String last, boolean twiceAllowed) {
      this.visited = visited;
      this.last = last;
      this.twiceAllowed = twiceAllowed;
    }

    // --
    boolean visit(String node) {
      // Set.add() returns false if it already has the element
      if (this.visited.add(node)) {
        this.last = node;
        return true;
      }

      if (node.toLowerCase().equals(node)) {
        if (!this.twiceAllowed) { // second time a lower case gets added
          return false;
        } else {
          this.twiceAllowed = false;
        }
      }
      this.last = node;
      return true;
    }

    // --
    public Path clone() {
      return new Path(new HashSet<>(this.visited),
                      this.last,
                      this.twiceAllowed);
    }
  }

}
