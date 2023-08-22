package com.example.mobilecheckdevice;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuya.smart.sdk.api.ITuyaActivator;

public class ScanningDialog {

    private Dialog d ;
    private TextView message ;
    private Button stop ;

    public ScanningDialog(Context c, String Message) {
        d = new Dialog(c);
        d.setContentView(R.layout.scanning_dialog);
        d.setCancelable(false);
        Window w = d.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        message = d.findViewById(R.id.textViewdfsdf);
        stop = d.findViewById(R.id.button27);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
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
