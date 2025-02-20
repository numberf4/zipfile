package vn.tapbi.zazip.model;

public class ResultExtract {
    private int result;
    private String folderExtract;

    public ResultExtract(int result, String folderExtract) {
        this.result = result;
        this.folderExtract = folderExtract;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getFolderExtract() {
        return folderExtract;
    }

    public void setFolderExtract(String folderExtract) {
        this.folderExtract = folderExtract;
    }
}
