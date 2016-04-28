package com.example.yaginuma.whatisyourname.widget;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by yaginuma on 15/06/03.
 */
public class ProgressDialogBuilder {
    public static ProgressDialog build(Context context, String message) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(message);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        return dialog;
    }
}
