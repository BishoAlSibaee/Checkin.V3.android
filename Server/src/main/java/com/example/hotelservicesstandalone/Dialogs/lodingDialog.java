package com.example.hotelservicesstandalone.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.example.hotelservicesstandalone.R;

public class lodingDialog
{
    private Dialog d ;

    public lodingDialog(Context c) {
        try {
            d = new Dialog(c);
            d.setContentView(R.layout.loading_layout);
            d.setCancelable(false);
            d.show();
        }
        catch (Exception e) {
            Log.d("loadingError",e.getMessage());
        }
    }
    public void stop()
    {
        try {
            d.dismiss();
        }
        catch (Exception e) {
            Log.d("loadingError",e.getMessage());
        }
    }
}
