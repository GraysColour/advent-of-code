package Year2021;

/*
  --- Part 1 ---:
    Once the hexadecimal is converted into 0s and 1s, it seems
    that parsing the string into a syntax tree is the obvious choice.

    The tricky part is keeping track of the input string.
    One option is to delete the bits from the string as they are being parsed.
    Another option is to keep a pointer to the current index on the string.
    Each option can be implemented using a class, BitTracker, that has a method next().
    next(n) will return any number, n, of next characters from the string.
    The actual implementation can then be abstracted away.
    This implementation uses the option that keeps a pointer.

    Since packets can contain both Literals and Operators, that share
    having a type and a version, they can both be subclassing a Token.
    Having a common type makes is easy to traverse the parse tree.

    Parsing the string starts with calling getToken(),
    that parses the first 6 bits of the string as type and version.
    Based on the type, it will either create:
      - a Literal
      - an Operator that contains a list of Tokens.
    Then it returns the token.

    ## Literals
      Literals are created calling the Literal constructor with the BitTracker.
      It will keep parsing bits until it meets the 0XXXX indicating the end of its value.

    ## Operators
      Operators are created similarly, but uses an intermidiary getOperator() method.
      It creates a new Operator and then adds Tokens to its list using operator.add(token).
      It distinguish between lenghtTypes:
        - If the lenghtype is to include X other tokens,
          then it loops while adding X tokens to the operator's list.
          These tokens are created by calling getToken().
        - If the lenghtype is to include the next Y bits,
          then it loops to add tokens also returned by getToken(),
          but using a cloned BitTracker that stops when the bits have been reached.
      getOperator() returns the newly created Operator with its list of Tokens.

      Since this is effectively recursive: getToken -> getOperator -> getToken
      the first Token created will be the root of an entire parse tree.

    Now that it's a tree of tokens, getting the sum of versions it just:
    Start at the root:
      - take the version of the current token.
      - recursive add the sum of the tokens in an operator's list.

  --- Part 2 ---:
    This is basically interpreting the tree as a syntax tree.
    Using preorder traversal, first finding the type of a node and
    then applying the operation on its list of tokens (its children).
    See https://en.wikipedia.org/wiki/Tree_traversal
*/

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import absbase.DayX;
import util.Result;

public class Day16 extends DayX {

  private int minimumTokenBits = 11;
  private Token topToken;

  // -----
  public static void main(String args[]){
    new Day16(args).doIt();
  }

  public Day16(String[] args) {
    super(args);
  }

  // -----
  public Result solve(String input) {
    String binaryString = hexToBin(input);

    BitTracker bitTracker = new BitTracker(binaryString, 0, 0);
    List<Token> tokenList = new ArrayList<>(1); // in case there are more than one "program"

    while (bitTracker.hasNext(minimumTokenBits)) {
      // this basically creates a parseTree of the input.
      tokenList.add(getToken(bitTracker));
    }
    this.topToken = tokenList.get(0);

    return Result.createResult(tokenWalkerVersionSum(this.topToken));
  }

  // -----
  private int intFromBit(String bits) {
    return Integer.parseInt(bits, 2);
  }

  // -----
  private long longFromBit(String bits) {
    return Long.parseLong(bits, 2);
  }

  // -----
  private int tokenWalkerVersionSum(Token token) {
    int versionSum = token.getVersion();
    if (token.getType() != 4) {
      versionSum += token.getKids()
                         .stream()
                         .map(kid -> tokenWalkerVersionSum(kid))
                         .reduce(0, (kid1, kid2) -> kid1 + kid2);
    }
    return versionSum;
  }

  // -----
  private Token getToken(BitTracker bitTracker) {
    int version = intFromBit(bitTracker.next(3));
    int type = intFromBit(bitTracker.next(3));

    if (type == 4) {
      return new Literal(version, type, bitTracker);
    } else {
      return getOperator(version, type, bitTracker);
    }
  }

  // -----
  private Token getOperator(int version, int type, BitTracker bitTracker) {
    int lengthType = intFromBit(bitTracker.next());
    Operator operator = null;

    if (lengthType == 0) {

      int length = intFromBit(bitTracker.next(15));
      operator = new Operator(version, type, lengthType, length, -1);

      BitTracker subsetBitTracker = bitTracker.clone(length);
      while (subsetBitTracker.hasNext(minimumTokenBits)) {
        Token token = getToken(subsetBitTracker);
        operator.add(token);
      }

    } else { // lengthType == 1

      int subPackets = intFromBit(bitTracker.next(11));
      operator = new Operator(version, type, lengthType, -1, subPackets);
      for (int i = 0; i < subPackets; i++) {
        operator.add(getToken(bitTracker));
      }

    }

    return operator;
  }

  // -----
  private abstract class Token {
    String tokenType;
    int version;
    int type;

    Token(int version, int type) {
      this.version = version;
      this.type = type;
    }

    int getVersion() {
      return this.version;
    }
    int getType() {
      return this.type;
    }

    List<Token> getKids() {
      return List.of();
    }

    @Override
    public String toString() {
      return tokenType + " version: " + version + ", type: " + type + ": " + getStringType(type);
    }
  }

