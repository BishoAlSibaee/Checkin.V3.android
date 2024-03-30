package com.example.mobilecheckdevice;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class LoadingDialog
{
    private Dialog d ;

    public LoadingDialog(Context c ) {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
    }

    public LoadingDialog(Context c, String Message ) {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        TextView m = d.findViewById(R.id.textViewdfsdf);
        m.setText(Message);
        d.show();
    }
    public void stop()
    {
        d.dismiss();
    }
}
