package com.example.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RenameButtonDialog {
    Dialog D ;

    RenameButtonDialog(Activity act, String buttonName, View.OnClickListener listener) {
        this.D = new Dialog(act);
        D.setContentView(R.layout.rename_button_dialog);
        D.setCancelable(false);
        Window w = D.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        w.setBackgroundDrawableResource(R.color.transparent);
        TextView caption = D.findViewById(R.id.textView47);
        caption.setText("Rename "+buttonName);
        EditText newName = D.findViewById(R.id.editTextTextPersonName4);
        newName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                ScreenButtons.NewName = newName.getText().toString();
            }
        });
        Button save = D.findViewById(R.id.button42);
        save.setOnClickListener(listener);
        ImageButton exit = D.findViewById(R.id.imageView8);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    void show() {
        D.show();
    }

    void close() {
        D.dismiss();
    }
}
