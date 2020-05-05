package com.baidu.idl.face.main.utils;

import android.graphics.Matrix;
import android.media.FaceDetector;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.util.Log;
import android.util.Pair;
import com.baidu.idl.face.main.callback.FaceDetectCallBack;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.manager.FaceTrackManager;
import com.baidu.idl.face.main.model.LivenessModel;
import com.baidu.idl.face.main.model.SingleBaseConfig;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.facesdkdemo.R;
import com.baidu.idl.main.facesdk.FaceInfo;
import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceOcclusion;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;
import com.baidu.idl.face.main.utils.ImageUtils;
import com.baidu.idl.main.facesdk.utils.PreferencesUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransformUtils {

    private TransformUtils(){}

    public static String Byte2Str(byte[] arr)
    {
        return Base64.encodeToString(arr, Base64.DEFAULT);
    }
    public static byte[] Str2Byte(String str)
    {
        return Base64.decode(str, Base64.DEFAULT);
    }
    public static Bitmap StrToBitmap(String str)
    {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(str, Base64.DEFAULT);//注意解码的时候要把编码的头（"data:image/png;base64,"）去掉，否则将会失效
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
    public static byte[] Bitmap2Byte(Bitmap bitmap)
    {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = null;
        baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            baos.flush();
            baos.close();
            byte[] bitmapBytes = baos.toByteArray();
            return bitmapBytes;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String Bitmap2Str(Bitmap bitmap) {
        String result = null;
        result = Base64.encodeToString(Bitmap2Byte(bitmap), Base64.DEFAULT);
        return result;
    }


    public static byte[] BitmaptoYUV(Bitmap bitmap)
    {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = width * height;

        int pixels[] = new int[size];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int len = width * height;
        byte[] yuv = new byte[len * 3 / 2];
        Log.e("TransformUtils","[BitmaptoYUV] w = " + width + ",h = " + height + ",size = " + yuv.length);
        int y, u, v;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int rgb = pixels[i * width + j] & 0x00FFFFFF;   // 屏蔽ARGB的透明度值
                int r = rgb & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = (rgb >> 16) & 0xFF;
                // 套用公式
                y = ((66 * r + 129 * g + 25 * b + 128) >> 8) + 16;
                u = ((-38 * r - 74 * g + 112 * b + 128) >> 8) + 128;
                v = ((112 * r - 94 * g - 18 * b + 128) >> 8) + 128;
                // rgb2yuv
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.147 * r - 0.289 * g + 0.437 * b);
                // v = (int) (0.615 * r - 0.515 * g - 0.1 * b);
                // RGB转换YCbCr
                // y = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                // u = (int) (-0.1687 * r - 0.3313 * g + 0.5 * b + 128);
                // if (u > 255)
                // u = 255;
                // v = (int) (0.5 * r - 0.4187 * g - 0.0813 * b + 128);
                // if (v > 255)
                // v = 255;
                // 调整
                y = y < 16 ? 16 : (y > 255 ? 255 : y);
                u = u < 0 ? 0 : (u > 255 ? 255 : u);
                v = v < 0 ? 0 : (v > 255 ? 255 : v);
                // 赋值
                yuv[i * width + j] = (byte) y;
                yuv[len + (i >> 1) * width + (j & ~1) + 0] = (byte) u;
                yuv[len + +(i >> 1) * width + (j & ~1) + 1] = (byte) v;
            }
        }
        return yuv;
    }



    //人脸比对
    public static float Compare(byte[] firstFeature,byte[] secondFeature)
    {
        if(firstFeature==null || secondFeature==null) return -1.0f;

        //阈值
        //int idFeatureValue = SingleBaseConfig.getBaseConfig().getThreshold();
        float score = 0;

        score = FaceSDKManager.getInstance().getFaceFeature().featureCompare(
                BDFaceSDKCommon.FeatureType.BDFACE_FEATURE_TYPE_ID_PHOTO,
                firstFeature, secondFeature, true);
        //Log.e("TransformUtils", "Compare, score = " + score);
        return score;
    }


    //活体检测
    public static float liveness(Bitmap bitmap)
    {
        if (bitmap == null) return -1.0f;
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect().detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        float rgbScore = FaceSDKManager.getInstance().getFaceLiveness().silentLive(BDFaceSDKCommon.LiveType.BDFACE_SILENT_LIVE_TYPE_RGB, rgbInstance, faceInfos[0].landmarks);
        Log.e("TransformUtils", "liveness = " + rgbScore);
        return rgbScore;
    }

    //属性检测
    public static FaceInfo Bitmap2Msg(Bitmap bitmap)
    {
        SingleBaseConfig.getBaseConfig().setAttribute(true);
        FaceSDKManager.getInstance().initConfig();

        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect().track(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);
        if (faceInfos == null || faceInfos.length==0) return null;//"未检测到人脸";
        return faceInfos[0];
    }

    public static FaceInfo Bitmap2Faceinfo(Bitmap bitmap) {
        if (bitmap == null) return null;
        BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
        FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);

        if (faceInfos != null && faceInfos.length > 0) {
            return faceInfos[0];
        }
        return null;
    }

    public static String Faceinfo2Msg(FaceInfo faceInfo){
        if (faceInfo == null ) return null;
        StringBuilder msg = new StringBuilder();
        msg.append(faceInfo.age);
        msg.append(",").append(faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_NEUTRAL ? "neutral"
                : faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_BIG_SMILE ? "laugh"
                : faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_SMILE ? "smile" : "no");
        msg.append(",").append(faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_FEMALE ? "female" :
                faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_MALE ? "male" : "baby");
        msg.append(",").append(faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_NO_GLASSES ? "no"
                : faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_GLASSES ? "yes"
                : faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_SUN_GLASSES ? "sunglasses" : "sunglasses");
        msg.append(",").append(faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_YELLOW ? "yellow"
                : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_WHITE ? "white"
                : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_BLACK ? "black"
                : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_INDIAN ? "indian" : "human");
        return msg.toString();
    }
    public static String Faceinfo2Msg_cn(FaceInfo faceInfo) {
        if (faceInfo == null ) return null;
        StringBuilder msg = new StringBuilder();
        if (faceInfo != null) {
            msg.append(faceInfo.age);
            msg.append(",").append(faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_NEUTRAL ?
                    "中性表情"
                    : faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_BIG_SMILE ? "大笑"
                    : faceInfo.emotionThree == BDFaceSDKCommon.BDFaceEmotion.BDFACE_EMOTION_SMILE ? "微笑" : "没有表情");
            msg.append(",").append(faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_FEMALE ? "女性" :
                    faceInfo.gender == BDFaceSDKCommon.BDFaceGender.BDFACE_GENDER_MALE ? "男性" : "婴儿");
            msg.append(",").append(faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_NO_GLASSES ? "无眼镜"
                    : faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_GLASSES ? "有眼镜"
                    : faceInfo.glasses == BDFaceSDKCommon.BDFaceGlasses.BDFACE_SUN_GLASSES ? "墨镜" : "太阳镜");
            msg.append(",").append(faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_YELLOW ? "黄种人"
                    : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_WHITE ? "白种人"
                    : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_BLACK ? "黑种人"
                    : faceInfo.race == BDFaceSDKCommon.BDFaceRace.BDFACE_RACE_INDIAN ? "印度人"
                    : "地球人");
        }
        return msg.toString();
    }
    //人脸识别
    public static Pair<Boolean,byte[]> Bitmap2Feature(Bitmap bitmap) throws UnsupportedEncodingException {
            BDFaceImageInstance rgbInstance = new BDFaceImageInstance(bitmap);
            FaceInfo[] faceInfos = FaceSDKManager.getInstance().getFaceDetect()
                    .detect(BDFaceSDKCommon.DetectType.DETECT_VIS, rgbInstance);

            byte[] feature = new byte[512];
            if (faceInfos != null && faceInfos.length > 0) {
                // 判断质量检测，针对模糊度、遮挡、角度
                Pair<Boolean, String> pair = qualityCheck(faceInfos[0]);

                float ret = -1;
                if (pair.first) {
                    ret = FaceSDKManager.getInstance().getFaceFeature().feature(BDFaceSDKCommon.FeatureType.
                            BDFACE_FEATURE_TYPE_ID_PHOTO, rgbInstance, faceInfos[0].landmarks, feature);
                    Log.i("qing", "ret:" + ret);
                    if (ret == 128) {
                        return new Pair<>(true, feature);
                    } else {
                        return new Pair<>(false, "未完成人脸比对，可能原因，人脸太小（小于sdk初始化设置的最小检测人脸），人脸不是朝上，sdk不能检测出人脸".getBytes("utf-8"));
                    }
                } else {
                    return new Pair<>(false, pair.second.getBytes("utf-8"));
                }
            } else {
                return new Pair<>(false, "未检测到人脸,可能原因人脸太小".getBytes("utf-8"));
            }
    }

    //魔改自FaceIdCompareActivity
    public static Pair<Boolean,String> qualityCheck(final FaceInfo faceInfo) {
        if (!SingleBaseConfig.getBaseConfig().isQualityControl()) {
            return new Pair<>(true,null);
        }
        if (faceInfo != null) {

            // 模糊结果过滤
            float blur = faceInfo.bluriness;
            if (blur > SingleBaseConfig.getBaseConfig().getBlur()) {
                return new Pair<>(false,"图片模糊");
            }

            // 光照结果过滤
            float illum = faceInfo.illum;
            if (illum < SingleBaseConfig.getBaseConfig().getIllumination()) {
                return new Pair<>(false,"图片光照不通过");
            }

            // 遮挡结果过滤
            if (faceInfo.occlusion != null) {
                BDFaceOcclusion occlusion = faceInfo.occlusion;
                if (occlusion.leftEye > SingleBaseConfig.getBaseConfig().getLeftEye()) {
                    return new Pair<>(false,"左眼遮挡");
                } else if (occlusion.rightEye > SingleBaseConfig.getBaseConfig().getRightEye()) {
                    return new Pair<>(false,"右眼遮挡");
                } else if (occlusion.nose > SingleBaseConfig.getBaseConfig().getNose()) {
                    return new Pair<>(false,"鼻子遮挡");
                } else if (occlusion.mouth > SingleBaseConfig.getBaseConfig().getMouth()) {
                    return new Pair<>(false,"嘴巴遮挡");
                } else if (occlusion.leftCheek > SingleBaseConfig.getBaseConfig().getLeftCheek()) {
                    return new Pair<>(false,"左脸遮挡");
                } else if (occlusion.rightCheek > SingleBaseConfig.getBaseConfig().getRightCheek()) {
                    return new Pair<>(false,"右脸遮挡");
                } else if (occlusion.chin > SingleBaseConfig.getBaseConfig().getChinContour()) {
                    return new Pair<>(false,"下巴遮挡");
                } else {
                    return new Pair<>(true,null);
                }
            }
        }
        return new Pair<>(false,"未知异常");
    }
}
