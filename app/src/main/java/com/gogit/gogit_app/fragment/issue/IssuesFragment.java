package com.gogit.gogit_app.fragment.issue;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.IssueAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.model.github.issue.Issue;
import com.gogit.gogit_app.model.github.repo.Repository;
import com.gogit.gogit_app.request.AddIssueRequest;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.ToastHelper;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IssuesFragment extends Fragment {

    Retrofit retrofit;
    GithubService githubService;
    RecyclerView issuesRecyclerView;
    SessionManager sessionManager;
    String token;
    String userId;
    Spinner repoSpinner;
    String selectedRepoName;

    Button uploadButton;
    EditText issueTitleEditText;


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.issues, container, false);

        sessionManager = new SessionManager(getContext());
        token = sessionManager.getToken();
        userId = sessionManager.getUserId();

        retrofit = GithubRetrofitClient.getRetrofitInstance();
        githubService = retrofit.create(GithubService.class);

        issuesRecyclerView = view.findViewById(R.id.issues_recyclerview);
        issuesRecyclerView.setHasFixedSize(false);
        issuesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        repoSpinner = view.findViewById(R.id.repo_spinner);

        uploadButton = view.findViewById(R.id.upload_button);
        issueTitleEditText = view.findViewById(R.id.issue_title_edittext);

        issueTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 텍스트 변경 전
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 텍스트 변경 중
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 텍스트 변경 후
                if (s.toString().trim().length() != 0) {
                    uploadButton.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
                    uploadButton.setEnabled(true);
                } else {
                    uploadButton.setBackgroundTintList(ColorStateList.valueOf(Color.LTGRAY));
                    uploadButton.setEnabled(false);
                }
            }
        });
        uploadButton.setOnClickListener(e -> {
            String title = issueTitleEditText.getText().toString();
            if (title.trim().length() == 0) {
                ToastHelper.showToast(getContext(), "이슈 제목을 입력해주세요.");
                return;
            }

            createIssue(token, userId, selectedRepoName, title);
        });
        showIssues(token);
        setRepoSpinner(token, userId, repoSpinner);
        return view;
    }

    private void showIssues(String token) {
        Call<List<Issue>> call = githubService.getUesrsIssues(
                "Bearer " + token);
        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(Call<List<Issue>> call, Response<List<Issue>> response) {
                List<Issue> issues = response.body();
                if (issues != null) {
                    issuesRecyclerView.setAdapter(new IssueAdapter(issues));
                } else {
                    ToastHelper.showToast(getContext(), "이슈가 존재하지 않습니다.");
                }
            }

            @Override
            public void onFailure(Call<List<Issue>> call, Throwable t) {
                ToastHelper.showNetworkErrorToast(getContext());
            }
        });
    }

    private void setRepoSpinner(String token, String login, Spinner spinner) {
        Call<List<Repository>> call = githubService.getRepos("Bearer " + token, login);
        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repositories = response.body();
                if (repositories != null) {
                    String itemNames = "";
                    for (Repository repository : repositories) {
                        itemNames += repository.getFull_name().split("/")[1] + ",";
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            getContext(), android.R.layout.simple_spinner_item, itemNames.split(","));

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            // 선택된 항목 처리
                            String selectedValue = (String) parentView.getItemAtPosition(position);
                            selectedRepoName = selectedValue;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                            ToastHelper.showToast(getContext(), "리포지토리가 선택되지 않았습니다.");
                        }
                    });
                } else {
                    ToastHelper.showNetworkErrorToast(getContext());
                }
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                ToastHelper.showNetworkErrorToast(getContext());
            }
        });


    }

    private void createIssue(String token, String login, String repo, String title) {
        ArrayList<String> list = new ArrayList<>();
        list.add(login);

        Call<Issue> call = githubService.createIssue(
                "Bearer " + token, login, repo, new AddIssueRequest(title, list)
        );

        Log.d("my tag", new AddIssueRequest(title, list).toString());

        call.enqueue(new Callback<Issue>() {
            @Override
            public void onResponse(Call<Issue> call, Response<Issue> response) {

                if (response.isSuccessful()) {
                    ToastHelper.showToast(getContext(), "이슈가 추가되었습니다.");
                    issueTitleEditText.setText("");
                    showIssues(token);

                } else {
                    ToastHelper.showToast(getContext(), "이슈 추가를 실패했습니다.");
                }
            }

            @Override
            public void onFailure(Call<Issue> call, Throwable t) {
                ToastHelper.showNetworkErrorToast(getContext());
            }
        });
    }


}