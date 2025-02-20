package vn.tapbi.zazip.common.models;

public class EventModifyName {
    private String newName;
    private boolean isDelete;

    public EventModifyName(String newName) {
        this.newName = newName;
    }

    public EventModifyName(boolean isDelete) {
        this.isDelete = isDelete;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
