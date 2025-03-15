package com.syriasoft.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;

public class ScanningDialog {

    private final Dialog d ;

    public ScanningDialog(Context c, String Message) {
        d = new Dialog(c);
        d.setContentView(R.layout.scanning_dialog);
        d.setCancelable(false);
        Window w = d.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView message = d.findViewById(R.id.textViewdfsdf);
        Button stop = d.findViewById(R.id.button27);
        stop.setOnClickListener(view -> d.dismiss());
        message.setText(Message);
        d.show();
    }

    public Dialog show() {
        d.show();
        return d;
    }

    public void close()
    {
        d.dismiss();
    }
}
