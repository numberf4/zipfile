package vn.tapbi.zazip.model;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class EventLoginGoogleDrive {
    private GoogleSignInClient googleSignInClient;

    public EventLoginGoogleDrive(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

}
