package com.example.hotelservicesstandalone.Dialogs;
import android.app.Dialog;
import android.content.Context;

import com.example.hotelservicesstandalone.R;

public class lodingDialog
{
    private Dialog d ;

    public lodingDialog(Context c )
    {
        d = new Dialog(c);
        d.setContentView(R.layout.loading_layout);
        d.setCancelable(false);
        d.show();
    }
    public void stop()
    {
        d.dismiss();
    }
}
