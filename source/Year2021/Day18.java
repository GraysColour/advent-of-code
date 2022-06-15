package Year2021;

/*
  --- Part 1 ---:
    Since each snailnumber is a pair, it's easy to parse it into a tree of nodes.
    Each node either has a left and a right child, or it's a leaf with a value.
    Any node that's not a leaf will get a fixed value of -1.

    Parsing is done using a "BitTracker" that keeps track of the current index,
    and is able to get the next character from the String input.

    All the tree traversals (called "Walker" in the code) for processing the node-tree
    uses common recursive preorder traversals.
    They first go left and then right.
    See https://en.wikipedia.org/wiki/Tree_traversal
    Note that NodeWalkers are not agnostic and each type is separately implemented.
    Mostly because the parameters on each "Walker" varies.

    The only tricky bit is finding the leftmost (& rightmost) other node.
    This is done by going:
    - To the parent
    - If starting node is the left child, then go to the parent of the parent
    - Get the left child
    - Then going right until a leaf is found

          N
          / \
        N   L <-- starting here
        / \
          L <-- Leftmost other node

    Finding the rightmost other node is similar.

    ## When to split and when to explode

      Note that level 6's are not allowed.
      As soon as a level 5 is reached on a split, it must be exploded immediately!

      Example walkthrough:

            [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]] + [7,[[[3,7],[4,3]],[[6,3],[8,8]]]]

          [ [[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]] , [7,[[[3,7],[4,3]],[[6,3],[8,8]]]] ]

        explode:
          [ [[[4,0],[5,0]],[[[4,5],[2,6]],[9,5]]] , [7,[[[3,7],[4,3]],[[6,3],[8,8]]]] ]
          [ [[[4,0],[5,4]],[[0,[7,6]],[9,5]]] , [7,[[[3,7],[4,3]],[[6,3],[8,8]]]] ]
          [ [[[4,0],[5,4]],[[7,0],[15,5]]] , [7,[[[3,7],[4,3]],[[6,3],[8,8]]]] ]
          [ [[[4,0],[5,4]],[[7,0],[15,5]]] , [10,[[0,[11,3]],[[6,3],[8,8]]]] ]
          [ [[[4,0],[5,4]],[[7,0],[15,5]]] , [10,[[11,0],[[9,3],[8,8]]]] ]
          [ [[[4,0],[5,4]],[[7,0],[15,5]]] , [10,[[11,9],[0,[11,8]]]] ]
          [ [[[4,0],[5,4]],[[7,0],[15,5]]] , [10,[[11,9],[11,0]]] ]

        split:
          [ [[[4,0],[5,4]],[[7,0],[[7,8],5]]] , [10,[[11,9],[11,0]]] ] <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[0,13]]] , [10,[[11,9],[11,0]]] ]

        re-split:
          [ [[[4,0],[5,4]],[[7,7],[0,[6,7]]]] , [10,[[11,9],[11,0]]] ] <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [17,[[11,9],[11,0]]] ]

        re-split:
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,9],[[11,9],[11,0]]] ]
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,9],[[[5,6],9],[11,0]]] ]  <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,14],[[0,15],[11,0]]] ]

        re-split:
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[0,15],[11,0]]] ]
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[0,[7,8]],[11,0]]] ]  <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,0],[19,0]]] ]

        re-split:
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,0],[[9,10],0]]] ]  <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,9],[0,10]]] ]

        re-split:
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,9],[0,[5,5]]]] ]  <-- level 5. Explode it now!
          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,9],[5,0]]] ]

          [ [[[4,0],[5,4]],[[7,7],[6,0]]] , [[8,[7,7]],[[7,9],[5,0]]] ] <-- destination


      Due to this, any current split needs to be aborted when an explode occurs.
      A new split from the beginning must take place instead.

  --- Part 2 ---:
    Since all trees contains Nodes, which are not immutable
    each calculation is done by first parsing and creating a new tree of each number
    and then reducing the "sum" of those two combined trees.
*/

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;

import absbase.DayX;
import util.Result;

public class Day18 extends DayX {

  private int notLeaf = -1;
  private String[] snailFishNumbers;

  // -----
  public static void main(String args[]){
    new Day18(args).doIt();
  }

  public Day18(String[] args) {
    super(args);
  }

  // -----
  private void setup(String input) {
    snailFishNumbers = input.split("\\R");
  }

  // -----
  public Result solve(String input) {
    if (this.snailFishNumbers == null || this.snailFishNumbers.length == 0) {
      setup(input);
    }

    List<Node> snailNumberNodeList = new ArrayList<>();

    for (String str : snailFishNumbers) {
      StringTracker tracker = new StringTracker(str);
      snailNumberNodeList.add(nodeParser(tracker, null));
    }

    Node snailNumber = snailNumberNodeList.get(0);
    for (int i = 1; i < snailNumberNodeList.size(); i++) {
      snailNumber.combineWith(snailNumberNodeList.get(i)).reduceMe();
    }

    return Result.createResult(snailNumber.magnitude());
  }

  // -----
  private long nodeWalkerMagnitude(Node node) {
    long result = 0;
    if (node.value != notLeaf) {
      return node.value;
    }
    if (node.left != null) {
      result += 3 * nodeWalkerMagnitude(node.left);
    }
    if (node.right != null) {
      result += 2 * nodeWalkerMagnitude(node.right);
    }
    return result;
  }

  // -----
  private void reduce(Node reduceMe) {
    // Overkill, but it's so easy to manage.
    AtomicBoolean abortCurrentSplit = new AtomicBoolean(false);

    boolean reduced = nodeWalkerExplode(reduceMe, 0);
    while (reduced) {
      abortCurrentSplit.set(false);
      reduced = nodeWalkerSplit(reduceMe, 0, abortCurrentSplit);
    }
  }

