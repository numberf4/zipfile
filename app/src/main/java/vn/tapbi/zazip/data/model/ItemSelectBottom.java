package vn.tapbi.zazip.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemSelectBottom implements Parcelable {
    private int imageOption;
    private String txtOption;
    private int colorText;

    protected ItemSelectBottom(Parcel in) {
        imageOption = in.readInt();
        txtOption = in.readString();
        colorText = in.readInt();
    }

    public static final Creator<ItemSelectBottom> CREATOR = new Creator<ItemSelectBottom>() {
        @Override
        public ItemSelectBottom createFromParcel(Parcel in) {
            return new ItemSelectBottom(in);
        }

        @Override
        public ItemSelectBottom[] newArray(int size) {
            return new ItemSelectBottom[size];
        }
    };

    public int getColorText() {
        return colorText;
    }

    public void setColorText(int colorText) {
        this.colorText = colorText;
    }

    public int getImageOption() {
        return imageOption;
    }

    public void setImageOption(int imageOption) {
        this.imageOption = imageOption;
    }

    public String getTxtOption() {
        return txtOption;
    }

    public void setTxtOption(String txtOption) {
        this.txtOption = txtOption;
    }

    public ItemSelectBottom() {
    }

    public ItemSelectBottom(int imageOption, String txtOption, int colorText) {
        this.imageOption = imageOption;
        this.txtOption = txtOption;
        this.colorText = colorText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageOption);
        dest.writeString(txtOption);
        dest.writeInt(colorText);
    }
}
