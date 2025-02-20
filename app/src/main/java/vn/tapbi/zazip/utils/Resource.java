package vn.tapbi.zazip.utils;

import android.content.Intent;

public class Resource<T> {
    Status status;
    T data;
    String s;
    Intent intent;

    public Resource(Status status, T data, String s) {
        this.status = status;
        this.data = data;
        this.s = s;
    }

    public Resource(Status status, T data, String s, Intent intent) {
        this.status = status;
        this.data = data;
        this.s = s;
        this.intent = intent;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Resource() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public Resource<T> loading(T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    public Resource<T> error(T data, String message) {
        return new Resource<>(Status.ERROR, data, message);
    }

}
