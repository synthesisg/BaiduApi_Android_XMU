package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.db.DBManager;
import com.baidu.idl.face.main.listener.SdkInitListener;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.utils.ConfigUtils;
import com.baidu.idl.face.main.utils.LogUtils;
import com.baidu.idl.face.main.utils.ToastUtils;
import com.baidu.idl.face.main.utils.Utils;
import com.baidu.idl.face.main.utils.FileUtils;
import com.baidu.idl.facesdkdemo.R;


import java.io.*;
import java.util.zip.ZipOutputStream;

public class BaseFileActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;

    private Button btnBaseDownload, btnBaseUpload,btnBaseZip,btnBazeUnzip;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_file);


        mContext = this;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库
        //DBManager.getInstance().release();
    }

    /**
     * UI 相关VIEW 初始化
     */
    private void initView() {
        btnBaseDownload = findViewById(R.id.btn_basedd);
        btnBaseUpload = findViewById(R.id.btn_baseud);
        btnBaseZip = findViewById(R.id.btn_basezip);
        btnBazeUnzip = findViewById(R.id.btn_baseunzip);

        TextView versionTv = findViewById(R.id.tv_version);
        versionTv.setText(String.format(" V %s", Utils.getVersionName(mContext)));

        btnBaseDownload.setOnClickListener(this);
        btnBaseUpload.setOnClickListener(this);
        btnBaseZip.setOnClickListener(this);
        btnBazeUnzip.setOnClickListener(this);
    }

    /**
     * 点击事件跳转路径
     *
     * @param view
     */
    @Override
    public void onClick(View view){
        try {
        //LogUtils.d("[click]","CLICK"+view.getId());
        String path="";
        switch (view.getId()) {

            case R.id.btn_basedd:
                LogUtils.d("[base]","Download");
                break;
            case R.id.btn_baseud:
                LogUtils.d("[base]","Upload");
                break;
            case R.id.btn_basezip:
                LogUtils.d("[base]","Zip");
                path=FileUtils.getSDRootFile().getPath()+"/ademo";
                LogUtils.e("[ZIP_basepath]","path======"+path);
                Toast.makeText(this, "Ziping.", Toast.LENGTH_LONG).show();
                FileUtils.zipFile(new File(path + "/zup"), new ZipOutputStream(new FileOutputStream(path + "/cdx.zip")), "azup", true);
                Toast.makeText(this, "Done.", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_baseunzip:
                LogUtils.d("[base]","Unzip");
                path=FileUtils.getSDRootFile().getPath()+"/ademo";
                LogUtils.e("[UNZIP_basepath]","path======"+path);
                Toast.makeText(this, "Unziping.", Toast.LENGTH_LONG).show();
                FileUtils.unzipFile(new File (path+"/cdx.zip"),path);
                Toast.makeText(this, "Done.", Toast.LENGTH_LONG).show();
                break;
            /*
            case R.id.btn_attribute:
                startActivity(new Intent(BaseFileActivity.this, FaceAttributeRGBActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(BaseFileActivity.this, SettingMainActivity.class));
                break;
            case R.id.btn_auth:
                // 跳转授权激活页面
                startActivity(new Intent(BaseFileActivity.this, FaceAuthActicity.class));
                break;
            case R.id.btn_base1:
                LogUtils.d("[base]","BASE1");
                startActivity(new Intent(BaseFileActivity.this, FrontPageActivity.class));
                break;
            case R.id.btn_base2:
                LogUtils.d("[base]","BASE2");
                startActivity(new Intent(BaseFileActivity.this, Base2Activity.class));  //FaceRGBOpenDebugSearchActivity
                break;
                */
        }
        }catch (Exception ex){}
    }


}
