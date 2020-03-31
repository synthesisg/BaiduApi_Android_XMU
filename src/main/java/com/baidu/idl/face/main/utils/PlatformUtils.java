package com.baidu.idl.face.main.utils;

import android.util.Log;
import com.baidu.idl.face.main.model.User;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PlatformUtils {
    private static PlatformUtils instance;
    private static boolean isLogin=false;
    private static String username=null;
    private static User user=null;
    /*
    * conn...
    * */
    private PlatformUtils()
    {
        //单例
    }
    public static synchronized PlatformUtils getInstance() {
        if (instance == null) {
            instance = new PlatformUtils();
        }
        return instance;
    }
    public void addUser()
    {
        String username=null,groupId=null,userInfo=null;
        byte[] faceFeature=null;
        String imageName = groupId + "-" + username + ".jpg";

        //registerUserIntoDBmanager(groupId, username, imageName, userInfo, faceFeature);
    }
    public void queryUser(String username)
    {

    }
    public void queryList()
    {

    }
    public static void updateUser()
    {

    }
    public static String conn(String _url, Map<String, String> map)
    {
        try {
            URL url = new URL(_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // 设置请求参数（过期时间，输入、输出流、访问方式），以流的形式进行连接
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(false);  //POST为true
            connection.setDoInput(true);
            connection.setRequestMethod("GET");//POST
            // 设置是否使用缓存
            connection.setUseCaches(true);//可false
            // 设置此 HttpURLConnection 实例是否应该自动执行 HTTP 重定向
            connection.setInstanceFollowRedirects(true);
            connection.setConnectTimeout(3000);

            // 写入数据
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            StringBuffer sBuffer = new StringBuffer();
            for (String key : map.keySet()) {
                sBuffer.append(key + "=" + map.get(key) + "&");
            }
            out.writeBytes(sBuffer.toString());
            out.flush();
            out.close();


            connection.connect();
            int code = connection.getResponseCode();
            String msg = "";
            if (code == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {msg += line + "\n"; }
                reader.close();
            }
            connection.disconnect();
            System.out.println(msg);
            return msg;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "-1";
    }

    //android.os.NetworkOnMainThreadException
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

    public static void Login(User _user)
    {
        user=_user;
        isLogin=true;
    }
    public static void Logout()
    {
        username=null;
        user=null;
        isLogin=false;
    }
    public static User getUser()
    {
        return user;
    }
    public static void setUser(User _user) { user=_user; }
    public static boolean isLogin()
    {
        return isLogin;
    }
}
