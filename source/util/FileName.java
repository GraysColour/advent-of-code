package util;

import base.DayI;

public class FileName {

  private final String fullFilename;

  private FileName(String fullFilename) {
    this.fullFilename = fullFilename;
  }

  public String getfileName() {
    return fullFilename;
  }

  public static class Builder {

    private String fileSeparator    = System.getProperty("file.separator");
    private String fileWildcard     = "%";

    private String foldernameParent = "resources";
    private String foldernameYear;
    private String foldername       = "sample";
    private String classname;
    private String filename;
    private String path;
    private String fullFilename;

    public Builder() {
      // defer setting DayX
    }
    public Builder(DayI dayI) {
      this(Classes.checkClassName(dayI));
    }
    private Builder(String[] splitName) {
      this.foldernameYear = splitName[0].replace("Year","");
      this.classname      = splitName[1];
    }

    // ----------- Setters
    public void setDayX(DayI dayI){
      String[] splitName = Classes.checkClassName(dayI);
      this.classname = splitName[1];
      if (this.foldernameYear == null || this.foldernameYear.isEmpty()) {
        this.foldernameYear = splitName[0].replace("Year","");
      }
    }

    public void setFileSeparator(String fileSeparator){
      this.fileSeparator = fileSeparator;
    }

    public void setParent(String foldernameParent){
      this.foldernameParent = foldernameParent;
    }

    public void setYear(String foldernameYear){
      this.foldernameYear = foldernameYear;
    }

    public void setFolderName(String foldername){
      this.foldername = foldername;
    }

    public void setFileName(String filename){
      this.filename = filename;
    }

    public void setPath(String path){
      this.path = path;
    }

    public void setFullFilename(String fullFilename){
      this.fullFilename = fullFilename;
    }

    // ----------- Chained Setters
    public Builder withDayX(DayI dayI){
      setDayX(dayI);
      return this;
    }

    public Builder withFileSeparator(String fileSeparator){
      setFileSeparator(fileSeparator);
      return this;
    }

    public Builder withParent(String foldernameParent){
      setParent(foldernameParent);
      return this;
    }

    public Builder withYear(String foldernameYear){
      setYear(foldernameYear);
      return this;
    }

    public Builder withFolderName(String foldername){
      setFolderName(foldername);
      return this;
    }

    public Builder withFileName(String filename){
      setFileName(filename);
      return this;
    }

    public Builder withPath(String path){
      setPath(path);
      return this;
    }

    public Builder withFullFilename(String fullFilename){
      setFullFilename(fullFilename);
      return this;
    }


    // ----------- Build it!
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
