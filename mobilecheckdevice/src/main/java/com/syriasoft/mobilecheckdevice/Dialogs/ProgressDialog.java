package com.syriasoft.mobilecheckdevice.Dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;

public class ProgressDialog {

    Dialog D;

    public ProgressDialog(Activity act, String dialogName, int max) {
        D = new Dialog(act);
        D.setContentView(R.layout.progress_dialog);
        Window w = D.getWindow();
        if (w != null) {
            w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            w.setBackgroundDrawableResource(R.color.transparent);
        }
        D.setCancelable(false);
        TextView dName = D.findViewById(R.id.textView52);
        dName.setText(dialogName);
        ProgressBar P = D.findViewById(R.id.progressBar);
        P.setProgress(0);
        P.setMax(max);
    }

    public void setProgress(int progress) {
        ProgressBar P = D.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            P.setProgress(progress,true);
        }
        else {
            P.setProgress(progress);
        }
        if (progress == P.getMax()) {
            close();
        }
    }

    public void setProgress(int progress,String text) {
        ProgressBar P = D.findViewById(R.id.progressBar);
        setText(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            P.setProgress(progress,true);
        }
        else {
            P.setProgress(progress);
        }
        if (progress == P.getMax()) {
            close();
        }
    }

    public void setText(String text) {
        TextView dName = D.findViewById(R.id.textView52);
        dName.setText(text);
    }

    public void close() {
        D.dismiss();
    }

    public void show() {
        D.show();
    }
}
