package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.db.DBManager;
import com.baidu.idl.face.main.listener.SdkInitListener;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.utils.*;
import com.baidu.idl.facesdkdemo.R;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;


    private Button btnDownload, btnUpdateFace,btnUpdateBase,btnInformation,btnLogout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


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
        btnUpdateBase = findViewById(R.id.btn_update_base);
        btnUpdateFace = findViewById(R.id.btn_update_face);
        btnInformation = findViewById(R.id.btn_information);
        btnDownload = findViewById(R.id.btn_download);
        btnLogout = findViewById(R.id.btn_logout);

        TextView versionTv = findViewById(R.id.tv_version);
        versionTv.setText(String.format(" V %s", Utils.getVersionName(mContext)));
        if(PlatformUtils.getInstance().isLogin()==false||PlatformUtils.getInstance().getUser()==null)
        {
            //打回
            PlatformUtils.getInstance().Logout();
            Toast.makeText(WelcomeActivity.this, "非法访问，请重新登录。", Toast.LENGTH_LONG).show();
            startActivity(new Intent(WelcomeActivity.this, FrontPageActivity.class));
            finish();
        }
        TextView titleTv = findViewById(R.id.tv_title);
        if (PlatformUtils.getInstance().getUser()!=null && PlatformUtils.getInstance().getUser().getUserName()!=null)
            titleTv.setText("欢迎，"+ PlatformUtils.getInstance().getUser().getUserName()+"！Group="+PlatformUtils.getInstance().getUser().getGroupId());

        btnUpdateBase.setOnClickListener(this);
        btnUpdateFace.setOnClickListener(this);
        btnInformation.setOnClickListener(this);
        btnDownload.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
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
            Intent intent=null;
            switch (view.getId()) {
                case R.id.btn_update_base:
                    Log.e("[base]","btn_update_base");
                    intent = new Intent(mContext, FaceRegisterActivity.class);
                    intent.putExtra("page_type", "update_base");
                    startActivity(intent);
                    break;
                case R.id.btn_update_face:
                    Log.e("[base]","btn_update_face");
                    intent = new Intent(mContext, FaceRGBRegisterActivity.class);
                    intent.putExtra("page_type", "update_face");
                    startActivity(intent);
                    break;
                case R.id.btn_information://activity_user_info.xml
                    Log.e("[base]","btn_information");
                    intent = new Intent(mContext, FaceUserInfoActivity.class);
                    intent.putExtra("page_type", "user_info");
                    startActivity(intent);
                    break;
                case R.id.btn_download:
                    Log.e("[base]","btn_download");
                    intent = new Intent(mContext, FileDownloadActivity.class);
                    intent.putExtra("page_type", "download");
                    startActivity(intent);
                    break;
                case R.id.btn_logout:
                    Log.e("[base]","btn_logout");
                    PlatformUtils.getInstance().Logout();
                    startActivity(new Intent(mContext, FrontPageActivity.class));
                    break;
            }
        }catch (Exception ex){}
    }


}
