
package vn.tapbi.zazip.data.model.onedrive;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hashes {

    @SerializedName("quickXorHash")
    @Expose
    private String quickXorHash;
    @SerializedName("sha1Hash")
    @Expose
    private String sha1Hash;
    @SerializedName("sha256Hash")
    @Expose
    private String sha256Hash;

    public String getQuickXorHash() {
        return quickXorHash;
    }

    public void setQuickXorHash(String quickXorHash) {
        this.quickXorHash = quickXorHash;
    }

    public String getSha1Hash() {
        return sha1Hash;
    }

    public void setSha1Hash(String sha1Hash) {
        this.sha1Hash = sha1Hash;
    }

    public String getSha256Hash() {
        return sha256Hash;
    }

    public void setSha256Hash(String sha256Hash) {
        this.sha256Hash = sha256Hash;
    }

}
