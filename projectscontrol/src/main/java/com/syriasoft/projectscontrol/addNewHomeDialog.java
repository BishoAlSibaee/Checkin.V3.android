package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.syriasoft.projectscontrol.RequestCallBacks.RequestCallback;

public class addNewHomeDialog {

    Dialog D ;

    addNewHomeDialog(Activity act, String pName) {
        this.D = new Dialog(act);
        D.setContentView(R.layout.add_new_home_dialog);
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
                ProjectActivity.New_Home_Name = name.getText().toString();
            }
        });
        Button add = D.findViewById(R.id.button6);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectActivity.createTuyaHome(pName+ProjectActivity.New_Home_Name, new RequestCallback() {
                    @Override
                    public void onSuccess(String result) {
                        new MessageDialog("project created","Done",act);
                        D.dismiss();
                    }

                    @Override
                    public void onFailed(String error) {
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
