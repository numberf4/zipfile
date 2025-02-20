package vn.tapbi.zazip.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Account", indices = @Index(value = {"idAccount","accountName","type"}, unique = true))//if any of accountId and accountName and type exist in the same column data will update otherwise data will insert at new row
public class Account {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo
    private String accountName;

    @ColumnInfo
    private long time;

    @ColumnInfo
    private String type;

    @ColumnInfo
    private String accessToken;

    @ColumnInfo
    private String idAccount;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Account(String accountName, long time, String type, String accessToken, String idAccount) {
        this.accountName = accountName;
        this.time = time;
        this.type = type;
        this.accessToken = accessToken;
        this.idAccount = idAccount;
    }

    public String getIdAccount() {
        return idAccount;
    }

    public void setIdAccount(String idAccount) {
        this.idAccount = idAccount;
    }


    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", time=" + time +
                ", type='" + type + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
