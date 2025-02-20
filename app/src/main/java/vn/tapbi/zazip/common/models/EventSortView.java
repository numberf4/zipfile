package vn.tapbi.zazip.common.models;

public class EventSortView {
    private int positionPresentationView;
    private int positionSortView;
    private boolean sortDesc;
    private int screen;

    public int getScreen() {
        return screen;
    }

    public void setScreen(int screen) {
        this.screen = screen;
    }

    public int getPositionPresentationView() {
        return positionPresentationView;
    }

    public void setPositionPresentationView(int positionPresentationView) {
        this.positionPresentationView = positionPresentationView;
    }

    public int getPositionSortView() {
        return positionSortView;
    }

    public void setPositionSortView(int positionSortView) {
        this.positionSortView = positionSortView;
    }

    public boolean isSortDesc() {
        return sortDesc;
    }

    public void setSortDesc(boolean sortDesc) {
        this.sortDesc = sortDesc;
    }

    public EventSortView(int positionPresentationView, int positionSortView, boolean sortDesc) {
        this.positionPresentationView = positionPresentationView;
        this.positionSortView = positionSortView;
        this.sortDesc = sortDesc;
    }

    public EventSortView(int positionPresentationView, int screen) {
        this.positionPresentationView = positionPresentationView;
        this.screen = screen;
    }

    public EventSortView(int positionSortView, boolean sortDesc) {
        this.positionSortView = positionSortView;
        this.sortDesc = sortDesc;
    }
}
