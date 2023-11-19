package com.gogit.gogit_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.client.GithubRetrofitClient;
import com.gogit.gogit_app.util.SessionManager;
import com.gogit.gogit_app.model.github.issue.Issue;
import com.gogit.gogit_app.service.GithubService;
import com.gogit.gogit_app.util.ToastHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class IssueAdapter
        extends RecyclerView.Adapter<IssueAdapter.ItemViewHolder> {

    List<Issue> data;

    public IssueAdapter(List<Issue> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.issues_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueAdapter.ItemViewHolder holder, int position) {
        View view = holder.view;
        Issue issue = data.get(position);

        TextView issueAddressTextView = view.findViewById(R.id.issues_address);
        TextView issueTitleTextView = view.findViewById(R.id.issues_title);
        Button lockButton = view.findViewById(R.id.issues_check);

        SessionManager sessionManager = new SessionManager(view.getContext());
        String token = sessionManager.getToken();
        String login = sessionManager.getUserId();

        issueAddressTextView.setText(issue.getRepository().getFull_name());
        issueTitleTextView.setText(issue.getTitle());

        lockButton.setOnClickListener(e -> {
            Retrofit retrofit = GithubRetrofitClient.getRetrofitInstance();
            GithubService githubService = retrofit.create(GithubService.class);
            HashMap<String, String> state = new HashMap<>();
            state.put("state", "closed");

            Call<Map<String, Object>> call = githubService.closeIssue(
                    "Bearer " + token,
                    login,
                    issue.getRepository().getFull_name().split("/")[1],
                    issue.getNumber(),
                    state
                    );

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    ToastHelper.showToast(view.getContext(), "이슈가 close 되었습니다.");

                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    ToastHelper.showNetworkErrorToast(view.getContext());
                }
            });
        });


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }

}
