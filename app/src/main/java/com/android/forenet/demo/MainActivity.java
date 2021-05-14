package com.android.forenet.demo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import net.sdvn.cmapi.Network;
import net.sdvn.cmapi.RealtimeInfo;
import net.sdvn.cmapi.protocal.ConnectStatusListenerPlus;
import net.sdvn.cmapi.protocal.EventObserver;
import net.sdvn.shield.MobileAPI;
import net.sdvn.cmapi.global.Constants;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //监听VPN隧道占用情况、实时监听在线时长或时延等信息
        MobileAPI.subscribe(mEventObserver);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Activity页面恢复时，处理最新状态
        dealLastStatus();
        //添加登录状态监听器
        MobileAPI.addConnectionStatusListener(statusListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //移除状态监听器
        MobileAPI.removeConnectionStatusListener(statusListener);
    }
    private TextView tvStatus,tvUptimeTitle,tvNetworkTitle,tvLatencyTitle;
    private View loading;
    private void initView(){
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvUptimeTitle = (TextView) findViewById(R.id.tvUptimeTitle);
        tvNetworkTitle = (TextView) findViewById(R.id.tvNetworkTitle);
        tvLatencyTitle = (TextView) findViewById(R.id.tvLatencyTitle);
        loading = findViewById(R.id.loading);
    }

    /**
     * 退出登录功能
     * @param view
     */
    public void loginOut(View view){
        //layout中btnLoginOut按钮配置   android:onClick="loginOut"
        //用户点击了退出登录按钮
        if(view.getId() == R.id.btnLoginOut){
            showLoadingProgress();
            //调用退出登录API
            MobileAPI.disconnect();
            //移除帐号信息
            MobileAPI.removeUser(MobileAPI.getBaseInfo().getUserId());
            if(!checkNetwork()){//无网络情况下(无法连接外网)，会自动断开，无需等待回调
                //不再重连
                MobileAPI.cancelLogin();
                //退出登录
                exit();
            }else{//等待连接断开
                showLoadingProgress();
            }
        }
    }

    //上一次刷新时延的时间
    protected long realTimeMillis = 0;
    //刷新的间隔时间
    protected double intervalTime = 0;
    long mOnlineTime = 0;

    public EventObserver mEventObserver = new EventObserver() {
        @Override
        public void onRealTimeInfoChanged(RealtimeInfo info) {
            super.onRealTimeInfoChanged(info);
            long time = System.currentTimeMillis();
            int netLatency = info.getNetLatency();
            if (checkNetwork()){
                tvUptimeTitle.setText("在线时长："+getUptime(mOnlineTime++));
                if (time - realTimeMillis >= intervalTime) {
                    if (netLatency >= 200 || netLatency <= 1) {
                        intervalTime = 900;
                    } else {
                        intervalTime = 4500;
                    }
                    tvLatencyTitle.setText("时延："+ getLatencyText(netLatency));
                    realTimeMillis = time;
                }

            }
        }
        @Override
        public void onTunnelRevoke(boolean isRevoked) {
            super.onTunnelRevoke(isRevoked);
            Toast.makeText(MainActivity.this, "VPN通道被占用", Toast.LENGTH_LONG).show();
        }
    };
    private void showLoadingProgress() {
        loading.setVisibility(View.VISIBLE);
    }

    private void dismissLoadingProgress() {
        loading.setVisibility(View.GONE);
    }

    protected ConnectStatusListenerPlus statusListener = new ConnectStatusListenerPlus() {
        @Override
        public void onConnecting() {
            tvStatus.setText("开始连接");
        }

        @Override
        public void onAuthenticated() {
            tvStatus.setText("认证成功");
        }

        @Override
        public void onConnected() {
            tvStatus.setText("连接成功");
        }

        @Override
        public void onEstablished() {
            String virtualNetworkName = getCurrentVirtualNetworkName();
            tvNetworkTitle.setText("当前网络："+(TextUtils.isEmpty(virtualNetworkName) ? "N/A": virtualNetworkName));
            tvStatus.setText("登录成功");
        }

        @Override
        public void onDisconnecting() {
            tvStatus.setText("断开连接中");
        }

        @Override
        public void onDisconnected(int reason) {//已断开连接
            dismissLoadingProgress();
            tvStatus.setText("已断开");
            if(reason == Constants.DR_BY_USER){//退出登录
                exit();
            }

        }
    };
    private void exit(){
        //清除登录帐号信息
        SharedPreferences sp = getSharedPreferences("user",Context.MODE_PRIVATE);
        sp.edit().clear().apply();
        //退出登录成功，跳转到登录页面
        startActivity(new Intent(MainActivity.this,LoginActivity.class));
        finish();
    }

    /**
     * Activity页面恢复时，处理最新状态
     */
    public void dealLastStatus() {
        if(MobileAPI.getRealtimeInfo()!=null){
            int status = MobileAPI.getRealtimeInfo().getCurrentStatus();
            switch (status){
                case Constants.CS_CONNECTING://正在重连...
                    statusListener.onConnecting();
                    break;
                case Constants.CS_AUTHENTICATED:
                    statusListener.onAuthenticated();
                    break;
                case Constants.CS_CONNECTED:
                    statusListener.onConnected();
                    break;
                case Constants.CS_ESTABLISHED:
                    statusListener.onEstablished();
                    break;
                case Constants.CS_DISCONNECTING:
                    statusListener.onDisconnecting();
                    break;
                case Constants.CS_DISCONNECTED:
                    statusListener.onDisconnected(0);
                    break;

            }
        }
    }
    public String getLatencyText(int latency) {
        String latency_text;
        if (latency >= 1000) {
            latency = latency / 1000;
            latency_text = latency + " s";
        } else latency_text = latency + " ms";
        return latency_text;
    }

    public String getUptime(long time) {
        if (time > 0) {
            int hour = (int) (time / 60 / 60);
            int min = (int) (time / 60 % 60);
            int second = (int) (time % 60);
            final String h = hour > 9 ? "" + hour : "0" + hour;
            final String m = min > 9 ? "" + min : "0" + min;
            final String s = second > 9 ? "" + second : "0" + second;
            return h + ":" + m + ":" + s;
        } else return "00:00:00";
    }

    public  boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager == null ? null : connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /**
     * 获取当前连接的虚拟网络名
     * @return
     */
    public String getCurrentVirtualNetworkName() {
        for (Network network : MobileAPI.getNetworkList()) {
            if (network.isCurrent())
                return network.getName();
        }
        return "";
    }
}
