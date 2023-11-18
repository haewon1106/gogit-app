package com.gogit.gogit_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gogit.gogit_app.R;
import com.gogit.gogit_app.model.github.commit.Commit;
import com.gogit.gogit_app.model.github.commit.RepoCommit;

import java.util.List;

public class CommitAdapter
        extends RecyclerView.Adapter<CommitAdapter.ItemViewHolder> {

    List<RepoCommit> data;

    public CommitAdapter(List<RepoCommit> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CommitAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.commit_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommitAdapter.ItemViewHolder holder, int position) {
        View view = holder.view;
        Commit commit = data.get(position).getCommit();

        TextView commitMessageTextView = view.findViewById(R.id.commit_message);
        TextView commitUserNameTextView = view.findViewById(R.id.commit_item_user_name);

        commitMessageTextView.setText(commit.getMessage());
        commitUserNameTextView.setText(commit.getAuthor().get("name"));
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
