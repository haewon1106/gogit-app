package com.gogit.gogit_app.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

public class ClipboardHelper {
    public static void copyToClipboard(String text, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Gogit URL", text);
        clipboardManager.setPrimaryClip(clipData);

        ToastHelper.showToast(context, "URL이 복사되었습니다.");
    }
}
