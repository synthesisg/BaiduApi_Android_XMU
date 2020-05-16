package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.db.DBManager;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.face.main.utils.*;
import com.baidu.idl.face.main.listener.SdkInitListener;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.statistic.NetWorkUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 主功能页面，包含人脸检索入口，认证比对，功能设置，授权激活
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static boolean open_socket = true;

    private Context mContext;

    private Button btnSearch;
    private Button btnIdentity;
    private Button btnAttribute;
    private Button btnSetting;
    private Button btnAuth;
    private Boolean isInitConfig;
    private Boolean isConfigExit;

    private Button btnBase1, btnBase2, btnBaseFile;

    public static SocketUtils socketutil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // todo shangrong 增加配置信息初始化操作

        isConfigExit = ConfigUtils.isConfigExit();
        isInitConfig = ConfigUtils.initConfig();
        if (isInitConfig && isConfigExit) {
            Toast.makeText(MainActivity.this, "初始配置加载成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "初始配置失败,将重置文件内容为默认配置", Toast.LENGTH_SHORT).show();
            ConfigUtils.modityJson();
        }

        mContext = this;
        try {
            initView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initLicense();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库
        DBManager.getInstance().release();
    }

    /**
     * UI 相关VIEW 初始化
     */
    private void initView() throws JSONException {
        btnSearch = findViewById(R.id.btn_search);
        btnIdentity = findViewById(R.id.btn_identity);
        btnAttribute = findViewById(R.id.btn_attribute);
        btnSetting = findViewById(R.id.btn_setting);
        btnAuth = findViewById(R.id.btn_auth);
        TextView versionTv = findViewById(R.id.tv_version);
        versionTv.setText(String.format(" V %s", Utils.getVersionName(mContext)));
        btnSearch.setOnClickListener(this);
        btnIdentity.setOnClickListener(this);
        btnAttribute.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnAuth.setOnClickListener(this);


        btnBase1 = findViewById(R.id.btn_base1);
        btnBase2 = findViewById(R.id.btn_base2);
        btnBaseFile = findViewById(R.id.btn_base3);
        btnBase1.setOnClickListener(this);
        btnBase2.setOnClickListener(this);
        btnBaseFile.setOnClickListener(this);

        socketutil = new SocketUtils(handler);
        if(socketutil.getstate() == -1 && open_socket) {
            Log.e("Main", "========================================================================================");
            socketutil.start();
            if(socketutil.isConnected()) {
                Toast.makeText(MainActivity.this, "连接服务器成功", Toast.LENGTH_LONG).show();
                //更新数据
                //PlatformUtils.getInstance().UpdateData();
            }
        }

        //testjson
        /*
        Map<String, Object> map = JsonUtils.jsonToMap(strjs);
        //这里最好写一个循环输出map的方法 ，我这是偷懒的写法
        for(Map.Entry<String, Object> entry : map.entrySet()){
            Log.e("JSON",entry.getKey() + "|||" +entry.getValue());
        }
        Map<String, Object> arr = (Map<String, Object>) map.get("picturegroup");
        Log.e("JSON-",""+arr);
        for(Map.Entry<String, Object> entry : arr.entrySet()){
            Log.e("JSON--",entry.getKey() + "|||" + entry.getValue());
            Map<String, Object> arr2 = (Map<String, Object>) entry.getValue();
            for(Map.Entry<String, Object> entry2 : arr2.entrySet()){
                Log.e("JSON-",entry2.getKey() + "|||" +entry2.getValue());
            }
        }
        switch (map.get("operateType").toString())
        {
            case "1:n":
                String calresultid = map.get("calresultid").toString();
                //JSONArray picturegroup = new JSONArray( map.get("picturegroup").toString());
                Map<String, Object> innerObject = (Map<String, Object>) map.get("picturedata");
                String data = innerObject.get("data").toString();
                String facepictureid = innerObject.get("facepictureid").toString();
                Log.e("JSON","calresultid = "+ calresultid);
                //Log.e("JSON","picturegroup = "+ picturegroup.length());
                Log.e("JSON","data = "+ data);
                Log.e("JSON","facepictureid = "+ facepictureid);
                break;
                case "666":
                    break;
        }
        //*/

        //hashmap to json
        /*
		JSONObject allJson = new JSONObject();
		allJson.put("t1","a1");
		allJson.put("t2",666);
        JSONObject cdx = new JSONObject();
		cdx.put("inner1","i1");
        cdx.put("inner2",2);
        allJson.put("Map",cdx);
        List<Integer> lists = new ArrayList<>();
        lists.add(10);
        lists.add(20);
        lists.add(30);
        allJson.put("List",lists);
		Log.e("JsonStr",allJson.toString());
        //*/
        //doc
        /*
        Thread thread = new NetworkUtils.MSGThread("senddoc",null,"GET");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String cdx = ((NetworkUtils.MSGThread) thread).getMessage();
        Map<String, Object> map = JsonUtils.jsonToMap(cdx);
        Map<String, Object> arr = (Map<String, Object>) map.get("docdata");
        //这里最好写一个循环输出map的方法 ，我这是偷懒的写法
        for(Map.Entry<String, Object> entry : arr.entrySet()){
            //Log.e("JSON--",entry.getKey() + "|||" + entry.getValue());
            Map<String, Object> arr2 = (Map<String, Object>) entry.getValue();
            Log.e("JSON-",arr2.get("docname") + "|||" +arr2.get("url"));
        }
        //*/
    }

    // handler对象，用来接收消息~
    private static String strjs = "{\"picturedata\":{\"facepictureid\":\"1250783408485158912\",\"data\":\"66666\"},\"calresultid\":\"test\",\"operateType\":\"1:n\",\"picturegroup\":[{\"facepictureid\": \"1\",\"data\": \"base64encode2\"}, {\"facepictureid\": \"2\",\"data\": \"base64encode3\"}]}";
    private static String docjs = "{\"docdata\":[{\"docid\":\"1255066968015532032\",\"url\":\"faceSearch/download?filepath=C:/jeesite/userfiles/fileupload/202004/1252143585762332674.doc;\"},{\"docid\":\"1255067360627552256\",\"url\":\"faceSearch/download?filepath=C:/jeesite/userfiles/fileupload/202004/1255067355170762754.doc;\"},{\"docid\":\"1255067710692552704\",\"url\":\"faceSearch/download?filepath=C:/jeesite\"},{\"docid\":\"1255102427733225472\",\"url\":\"faceSearch/download?filepath=C:/jeesite/userfiles/fileupload/202004/1254731127795511298.png;\"},{\"docid\":\"1256157761143320576\",\"url\":\"faceSearch/download?filepath=C:/jeesite/userfiles/fileupload/202005/1256157725672091650.txt;\"}]}";
    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {  //这个是发送过来的消息
            // 处理从子线程发送过来的消息
            //int arg1 = msg.arg1;  //获取消息携带的属性值
            //int arg2 = msg.arg2;
            int what = msg.what;
            Object result = msg.obj;
            /* save json
            Log.e("Save",FileUtils.getSDRootFile().getPath()+"/ademo/aJson.txt");
            try {
                FileUtils.writeTxtFile(result.toString(),FileUtils.getSDRootFile().getPath()+"/ademo/aJson.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //*/
            //Log.e("-what--->>" , ""+what);
            //Log.e("-result--->>" , "" + result);
            if ("hhh".equals(result)) return;
            //Bundle bundle = msg.getData(); // 用来获取消息里面的bundle数据
            //System.out.println("-getData--->>" + bundle.getStringArray("strs").length);


            Map<String, Object> map = JsonUtils.jsonToMap(result.toString());
            JSONObject allJson = new JSONObject();
            if (map.get("operateType") == null || map.get("calresultid") == null)
            {
                Log.e("Handler","NULL operateType/calresultid");
                return;
            }
            //for(Map.Entry<String, Object> entry : map.entrySet()) Log.e("JSON",entry.getKey() + "|||" +entry.getValue());

            String calresultid = map.get("calresultid").toString();
            String operateType = map.get("operateType").toString();

            Log.e("JSON", "calresultid = " + calresultid);
            try {
                Map<String,String>ret_msg = new HashMap<>();
                String url = null;
                Map<String, Object> innerObject = null;
                String data = null;
                String facepictureid = null;
                Bitmap bitmap = null;
                if (map.get("picturedata") != null) {
                    innerObject = (Map<String, Object>) map.get("picturedata");
                    data = innerObject.get("data").toString();
                    facepictureid = innerObject.get("facepictureid").toString();
                    bitmap = TransformUtils.StrToBitmap(data);
                    ret_msg.put("facepictureid" , facepictureid);
                    allJson.put("facepictureid" , facepictureid);
                }
                ret_msg.put("calresultid" , calresultid);
                ret_msg.put("result","fail");

                allJson.put("calresultid" , calresultid);
                allJson.put("result","fail");

                switch (operateType) {
                    //1:n
                    case "1:n":{
                        url = "send/1comparen";
                        ret_msg.put("operatetype" , "1:N");
                        allJson.put("operatetype" , "1:N");

                        Pair<Boolean,byte[]> ret_1 = TransformUtils.Bitmap2Feature(bitmap);

                        if(ret_1.first)
                        {
                            Map<String, Object> arr = (Map<String, Object>) map.get("picturegroup");
                            for(Map.Entry<String, Object> entry : arr.entrySet()){
                                Map<String, Object> arr2 = (Map<String, Object>) entry.getValue();
                                for(Map.Entry<String, Object> entry2 : arr2.entrySet()){
                                    Log.e("JSON--Inner",entry2.getKey());
                                    Bitmap bitmap_n = TransformUtils.StrToBitmap(entry2.getValue().toString());
                                    Pair<Boolean,byte[]> ret_n = TransformUtils.Bitmap2Feature(bitmap_n);
                                    if(ret_n.first)
                                    {
                                        float score = TransformUtils.Compare(ret_1.second,ret_n.second);
                                        if (score > SingleBaseConfig.getBaseConfig().getThreshold())
                                        {
                                            ret_msg.put("result","success");
                                            ret_msg.put("resultpictureid" , entry2.getKey());
                                            allJson.put("result","success");
                                            allJson.put("resultpictureid" , entry2.getKey());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        break;}

                    //公安验证
                    case "public_security_verification":{
                        url = "send/public_security_verification";
                        ret_msg.put("operatetype" , "public_security_verification");
                        allJson.put("operatetype" , "public_security_verification");

                        Map<String, Object> innerObject_psv = (Map<String, Object>) map.get("public_security_verificationData");
                        String data_psv = innerObject_psv.get("data").toString();
                        String facepictureid_psv = innerObject_psv.get("public_security_verificationID").toString();

                        ret_msg.put("resultpictureid" , facepictureid_psv);
                        allJson.put("resultpictureid" , facepictureid_psv);

                        Pair<Boolean,byte[]> ret_1 = TransformUtils.Bitmap2Feature(bitmap);
                        Bitmap bitmap_psv = TransformUtils.StrToBitmap(data_psv);
                        Pair<Boolean,byte[]> ret_psv = TransformUtils.Bitmap2Feature(bitmap_psv);

                        if(ret_1.first && ret_psv.first && TransformUtils.Compare(ret_1.second,ret_psv.second)> SingleBaseConfig.getBaseConfig().getThreshold()) {
                            ret_msg.put("result", "success");
                            allJson.put("result", "success");
                        }
                        break;}

                    //活体检测
                    case "biopsy":{
                        url = "send/biopsy";
                        ret_msg.put("operatetype" , "biopsy");
                        ret_msg.put("context" , "notlive");
                        allJson.put("operatetype" , "biopsy");
                        allJson.put("context" , "notlive");

                        float score = TransformUtils.liveness(bitmap);
                        if (score*100 > SingleBaseConfig.getBaseConfig().getRgbLiveScore()) {
                            ret_msg.put("context", "living");
                            allJson.put("context", "living");
                        }
                        break;}

                    //人脸属性测试 人脸添加修改
                    case "facecharactercheck":
                    case "faceinfoAdd":
                    case "faceinfoUpdate":{

                        JSONObject info = new JSONObject();
                        switch (operateType)
                        {
                            case "facecharactercheck":
                                url = "send/facecharactercheck";
                                ret_msg.put("operatetype" , "facecharactercheck");
                                allJson.put("operatetype" , "facecharactercheck");
                                break;
                            case "faceinfoAdd":
                                url = "send/faceinfoadd";
                                ret_msg.put("operatetype" , "faceinfoAdd");
                                ret_msg.put("faceinfoid" , map.get("faceinfoid").toString());
                                allJson.put("operatetype" , "faceinfoAdd");
                                allJson.put("faceinfoid" , map.get("faceinfoid").toString());
                                info.put("id",map.get("faceinfoid").toString());
                                break;
                            case "faceinfoUpdate":
                                url = "send/faceinfoupdate";
                                ret_msg.put("operatetype" , "faceinfoUpdate");
                                ret_msg.put("faceinfoid" , map.get("faceinfoid").toString());
                                allJson.put("operatetype" , "faceinfoUpdate");
                                allJson.put("faceinfoid" , map.get("faceinfoid").toString());
                                info.put("id",map.get("faceinfoid").toString());
                                break;
                        }

                        FaceInfo faceinfo = TransformUtils.Bitmap2Msg(bitmap);
                        Pair<Boolean,byte[]> feature = TransformUtils.Bitmap2Feature(bitmap);
                        if (faceinfo != null)
                        {
                            ret_msg.put("centerX" , faceinfo.centerX + "");
                            ret_msg.put("centerY" , faceinfo.centerY + "");
                            ret_msg.put("width" , faceinfo.width + "");
                            ret_msg.put("height" , faceinfo.height + "");
                            ret_msg.put("angle" , faceinfo.angle + "");
                            ret_msg.put("score" , faceinfo.score + "");
                            ret_msg.put("yaw" , faceinfo.yaw + "");
                            ret_msg.put("roll" , faceinfo.roll + "");
                            ret_msg.put("pitch" , faceinfo.pitch + "");
                            ret_msg.put("bluriness" , faceinfo.bluriness + "");
                            ret_msg.put("illum" , faceinfo.illum + "");
                            ret_msg.put("mouthclose" , faceinfo.mouthclose + "");
                            ret_msg.put("leftEyeclose" , faceinfo.leftEyeclose + "");
                            ret_msg.put("rightEyeclose" , faceinfo.rightEyeclose + "");
                            ret_msg.put("occlusion" , faceinfo.occlusion + "");

                            //年龄 表情 行吧 眼镜 人种
                            String face_msg = TransformUtils.Faceinfo2Msg(faceinfo);
                            String[] args = face_msg.split(",");

                            ret_msg.put("age" , args[0]);
                            ret_msg.put("expression" , args[1]);
                            ret_msg.put("gender" , args[2]);
                            ret_msg.put("glasses" , args[3]);
                            ret_msg.put("race" , args[4]);

                            info.put("centerx" , faceinfo.centerX);
                            info.put("centery" , faceinfo.centerY);
                            info.put("width" , faceinfo.width);
                            info.put("height" , faceinfo.height);
                            info.put("angle" , faceinfo.angle);
                            info.put("score" , faceinfo.score);
                            info.put("yaw" , faceinfo.yaw);
                            info.put("roll" , faceinfo.roll);
                            info.put("pitch" , faceinfo.pitch);
                            info.put("bluriness" , faceinfo.bluriness);
                            info.put("illum" , faceinfo.illum + "");
                            info.put("mouthclose" , faceinfo.mouthclose);
                            info.put("lefteyeclose" , faceinfo.leftEyeclose);
                            info.put("righteyeclose" , faceinfo.rightEyeclose );
                            info.put("occlusion" , faceinfo.occlusion + "");

                            info.put("age" , Integer.parseInt(args[0]));
                            info.put("expression" , args[1]);
                            info.put("gender" , args[2]);
                            info.put("glasses" , args[3]);
                            info.put("race" ,  args[4]);
                            allJson.put("faceinfo",info);

                            allJson.put("age" , Integer.parseInt(args[0]));
                            allJson.put("expression" , args[1]);
                            allJson.put("gender" , args[2]);
                            allJson.put("glasses" , args[3]);
                            allJson.put("race" ,  args[4]);

                            if (feature.first) {
                                ret_msg.put("feature", TransformUtils.Byte2Str(feature.second));
                                allJson.put("feature", TransformUtils.Byte2Str(feature.second));
                            }
                            ret_msg.put("result" , "success");
                            allJson.put("result" , "success");
                        }


                        //* add to local
                        if ("faceinfoAdd".equals(operateType) && feature.first) {
                            User newUser = new User();
                            String uuid = UUID.randomUUID().toString();
                            String path = "default-"+uuid+".jpg";
                            newUser.setUserId(uuid);
                            newUser.setUserName(uuid);
                            newUser.setGroupId("default");
                            newUser.setFeature(feature.second);
                            newUser.setImageName(path);
                            FaceApi.getInstance().userAdd(newUser);
                            //保存图片 更新
                        }
                        //*/

                        break;}
                    //视频流人脸检测
                    case "videofacecheck":{
                        url = "send/videofacecheck";
                        ret_msg.put("operatetype" , "videofacecheck");
                        allJson.put("operatetype" , "videofacecheck");
                        String uri = map.get("video_url").toString();
                        ret_msg.put("uri" , uri);
                        allJson.put("uri" , uri);


                        Log.e("Video","Begin DL.");
                        Thread thread = new NetworkUtils.DLThread("cache.mp4",NetworkUtils.URL_h + uri);
                        thread.start();
                        thread.join();
                        Log.e("Video","End DL. Begin Extract at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));

                        MediaUtils media = new MediaUtils(FileUtils.getSDRootFile().getPath()+"/ademo/" + "cache.mp4");
                        List<String> lists = media.autoRun();
                        Log.e("Video","End Extract at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));

                        //===============================================================doing============================

                        if(lists!= null && lists.size() != 0)
                        {
                            ret_msg.put("resultpicture" , lists.get(0));
                            allJson.put("resultpicture" , lists.get(0));

                            ret_msg.put("result","success");
                            allJson.put("result","success");
                        }
                        break;}
                    default:
                        Log.e("Handle", "Unknown handle " + map.get("operateType").toString());
                }
                if (url != null) {
                    ret_msg.put("finishtime" , "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                    //allJson.put("finishtime" , "" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                    //hashmap to json
                    //Log.e("Send JSON","Json = " +allJson.toString());
                    Thread thread =  new NetworkUtils.MSGThread(url, allJson);
                    thread.start();
                }
            }
            catch (Exception e)
            {
                Log.e("Handler","" + e);
            }
        };
    };


    /**
     * 启动应用程序，如果之前初始过，自动初始化鉴权和模型（可以添加到Application 中）
     */
    private void initLicense() {
        if (FaceSDKManager.initStatus != FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {
            FaceSDKManager.getInstance().init(mContext, new SdkInitListener() {
                @Override
                public void initStart() {

                }

                @Override
                public void initLicenseSuccess() {

                }

                @Override
                public void initLicenseFail(int errorCode, String msg) {
                    // 如果授权失败，跳转授权页面
                    ToastUtils.toast(mContext, errorCode + msg);
                    startActivity(new Intent(mContext, FaceAuthActicity.class));
                }

                @Override
                public void initModelSuccess() {
                }

                @Override
                public void initModelFail(int errorCode, String msg) {

                }
            });
        }
    }

    /**
     * 点击事件跳转路径
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        //LogUtils.d("[click]","CLICK"+view.getId());
        switch (view.getId()) {
            case R.id.btn_search:
            case R.id.btn_identity:
                // 【1：N 人脸搜索】 和 【1：1 人证比对】跳转判断授权和模型初始化状态
                if (FaceSDKManager.getInstance().initStatus == FaceSDKManager.SDK_UNACTIVATION) {
                    Toast.makeText(MainActivity.this, "SDK还未激活初始化，请先激活初始化", Toast.LENGTH_LONG).show();
                    return;
                } else if (FaceSDKManager.getInstance().initStatus == FaceSDKManager.SDK_INIT_FAIL) {
                    Toast.makeText(MainActivity.this, "SDK初始化失败，请重新激活初始化", Toast.LENGTH_LONG).show();
                    return;
                } else if (FaceSDKManager.getInstance().initStatus == FaceSDKManager.SDK_INIT_SUCCESS) {
                    Toast.makeText(MainActivity.this, "SDK正在加载模型，请稍后再试", Toast.LENGTH_LONG).show();
                    return;
                } else if (FaceSDKManager.getInstance().initStatus == FaceSDKManager.SDK_MODEL_LOAD_SUCCESS) {

                    switch (view.getId()) {
                        // 返回
                        case R.id.btn_search:
                            // 【1：N 人脸搜索】页面跳转
                            startActivity(new Intent(MainActivity.this, FaceMainSearchActivity.class));
                            break;
                        case R.id.btn_identity:
                            // 【1：1 人证比对】页面跳转
                            startActivity(new Intent(MainActivity.this, FaceIdCompareActivity.class));
                            break;
                        default:
                            break;

                    }
                }

                break;
            case R.id.btn_attribute:
                startActivity(new Intent(MainActivity.this, FaceAttributeRGBActivity.class));
                break;
            case R.id.btn_setting:
                startActivity(new Intent(MainActivity.this, SettingMainActivity.class));
                break;
            case R.id.btn_auth:
                // 跳转授权激活页面
                startActivity(new Intent(MainActivity.this, FaceAuthActicity.class));
                break;
            case R.id.btn_base1:
                LogUtils.d("[base]","BASE1");
                startActivity(new Intent(MainActivity.this, FrontPageActivity.class));
                break;
            case R.id.btn_base2:
                LogUtils.d("[base]","BASE2");
                startActivity(new Intent(MainActivity.this, Base2Activity.class));  //FaceRGBOpenDebugSearchActivity
                break;
             case R.id.btn_base3:
                LogUtils.d("[base]","BASE3");
                startActivity(new Intent(MainActivity.this, BaseFileActivity.class));  //FaceRGBOpenDebugSearchActivity
                break;
        }
    }

    public class pic
    {
       private String facepictureid;

        public String getFacepictureid() {
            return facepictureid;
        }

        public void setFacepictureid(String facepictureid) {
            this.facepictureid = facepictureid;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        private String data;
    }
}
