package com.baidu.idl.face.main.utils;

import android.util.Log;
import android.util.Pair;
import com.baidu.idl.face.main.activity.FileDownloadActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {
    private static String p_username="system";
    private static String p_password="FaceDetection";
    public static String URL_h="http://192.168.1.8:8080/web/f/appApi/";
    public static int WaitSec=5;

    public static Pair<Integer, String> testimpl()
    {
        Map<String, String> p = new HashMap<>();
        p.put("string","test");
        p.put("name","wtf");
        return conn_get(URL_h+"getstring", p);
        //return conn_post(URL_h+"getnull", null);
        //return conn_post(URL_h+"getpost", p);
    }
    public static Pair<Integer, String> conn_get(String _url, Map<String, String> map)
    {
        Log.e("CONN_GET","URL = "+ _url);
        try {
            StringBuffer tail=new StringBuffer("");
            // 写入数据
            if(map!=null&&map.isEmpty()==false) {
                tail.append("?");
                for (String key : map.keySet()) {
                    tail.append(key + "=" + map.get(key) + "&");
                }
                Log.e("CONN_GET","Buffer = "+ tail);
            }

            URL url = new URL(_url + tail);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setDoOutput(false);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(3000);
            connection.connect();
            int code = connection.getResponseCode();
            Log.e("CONN_GET","Code = "+ code);
            String msg = "";
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {msg += line + "\n"; }
                reader.close();
            }
            connection.disconnect();
            Log.e("CONN_GET","Message = "+ msg);
            return new Pair<>(code, msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(-1, null);
    }
    public static Pair<Integer, String> conn_post(String _url, Map<String, String> map)
    {
        Log.e("CONN","URL = "+ _url);
        try {
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(3000);

            // 写入数据
            if(map!=null && map.isEmpty()==false) {
                connection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                StringBuffer sBuffer = new StringBuffer();
                for (String key : map.keySet()) {
                    sBuffer.append(key + "=" + map.get(key) + "&");
                }

                Log.e("CONN_POST", "Buffer = " + sBuffer);
                out.writeBytes(sBuffer.toString());
                out.flush();
                out.close();
            }
            else
            {
                connection.setDoOutput(false);
            }

            connection.connect();
            int code = connection.getResponseCode();
            Log.e("CONN_POST","Code = "+ code);
            String msg = "";
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {msg += line + "\n"; }
                reader.close();
            }
            connection.disconnect();
            Log.e("CONN_POST","Message = "+ msg);
            return new Pair<>(code, msg);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Pair<>(-1, null);
    }

    //android.os.NetworkOnMainThreadException 需要多线程
    public static boolean downlaodFile(String urlStr, String path, String fileName) {
        Log.e("PlatformUtils","[downlaodFile] url = "+urlStr);
        InputStream input = null;
        try {
            Log.e("PlatformUtils","[getInputStearmFormUrl] url = " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setRequestMethod("GET");
            //urlConn.setDoOutput(true);
            //urlConn.setDoInput(true);
            urlConn.setRequestProperty("Content-Type", "application/octet-stream");
            urlConn.setRequestProperty("Connection", "Keep-Alive");
            urlConn.setRequestProperty("Charset", "UTF-8");
            urlConn.connect();
            Log.e("PlatformUtils","[getInputStearmFormUrl] CONN" );
            input = urlConn.getInputStream();
            Log.e("PlatformUtils","[getInputStearmFormUrl] input = "+ (input!=null) +", len = " + urlConn.getContentLength()+", path = "  +path+File.separator+fileName );

            if (input==null) return false;

            boolean succ = FileUtils.write2SDFromInput(input, path+File.separator+fileName);
            Log.e("PlatformUtils","[downlaodFile] done, succ = " + succ );
            return succ;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            try {
                if(input!=null) input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DLThread extends Thread {
        private FileDownloadActivity.dat item;

        public DLThread(FileDownloadActivity.dat _item) {
            item=_item;
            Log.e("DLThread","Create , url = " + item.url);
        }

        public void run() {
            Log.e("DLThread","Begin , path = "+ FileUtils.getSDRootFile().getPath()+"/ademo" + ", url = " + item.url);
            NetworkUtils.downlaodFile(item.url,FileUtils.getSDRootFile().getPath()+"/ademo",item.name);
            Log.e("DLThread","End .");
        }
    }

    public static class MSGThread extends Thread{
        private String message=null;
        private int code=0;
        private String url,method;
        private Map<String, String> map;

        public MSGThread(String _url, Map<String, String> _map, String method)
        {
            url=_url;
            map=_map;
        }
        public void run()
        {
            Log.e("MSGThread" , "Run");
            Pair<Integer, String> pair = null;
            //*
            pair = NetworkUtils.testimpl();
            //*/
            /*
            if ("GET".equals(method))
            {
                pair = PlatformUtils.conn_get(url,map);
            }
            if("POST".equals(method))
            {
                pair = PlatformUtils.conn_post(url,map);
            }
            //*/
            code = pair.first;
            message = pair.second;
            Log.e("MSGThread" , "code = " + code + " ,message = "+message);
        }
        public  String getMessage(){return message;}
        public int getCode() {return code;}
    }
}
