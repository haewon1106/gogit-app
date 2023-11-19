package com.gogit.gogit_app.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.config.Config;
import com.gogit.gogit_app.util.NetworkUtils;
import com.gogit.gogit_app.util.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericIdpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_idp);

        OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

        provider.addCustomParameter("client_id", Config.CLIENT_ID);
        provider.addCustomParameter("redirect_uri", Config.REDIRECT_URI);
        provider.addCustomParameter("client_id", Config.CLIENT_ID);
        // Target specific email with login hint.
        provider.addCustomParameter("login", "w2218@e-mirim.hs.kr");

        if (NetworkUtils.isNetworkAvailable(this)) {
            Log.d("my tag", "network ok");
        } else {
            Log.d("my tag", "network bad");
        }


        List<String> scopes =
                new ArrayList<String>() {
                    {
                        add("user:email");
                        add("public_repo");
                        add("user:user");
                        add("user:follow");

                    }
                };
        provider.setScopes(scopes);


        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Log.d("my tag", firebaseAuth.toString());

        Task<AuthResult> pendingResultTask = firebaseAuth.getPendingAuthResult();
        if (pendingResultTask != null) {
            pendingResultTask
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    Map<String, Object> oAuthCredential = authResult.getAdditionalUserInfo().getProfile();
                                    Log.d("my tag", oAuthCredential.toString());
                                    Intent intent = new Intent(GenericIdpActivity.this, MainActivity.class);
                                    String login = (String) oAuthCredential.get("login");
                                    intent.putExtra("login", login);

                                    startActivity(intent);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("my tag", "failure");
                                }
                            });
        } else {

        }

        firebaseAuth
                .startActivityForSignInWithProvider(/* activity= */ this, provider.build())
                .addOnSuccessListener(
                        new OnSuccessListener<AuthResult>() {
                            // 로그인 되었을 때
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                Map<String, Object> oAuthCredential = authResult.getAdditionalUserInfo().getProfile();
                                Log.d("my tag", oAuthCredential.toString());
                                String login = (String) oAuthCredential.get("login");

                                Intent intent = new Intent(GenericIdpActivity.this, TokenActivity.class);
                                intent.putExtra(SessionManager.KEY_USERID, login);
                                intent.putExtra("avatar_url", (String) oAuthCredential.get("avatar_url"));
                                intent.putExtra("html_url", (String) oAuthCredential.get("html_url"));
                                startActivity(intent);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("my tag", "failure");
                            }
                        });


    }
}