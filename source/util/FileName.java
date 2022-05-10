package util;

import base.DayI;

/**
 * Handles the path to an input file for a {@link base.DayI}.
 *
 * <p> The file name is kept in a {@link String} {@link #fullFilename},
 * which isn't necessarily an absolute path, but can be a relative path to an input file.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class FileName {

  /**
   * Absolute or relative {@link String} path to an input file for a {@link base.DayI}.
   */
  private final String fullFilename;

  /**
   * Sets the absolute or relative {@link String} path to an input file for a {@link base.DayI}
   * in the <code>final</code> instance variable {@link #fullFilename}
   *
   * @param fullFilename absolute or relative {@link String} path.
   */
  private FileName(String fullFilename) {
    this.fullFilename = fullFilename;
  }

  /**
   * Gets the absolute or relative {@link String} path to an input file.
   *
   * @return the absolute or relative {@link String} path
   */
  public String getfileName() {
    return fullFilename;
  }


  /**
   * Builder for the path to an input file for a {@link base.DayI}.
   *
   * <p> The file name can be build from parts or directly
   * from a {@link #fullFilename} in this order of precedence:
   *   <ul>
   *     <li>{@link #fullFilename}</li>
   *   </ul>
   * or
   *   <ul>
   *     <li>{@link #path}</li>
   *     <li>{@link #filename}</li>
   *   </ul>
   * or
   *   <ul>
   *     <li>{@link #foldernameParent}</li>
   *     <li>{@link #foldernameYear}</li>
   *     <li>{@link #foldername}</li>
   *     <li>{@link #filename}</li>
   *   </ul>
   *
   * <p> This means if a {@link #fullFilename} is set, the parts of
   * {@link #path} and {@link #filename} will not be used.
   * <br> Likewise, if a {@link #path} is set, the breakdown of {@link #foldernameParent},
   * {@link #foldernameYear}, and {@link #foldername} will not be used.
   *
   * <p> The path to an input file from the <b>executions root</b> is expected to be at either:
   * <ul>
   *   <li>{@link #path}\{@link #filename}</li>
   *   <li>{@link #foldernameParent}\{@link #foldernameYear}\{@link #foldername}\{@link #filename}</li>
   * </ul>
   *
   * <br> unless a {@link fullFilename} is specified, which must then contain the full path including the filename.
   *
   * <p> The default path to an input file for a <code>DayZ</code>
   * (where <code>Z</code> is the digit of the day)
   * implementation with the fully qualified name of for example "Year2021.Day7" is:
   * <pre>    resources\2021\sample\Day7.txt</pre>
   * and can be created by: <br><br>
   * <pre>    FileName fileName = new FileName.Builder(dayI).build();</pre>
   *
   * @author  GraysColour
   * @version 1.0
   * @since   1.0
   */
  public static class Builder {

    /**
     * System file separator.
     *
     * <p> On Windows systems this will be "\". On Linux it will be "/".
     */
    private String fileSeparator    = System.getProperty("file.separator");

    /**
     * Wildcard to be substituted by a class name.
     *
     * <p> For example "%_mine.txt" will be replaced to "DayZ_mine.txt"
     * where Z is the integer corresponding to the Day implementation.
     * <br> Hence, if running <code>Day7</code>, the filename will be replaced with "Day7_mine.txt"
     */
    private String fileWildcard     = "%";


    /**
     * The foldername containing all input file resources.
     *
     * <p> This folder is expected to be at the root of the project.
     *
     * <p> Default is "resources".
     */
    private String foldernameParent = "resources";

    /**
     * The foldername that corresponds to the package name that a {@link base.DayI} is in.
     *
     * <p> For example "2021" for the package "Year2021".
     */
    private String foldernameYear;

    /**
     * The foldername that is the direct parent of an input file to a {@link base.DayI}.
     *
     * <p> For example "challenge". The default is "sample".
     */
    private String foldername       = "sample";

    /**
     * The classname corresponding to a {@link base.DayI}. For example "Day7".
     */
    private String classname;


    /**
     * The filename of an input file for a {@link base.DayI}.
     *
     * <p> This is default set to the name of the {@link base.DayI}
     * with a <code>.txt</code> suffix. For example: Day7.txt
     */
    private String filename;

    /**
     * Absolute or relative {@link String} path not including the filename
     * to an input file for a {@link base.DayI}.
     */
    private String path;

    /**
     * Absolute or relative {@link String} path to an input file
     * for a {@link base.DayI}. Including the filename.
     */
    private String fullFilename;

    /**
     * Creates an empty {@link FileName.Builder}.
     *
     * <p> This constructor just defers setting the {@link base.DayI}.
     * <br><i> Note: use {@link #setDayX} to set a {@link base.DayI}
     * or preferable use the {@link #Builder(base.DayI)} constructor instead.</i>
     *
     */
    public Builder() {
      // defer setting DayX
    }

    /**
     * Creates a {@link FileName.Builder} with the specified {@link base.DayI}.
     *
     * <p> This constructor checks that the {@link base.DayI} resides (from the root) in a
     * package with the name "YearYYYY", where YYYY are a 4 digit year.
     * If it doesn't an {@link AssertionError} is raised with the message:
     * <pre>    Invalid package structure! Use Year&lt;YYYY&gt;.&lt;classname&gt;</pre>
     *
     * <p> It calls the check while invoking another constructor {@link #Builder(String[])}
     * making the check happening prior to constructing the {@link FileName.Builder}.
     *
     * <br> See details about this trick at <a style="color:lightseagreen" href=
     * "https://wiki.sei.cmu.edu/confluence/display/java/OBJ11-J.+Be+wary+of+letting+constructors+throw+exceptions"
     * >Be wary of letting constructors throw exceptions</a>
     *
     * @param dayI the {@link base.DayI} to initialize the {@link FileName.Builder} with.
     */
    public Builder(DayI dayI) {
      this(Classes.checkClassName(dayI));
    }

    /**
     * Creates a {@link FileName.Builder} with the specified {@link String} array.
     *
     * <p> The array is used to set the {@link #foldernameYear} from index 0
     * and the {@link #classname} from index 1.
     *
     * <p> The array must contain a package name at index 0 and a class name at index 1.
     * It can be optained by calling {@link Classes#checkClassName} with a {@link base.DayI}
     *
     * @param splitName a {@link String} array of the example form: <code>["Year2021", "Day7"]</code>
     */
    private Builder(String[] splitName) {
      this.foldernameYear = splitName[0].replace("Year","");
      this.classname      = splitName[1];
    }


    // ----------- Setters
    /**
     * Uses the {@link base.DayI} to set {@link #foldernameYear} and {@link #classname} attributes.
     *
     * <br><br><ul>
     *   <li>{@link #foldernameYear} to the digit part of the package name.</li>
     *   <li>{@link #classname} to the class name.</li>
     * </ul>
     *
     * <p> This is done after first checking the {@link base.DayI},
     * see {@link #Builder(DayI)} for details.
     *
     * @param dayI a {@link base.DayI} the {@link #fullFilename} should be based on.
     */
    public void setDayX(DayI dayI){
      String[] splitName = Classes.checkClassName(dayI);
      this.classname = splitName[1];
      if (this.foldernameYear == null || this.foldernameYear.isEmpty()) {
        this.foldernameYear = splitName[0].replace("Year","");
      }
    }

    /**
     * Sets the {@link #fileSeparator}.
     *
     * <p> Note that the default {@link #fileSeparator} is set to
     *
     * <pre>    System.getProperty("file.separator")</pre>
     *
     * which is "\" on Windows systems and "/" on Linux.
     *
     * @param fileSeparator a {@link String} fileSeparator.
     */
    public void setFileSeparator(String fileSeparator){
      this.fileSeparator = fileSeparator;
    }

    /**
     * Sets the {@link #foldernameParent}.
     *
     * <p> Note that the default {@link #foldernameParent} is set to "resources".
     *
     * @param foldernameParent a {@link String} folder name.
     */
    public void setParent(String foldernameParent){
      this.foldernameParent = foldernameParent;
    }

    /**
     * Sets the {@link #foldernameYear}.
     *
     * <p> Note that the default {@link #foldernameYear} is set
     * to the 4 digits of the package name of a {@link base.DayI}.
     *
     * <p> For example: <br> A <code>DayZ</code> implementation of <code>Year2021.Day7</code>
     * will have {@link #foldernameYear} default set to "2021" when using one of
     * <ul>
     *   <li>{@link #Builder(DayI)}</li>
     *   <li>{@link #setDayX(DayI)}</li>
     *   <li>{@link #withDayX(DayI)}</li>
     * </ul>
     *
     * @param foldernameYear a {@link String} folder name.
     */
    public void setYear(String foldernameYear){
      this.foldernameYear = foldernameYear;
    }

    /**
     * Sets the {@link #foldername}.
     *
     * <p> Note that the default {@link #foldernameParent} is set to "sample".
     *
     * @param foldername a {@link String} folder name.
     */
    public void setFolderName(String foldername){
      this.foldername = foldername;
    }

    /**
     * Sets the {@link #filename}.
     *
     * <p> Note that the default {@link #filename} is set
     * to the class name of a {@link base.DayI}.
     *
     * <p> For example: <br> A <code>DayZ</code> implementation of <code>Year2021.Day7</code>
     * will have {@link #filename} default set to "Day7.txt" during the {@link #build()} phase
     * after using one of
     * <ul>
     *   <li>{@link #Builder(DayI)}</li>
     *   <li>{@link #setDayX(DayI)}</li>
     *   <li>{@link #withDayX(DayI)}</li>
     * </ul>
     *
     * @param filename a {@link String} file name.
     */
    public void setFileName(String filename){
      this.filename = filename;
    }

    /**
     * Sets the {@link #path}.
     *
     * <p> This is the {@link #path} to the input file excluding the filename.
     *
     * @param path a {@link String} path.
     */
    public void setPath(String path){
      this.path = path;
    }

    /**
     * Sets the {@link #fullFilename}.
     *
     * <p> This is the full path to the input file including the filename.
     *
     * @param fullFilename a {@link String} with a full path to an input file.
     */
    public void setFullFilename(String fullFilename){
      this.fullFilename = fullFilename;
    }


    // ----------- Chained Setters
    /**
     * Same as {@link #setDayX(DayI)}
     *
     * @param dayI a {@link base.DayI} the {@link #fullFilename} should be based on.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withDayX(DayI dayI){
      setDayX(dayI);
      return this;
    }

    /**
     * Same as {@link #setFileSeparator(String)}
     *
     * @param fileSeparator a {@link String} fileSeparator.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withFileSeparator(String fileSeparator){
      setFileSeparator(fileSeparator);
      return this;
    }

    /**
     * Same as {@link #setParent(String)}.
     *
     * @param foldernameParent a {@link String} folder name.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withParent(String foldernameParent){
      setParent(foldernameParent);
      return this;
    }

    /**
     * Same as {@link #setYear(String)}.
     *
     * @param foldernameYear a {@link String} folder name.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withYear(String foldernameYear){
      setYear(foldernameYear);
      return this;
    }

    /**
     * Same as {@link #setFolderName(String)}.
     *
     * @param foldername a {@link String} folder name.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withFolderName(String foldername){
      setFolderName(foldername);
      return this;
    }

    /**
     * Same as {@link #setFileName(String)}.
     *
     * @param filename a {@link String} file name.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withFileName(String filename){
      setFileName(filename);
      return this;
    }

    /**
     * Same as {@link #setPath(String)}.
     *
     * @param path a {@link String} folder path.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withPath(String path){
      setPath(path);
      return this;
    }

    /**
     * Same as {@link #setFullFilename(String)}.
     *
     * @param fullFilename a {@link String} full file path including the file name.
     * @return <code>this</code> {@link FileName.Builder}.
     */
    public Builder withFullFilename(String fullFilename){
      setFullFilename(fullFilename);
      return this;
    }


    // ----------- Build it!
    /**
     * Builds a {@link #fullFilename} as per the precendence specificed at the top.
     *
     * <p> If no {@link #filename} has been specified, the default filename
     * will be the class name of the {@link base.DayI} used in either:
     * <ul>
     *   <li>{@link #Builder(DayI)}</li>
     *   <li>{@link #setDayX(DayI)}</li>
     *   <li>{@link #withDayX(DayI)}</li>
     * </ul>
     *
     * <p> If the wildcard "%" has been used in the
     * {@link #filename}, it will be replaced with the class name.
     * <br> For example: <br> "%_mine.txt" will be replaced to "DayZ_mine.txt"
     * where Z is the integer corresponding to the Day implementation.
     * <br> Hence, if running <code>Day7</code>, the filename will be replaced with "Day7_mine.txt"
     *
     * @return {@link util.FileName} containing the {@link String} {@link #fullFilename}
     */
    public FileName build() {
      StringBuilder str = new StringBuilder();
      if (this.filename == null || this.filename.isEmpty()) {
        str.append(this.classname);
        str.append(".txt");

        this.filename = str.toString();
      }

      str.setLength(0); // reset the StringBuilder
      if (this.path == null || this.path.isEmpty()) {
        str.append(foldernameParent);
        str.append(fileSeparator);
        str.append(foldernameYear);
        str.append(fileSeparator);
        str.append(foldername);

        this.path = str.toString();
      }

      str.setLength(0);  // reset the StringBuilder
      if (this.fullFilename == null || this.fullFilename.isEmpty()) {
        str.append(path);
        str.append(fileSeparator);
        str.append(filename);

        this.fullFilename = str.toString();
      }

      if (this.fullFilename.indexOf(fileWildcard) > -1) {
        this.fullFilename = this.fullFilename.replace(fileWildcard, this.classname);
      }

      return new FileName(fullFilename);
    }


    /**
     * Prints the current {@link FileName.Builder} configuration to the console.
     *
     * <p> This only exists to aid with debugging the {@link FileName.Builder}.
     */
    public void printMe() {
      String format = "\t%20s: %s\n";
      System.out.printf(format, "fileSeparator", fileSeparator);
      System.out.printf(format, "foldernameParent", foldernameParent);
      System.out.printf(format, "foldernameYear", foldernameYear);
      System.out.printf(format, "foldername", foldername);
      System.out.printf(format, "classname", classname);
      System.out.printf(format, "filename", filename);
      System.out.printf(format, "path", path);
      System.out.printf(format, "fullFilename", fullFilename);
    }
  }

}
