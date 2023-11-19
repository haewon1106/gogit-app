package com.gogit.gogit_app.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.PostAdapter;
import com.gogit.gogit_app.client.PostRetrofitClient;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.model.Post;
import com.gogit.gogit_app.service.PostService;
import com.gogit.gogit_app.util.FragmentHelper;
import com.gogit.gogit_app.util.ToastHelper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment {

    EditText searchEditText;
    RecyclerView postsView;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        Long pk = sessionManager.getPk();

        searchEditText = view.findViewById(R.id.search);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = searchEditText.getText().toString().trim();

            // 아무것도 검색하지 않았을 때 처리
            if (keyword.length() == 0) {
                ToastHelper.showToast(getContext(), "검색어를 입력하세요.");
                return false;
            }

            // 엔터키를 눌렀을 때
            if (actionId == 0) {
                SearchResultFragment searchResultFragment = SearchResultFragment.newInstance(keyword);
                FragmentHelper.replaceFragment(getFragmentManager(),
                        R.id.containers, searchResultFragment);
                return true;
            }

            return false;
        });

        postsView = view.findViewById(R.id.post_recyclerview);
        postsView.setHasFixedSize(false);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 글 정보 불러오기
        loadAndSetPosts();

        return view;
    }

    private void loadAndSetPosts() {
        Retrofit retrofit = PostRetrofitClient.getRetrofitInstance();
        PostService postService = retrofit.create(PostService.class);
        Call<List<Post>> postListCall = postService.getAllPosts();
        postListCall.enqueue(new Callback<List<Post>>(){

            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.code() != 404) {
                    List<Post> posts = response.body();
                    postsView.setAdapter(new PostAdapter(posts));
                    Log.d("my tag", posts.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d("my tag", t.getMessage());
            }
        });
    }
}