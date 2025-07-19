package com.syriasoft.server.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.hotelservicesstandalone.R;

import java.util.Objects;

public class loadingDialog
{
    private Dialog d ;
    TextView messageText;

    public loadingDialog(Context c) {
        try {
            d = new Dialog(c);
            d.setContentView(R.layout.loading_layout);
            d.setCancelable(false);
            messageText = d.findViewById(R.id.textViewdfsdf);
            d.show();
        }
        catch (Exception e) {
            Log.d("loadingError", Objects.requireNonNull(e.getMessage()));
        }
    }

    public void setMessage(String message) {
        messageText.setText(message);
    }


    public void stop() {
        try {
            d.dismiss();
        }
        catch (Exception e) {
            Log.d("loadingError", Objects.requireNonNull(e.getMessage()));
        }
    }
}
