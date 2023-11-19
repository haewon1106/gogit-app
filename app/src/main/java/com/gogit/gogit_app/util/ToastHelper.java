package com.gogit.gogit_app.util;

import android.content.Context;
import android.widget.Toast;

public class ToastHelper {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showNetworkErrorToast(Context context) {
        Toast.makeText(context, "네트워크가 불안정합니다.", Toast.LENGTH_SHORT).show();
    }

}

