package com.gogit.gogit_app.fragment.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.FollowerAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.config.Config;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.model.github.user.GithubUser;
import com.gogit.gogit_app.service.GithubService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FollowerFragment extends Fragment {
    public FollowerFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view = inflater.inflate(R.layout.follower, container, false);

        // 로그인 정보 가져오기
        SessionManager sessionManager = new SessionManager(getContext());
        String login = sessionManager.getUserId();

        // 이전 버튼 액션 리스너
        ImageButton prevButton = view.findViewById(R.id.prev_button);
        prevButton.setOnClickListener(e -> {
            // 프래그먼트 트랜잭션 시작
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            // Follower 프래그먼트를 생성하고 추가
            MyPageFragment myPageFragment = new MyPageFragment();
            transaction.replace(R.id.containers, myPageFragment);
            transaction.addToBackStack(null); // 이전 상태를 백 스택에 추가

            // 트랜잭션 커밋
            transaction.commit();
        });


        Retrofit githubRetrofit = GithubRetrofitClient.getRetrofitInstance();
        GithubService githubService = githubRetrofit.create(GithubService.class);

        RecyclerView followerView = view.findViewById(R.id.follow_list);
        followerView.setHasFixedSize(false);
        followerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Call<List<GithubUser>> call = githubService.getFollowers(
                "Bearer " + Config.GITHUB_TOKEN, login);

        call.enqueue(new Callback<List<GithubUser>>() {

            @Override
            public void onResponse(Call<List<GithubUser>> call, Response<List<GithubUser>> response) {
                if (response.code() != 404) {
                    List<GithubUser> githubUsers = response.body();

                    followerView.setAdapter(new FollowerAdapter(githubUsers));
                }
            }

            @Override
            public void onFailure(Call<List<GithubUser>> call, Throwable t) {
                Log.d("my tag", t.getMessage());
            }
        });
        return view;
    }
}