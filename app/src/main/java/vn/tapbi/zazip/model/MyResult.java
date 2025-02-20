package vn.tapbi.zazip.model;

public class MyResult {
    private int result;
    private String resultDownload;
    private ProcessNoty processNoty;

    public MyResult(String resultDownload, ProcessNoty processNoty) {
        this.resultDownload = resultDownload;
        this.processNoty = processNoty;
    }

    public MyResult(String resultDownload) {
        this.resultDownload = resultDownload;
    }

    public String getResultDownload() {
        return resultDownload;
    }

    public void setResultDownload(String resultDownload) {
        this.resultDownload = resultDownload;
    }

    public MyResult(int result, ProcessNoty processNoty) {
        this.result = result;
        this.processNoty = processNoty;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public ProcessNoty getProcessNoty() {
        return processNoty;
    }

    public void setProcessNoty(ProcessNoty processNoty) {
        this.processNoty = processNoty;
    }

    @Override
    public String toString() {
        return "MyResult{" +
                "result=" + result +
                ", resultDownload='" + resultDownload + '\'' +
                '}';
    }
}
