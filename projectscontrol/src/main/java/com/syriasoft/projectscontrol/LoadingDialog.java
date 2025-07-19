package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

public class LoadingDialog {

    private Dialog D ;

    LoadingDialog(Activity act) {
        D = new Dialog(act);
        D.setContentView(R.layout.loading_dialog);
        D.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));;
        D.setCancelable(false);
    }

    LoadingDialog(Activity act,String message) {
        D = new Dialog(act);
        D.setContentView(R.layout.loading_dialog);
        TextView Message = D.findViewById(R.id.textView);
        Message.setText(message);
        D.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));;
        D.setCancelable(false);
    }

    LoadingDialog(Activity act,boolean cancellable) {
        D = new Dialog(act);
        D.setContentView(R.layout.loading_dialog);
        D.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));;
        D.setCancelable(cancellable);
    }

    void show() {
        D.show();
    }

    void close() {
        D.dismiss();
    }
}
