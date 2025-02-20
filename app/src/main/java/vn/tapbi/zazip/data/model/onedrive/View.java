
package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class View {

    @SerializedName("viewType")
    @Expose
    private String viewType;
    @SerializedName("sortBy")
    @Expose
    private String sortBy;
    @SerializedName("sortOrder")
    @Expose
    private String sortOrder;

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

}
