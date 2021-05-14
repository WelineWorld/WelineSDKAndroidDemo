package com.android.forenet.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import net.sdvn.cmapi.protocal.ConnectStatusListenerPlus;
import net.sdvn.cmapi.protocal.ResultListener;
import net.sdvn.shield.MobileAPI;

/**
 * @author Raleigh.Luo
 * date：20/9/4 14
 * describe：
 */
import net.sdvn.cmapi.global.Constants;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    private EditText etAccount, etVerifyCode;
    private Button btnLogin;
    private View loading;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //配置你的登录认证服务器地址，如 net.cmhk.com
        MobileAPI.setLoginAsHost("test.memenet.net");
        initView();
//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED)
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        //如果您希望登录后，下次进入APP能自动登录
//        autoLogin();
    }

    public void login(View view) {
        clapseSoftInputMethod(this);
        //layout中btnLogin按钮配置   android:onClick="login"
        //用户点击了登录按钮
        if (view.getId() == R.id.btnLogin) {
//            //获取用户输入的手机号码
//            String account = etAccount.getText().toString();
//            String password = etVerifyCode.getText().toString();
            String account = "luoli214336774";
            String password = "luoli214336774.";
//            //请求Token
//            String token = requestToken(account);
//            //调用平台短信验证码登录API
            MobileAPI.login(account, password, new ResultListener() {
                @Override
                public void onError(int errorCode) {
                    if (errorCode == Constants.DR_CALL_THIRD_API_FAIL || errorCode == Constants.DR_INVALID_CODE) {
                        Toast.makeText(LoginActivity.this, "token错误", Toast.LENGTH_LONG).show();
                    } else if (errorCode == Constants.DR_INVALID_USER) {
                        Toast.makeText(LoginActivity.this, "用户名错误", Toast.LENGTH_LONG).show();
                    } else if (errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED) {
                        Toast.makeText(LoginActivity.this, "VPN通道被占用", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "登录失败 errorCode=" + errorCode, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void initView() {
        etAccount = (EditText) findViewById(R.id.etAccount);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        loading = findViewById(R.id.loading);
        etVerifyCode = (EditText) findViewById(R.id.etVerifyCode);
        etAccount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnLogin.setEnabled(etAccount.getText().length() > 0);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void autoLogin() {
        if (MobileAPI.getBaseInfo() != null) {
            //获取上一次登录帐号
            SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
            String lastLoginAccount = sp.getString("account", null);
            if (!TextUtils.isEmpty(lastLoginAccount)) {
                etAccount.setText(lastLoginAccount);
                //调用自动登录API
                MobileAPI.loginByTicket(lastLoginAccount, new ResultListener() {
                    @Override
                    public void onError(int errorCode) {
                        if (errorCode == Constants.DR_VPN_TUNNEL_IS_OCCUPIED) {
                            Toast.makeText(LoginActivity.this, "VPN通道被占用", Toast.LENGTH_LONG).show();
                        } else if (errorCode == Constants.CE_INVALID_TICKET) {
                            //登录信息被清除，如退出登录，
                            Toast.makeText(LoginActivity.this, "登录信息已失效或已被清除，无法自动登录", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "登录失败 errorCode=" + errorCode, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }

    /**
     * 关闭虚拟键盘
     *
     * @param activity
     */
    public void clapseSoftInputMethod(Activity activity) {

        try {//activity
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive())
                //键盘是打开的状态
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }

    }


    private void showLoadingProgress() {
        loading.setVisibility(View.VISIBLE);
    }

    private void dismissLoadingProgress() {
        loading.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //添加监听器
        MobileAPI.addConnectionStatusListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //移除监听器
        MobileAPI.removeConnectionStatusListener(listener);
    }

    private ConnectStatusListenerPlus listener = new ConnectStatusListenerPlus() {
        @Override
        public void onConnecting() {//正在登录
            Log.d(TAG, "1--开始连接");
            //显示加载进度条
            showLoadingProgress();
        }

        @Override
        public void onAuthenticated() {
            Log.d(TAG, "2--认证成功");
        }

        @Override
        public void onConnected() {
            Log.d(TAG, "3--连接成功");
        }

        @Override
        public void onEstablished() {//登录成功
            Log.d(TAG, "4--登录成功");
            dismissLoadingProgress();
            SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
            //保存登录帐号
            sp.edit().putString("account", etAccount.getText().toString()).apply();
            //如登录页面，跳转到主页
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        @Override
        public void onDisconnecting() {
            Log.d(TAG, "5--断开连接中");

        }


        @Override
        public void onDisconnected(int i) {
            dismissLoadingProgress();
            Log.d(TAG, "6--已断开");
        }
    };


}