  // -----
  private boolean nodeWalkerSplit(Node node, int level, AtomicBoolean abort) {
    if (abort.get()) {
      return true;
    }

    boolean split = false;

    if (node.value > 9) {
      split(node, level, abort);
      if (abort.get()) {
         return true;
      }
      split = true;
    }

    if (node.left != null) {
      split |= nodeWalkerSplit(node.left, level + 1, abort);
    }
    if (node.right != null) {
      split |= nodeWalkerSplit(node.right, level + 1, abort);
    }
    return split;
  }

  // -----
  private void split(Node node, int level, AtomicBoolean abort) {
    Node splitLeft = new Node(node, node.value / 2);
    Node splitRight = new Node(node, node.value / 2 + node.value % 2);

    node.left = splitLeft;
    node.right = splitRight;
    node.value = notLeaf; // no longer a leaf

    if (level > 3) {   // must be 4 then
      explode(node);
      abort.set(true); // make sure the nodeWalker stops.
    }
  }

  // -----
  private boolean nodeWalkerExplode(Node node, int level) {
    boolean exploded = false;
    if (level > 3 && node.value == notLeaf) {
      explode(node);
      return true;
    }
    if (node.left != null) {
      exploded |= nodeWalkerExplode(node.left, level + 1);
    }
    if (node.right != null) {
      exploded |= nodeWalkerExplode(node.right, level + 1);
    }
    return exploded;
  }

  // -----
  private void explode(Node node) {
    // No null guards. Please blow up if a leaf is missing.
    explodeLeft(node, node.left.value);
    explodeRight(node, node.right.value);
    node.left = null;
    node.right = null;
    node.value = 0;
  }

  // -----
  private void explodeLeft(Node explode, int value) {
    if (explode.parent == null)
      return;

    if (explode.parent.right == explode) { // myself
      Node next = explode.parent.left;
      while (next.right != null) {
        next = next.right;
      }
      next.value += value;
    } else { // explode.parent.left == explode
      explodeLeft(explode.parent, value);
    }
  }

  // -----
  private void explodeRight(Node explode, int value) {
    if (explode.parent == null)
      return;

    if (explode.parent.left == explode) { // myself
      Node next = explode.parent.right;
      while (next.left != null) {
        next = next.left;
      }
      next.value += value;
    } else { // explode.parent.right == explode
      explodeRight(explode.parent, value);
    }
  }

  // -----
  private class Node {
    int value = notLeaf;
    Node parent;
    Node left;
    Node right;

    Node(Node parent){
      this.parent = parent;
    }
    Node(Node parent, int value){
      this.parent = parent;
      this.value = value;
    }

    Node combineWith(Node right) {
      Node node = new Node(this);

      // make the left and right of the new be this current left and right
      this.right.parent = node;
      node.right = this.right;
      this.left.parent = node;
      node.left = this.left;

      // put the new node in as a left child
      this.left = node;

      this.right = right;
      right.parent = this;
      return this;
    }

    void reduceMe() {
      reduce(this);
    }

    long magnitude() {
      return nodeWalkerMagnitude(this);
    }

    public String toString() {
      return (value == notLeaf ? "node: " : "value: " + value);
    }
  }

  // -----
  private Node nodeParser(StringTracker tracker, Node parent) {
    Node node = new Node(parent);

    while (tracker.hasNext()) {
      String item = tracker.next();
      switch (item) {
        case "[" : node.left = nodeParser(tracker, node);
                   break;
        case "," : node.right = nodeParser(tracker, node);
                   break;
        case "]" : return node;
        default  : return new Node(parent, Integer.parseInt(item));
      }
    }
    return node;
  }

  // -----
  private class StringTracker {
    private String snailnumber;
    private int charAt;
    private int max;

    StringTracker(String snailnumber) {
      this.snailnumber = snailnumber;
      this.charAt = 0;
      this.max = snailnumber.length() + 1;
    }

    boolean hasNext() {
      return max > charAt + 1;
    }

    String next() {
      checkBound(1);
      this.charAt++;
      return snailnumber.substring(charAt - 1, charAt);
    }

    void checkBound(int amount) {
      if (this.charAt + amount > max) {
        throw new IndexOutOfBoundsException("charAt " + this.charAt + " exceeds " + max);
      }
    }
  }

  // -----
  public Result solvePart2(String input) {
    if (this.snailFishNumbers == null || this.snailFishNumbers.length == 0) {
      setup(input);
    }

    // Nodes are not immutatable, so they need to be recreated.
    return Result.createResult(
             IntStream
               .range(0, snailFishNumbers.length)
               .mapToObj(i -> IntStream
                                  // do NOT add a number to itself.
                                 .range(i + 1, snailFishNumbers.length)
                                 .mapToLong(j -> Math.max(magnitudeOfTwo(snailFishNumbers[i],
                                                                         snailFishNumbers[j]),
                                                          magnitudeOfTwo(snailFishNumbers[j],
                                                                         snailFishNumbers[i]))))
               .flatMapToLong(s -> s)
               .max()
               .orElseThrow());
  }

  private long magnitudeOfTwo(String snailFishNumber1, String snailFishNumber2) {
    StringTracker tracker1 = new StringTracker(snailFishNumber1);
    StringTracker tracker2 = new StringTracker(snailFishNumber2);

    Node snailNumber1 = nodeParser(tracker1, null);
    Node snailNumber2 = nodeParser(tracker2, null);

    snailNumber1.combineWith(snailNumber2).reduceMe();
    return snailNumber1.magnitude();
  }
}
