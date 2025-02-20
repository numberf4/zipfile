
package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Folder {

    @SerializedName("childCount")
    @Expose
    private Integer childCount;
    @SerializedName("view")
    @Expose
    private View view;

    public Integer getChildCount() {
        return childCount;
    }

    public void setChildCount(Integer childCount) {
        this.childCount = childCount;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}
