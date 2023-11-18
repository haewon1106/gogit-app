package com.gogit.gogit_app.fragment.repo;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.fragment.user.MyPageFragment;
import com.gogit.gogit_app.request.AddRepositoryRequest;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.FragmentHelper;
import com.gogit.gogit_app.util.MyToast;


import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class CreateRepoFragment extends Fragment {

    GithubService githubService;
    Retrofit githubRetrofit = GithubRetrofitClient.getRetrofitInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_repository, container, false);

        EditText nameEditText = view.findViewById(R.id.name_editText);
        EditText descEditText = view.findViewById(R.id.desc_editText);
        Button uploadButton = view.findViewById(R.id.upload_button);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group);


        // 유저 정보
        SessionManager sessionManager = new SessionManager(getContext());
        String token = sessionManager.getToken();

        githubService = githubRetrofit.create(GithubService.class);

        uploadButton.setOnClickListener(e -> {
            String name = nameEditText.getText().toString();
            String description = descEditText.getText().toString();

            if (name.length() == 0 || description.length() == 0) {
                MyToast.showToast(getContext(), "빈 칸이 있습니다.");
                return;
            }

            int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

            // private
            if (selectedRadioButtonId == 2131231030) createRepo(token, name, description, false);
            else createRepo(token, name, description, true);

        });

        return view;
    }

    public void createRepo(String token, String name, String description, Boolean isPrivate) {
        AddRepositoryRequest addRepositoryRequest = new AddRepositoryRequest(name, description, isPrivate);

        Call<Map<String, Object>> createRepoCall = githubService.createRepo(
                "Bearer " + token,
                addRepositoryRequest);

        createRepoCall.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                // TODO: 리포지토리 목록 나열할 페이지 만들기
                MyToast.showToast(getContext(), "리포지토리가 추가되었습니다.");
                FragmentHelper.replaceFragment(getFragmentManager(), R.id.containers, new MyPageFragment());
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                MyToast.showToast(getContext(), "네트워크가 불안정합니다.");
            }
        });
    }

}