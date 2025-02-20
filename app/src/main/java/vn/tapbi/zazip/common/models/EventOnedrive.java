package vn.tapbi.zazip.common.models;

import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.microsoft.identity.client.IAccount;


import vn.tapbi.zazip.data.model.Account;

public class EventOnedrive {
    private IAccount iAccount;
    private Account account;
    private String type;
    private Bundle bundle;
    private GoogleSignInAccount googleSignInAccount;

    public EventOnedrive(IAccount iAccount, Account account) {
        this.iAccount = iAccount;
        this.account = account;
    }

    public EventOnedrive(IAccount iAccount, Account account, String type) {
        this.iAccount = iAccount;
        this.account = account;
        this.type = type;
    }

    public EventOnedrive(IAccount iAccount, Account account, String type, GoogleSignInAccount googleSignInAccount) {
        this.iAccount = iAccount;
        this.account = account;
        this.type = type;
        this.googleSignInAccount = googleSignInAccount;
    }

    public EventOnedrive(IAccount iAccount, Account account, String type, Bundle bundle) {
        this.iAccount = iAccount;
        this.account = account;
        this.type = type;
        this.bundle = bundle;
    }

    public GoogleSignInAccount getGoogleSignInAccount() {
        return googleSignInAccount;
    }

    public void setGoogleSignInAccount(GoogleSignInAccount googleSignInAccount) {
        this.googleSignInAccount = googleSignInAccount;
    }

    public String getType() {
        return type;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IAccount getIAccount() {
        return iAccount;
    }

    public void setIAccount(IAccount iAccount) {
        this.iAccount = iAccount;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
