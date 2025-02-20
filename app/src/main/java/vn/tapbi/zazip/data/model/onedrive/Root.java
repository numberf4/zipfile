package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Root {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lastModifiedDateTime")
    @Expose
    private String lastModifiedDateTime;
}
