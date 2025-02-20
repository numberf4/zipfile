package vn.tapbi.zazip.model;

public class FileCloudDownload {
    private String nameFile;
    private String mimeFile;
    private String idFile;
    private String fullName;
    private String urlOneDrive;
    private String revDropBox;
    private String accessTokenDropBox;
    private String pathDisplayDropBox;

    public FileCloudDownload(String nameFile, String mimeFile, String idFile,  String urlOneDrive, String revDropBox, String accessTokenDropBox, String pathDisplayDropBox) {
        this.nameFile = nameFile;
        this.mimeFile = mimeFile;
        this.idFile = idFile;
        this.urlOneDrive = urlOneDrive;
        this.revDropBox = revDropBox;
        this.accessTokenDropBox = accessTokenDropBox;
        this.pathDisplayDropBox = pathDisplayDropBox;
    }

    public String getPathDisplayDropBox() {
        return pathDisplayDropBox;
    }

    public void setPathDisplayDropBox(String pathDisplayDropBox) {
        this.pathDisplayDropBox = pathDisplayDropBox;
    }

    public String getAccessTokenDropBox() {
        return accessTokenDropBox;
    }

    public void setAccessTokenDropBox(String accessTokenDropBox) {
        this.accessTokenDropBox = accessTokenDropBox;
    }

    public String getRevDropBox() {
        return revDropBox;
    }

    public void setRevDropBox(String revDropBox) {
        this.revDropBox = revDropBox;
    }

    public String getUrlOneDrive() {
        return urlOneDrive;
    }

    public void setUrlOneDrive(String urlOneDrive) {
        this.urlOneDrive = urlOneDrive;
    }

    public String getNameFile() {
        return nameFile;
    }

    public void setNameFile(String nameFile) {
        this.nameFile = nameFile;
    }

    public String getMimeFile() {
        return mimeFile;
    }

    public void setMimeFile(String mimeFile) {
        this.mimeFile = mimeFile;
    }

    public String getIdFile() {
        return idFile;
    }

    public void setIdFile(String idFile) {
        this.idFile = idFile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
