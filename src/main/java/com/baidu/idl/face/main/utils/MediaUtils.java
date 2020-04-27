package com.baidu.idl.face.main.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Pair;
import com.baidu.idl.main.facesdk.FaceInfo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

public class MediaUtils {
    private String path;
    private MediaMetadataRetriever mmr  = new android.media.MediaMetadataRetriever();
    private String duration;
    private int time;
    public MediaUtils(String _path)
    {
        path = _path;
        mmr.setDataSource(path);
        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        time = Integer.parseInt(duration);
    }
    public Bitmap getFrame(int ms)
    {
        if (mmr == null) return null;
        return mmr.getFrameAtTime(ms*1000,OPTION_CLOSEST_SYNC );
    }
    public List<Bitmap> getAllFrame()
    {
        if (mmr == null) return null;
        List<Bitmap> lists = new ArrayList<>();
        for(int i=0;i<time;i+=500) lists.add(getFrame(i));
        return lists;
    }
    public String getDuration() {return duration;}
    public List<String> autoRun() throws UnsupportedEncodingException {
        if (mmr == null) return null;
        List<Bitmap> bitmaps = getAllFrame();
        List<FaceInfo> faceinfos = new ArrayList<>();
        List<byte[]> bytes = new ArrayList<>();
        for(Bitmap item : bitmaps)
        {
            Pair<Boolean,byte[]> feature = TransformUtils.Bitmap2Feature(item);
            if(feature.first)
            {
                boolean com = true;
                for(byte[] by : bytes)
                {
                    if(TransformUtils.Compare(feature.second,by)>80)
                    {
                        com = false;
                        break;
                    }
                }
                if(com) {
                    bytes.add(feature.second);
                    faceinfos.add(TransformUtils.Bitmap2Faceinfo(item));
                }
            }
        }
        List<String> ret = new ArrayList<>();
        for(byte[] by : bytes) {
            ret.add(TransformUtils.Byte2Str(by));
        }

        mmr = null ;
        //del file
        return ret;
    }
}
