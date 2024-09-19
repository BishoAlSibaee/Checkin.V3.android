package com.syriasoft.house_keeping;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.LinearLayout;

import com.syriasoft.hotelservices.R;

public class LoadingDialog {
    Context c;
    Dialog d;

    LoadingDialog(Context c) {
        if (c != null) {
            this.c = c;
            d = new Dialog(c);
            d.setContentView(R.layout.loading_dialog);
            Window w = d.getWindow();
            w.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            w.setBackgroundDrawableResource(R.color.transparent);
            d.setCancelable(false);
        }
    }

    void show() {
        d.show();
    }

    void close() {
        d.dismiss();
    }


}
