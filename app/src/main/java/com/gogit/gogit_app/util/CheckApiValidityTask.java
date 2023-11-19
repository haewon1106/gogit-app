package com.gogit.gogit_app.util;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.gogit.gogit_app.ui.TokenActivity;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

import java.io.IOException;

public class CheckApiValidityTask extends AsyncTask<Void, Void, Boolean> {

    private String token;
    private Context context;

    public CheckApiValidityTask(String token, Context context) {
        this.token = token;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        try {
            GitHub gitHub = new GitHubBuilder().withOAuthToken(token).build();
            gitHub.checkApiUrlValidity();
            return true; // 성공
        } catch (IOException e) {
            Log.d("my tag", e.getMessage());
            return false; // 실패
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {

        } else {
            ToastHelper.showToast(context, "토큰이 유효하지 않습니다.");
            Intent intent = new Intent(context, TokenActivity.class);
            context.startActivity(intent);
        }
    }
}
