package vn.tapbi.zazip.model;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class EventGoogleSignInClient {
    private GoogleSignInClient googleSignInClient;

    public GoogleSignInClient getGoogleSignInClient() {
        return googleSignInClient;
    }

    public void setGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }

    public EventGoogleSignInClient(GoogleSignInClient googleSignInClient) {
        this.googleSignInClient = googleSignInClient;
    }
}
