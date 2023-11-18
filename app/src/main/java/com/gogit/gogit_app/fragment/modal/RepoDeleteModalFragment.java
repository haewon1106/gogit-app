package com.gogit.gogit_app.fragment.modal;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.fragment.user.MyPageFragment;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.FragmentHelper;
import com.gogit.gogit_app.util.MyToast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RepoDeleteModalFragment extends DialogFragment {

    private static final String REPO_NAME = "repo_name";

    private String repoName;

    // 뷰
    TextView repoNameTextView;
    EditText repoNameEditText;
    Button deleteButton;
    View modalCloseView;

    private String login;
    private String token;

    private Retrofit retrofit;
    private GithubService githubService;

    public RepoDeleteModalFragment() {
    }

    public static RepoDeleteModalFragment newInstance(String repoName) {
        RepoDeleteModalFragment fragment = new RepoDeleteModalFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_repository_modal, container, false);

        // 세션 정보
        SessionManager sessionManager = new SessionManager(getContext());
        login = sessionManager.getUserId();
        token = sessionManager.getToken();

        // 뷰
        repoNameTextView = view.findViewById(R.id.delete_repository_name);
        repoNameEditText = view.findViewById(R.id.delete_repository_name_edittext);
        deleteButton = view.findViewById(R.id.repo_delete_button);
        modalCloseView = view.findViewById(R.id.repository_delete_modal_close);

        repoNameTextView.setText(login + "/" + repoName);
        repoNameEditText.setHint(login + "/" + repoName);

        // 레트로핏, 서비스
        retrofit = GithubRetrofitClient.getRetrofitInstance();
        githubService = retrofit.create(GithubService.class);


        // 이벤트 리스너
        modalCloseView.setOnClickListener(e -> {
            dismiss();
        });

        deleteButton.setOnClickListener(e -> {
            String input = repoNameEditText.getText().toString();
            if (input.equals(repoNameTextView.getText())) {
                deleteRepository(token, login, repoName);
            } else {
                MyToast.showToast(getContext(), "이름이 알맞지 않습니다.");
                repoNameEditText.setText("");
            }
        });
        return view;
    }

    private void deleteRepository(String token, String login, String repo) {
        Call<Object> call = githubService.deleteRepo(
                "Bearer " + token, login, repo
        );

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                MyToast.showToast(getContext(), "리포지토리가 삭제되었습니다.");
                FragmentHelper.replaceFragment(getActivity().getSupportFragmentManager(), R.id.containers, new MyPageFragment());
                dismiss();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                MyToast.showNetworkErrorToast(getContext());
            }
        });

    }

}