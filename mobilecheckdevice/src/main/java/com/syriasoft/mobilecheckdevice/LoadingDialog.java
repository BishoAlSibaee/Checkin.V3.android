package com.syriasoft.mobilecheckdevice;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.example.mobilecheckdevice.R;

public class LoadingDialog
{
    private Dialog d ;

    public LoadingDialog(Context c ) {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        Window w = d.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        d.show();
    }

    public LoadingDialog(Context c, String Message ) {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        Window w = d.getWindow();
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView m = d.findViewById(R.id.textViewdfsdf);
        m.setText(Message);
        d.show();
    }
    public void stop()
    {
        d.dismiss();
    }
}
