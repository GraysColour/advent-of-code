package util;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ConvertAscII {

  private ConvertAscII() {}

  public static String findLetters(String[] ascII) {
    StringBuilder letters = new StringBuilder();
    int stringPointer = 0;

    while (stringPointer < ascII[0].length()) {
      // look for the next space 3 character from the current
      int nextSpace = getNextSpace(stringPointer + 3, ascII);

      // Local variable stringPointer defined in an enclosing
      // scope must be final or effectively final.. blah blah blah..
      int finalstringPointer = stringPointer;
      int finalNextSpace     = nextSpace;
      String[] nextCharacter
        = Arrays.stream(ascII)
                .map(str -> str.substring(finalstringPointer, finalNextSpace))
                .toArray(String[]::new);

      String letter = letterMap.get(nextCharacter);
      // because StringBuilder.append() will append the
      // litteral string "null" if the parameter is null :O !!!
      if (letter != null) {
        letters.append(letter);
      }

      // move past the line of spaces
      stringPointer = nextSpace + 1;
    }

    return letters.toString();
  }

  private static int getNextSpace(int fromIndex, String[] ascII) {
    int nextSpace = fromIndex - 1;
    boolean consistent = false;

    while (!consistent && nextSpace < ascII[0].length()) {
      consistent = true;
      nextSpace = ascII[0].indexOf(" ", nextSpace + 1); // do not look at current line

      if (nextSpace == -1) {      // there are no more spaces
        return ascII[0].length();
      }

      for (String str : ascII) {
        if (str.charAt(nextSpace) != ' ') {
          consistent = false;
          break;
        }
      }
    }

    return nextSpace;
  }


  private static String[] A
    = new String[]{" ## ",
                   "#  #",
                   "#  #",
                   "####",
                   "#  #",
                   "#  #"};

  private static String[] B
    = new String[]{"### ",
                   "#  #",
                   "### ",
                   "#  #",
                   "#  #",
                   "### "};

  private static String[] C
    = new String[]{" ## ",
                   "#  #",
                   "#   ",
                   "#   ",
                   "#  #",
                   " ## "};

  private static String[] E
    = new String[]{"####",
                   "#   ",
                   "### ",
                   "#   ",
                   "#   ",
                   "####"};

  private static String[] F
    = new String[]{"####",
                   "#   ",
                   "### ",
                   "#   ",
                   "#   ",
                   "#   "};

  private static String[] G
    = new String[]{" ## ",
                   "#  #",
                   "#   ",
                   "# ##",
                   "#  #",
                   " ###"};

  private static String[] H
    = new String[]{"#  #",
                   "#  #",
                   "####",
                   "#  #",
                   "#  #",
                   "#  #"};

  private static String[] I
    = new String[]{"###",
                   " # ",
                   " #",
                   " # ",
                   " # ",
                   "###"};

  private static String[] J
    = new String[]{"  ##",
                   "   #",
                   "   #",
                   "   #",
                   "#  #",
                   " ## "};

  private static String[] K
    = new String[]{"#  #",
                   "# # ",
                   "##  ",
                   "# # ",
                   "# # ",
                   "#  #"};

  private static String[] L
    = new String[]{"#   ",
                   "#   ",
                   "#   ",
                   "#   ",
                   "#   ",
                   "####"};

  private static String[] P
    = new String[]{"### ",
                   "#  #",
                   "#  #",
                   "### ",
                   "#   ",
                   "#   "};

  private static String[] R
    = new String[]{"### ",
                   "#  #",
                   "#  #",
                   "### ",
                   "# # ",
                   "#  #"};

  private static String[] U
    = new String[]{"#  #",
                   "#  #",
                   "#  #",
                   "#  #",
                   "#  #",
                   " ## "};

  private static String[] Z
    = new String[]{"####",
                   "   #",
                   "  # ",
                   " #  ",
                   "#   ",
                   "####"};


  private static Map<String[], String> letterMap = new TreeMap<>(Arrays::compare)
  {{ // double curlies are first subclassing
     // and then an instance initialization block

      put(A, "A");
      put(B, "B");
      put(C, "C");

      put(E, "E");
      put(F, "F");
      put(G, "G");
      put(H, "H");
      put(I, "I");
      put(J, "J");
      put(K, "K");
      put(L, "L");

      put(P, "P");

      put(R, "R");

      put(U, "U");

      put(Z, "Z");
  }};

}
