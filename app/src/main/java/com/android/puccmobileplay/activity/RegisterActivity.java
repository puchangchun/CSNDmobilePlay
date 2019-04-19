package com.android.puccmobileplay.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.puccmobileplay.R;
import com.android.puccmobileplay.Util.Utils;
import com.android.puccmobileplay.base.AlertDialogCallBack;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText registerPhoneEditText;
    private Button nextButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViews();
    }

    private void findViews() {
        registerPhoneEditText = (EditText)findViewById(R.id.register_phone);
        nextButton = (Button)findViewById(R.id.register_next);

        nextButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String phoneNum = registerPhoneEditText.getText().toString();
        if (TextUtils.isEmpty(phoneNum)){
            //跳转到验证码页面

        }else {
            //跳出错误的窗口
            Utils.showAlertDialog(getBaseContext(), "错误", "确认手机号是否正确", new AlertDialogCallBack() {
                @Override
                public void startPositive() {

                }

                @Override
                public void startNegative() {

                }
            });
        }
    }
}
