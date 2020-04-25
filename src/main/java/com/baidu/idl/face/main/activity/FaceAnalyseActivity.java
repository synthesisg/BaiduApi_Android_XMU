package com.baidu.idl.face.main.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.utils.ImageUtils;
import com.baidu.idl.face.main.utils.TransformUtils;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.main.facesdk.utils.PreferencesUtil;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;


/**
 * @Time: 2019/5/24
 * @Author: v_zhangxiaoqing01
 * @Description: 人证对比界面
 */

public class FaceAnalyseActivity extends BaseActivity implements View.OnClickListener {

    private ImageView imgFirst;
    private TextView tvState;
    private TextView tvScore;
    private TextView tvCurrentThreshold;
    private TextView modelType;
    private static final int PICK_PHOTO_FRIST = 100;
    private static final int PICK_VIDEO_FRIST = 101;

    private byte[] firstFeature = new byte[512];

    private volatile boolean firstFeatureFinished = false;
    public static final int SOURCE_DETECT = 0; // 采集
    public static final int SOURCE_IR_DETECT = 0; // 采集

    private String msg = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_analyse);
        initView();
    }

    private void initView() {
        Button btFirstPickFromPhoto = findViewById(R.id.bt_first_pick_from_photo);
        Button btFirstPickFromVideo = findViewById(R.id.bt_first_pick_from_video);
        Button btStartCompare = findViewById(R.id.bt_start_compare);
        Button btSetting = findViewById(R.id.btn_setting);
        Button backBtn = findViewById(R.id.btn_back);
        imgFirst = findViewById(R.id.img_first);
        tvScore = findViewById(R.id.tv_score);
        tvState = findViewById(R.id.tv_state);
        modelType = findViewById(R.id.tv_model_type);
        tvCurrentThreshold = findViewById(R.id.tv_current_threshold);
        btFirstPickFromPhoto.setOnClickListener(this);
        btFirstPickFromVideo.setOnClickListener(this);
        btStartCompare.setOnClickListener(this);
        btSetting.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        PreferencesUtil.initPrefs(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.bt_first_pick_from_photo:
                // 从相册取图片
                firstFeatureFinished = false;
                clear();
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_PHOTO_FRIST);
                break;
            case R.id.bt_first_pick_from_video:
                // 从视频流取图片
                firstFeatureFinished = false;
                clear();
                startToActivity(PICK_VIDEO_FRIST);
                break;
            case R.id.bt_start_compare:
                match();
                break;
            case R.id.btn_setting:
                Intent intent2 = new Intent(this, SettingMainActivity.class);
                startActivity(intent2);
                break;
            case R.id.btn_back:
                FaceAnalyseActivity.this.finish();
                break;
            default:
                break;
        }

    }


    private void startToActivity(int pickVideoCode) {

        int type = SingleBaseConfig.getBaseConfig().getType();
        switch (Integer.valueOf(type)) {
            case 1: { // 非活体
                Toast.makeText(this, "当前活体策略：无活体", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(FaceAnalyseActivity.this, FaceRGBDetectActivity.class);
                intent1.putExtra("pageType", SOURCE_DETECT);
                startActivityForResult(intent1, pickVideoCode);
            }
                break;

            case 2: { //  RGB活体
                Toast.makeText(this, "当前活体策略：RGB活体", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(FaceAnalyseActivity.this, FaceRGBDetectActivity.class);
                intent1.putExtra("pageType", SOURCE_DETECT);
                startActivityForResult(intent1, pickVideoCode);
            }
                break;
            case 3: { // IR活体
                Toast.makeText(this, "当前活体策略：RGB+IR活体", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(FaceAnalyseActivity.this, FaceIRLivenessActivity.class);
                intent1.putExtra("pageType", SOURCE_IR_DETECT);
                startActivityForResult(intent1, pickVideoCode);
            }
                break;
            case 4: { // 深度活体
                Toast.makeText(this, "当前活体策略：RGB+Depth活体，需要使用带深度的摄像头", Toast.LENGTH_SHORT).show();
                int cameraType = SingleBaseConfig.getBaseConfig().getCameraType();
                switch (cameraType) {
                    case 1: { // pro
                        Intent proIntent = new Intent(FaceAnalyseActivity.this, FaceDepthLivenessActivity.class);
                        startActivityForResult(proIntent, pickVideoCode);
                    }
                        break;
                    case 2: { // atlas
                        Intent proIntent = new Intent(FaceAnalyseActivity.this, FaceDepthLivenessActivity.class);
                        startActivityForResult(proIntent, pickVideoCode);
                    }
                        break;
                    default:
                        break;
                }
            }
                break;
            default:
                break;

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_PHOTO_FRIST && (data != null && data.getData() != null)) {
            Uri uri1 = ImageUtils.geturi(data, this);
            try {
                final Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri1));
                if (bitmap != null) {
                    imgFirst.setImageBitmap(bitmap);
                    // 提取特征值
                    syncFeature(bitmap, firstFeature, 1, true);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }  else if (requestCode == PICK_VIDEO_FRIST && (data != null)) {
            String faceImagePath = data.getStringExtra("file_path");
            Bitmap bitmap = BitmapFactory.decodeFile(faceImagePath);
            if (bitmap != null) {
                imgFirst.setImageBitmap(bitmap);
                // 提取特征值
                syncFeature(bitmap, firstFeature, 1, false);
            }
        }
    }


    /**
     * bitmap -提取特征值
     *
     * @param bitmap
     * @param feature
     * @param index
     */

    private void syncFeature(final Bitmap bitmap, final byte[] feature, final int index, boolean isFromPhotoLibrary) {
        float ret = -1;
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect().detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        // 检测结果判断
        if (faceInfos != null && faceInfos.length > 0) {
            //* 特征提取测试
            TransformUtils.liveness(bitmap);//score
            msg = TransformUtils.Bitmap2Msg(bitmap);
            Log.e("syncFeature","faceInfo = " + msg);
            //*/
            Log.e("syncFeature","faceInfos.length = " + faceInfos.length + " ,landmarks = " + faceInfos[0].landmarks.length);
            // 判断质量检测，针对模糊度、遮挡、角度
            if (qualityCheck(faceInfos[0], isFromPhotoLibrary)) {
                ret = FaceSDKManager.getInstance().getFaceFeature().feature(BDFaceSDKCommon.FeatureType.
                        BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
                Log.i("qing", "ret:" + ret);
                if (ret == 128 && index == 1) {
                    firstFeatureFinished = true;
                }
                if (ret == 128) {
                    toast("图片" + index + "特征抽取成功");
                } else if (ret == -100) {
                    toast("未完成人脸比对，可能原因，图片1为空");
                } else if (ret == -101) {
                    toast("未完成人脸比对，可能原因，图片2为空");
                } else if (ret == -102) {
                    toast("未完成人脸比对，可能原因，图片1未检测到人脸");
                } else if (ret == -103) {
                    toast("未完成人脸比对，可能原因，图片2未检测到人脸");
                } else {
                    toast("未完成人脸比对，可能原因，"
                            + "人脸太小（小于sdk初始化设置的最小检测人脸）"
                            + "人脸不是朝上，sdk不能检测出人脸");
                }
            }

        } else {
            toast("未检测到人脸,可能原因人脸太小");
        }

    }


    /**
     * argb - 特征比对
     */

    private void match() {

        if (!firstFeatureFinished) {
            toast("图片一特征抽取失败");
            return;
        }

        int idFeatureValue = SingleBaseConfig.getBaseConfig().getThreshold();
        /*
        float score = 0;
        //  比较两个人脸
        score = FaceSDKManager.getInstance().getFaceFeature().featureCompare(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,
                firstFeature, secondFeature, true);
        Log.e("qing", "分数：" + String.valueOf(score));
        if (score > idFeatureValue) {
            tvState.setTextColor(getResources().getColor(R.color.buttonBg));
            tvState.setText("核验通过");
        } else {
            tvState.setTextColor(getResources().getColor(R.color.red));
            tvState.setText("核验不通过");
        tvScore.setText("相似度：" + score);
        }
        //*/
        try {
            Log.e("Feature", new String(firstFeature, "UTF-8"));
            tvScore.setText("Feature：" + firstFeature.length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        modelType.setText("当前特征抽取模型: 证件照模型, 阈值 = " + idFeatureValue);
        //tvCurrentThreshold.setText("相似度阈值：" + idFeatureValue);
        tvCurrentThreshold.setText("面部：" + msg);
    }

    private void toast(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(FaceAnalyseActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clear() {
        tvState.setText("");
        tvCurrentThreshold.setText(" ");
        tvScore.setText(" ");
        modelType.setText("");
    }


    /**
     * 质量检测
     * FaceInfo faceInfo
     *
     * @return
     */
    public boolean qualityCheck(final FaceInfo faceInfo, boolean isFromPhotoLibrary) {

        // 不是相册选的图片，不必再次进行质量检测，因为采集图片的时候已经做过了
        if (!isFromPhotoLibrary) {
            return true;
        }

        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return true;
        }

        if (faceInfo != null) {

            // 模糊结果过滤
            float blur = faceInfo.bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                toast("图片模糊");
                return false;
            }

            // 光照结果过滤
            float illum = faceInfo.illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                toast("图片光照不通过");
                return false;
            }

            // 遮挡结果过滤
            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;

                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    // 左眼遮挡置信度
                    toast("左眼遮挡");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    // 右眼遮挡置信度
                    toast("右眼遮挡");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    // 鼻子遮挡置信度
                    toast("鼻子遮挡");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    // 嘴巴遮挡置信度
                    toast("嘴巴遮挡");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    // 左脸遮挡置信度
                    toast("左脸遮挡");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    // 右脸遮挡置信度
                    toast("右脸遮挡");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    // 下巴遮挡置信度
                    toast("下巴遮挡");
                } else {
                    return true;
                }
            }
        }
        return false;
    }


}
