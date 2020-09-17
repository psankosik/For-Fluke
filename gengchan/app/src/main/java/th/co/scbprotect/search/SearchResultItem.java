package th.co.scbprotect.search;

public class SearchResultItem {
    private String fileTitle;
    private String fileTimeStamp;
    private String fileContent;
    private String fileType;
    private String fileStage;
    private String fileKeywords;
    private String filePath;

    public SearchResultItem(String fileTitle, String fileTimeStamp, String fileContent, String fileType, String fileStage, String fileKeywords, String filePath) {
        this.fileTitle = fileTitle;
        this.fileTimeStamp = fileTimeStamp;
        this.fileContent = fileContent;
        this.fileType = fileType;
        this.fileStage = fileStage;
        this.fileKeywords = fileKeywords;
        this.filePath = filePath;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public String getFileTimeStamp(){
        return fileTimeStamp;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getFileType() {
        return fileType;
    }

    public String getFileStage() {
        return fileStage;
    }

    public String getFileKeywords() {
        return fileKeywords;
    }

    public String getFilePath() {
        return filePath;
    }


}
