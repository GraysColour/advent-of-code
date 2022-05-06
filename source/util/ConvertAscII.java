package util;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * Converts an ascII {@link String} array to a {@link String} of letters.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class ConvertAscII {

  /**
   * @hidden
   */
  private ConvertAscII() {}

  /**
   * Converts an ascII {@link String} array to a {@link String} of letters.
   *
   * <p> Finds vertical lines of spaces in the ascII {@link String}
   * array to separate individual letters assuming
   * no letter can be less than 3 characters wide.
   *
   * <p> Using the indeces of the surrounding vertical lines of spaces,
   * starting from index 0, it creates a {@link String} array of
   * every suspected letter. This is then used as a map look-up.
   *
   * <p> If the map contains the isolated ascII letter, the associated
   * letter has been matched and is added to a return {@link String}.
   *
   * @param ascII {@link String} array of ascII "art" letters
   * @return a {@link String} of found letters or
   * an empty {@link String} if none can be found.
   */
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

  /**
   * Finds a line of spaces.
   *
   * <p> Starts with the specified <code>fromIndex</code> and finds the next space in
   * the first {@link String} in the ascII array. Then checks if there is
   * a space in the other Strings in the ascII array at the same index.
   *
   * <p> For example at index 4, there is such a line of spaces:
   * <pre> ##  ####
   *#  #    #
   *#      #
   *#     #
   *#  # #
   * ##  ####
   *    ^ index 4</pre>
   *
   * <p> If there isn't a space at the found index in all the Strings in
   * the ascII array, it loops starting from the previous found "space" index.
   *
   * <p> In the above example, there is a "space" index
   * at 0 and 3, but none of those have a line of spaces.
   *
   * <p> Returns the first index where there is a line of spaces.
   * If there's none to be found the index past the end will be returned.
   *
   *
   * @param fromIndex the first index to check for a line of spaces
   * @param ascII {@link String} array of ascII "art" letters
   * @return the index at the next lines of spaces or the length of the array
   */
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


  /**
   * {@link String} array of the letter A
   */
  private static String[] A
    = new String[]{" ## ",
                   "#  #",
                   "#  #",
                   "####",
                   "#  #",
                   "#  #"};

  /**
   * {@link String} array of the letter B
   */
  private static String[] B
    = new String[]{"### ",
                   "#  #",
                   "### ",
                   "#  #",
                   "#  #",
                   "### "};

  /**
   * {@link String} array of the letter C
   */
  private static String[] C
    = new String[]{" ## ",
                   "#  #",
                   "#   ",
                   "#   ",
                   "#  #",
                   " ## "};

  /**
   * {@link String} array of the letter E
   */
  private static String[] E
    = new String[]{"####",
                   "#   ",
                   "### ",
                   "#   ",
                   "#   ",
                   "####"};

  /**
   * {@link String} array of the letter F
   */
  private static String[] F
    = new String[]{"####",
                   "#   ",
                   "### ",
                   "#   ",
                   "#   ",
                   "#   "};

  /**
   * {@link String} array of the letter G
   */
  private static String[] G
    = new String[]{" ## ",
                   "#  #",
                   "#   ",
                   "# ##",
                   "#  #",
                   " ###"};

  /**
   * {@link String} array of the letter H
   */
  private static String[] H
    = new String[]{"#  #",
                   "#  #",
                   "####",
                   "#  #",
                   "#  #",
                   "#  #"};

  /**
   * {@link String} array of the letter I
   *
   * <p><i> Note: This letter may not be accurate</i>
   */
  private static String[] I
    = new String[]{"###",
                   " # ",
                   " #",
                   " # ",
                   " # ",
                   "###"};

  /**
   * {@link String} array of the letter J
   */
  private static String[] J
    = new String[]{"  ##",
                   "   #",
                   "   #",
                   "   #",
                   "#  #",
                   " ## "};

  /**
   * {@link String} array of the letter K
   */
  private static String[] K
    = new String[]{"#  #",
                   "# # ",
                   "##  ",
                   "# # ",
                   "# # ",
                   "#  #"};

  /**
   * {@link String} array of the letter L
   */
  private static String[] L
    = new String[]{"#   ",
                   "#   ",
                   "#   ",
                   "#   ",
                   "#   ",
                   "####"};

  /**
   * {@link String} array of the letter P
   */
  private static String[] P
    = new String[]{"### ",
                   "#  #",
                   "#  #",
                   "### ",
                   "#   ",
                   "#   "};

  /**
   * {@link String} array of the letter R
   */
  private static String[] R
    = new String[]{"### ",
                   "#  #",
                   "#  #",
                   "### ",
                   "# # ",
                   "#  #"};

  /**
   * {@link String} array of the letter U
   */
  private static String[] U
    = new String[]{"#  #",
                   "#  #",
                   "#  #",
                   "#  #",
                   "#  #",
                   " ## "};

  /**
   * {@link String} array of the letter Z
   */
  private static String[] Z
    = new String[]{"####",
                   "   #",
                   "  # ",
                   " #  ",
                   "#   ",
                   "####"};


  /**
   * Maps {@link String} arrays of letters to their corresponding {@link String} letter.
   */
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
