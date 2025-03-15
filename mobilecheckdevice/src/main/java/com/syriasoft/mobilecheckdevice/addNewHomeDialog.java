package com.syriasoft.mobilecheckdevice;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.syriasoft.mobilecheckdevice.Interface.RequestCallbackResult;
import com.example.mobilecheckdevice.R;

public class addNewHomeDialog {

    Dialog D ;

    addNewHomeDialog(Activity act, String pName) {
        this.D = new Dialog(act);
        D.setContentView(R.layout.add_new_home_dialog);
        Window w = D.getWindow();
        w.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView projectName = D.findViewById(R.id.textView24);
        projectName.setText(pName);
        EditText name = D.findViewById(R.id.editTextTextPersonName9);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Rooms.New_Home_Name = name.getText().toString();
            }
        });
        Button add = D.findViewById(R.id.button6);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Rooms.createTuyaHome(pName+Rooms.New_Home_Name, new RequestCallbackResult() {
                    @Override
                    public void onSuccess(String result) {
                        new MessageDialog("home created","Done",act);
                        D.dismiss();
                    }

                    @Override
                    public void onFail(String error) {
                        new MessageDialog(error,"error",act);
                    }
                });
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
