package com.gogit.gogit_app.fragment.modal;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.OrgsAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.model.github.org.Organization;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.ToastHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OrgModalFragment extends DialogFragment {


    public OrgModalFragment() {
    }

    private String login;
    private String token;
    private Retrofit retrofit;
    private GithubService githubService;
    RecyclerView orgRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.organization_popup, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        token = sessionManager.getToken();
        login = sessionManager.getUserId();


        retrofit = GithubRetrofitClient.getRetrofitInstance();
        githubService = retrofit.create(GithubService.class);

        orgRecyclerView = view.findViewById(R.id.org_recyclerview);
        orgRecyclerView.setHasFixedSize(false);
        orgRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        showOrgs(token, login);
        return view;
    }

    private void showOrgs(String token, String login) {
        Call<List<Organization>> call = githubService.getUsersOrgs(
                "Bearer " + token, login
        );

        call.enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                List<Organization> orgs = response.body();
                if (orgs != null) {
                    orgRecyclerView.setAdapter(new OrgsAdapter(orgs));

                    Log.d("my tag", orgs.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                ToastHelper.showNetworkErrorToast(getContext());
            }
        });
    }
}