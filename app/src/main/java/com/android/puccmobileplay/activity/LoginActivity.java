package com.android.puccmobileplay.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.puccmobileplay.R;
import com.android.puccmobileplay.Util.Utils;
import com.android.puccmobileplay.base.AlertDialogCallBack;
import com.android.puccmobileplay.model.ModelController;
import com.android.puccmobileplay.model.bean.UserInfo;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;


/**
 * 登陆界面
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText loginAccount;
    private EditText loginPassword;
    private TextInputLayout mInputLayoutAccount;
    private TextInputLayout mInputLayoutPassword;
    private Button signInButton;
    private Button registerButton;
    private TextView registerText;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupViews();
    }

    private void setupViews() {
        loginAccount = (EditText) findViewById(R.id.account);
        loginPassword = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        registerButton = (Button) findViewById(R.id.register_button);
        registerText = (TextView) findViewById(R.id.register);
        mInputLayoutAccount = (TextInputLayout) findViewById(R.id.text_layout_account);
        mInputLayoutPassword = (TextInputLayout) findViewById(R.id.text_layout_password);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        registerText.setOnClickListener(this);
        signInButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        loginAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkAccount();

            }
        });

        loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkPassword();

            }
        });
    }

    private boolean checkAccount() {
        if (TextUtils.isEmpty(loginAccount.getText())) {
            mInputLayoutAccount.setError("手机号不能为空");
            return false;
        } else {
            mInputLayoutAccount.setError(null);
        }
        return true;
    }

    private boolean checkPassword() {
        if (TextUtils.isEmpty(loginPassword.getText())) {
            mInputLayoutPassword.setError("密码不能为空");
            return false;
        } else {
            mInputLayoutPassword.setError(null);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton) {
            register();
        } else if (v == signInButton) {
           login();
            //跳转页面
         /*   startActivity(new Intent(LoginActivity.this, MainActivity.class));*/
            //销毁登陆页面
         /*   finish();*/
        } else if (v == registerText) {
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    /**
     * 登陆业务
     */
    private void login() {
        if (checkAccount() && checkPassword()) {
            mProgressBar.setVisibility(View.VISIBLE);
            //获取线程请求HX服务器
            ModelController.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    EMClient.getInstance().login(
                            loginAccount.getText().toString().trim(),
                            loginPassword.getText().toString().trim(),
                            new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    UserInfo userInfo = new UserInfo(
                                            loginAccount.getText().toString().trim()
                                    );

                                    //提示HX服务器加载会话
                                    EMClient.getInstance().groupManager().loadAllGroups();
                                    EMClient.getInstance().chatManager().loadAllConversations();
                                    //用户登陆成功Model要处理的业务
                                    ModelController.getInstance().loginSuccess(userInfo);
                                    //保存用户数据
                                    ModelController.getInstance()
                                            .getUserAccountDao()
                                            .addAccount(userInfo);
                                    //隐藏加载界面
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    });
                                    //跳转页面
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    //销毁登陆页面
                                    finish();
                                }

                                @Override
                                public void onError(int i, String s) {
                                    final String ss = s;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Utils.showAlertDialog(LoginActivity.this, "登陆失败", ss, new AlertDialogCallBack() {
                                                @Override
                                                public void startPositive() {

                                                }

                                                @Override
                                                public void startNegative() {

                                                }
                                            });
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }

                                @Override
                                public void onProgress(int i, String s) {
                                }
                            });
                }
            });
        }
    }

    /**
     * 注册业务
     */
    private void register() {
        if (checkAccount() && checkPassword()) {
            Log.i(TAG, "onClick: 注册按钮");
            ModelController.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().createAccount(loginAccount.getText().toString().trim(), loginPassword.getText().toString().trim());

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        Log.e(TAG, "注册失败失败了!!!!!!!!!!!!!!! " + e.getDescription());
                    }
                }
            });
        }
    }
}


