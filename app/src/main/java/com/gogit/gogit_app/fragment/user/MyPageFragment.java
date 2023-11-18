package com.gogit.gogit_app.fragment.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gogit.gogit_app.R;
import com.gogit.gogit_app.adapter.OrgsAdapter;
import com.gogit.gogit_app.adapter.PostAdapter;
import com.gogit.gogit_app.adapter.RepoAdapter;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.client.PostRetrofitClient;
import com.gogit.gogit_app.config.SessionManager;
import com.gogit.gogit_app.fragment.modal.OrgModalFragment;
import com.gogit.gogit_app.model.github.org.Organization;
import com.gogit.gogit_app.model.github.user.GithubUser;
import com.gogit.gogit_app.model.Post;
import com.gogit.gogit_app.model.github.repo.Repository;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.service.PostService;
import com.gogit.gogit_app.util.FragmentHelper;
import com.gogit.gogit_app.util.MyToast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyPageFragment extends Fragment {

    // view
    TextView userIdTextView;
    ImageView profileImageView;
    TextView repoTextview;
    TextView followerTextView;
    TextView usernameTextView;
    LinearLayout followerLayout;
    TextView orgCountTextView;
    LinearLayout orgLayout;
    RecyclerView postsView;
    RecyclerView reposView;
    ImageButton repImageButton;
    ImageButton postImageButton;
    Button profileShareButton;


    public MyPageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // 저장된 유저 정보
        SessionManager sessionManager = new SessionManager(getContext());
        String login = sessionManager.getUserId();
        String token = sessionManager.getToken();
        Long pk = sessionManager.getPk();

        // 뷰
        userIdTextView = view.findViewById(R.id.userId);
        profileImageView = view.findViewById(R.id.profileImg);
        repoTextview = view.findViewById(R.id.account_repositories);
        followerTextView = view.findViewById(R.id.account_follower);
        usernameTextView = view.findViewById(R.id.userName);
        followerLayout = view.findViewById(R.id.follower_layout);
        orgCountTextView = view.findViewById(R.id.account_organization);
        orgLayout = view.findViewById(R.id.org_view);
        repImageButton = view.findViewById(R.id.rep_image_button);
        postImageButton = view.findViewById(R.id.post_image_button);
        reposView = view.findViewById(R.id.repo_recyclerview);
        postsView = view.findViewById(R.id.post_recyclerview);
        profileShareButton = view.findViewById(R.id.qr_share_button);

        followerLayout.setOnClickListener(e -> {
            FragmentHelper.replaceFragment(getActivity().getSupportFragmentManager(),
                    R.id.containers,
                    new FollowerFragment());
        });

        orgLayout.setOnClickListener(e -> {
            OrgModalFragment orgModalFragment = new OrgModalFragment();
            orgModalFragment.show(getActivity().getSupportFragmentManager(), "dialog");
        });

        repImageButton.setOnClickListener(e -> {
            reposView.setVisibility(View.VISIBLE);
            postsView.setVisibility(View.GONE);
            Log.d("my tag", "rep click");
        });

        postImageButton.setOnClickListener(e -> {
            reposView.setVisibility(View.GONE);
            postsView.setVisibility(View.VISIBLE);
            Log.d("my tag", "post click");
        });

        profileShareButton.setOnClickListener(e -> {
            FragmentHelper.replaceFragment(
                    getActivity().getSupportFragmentManager(),
                    R.id.containers,
                    new ShowQRFragment()
            );
        });

        // 글 리사이클러 뷰
        postsView.setHasFixedSize(false);
        postsView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 리포 리사이클러 뷰
        reposView.setHasFixedSize(false);
        reposView.setLayoutManager(new LinearLayoutManager(getContext()));


        loadAndSetUserInfo(token, login);
        loadAndSetPosts(pk);
        loadAndSetRepos(token, login);
        setUsersOrgsCount(token, login);

        return view;
    }

    // 유저 정보 api요청
    private void loadAndSetUserInfo(String token, String login) {
        // 유저 정보
        Retrofit githubRetrofit = GithubRetrofitClient.getRetrofitInstance();
        GithubService githubService = githubRetrofit.create(GithubService.class);
        Call<GithubUser> userCall = githubService.getUser("Bearer " + token, login);

        // 유저 정보 가져오기
        userCall.enqueue(new Callback<GithubUser>() {
            @Override
            public void onResponse(Call<GithubUser> call, Response<GithubUser> response) {
                if (response.code() == 404) {
                    // 오류 처리
                } else {
                    GithubUser user = response.body();
                    if (user != null) {
                        setUserInfo(user);
                    }
                }
            }

            @Override
            public void onFailure(Call<GithubUser> call, Throwable t) {
                MyToast.showNetworkErrorToast(getContext());
            }
        });
    }

    // 뷰에 정보 할당
    private void setUserInfo(GithubUser user) {
        userIdTextView.setText(user.getLogin());
        usernameTextView.setText(user.getName());
        Glide.with(getContext())
                .load(user.getAvatar_url())
                .apply(RequestOptions.circleCropTransform())
                .error(R.drawable.git_logo)
                .placeholder(R.drawable.git_logo)
                .into(profileImageView);
        repoTextview.setText((user.getPublic_repos() + user.getTotal_private_repos()) + "");
        followerTextView.setText(user.getFollowers() + "");
    }

    private void setUsersOrgsCount(String token, String login) {
        Retrofit githubRetrofit = GithubRetrofitClient.getRetrofitInstance();
        GithubService githubService = githubRetrofit.create(GithubService.class);
        Call<List<Organization>> call = githubService.getUsersOrgs(
                "Bearer " + token, login
        );

        call.enqueue(new Callback<List<Organization>>() {
            @Override
            public void onResponse(Call<List<Organization>> call, Response<List<Organization>> response) {
                List<Organization> orgs = response.body();
                if (orgs != null) {
                    orgCountTextView.setText(orgs.size() + "");
                }
            }

            @Override
            public void onFailure(Call<List<Organization>> call, Throwable t) {
                MyToast.showNetworkErrorToast(getContext());
            }
        });
    }

    private void loadAndSetPosts(Long pk) {
        Retrofit retrofit = PostRetrofitClient.getRetrofitInstance();
        PostService postService = retrofit.create(PostService.class);

        Call<List<Post>> postListCall = postService.getMembersAllPosts(pk);
        postListCall.enqueue(new Callback<List<Post>>(){
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if (response.code() != 404) {
                    List<Post> posts = response.body();
                    PostAdapter postAdapter = new PostAdapter(posts);
                    postsView.setAdapter(postAdapter);
                    Log.d("my tag", posts.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                MyToast.showNetworkErrorToast(getContext());
                Log.d("my tag", t.getMessage());
            }
        });
    }

    private void loadAndSetRepos(String token, String login) {
        Retrofit githubRetrofit = GithubRetrofitClient.getRetrofitInstance();
        GithubService githubService = githubRetrofit.create(GithubService.class);

        Call<List<Repository>> call = githubService.getRepos(
                "Bearer " + token, login);
        // 레포 뷰 불러오기
        call.enqueue(new Callback<List<Repository>>() {
            @Override
            public void onResponse(Call<List<Repository>> call, Response<List<Repository>> response) {
                List<Repository> repos = response.body();
                RepoAdapter repoAdapter = new RepoAdapter(repos);
                reposView.setAdapter(new RepoAdapter(repos));
                Log.d("my tag", repos.toString());
            }

            @Override
            public void onFailure(Call<List<Repository>> call, Throwable t) {
                Log.d("my tag", t.getMessage());
            }
        });
    }
}