  // -----
  private String getStringType(int type){
    switch (type) {
      case 0: return "-->> sum <<--";
      case 1: return "-->> product <<--";
      case 2: return "-->> min <<--";
      case 3: return "-->> max <<--";
      case 4: return "-->> literal <<--";
      case 5: return "-->> greater than > <<--";
      case 6: return "-->> less than < <<--";
      case 7: return "-->> equals == <<--";
      default: return "-->> NO TYPE!!! <<--";
    }
  }

  // -----
  private class Operator extends Token {
    int lengthType;
    int length;
    int subPackets;
    List<Token> kids = new ArrayList<>();

    Operator(int version, int type, int lengthType, int length, int subPackets) {
      super(version, type);
      this.lengthType = lengthType;
      this.length = length;
      this.subPackets = subPackets;
      this.tokenType = "Operator";
    }

    void add(Token token) {
      kids.add(token);
    }

    @Override
    List<Token> getKids() {
      return kids;
    }

    @Override
    public String toString() {
      return super.toString() +
               ", lengthType: " + lengthType +
               (lengthType == 0 ? ", length: " + length : ", subPackets: " + subPackets) +
               ", kids: " + kids.size();
    }
  }

  // -----
  private class Literal extends Token {
    String bitString;
    long value;

    Literal(int version, int type, BitTracker bitTracker) {
      super(version, type);
      fetchValue(bitTracker);
      this.tokenType = "Literal";
    }

    private void fetchValue(BitTracker bitTracker) {
      String bits = "";
      int continueBit = -1;
      do {
        continueBit = intFromBit(bitTracker.next());
        bits += bitTracker.next(4);
      } while (continueBit == 1);

      this.bitString = bits;
      this.value = longFromBit(bitString);
    }

    long getValue() {
      return this.value;
    }

    @Override
    public String toString() {
      return super.toString() + ", value: " + value;
    }
  }

  // -----
  private class BitTracker {
    private String bits;
    private int bitAt;
    private int max;

    BitTracker(String bits, int bitAt, int max) {
      this.bits = bits;
      this.bitAt = bitAt;
      this.max = max == 0 ? bits.length() : max;
    }

    boolean hasNext(int amount) {
      return max > bitAt + amount;
    }

    String next() {
      checkBound(1);
      this.bitAt++;
      return bits.substring(bitAt - 1, bitAt);
    }
    String next(int amount) {
      checkBound(amount);
      this.bitAt += amount;
      return bits.substring(bitAt - amount, bitAt);
    }

    void checkBound(int amount) {
      if (this.bitAt + amount > max) {
        throw new IndexOutOfBoundsException("bitAt " + this.bitAt + " exceeds " + max);
      }
    }

    private BitTracker clone(int offsetLimit) {
      // max is the length, which is 1 more than the index range.
      BitTracker subsetTracker =
        new BitTracker(this.bits,
                       this.bitAt,
                       Math.min(this.bitAt + offsetLimit + 1, this.max));

      // offset this tracker, so the same bit isn't read twice.
      this.bitAt = bitAt + offsetLimit;

      return subsetTracker;
    }
  }

  // -----
  private String hexToBin(String hex){
    return hex.chars()
              .mapToObj(c -> {
                          // 3 will go into 11 losing it's left 0-padding.
                          // Adding 16 will make it 10011 and the leading 1 can then be removed.
                          int value = Integer.parseInt(String.valueOf((char) c), 16) | 16;
                          String valueString = Integer.toBinaryString(value);
                          return valueString.substring(1);
               })
              .collect(Collectors.joining(""));
  }

  // -----
  public Result solvePart2(String input) {
    if (this.topToken == null) {
      solve(input);
    }

    return Result.createResult(tokenInterpreter(this.topToken));
  }

  // -----
  private long tokenInterpreter(Token token) {
    int type = token.getType();
    switch (type) {
      case 0: return token.getKids()
                          .stream()
                          .mapToLong(kid -> tokenInterpreter(kid))
                          .sum();
      case 1: return token.getKids()
                          .stream()
                          .mapToLong(kid -> tokenInterpreter(kid))
                          .reduce(1L, (kid1, kid2) -> kid1 * kid2);
      case 2: return token.getKids()
                          .stream()
                          .mapToLong(kid -> tokenInterpreter(kid))
                          .min()
                          .orElseThrow();
      case 3: return token.getKids()
                          .stream()
                          .mapToLong(kid -> tokenInterpreter(kid))
                          .max()
                          .orElseThrow();
      case 4: return ((Literal) token).getValue();  // the long value of the leaf
      case 5: return tokenInterpreter(token.getKids().get(0))
                       > tokenInterpreter(token.getKids().get(1)) ? 1L : 0L;
      case 6: return tokenInterpreter(token.getKids().get(0))
                       < tokenInterpreter(token.getKids().get(1)) ? 1L : 0L;
      case 7: return tokenInterpreter(token.getKids().get(0))
                       == tokenInterpreter(token.getKids().get(1)) ? 1L : 0L;
      default: return 0L;
    }
  }
}
