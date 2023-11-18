package com.gogit.gogit_app.fragment.post;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.client.PostRetrofitClient;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.fragment.main.HomeFragment;
import com.gogit.gogit_app.request.AddPostRequest;
import com.gogit.gogit_app.service.PostService;
import com.gogit.gogit_app.util.FragmentHelper;
import com.gogit.gogit_app.util.MyToast;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatePostFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_post, container, false);

        // sesstion manager
        SessionManager sessionManager = new SessionManager(getContext());
        Log.d("my tag", sessionManager.getToken());
        Log.d("my tag", sessionManager.getUserId());

        // view
        EditText contentEditText = view.findViewById(R.id.content_edittext);
        Button uploadButton = view.findViewById(R.id.upload_button);

        contentEditText.addTextChangedListener(new TextWatcher() {
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
        // retrofit
        Retrofit retrofit = PostRetrofitClient.getRetrofitInstance();
        PostService postService = retrofit.create(PostService.class);

        // TODO: 글이 작성되어 있을 때만 버튼 클릭할 수 있게 하기

        uploadButton.setOnClickListener(e -> {

            String content = contentEditText.getText().toString();
            AddPostRequest addPostRequest = new AddPostRequest(sessionManager.getPk(), content);

            Log.d("my tag", addPostRequest.toString());
            Call<Map<String, Object>> call = postService.createdPost(addPostRequest);

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d("my tag", response.toString());

                    if (response.isSuccessful()) {
                        FragmentHelper.replaceFragment(
                                getActivity().getSupportFragmentManager(),
                                R.id.containers,
                                new HomeFragment());
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Log.d("my tag", "onFailure: " + t.getMessage());
                    MyToast.showToast(getContext(), "네트워크 에러 발생");
                }
            });

        });

        return view;
    }
}