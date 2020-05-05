package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.face.main.utils.PlatformUtils;
import com.baidu.idl.facesdkdemo.R;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.baidu.idl.face.main.activity.FaceMainSearchActivity.PAGE_TYPE;


/**
 * @Time: 2019/5/24
 * @Author: v_zhangxiaoqing01
 * @Description: 注册页面
 */

public class FaceRegisterActivity extends BaseActivity implements View.OnClickListener {


    public static final int SOURCE_REG = 1;
    private Context mContext;
    public static final int PICK_REG_VIDEO = 100;
    private EditText usernameEt;
    private EditText userGroupEt;
    private EditText userInfoEt;

    private Button autoDetectBtn;
    private ImageButton settingButton;
    private ImageButton backBtn;

    private static final int TEXT_LENGTH = 20;
    private static final int USERINFO_LENGTH = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_register);
        mContext = this;
        initView();

    }

    public void initView() {


        usernameEt = (EditText) findViewById(R.id.username_et);
        userGroupEt = (EditText) findViewById(R.id.userGroup_et);
        autoDetectBtn = (Button) findViewById(R.id.auto_detect_btn);
        settingButton = (ImageButton) findViewById(R.id.id_reg_setting);
        backBtn = (ImageButton) findViewById(R.id.id_reg_back);
        userInfoEt = (EditText) findViewById(R.id.user_info_tx);

        autoDetectBtn.setOnClickListener(this);
        settingButton.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        if(getIntent().getStringExtra("page_type")==null) return;

        Log.e("update_base_type",getIntent().getStringExtra("page_type"));
        if("update_base".equals(getIntent().getStringExtra("page_type")))
        {
            //填写默认信息
            User user=PlatformUtils.getInstance().getUser();
            usernameEt.setText(user.getUserName());
            userGroupEt.setText(user.getGroupId());
            userInfoEt.setText(user.getUserInfo());

            //UI修改
            TextView textTitle = findViewById(R.id.tv_title);
            textTitle.setText("修改信息");
            autoDetectBtn.setText("保存修改");
        }
    }


    // 正则只支持数字与字符
    private static final Pattern pattern = Pattern.compile("^[A-Za-z0-9]+$");

    @Override
    public void onClick(View view) {

        if (view == autoDetectBtn) {

            final String username = usernameEt.getText().toString().trim();
            String now_name = null;
            if(PlatformUtils.getInstance().getUser()!=null) now_name=PlatformUtils.getInstance().getUser().getUserName();
            if (TextUtils.isEmpty(username)) {
                Toast.makeText(FaceRegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }

            Matcher matcher = pattern.matcher(username);
            if (!matcher.matches()) {
                Toast.makeText(FaceRegisterActivity.this, "用户名由数字、字母中的一个或者多个组成", Toast.LENGTH_SHORT).show();
                return;
            }
            if (username.length() > TEXT_LENGTH) {
                Toast.makeText(FaceRegisterActivity.this, "用户名输入长度超过限制！", Toast.LENGTH_SHORT).show();
                return;
            }
            String groupId = null;
            String groupStr = userGroupEt.getText().toString();
            if (TextUtils.isEmpty(groupStr)) {
                groupId = "default";
            } else {
                groupId = userGroupEt.getText().toString().trim();
            }
            if (groupId.length() > TEXT_LENGTH) {
                Toast.makeText(FaceRegisterActivity.this, "用户组输入长度超过限制！", Toast.LENGTH_SHORT).show();
                return;
            }

            matcher = pattern.matcher(groupId);
            if (!matcher.matches()) {
                Toast.makeText(FaceRegisterActivity.this, "groupId由数字、字母中的一个或者多个组成",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // 获取用户
            List<User> listUsers = FaceApi.getInstance().getUserListByUserName(groupId, username);
            if (listUsers != null && listUsers.size() > 0) {
                for (User user : listUsers) {
                    String DBUserName = user.getUserName();
                    if (username.equals(DBUserName) && (DBUserName.equals(now_name)==false)) {
                        Toast.makeText(FaceRegisterActivity.this, "注册失败，有重名用户！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            final String userInfo = userInfoEt.getText().toString().trim();

            if (userInfo.length() > USERINFO_LENGTH) {
                Toast.makeText(FaceRegisterActivity.this, "用户信息输入长度超过限制！", Toast.LENGTH_SHORT).show();
                return;
            }

            //更新基本数据
            if(getIntent().getStringExtra("page_type")!=null && "update_base".equals(getIntent().getStringExtra("page_type")))
            {
                User update_user=PlatformUtils.getInstance().getUser();
                update_user.setUserName(usernameEt.getText().toString().trim());
                update_user.setGroupId(userGroupEt.getText().toString().trim());
                update_user.setUserInfo(userInfoEt.getText().toString().trim());
                update_user.setUpdateTime(System.currentTimeMillis());//手动改时间 因为服务器可能也修改
                boolean succ=FaceApi.getInstance().userUpdate(update_user);
                Log.e("update_base_succ",Boolean.toString(succ));
                //连接两数据库==========================================================上传服务器===========================================
                PlatformUtils.getInstance().setUser(update_user);
                PlatformUtils.getInstance().updateUser(update_user);
                Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(mContext, WelcomeActivity.class));
                finish();
            }
            //==================================================人脸注册================================================================
            else {
                // 判断活体类型
                int liveType = SingleBaseConfig.getBaseConfig().getType();
                if (liveType == 1 || liveType == 2) { // RGB
                    if (liveType == 1) {
                        Toast.makeText(this, "当前活体策略：无活体", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "当前活体策略：RGB活体", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(FaceRegisterActivity.this, FaceRGBRegisterActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("user_name", username);
                    if (!TextUtils.isEmpty(userInfo)) {
                        intent.putExtra("user_info", userInfo);
                    }
                    startActivityForResult(intent, PICK_REG_VIDEO);
                    finish();

                } else if (liveType == 3) { // NIR

                    Toast.makeText(this, "当前活体策略：IR活体", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FaceRegisterActivity.this, FaceIRRegisterActivity.class);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("user_name", username);
                    if (!TextUtils.isEmpty(userInfo)) {
                        intent.putExtra("user_info", userInfo);
                    }
                    startActivityForResult(intent, PICK_REG_VIDEO);
                    finish();

                } else if (liveType == 4) { // Depth

                    int cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
                    switch (cameraType) {
                        case 1: {
                            Intent proIntent = new Intent(FaceRegisterActivity.this, FaceDepthRegisterActivity.class);
                            proIntent.putExtra("group_id", groupId);
                            proIntent.putExtra("user_name", username);
                            if (!TextUtils.isEmpty(userInfo)) {
                                proIntent.putExtra("user_info", userInfo);
                            }
                            startActivityForResult(proIntent, PICK_REG_VIDEO);
                            finish();
                        }
                        break;
                        case 2: { // atlas
                            Intent proIntent = new Intent(FaceRegisterActivity.this, FaceDepthRegisterActivity.class);
                            proIntent.putExtra("group_id", groupId);
                            proIntent.putExtra("user_name", username);
                            if (!TextUtils.isEmpty(userInfo)) {
                                proIntent.putExtra("user_info", userInfo);
                            }
                            startActivityForResult(proIntent, PICK_REG_VIDEO);
                            finish();

                        }
                        break;
                        default:
                            break;
                    }
                }
            }
        } else if (view == settingButton) {
            Intent intent = new Intent(this, SettingMainActivity.class);
            intent.putExtra("page_type", "register");
            startActivityForResult(intent, PAGE_TYPE);
            finish();
        } else if (view == backBtn) {
            FaceRegisterActivity.this.finish();
        }
    }
}
