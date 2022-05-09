package util;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.Properties;

import base.*;

/**
 * Reads files using {@link StandardCharsets#UTF_8}.
 *
 * @author  GraysColour
 * @version 1.0
 * @since   1.0
 */

public class DataReader {

  /**
   * Set to {@link StandardCharsets#UTF_8}
   */
  private static Charset charset = StandardCharsets.UTF_8;

  /**
   * @hidden
   */
  private DataReader() {}


  /**
   * Retrives the class path directory name from config.properties.
   *
   * <p> The config.properties file is expected to be located at the default package directory level
   * and the property containing the class path directory name to be "run.dir".
   *
   * <p> If the file cannot be found a message is displayed on the console:
   * <pre>    Cannot find config.properties</pre>
   * If there's nothing in the property, this will be displayed instead:
   * <pre>    Nothing in "run.dir"</pre>
   * In both error situations the program exits.
   *
   * @return the {@link String} directory name of the class path from properties
   */
  public static String getRunDir() {

    try (InputStream file = DataReader.class
                                      .getClassLoader()
                                      .getResourceAsStream("config.properties")) {
      if (file == null) {
        System.out.println("Cannot find config.properties");
        System.exit(-1);
      }

      // load the properties file & return the value
      Properties prop = new Properties();
      prop.load(file);
      String rundir = prop.getProperty("run.dir").trim();
      if (rundir == null || rundir.isEmpty()) {
        System.out.println("Nothing in \"run.dir\"");
        System.exit(-1);
      } else {
        return rundir;
      }

    } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(-1);
    }

    return null;
  }


  /**
   * Will attempt to read a file based on the specified {@link String} <code>path</code>.
   * <br> If successfull calls the <code>setInput</code> with the content of the file.
   * Then returns a {@link util.State} with {@link base.Status#FILE_OK}.
   *
   * <p> If not successfull it will return a {@link util.State} with
   * <ul>
   *   <li>{@link Status#NO_FILE} and a message saying
   *       the file doesn't exist, if there is no file.</li>
   *   <li>{@link Status#NO_FILE_CONTENT} and a message saying
   *       the path is empty, if there is no content in the file.</li>
   *   <li>{@link Status#GOT_EXCEPTION} along with the
   *       {@link Exception} for any other error/exception.</li>
   * </ul>
   *
   * <p>Usage:
   *
   * <pre>
   *     readFile("resources\2021\sample\Day7.txt", (in) -&gt; dayI.setInput(in))</pre>
   *
   * @param path a {@link String} path to the file.
   * @param setInput the {@link Consumer} that will set the content of the file.
   * @return a {@link util.State} with a {@link base.Status} correspoding to the outcome.
   */
  public static State readFile(String path, Consumer<String> setInput) {
    Path filePath = null;
    try {
      filePath = Paths.get(path);
    } catch (InvalidPathException ex) {
      return new State(Status.GOT_EXCEPTION).withException(ex);
    }

    String input = "";

    if (!Files.exists(filePath)) {
      return new State(Status.NO_FILE).withMessage("file " + filePath + " does not exist");
    } else {
      try {
        input = Files.readString(filePath, DataReader.charset);
      } catch (IOException ex) {
        return new State(Status.GOT_EXCEPTION).withException(ex);
      }
    }

    if (input.isEmpty()) {
      return new State(Status.NO_FILE_CONTENT).withMessage(path + " is empty");
    }

    setInput.accept(input);
    return new State(Status.FILE_OK);
  }


  /**
   * Will attempt to read the ascII text file based on the specified {@link Path}.
   * <br> If successful it will return a {@link base.ResultI} containing an ascII {@link String}
   * array where each element corresponds to a line in the ascII text file.
   *
   * <p> If not successfull it will return a {@link base.ResultI} with a {@link util.State} of
   * <ul>
   *   <li>{@link Status#NO_FILE} and a message if there is no file.</li>
   *   <li>{@link Status#NO_FILE_CONTENT} and a message if there is no content in the file.</li>
   *   <li>{@link Status#GOT_EXCEPTION} along with the
   *       {@link Exception} for any other error/exception.</li>
   * </ul>
   *
   * @param path a {@link Path} to the ascII text file.
   * @return a {@link ResultI} correspoding to the outcome.
   */
  public static ResultI readAscIIFile(Path path) {
    String[] ascII = new String[0];

    if (!Files.exists(path)) {
      return Result.createFileErrorResult(
              new State(Status.NO_FILE).withMessage("file " + path + " does not exist")
             );
    } else {
      try {
        ascII = Files.readString(path, DataReader.charset).split("\\R");
      } catch (IOException ex) {
        return Result.createFileErrorResult(
                 new State(Status.GOT_EXCEPTION).withException(ex)
               );
      }
    }

    if (ascII.length == 0) {
      return Result.createFileErrorResult(
               new State(Status.NO_FILE_CONTENT).withMessage(path + " is empty")
             );
    }

    return Result.createAscIIResult(ascII);
  }

}
