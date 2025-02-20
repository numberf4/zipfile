package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Link {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("webUrl")
    @Expose
    private String webUrl;
    @SerializedName("application")
    @Expose
    private Application application;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

}
