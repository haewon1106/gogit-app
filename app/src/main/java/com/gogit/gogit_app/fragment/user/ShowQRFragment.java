package com.gogit.gogit_app.fragment.user;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gogit.gogit_app.R;
import com.gogit.gogit_app.config.Config;
import com.gogit.gogit_app.util.ClipboardHelper;
import com.gogit.gogit_app.util.SessionManager;

public class ShowQRFragment extends Fragment {

    ImageView qrCodeImageView;
    Button linkShareButton;
    public ShowQRFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_q_r, container, false);

        SessionManager sessionManager = new SessionManager(getContext());
        String userId = sessionManager.getUserId();
        qrCodeImageView = view.findViewById(R.id.qr_code);
        linkShareButton = view.findViewById(R.id.link_share_button);

        Glide.with(view)
                .load(Config.QR_CREATE_URL + userId +"\"")
                .error(R.drawable.git_logo)
                .into(qrCodeImageView);

        // url 복사
        linkShareButton.setOnClickListener(e -> {
            ClipboardHelper.copyToClipboard(Config.QR_CREATE_URL.split("\"")[1] + userId, getContext());
        });

        return  view;
    }
}