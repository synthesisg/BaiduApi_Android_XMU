package com.baidu.idl.face.main.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import static android.media.MediaMetadataRetriever.OPTION_CLOSEST_SYNC;

public class MediaUtils {
    private String path;
    private MediaMetadataRetriever mmr  = new android.media.MediaMetadataRetriever();
    public MediaUtils(String _path)
    {
        path = _path;
        mmr.setDataSource(path);
        mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
    }
    public Bitmap getFrame(int ms)
    {
        return mmr.getFrameAtTime(ms*1000,OPTION_CLOSEST_SYNC );
    }
}
