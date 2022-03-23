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

public class DataReader {

  private static Charset charset = StandardCharsets.UTF_8;
  private DataReader() {}

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
