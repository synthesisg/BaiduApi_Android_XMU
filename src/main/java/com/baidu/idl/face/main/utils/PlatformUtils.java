package com.baidu.idl.face.main.utils;

import com.baidu.idl.face.main.model.User;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    public void updateUser()
    {

    }
    public String conn(String _url, Map<String, String> map)
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
    public static void Login(String name)
    {
        username=name;
        isLogin=true;
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
