package com.gogit.gogit_app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.gogit.gogit_app.R;
import com.gogit.gogit_app.model.github.org.Organization;

import java.util.List;

public class OrgsAdapter
        extends RecyclerView.Adapter<OrgsAdapter.ItemViewHolder> {

    List<Organization> data;

    public OrgsAdapter(List<Organization> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public OrgsAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.org_item, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrgsAdapter.ItemViewHolder holder, int position) {
        View view = holder.view;
        Organization org = data.get(position);

        ImageView orgProfileImageView = view.findViewById(R.id.org_profile);
        TextView orgNameTextView = view.findViewById(R.id.org_name);

        Glide.with(view)
                .load(org.getAvatar_url())
                .error(R.drawable.git_logo)
                .into(orgProfileImageView);

        orgNameTextView.setText(org.getLogin());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public View view;

        public ItemViewHolder(@androidx.annotation.NonNull View itemView) {
            super(itemView);
            this.view = itemView;
        }
    }
}
