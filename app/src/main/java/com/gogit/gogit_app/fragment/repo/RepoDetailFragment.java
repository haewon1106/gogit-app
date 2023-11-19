package com.gogit.gogit_app.fragment.repo;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.CommitAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.fragment.modal.RepoDeleteModalFragment;
import com.gogit.gogit_app.model.github.commit.RepoCommit;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.ToastHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RepoDetailFragment extends Fragment {
    private static final String REPO_NAME = "repo_name";
    private String repoName;
    private String login;
    private String token;
    private Button repoDeleteButton;

    private Retrofit retrofit;
    private GithubService githubService;
    private RecyclerView commitRecyclerView;

    public RepoDetailFragment() {
    }

    public static RepoDetailFragment newInstance(String repoName) {
        RepoDetailFragment fragment = new RepoDetailFragment();
        Bundle args = new Bundle();
        args.putString(REPO_NAME, repoName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            repoName = getArguments().getString(REPO_NAME);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.repository_commit, container, false);

        // 뷰
        repoDeleteButton = view.findViewById(R.id.repository_delete_btn);

        // 레트로핏, 서비스
        retrofit = GithubRetrofitClient.getRetrofitInstance();
        githubService = retrofit.create(GithubService.class);

        // 세션매니저
        SessionManager sessionManager = new SessionManager(getContext());
        login = sessionManager.getUserId();
        token = sessionManager.getToken();

        // 리사이클러 뷰
        commitRecyclerView = view.findViewById(R.id.commit_recyclerview);
        commitRecyclerView.setHasFixedSize(false);
        commitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 커밋 목록
        showCommits(token, login, repoName);

        //이벤트 리스너
        repoDeleteButton.setOnClickListener(e -> {
            RepoDeleteModalFragment repoDeleteModalFragment = RepoDeleteModalFragment.newInstance(repoName);
            repoDeleteModalFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        });
        return view;
    }

    private void showCommits(String token, String owner, String repo) {
        Call<List<RepoCommit>> call = githubService.getReposCommits(
                "Bearer " + token,
                owner,
                repo
        );

        call.enqueue(new Callback<List<RepoCommit>>() {
            @Override
            public void onResponse(Call<List<RepoCommit>> call, Response<List<RepoCommit>> response) {
                if (response.isSuccessful()) {
                    List<RepoCommit> commits = response.body();
                    if (commits != null) {
                        commitRecyclerView.setAdapter(new CommitAdapter(commits));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<RepoCommit>> call, Throwable t) {
                ToastHelper.showNetworkErrorToast(getContext());
            }
        });
    }

}