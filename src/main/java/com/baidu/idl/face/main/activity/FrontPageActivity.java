package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.utils.LogUtils;
import com.baidu.idl.facesdkdemo.R;

/**
 * 闪屏页面，展示SDK版本信息...
 */
public class FrontPageActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext;

    private Button btnLogin;
    private Button btnRegister;
    private Button btnSetting;
    private Button btnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        mContext = this;
        initView();
    }

    @Override
    public void onClick(View view){
        Intent intent=null;
            switch (view.getId()) {
                case R.id.btn_h_login:
                    LogUtils.d("[base]","btn_h_login");
                    //intent = new Intent(mContext, FaceConfigActivity.class);
                    intent = new Intent(mContext, FaceRGBCloseDebugSearchActivity.class);
                    intent.putExtra("page_type", "login");
                    startActivity(intent); //PAGE_TYPE=999 数据来自FaceMainSearchActivity
                    break;
                case R.id.btn_h_register:
                    LogUtils.d("[base]","btn_h_register");
                    intent = new Intent(mContext, FaceRegisterActivity.class);
                    intent.putExtra("page_type", "register");
                    startActivity(intent);
                    break;
                case R.id.btn_h_setting:
                    LogUtils.d("[base]","btn_h_setting");
                    startActivity(new Intent(mContext, SettingMainActivity.class));
                    break;
                case R.id.btn_h_back:
                    LogUtils.d("[base]","btn_h_back");
                    startActivity(new Intent(mContext, MainActivity.class));
                    break;
            }
    }

    /**
     * UI 相关VIEW 初始化
     */
    private void initView() {
        btnLogin = findViewById(R.id.btn_h_login);
        btnRegister = findViewById(R.id.btn_h_register);
        btnSetting = findViewById(R.id.btn_h_setting);
        btnBack = findViewById(R.id.btn_h_back);

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }
}
