package vn.tapbi.zazip.common.models;

public class EventPath {
    private int type;
    private String path;

    public EventPath(int type, String path) {
        this.type = type;
        this.path = path;
    }

    public EventPath(String path) {
        this.path = path;
    }

    public EventPath(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
