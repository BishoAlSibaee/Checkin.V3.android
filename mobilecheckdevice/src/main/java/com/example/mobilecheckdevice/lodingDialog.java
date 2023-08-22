package com.example.mobilecheckdevice;
import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

public class lodingDialog
{
    private Dialog d ;

    public lodingDialog(Context c ) {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
    }

    public lodingDialog(Context c,String Message ) {
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
