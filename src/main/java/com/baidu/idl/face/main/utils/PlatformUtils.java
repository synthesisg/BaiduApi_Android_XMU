package com.baidu.idl.face.main.utils;

import android.util.Log;
import com.baidu.idl.face.main.api.FaceApi;
import com.baidu.idl.face.main.model.User;
import com.baidu.idl.face.main.model.uuid_utime;

import java.util.*;

import android.util.Pair;

public class PlatformUtils {
    private static PlatformUtils instance;
    private static boolean isLogin=false;
    private static User user=null;
    /*
    * 主要为与平台端相关的数据库交互。
    *  更新后还需拉取图片。
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
    public boolean addUser(User add_user)
    {
        return false;
        /*
        Pair<Integer,String> pair = NetworkUtils.conn_post(NetworkUtils.URL_h + "ADD",User2Map(add_user));
        if(pair.first==200) return true;
        return false;
        //*/
    }
    public boolean updateUser(User update_user)
    {
        return false;
        /*
        Pair<Integer,String> pair = NetworkUtils.conn_post(NetworkUtils.URL_h + "UPDATE",User2Map(update_user));
        if(pair.first==200) return true;
        return false;
        //*/
    }
    public User queryUserByUUID(String uuid)
    {
        Map<String, String> map=new HashMap<>();
        map.put("uuid",uuid);
        Pair<Integer,String> pair = NetworkUtils.conn_get(NetworkUtils.URL_h + "Query",map);
        if(pair.first==200) return String2User(pair.second);
        return null;
    }
    public List<uuid_utime> queryAllUser_time()
    {
        List<uuid_utime> lists=new ArrayList<>();
        Pair<Integer,String> pair = NetworkUtils.conn_get(NetworkUtils.URL_h + "QueryUI",null);
        if(pair.first==200) String2User(pair.second);
        return  lists;
    }
    public List<User> queryAllUser()
    {
        List<User> lists=new ArrayList<>();
        Pair<Integer,String> pair = NetworkUtils.conn_get(NetworkUtils.URL_h + "Query",null);
        if(pair.first==200) String2User(pair.second);
        return  lists;
    }
    public void UpdateData()
    {
        List<uuid_utime> locals = FaceApi.getInstance().getLocalList();
        List<uuid_utime> platforms =queryAllUser_time();

        List<String> DelLists = new ArrayList<>();
        List<String> UpdateLists = new ArrayList<>();
        List<String> AddLists = new ArrayList<>();

        List<String> LocalUidList = new LinkedList<>();
        for(uuid_utime item : locals)
            LocalUidList.add(item.uuid);

        for(uuid_utime item : platforms)
        {
            if(locals.contains(item))
            {
                //比对正确 剔除
                LocalUidList.remove(item.uuid);
            } else if (LocalUidList.contains(item.uuid))
            {
                //更新时间不一致 更新
                UpdateLists.add(item.uuid);
                LocalUidList.remove(item.uuid);
            } else
            {
                //本地无记录 添加
                AddLists.add(item.uuid);
            }
        }
        //服务器无记录 删除
        for(String uid : LocalUidList)
            DelLists.add(uid);

        for(String uid : DelLists) Log.e("DEL","uid = " + uid);
        for(String uid : UpdateLists) Log.e("UPD","uid = " + uid);
        for(String uid : AddLists) Log.e("ADD","uid = " + uid);

        /*
        for(String uid : DelLists) FaceApi.getInstance().userDelete(uid);
        for(String uid : UpdateLists)
        {
            FaceApi.getInstance().userUpdate(queryUserByUUID(uid));
        }
        for(String uid : AddLists)
        {
            FaceApi.getInstance().userAdd(queryUserByUUID(uid));
        }
         //*/
    }


    private User String2User(String string)
    {
        return new User();
    }
    private Map<String, String> User2Map(User _user)
    {
        Map<String, String> ret=new HashMap<>();
        ret.put("uuid",_user.getUserId());
        ret.put("user_name",_user.getUserName());
        ret.put("group_id",_user.getGroupId());
        ret.put("user_info",_user.getUserInfo());
        ret.put("feature", TransformUtils.Byte2Str(_user.getFeature()));
        ret.put("image_name",_user.getImageName());
        //ret.put("face_token",_user.getFaceToken());
        ret.put("update_time",""+_user.getUpdateTime());
        return ret;
    }
    public void Login(User _user)
    {
        user=_user;
        isLogin=true;
    }
    public void Logout()
    {
        user=null;
        isLogin=false;
    }
    public User getUser()
    {
        return user;
    }
    public void setUser(User _user) { user=_user; }
    public boolean isLogin()
    {
        return isLogin;
    }
}
