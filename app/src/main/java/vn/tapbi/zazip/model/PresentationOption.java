package vn.tapbi.zazip.model;

public class PresentationOption {
    private String nameOption;
    private int imageOption;
    private int imageOptionSelect;
    private int colorText;

    public int getColorText() {
        return colorText;
    }

    public int getImageOptionSelect() {
        return imageOptionSelect;
    }

    public void setImageOptionSelect(int imageOptionSelect) {
        this.imageOptionSelect = imageOptionSelect;
    }

    public void setColorText(int colorText) {
        this.colorText = colorText;
    }

    public String getNameOption() {
        return nameOption;
    }

    public void setNameOption(String nameOption) {
        this.nameOption = nameOption;
    }

    public int getImageOption() {
        return imageOption;
    }

    public void setImageOption(int imageOption) {
        this.imageOption = imageOption;
    }

    public PresentationOption(String nameOption, int imageOption, int imageSelect) {
        this.imageOptionSelect = imageSelect;
        this.nameOption = nameOption;
        this.imageOption = imageOption;
    }
}
