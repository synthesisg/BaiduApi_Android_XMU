package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import android.widget.Toast;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.utils.FileUtils;
import com.baidu.idl.face.main.utils.LogUtils;
import com.baidu.idl.face.main.utils.Utils;
import com.baidu.idl.facesdkdemo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * 闪屏页面，展示SDK版本信息...
 */
public class Base1Activity extends BaseActivity implements View.OnClickListener{

    private Context mContext;

    private Button btnLogin;
    private Button btnRegister;
    private Button btnSetting;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basetest);
        mContext = this;
        initView();
    }

    @Override
    public void onClick(View view){
            switch (view.getId()) {
                case R.id.btn_h_login:
                    LogUtils.d("[base]","btn_h_login");
                    Intent intent = new Intent(mContext, FaceConfigActivity.class);
                    intent.putExtra("page_type", "search");
                    startActivityForResult(intent, 999); //PAGE_TYPE=999 数据来自FaceMainSearchActivity
                    break;
                case R.id.btn_h_register:
                    LogUtils.d("[base]","btn_h_register");
                    startActivity(new Intent(Base1Activity.this, FaceRegisterActivity.class));
                    break;
                case R.id.btn_h_setting:
                    LogUtils.d("[base]","btn_h_setting");
                    startActivity(new Intent(Base1Activity.this, SettingMainActivity.class));
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

        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
    }
}
