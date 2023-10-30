package com.syriasoft.projectscontrol;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VerificationCodeDialog {

    Dialog D ;

    VerificationCodeDialog(Activity act,String title) {
        this.D = new Dialog(act);
        this.D.setContentView(R.layout.verification_code_dialog);
        TextView Title = D.findViewById(R.id.textView20);
        Title.setText(title);
        EditText code = D.findViewById(R.id.editTextTextPersonName8);
        Button continu = D.findViewById(R.id.button4);
        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText() == null || code.getText().toString().isEmpty()) {
                    new MessageDialog("please enter code","code?",act);
                    return;
                }
                AddNewProject.Code = code.getText().toString();
                D.dismiss();
                AddNewProject.ContinueWithVerificationCode(act,AddNewProject.Email,AddNewProject.Password,AddNewProject.pName,AddNewProject.City,AddNewProject.Salesman,AddNewProject.Url,AddNewProject.pId,AddNewProject.cId);
            }
        });
    }

    void show() {
        this.D.show();
    }
}
