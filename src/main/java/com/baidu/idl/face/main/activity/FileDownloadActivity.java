package com.baidu.idl.face.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.idl.face.main.activity.setting.SettingMainActivity;
import com.baidu.idl.face.main.db.DBManager;
import com.baidu.idl.face.main.listener.OnItemClickListener;
import com.baidu.idl.face.main.listener.OnItemLongClickListener;
import com.baidu.idl.face.main.listener.SdkInitListener;
import com.baidu.idl.face.main.manager.FaceSDKManager;
import com.baidu.idl.face.main.model.Group;
import com.baidu.idl.face.main.utils.*;
import com.baidu.idl.facesdkdemo.R;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import static java.lang.Thread.sleep;

public class FileDownloadActivity extends BaseActivity implements View.OnClickListener,
        OnItemClickListener, OnItemLongClickListener {

    private Context mContext;


    private Button btnBaseDownload, btnBaseUpload,btnBaseZip,btnBaseUnzip;
    private RecyclerView recyclerView;
    private FileDownloadAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);


        mContext = this;
        try {
            initView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * UI 相关VIEW 初始化
     */
    private void initView() throws InterruptedException {
        Toast.makeText(this, "Connecting...", Toast.LENGTH_LONG).show();

        Log.e("START","=========================");
        Thread thread = new NetworkUtils.MSGThread("senddoc",null,"GET");
        thread.start();
        thread.join();
        List<Pair<String,String>> fileLists = new ArrayList<>();
        if(((NetworkUtils.MSGThread) thread).getCode()!=200)
        {
            Toast.makeText(this, "Connection fail.", Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(this, "Connection success.", Toast.LENGTH_LONG).show();
            Map<String, Object> map = JsonUtils.jsonToMap(((NetworkUtils.MSGThread) thread).getMessage());
            Map<String, Object> arr = (Map<String, Object>) map.get("docdata");
            for(Map.Entry<String, Object> entry : arr.entrySet()){
                Map<String, Object> arr2 = (Map<String, Object>) entry.getValue();
                Log.e("JSON-",arr2.get("docname") + "|||" +arr2.get("url"));
                fileLists.add(new Pair<String, String> (arr2.get("docname").toString(),NetworkUtils.URL_h + arr2.get("url").toString()));
            }
        }


        btnBaseDownload = findViewById(R.id.btn_basedd);
        btnBaseUpload = findViewById(R.id.btn_baseud);
        btnBaseZip = findViewById(R.id.btn_basezip);
        btnBaseUnzip = findViewById(R.id.btn_baseunzip);

        TextView versionTv = findViewById(R.id.tv_version);
        versionTv.setText(String.format(" V %s", Utils.getVersionName(mContext)));

        btnBaseDownload.setOnClickListener(this);
        btnBaseUpload.setOnClickListener(this);
        btnBaseZip.setOnClickListener(this);
        btnBaseUnzip.setOnClickListener(this);


        recyclerView = findViewById(R.id.recycler_file_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);//线性布局
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FileDownloadAdapter(fileLists);
        recyclerView.setAdapter(adapter);
        adapter.setItemClickListener(this);
        adapter.setItemLongClickListener(this);
    }

    /**
     * 点击事件跳转路径
     *
     * @param view
     */
    @Override
    public void onClick(View view){
        try {
            //LogUtils.d("[click]","CLICK"+view.getId());
            String path="";
            switch (view.getId()) {

                case R.id.btn_basedd:
                    LogUtils.d("[base]","Download");
                    break;
                case R.id.btn_baseud:
                    LogUtils.d("[base]","Upload");
                    break;
                case R.id.btn_basezip:
                    LogUtils.d("[base]","Zip");
                    path=FileUtils.getSDRootFile().getPath()+"/ademo";
                    LogUtils.e("[ZIP_basepath]","path======"+path);
                    Toast.makeText(this, "Ziping.", Toast.LENGTH_LONG).show();
                    FileUtils.zipFile(new File(path + "/zup"), new ZipOutputStream(new FileOutputStream(path + "/cdx.zip")), "azup", true);
                    Toast.makeText(this, "Done.", Toast.LENGTH_LONG).show();
                    break;
                case R.id.btn_baseunzip:
                    LogUtils.d("[base]","Unzip");
                    path=FileUtils.getSDRootFile().getPath()+"/ademo";
                    LogUtils.e("[UNZIP_basepath]","path======"+path);
                    Toast.makeText(this, "Unziping.", Toast.LENGTH_LONG).show();
                    FileUtils.unzipFile(new File (path+"/cdx.zip"),path);
                    Toast.makeText(this, "Done.", Toast.LENGTH_LONG).show();
                    break;
            }
        }catch (Exception ex){}
    }



    /*
    public static class dat
    {
        public String name,url;
        public dat(){}
        public dat(String _n,String _u)
        {
            name=_n;
            url=_u;
        }
    }
    public List<dat> fakeList()
    {
        List<dat> ret=new ArrayList<>();
        ret.add(new dat("name1","url1"));
        ret.add(new dat("name2------","url2"));
        ret.add(new dat("20200216082350445.DOC","http://software.xmu.edu.cn/Manager/Upload/20200216082350445.DOC"));
        ret.add(new dat("__________name4","url4"));
        ret.add(new dat("title.jpg","http://software.xmu.edu.cn/View/Image/title.jpg"));
        return ret;
    }
    //*/

    class FileDownloadViewHolder extends RecyclerView.ViewHolder{
        private TextView textFile,textUrl;
        public FileDownloadViewHolder(View itemView) {
            super(itemView);
            textFile = itemView.findViewById(R.id.recycler_file);
            textUrl = itemView.findViewById(R.id.recycler_url);
        }
    }

    public class FileDownloadAdapter extends RecyclerView.Adapter<FileDownloadActivity.FileDownloadViewHolder> implements
            View.OnClickListener, View.OnLongClickListener {
        private List<Pair<String,String> > mList;
        private OnItemClickListener mItemClickListener;
        private OnItemLongClickListener mItemLongClickListener;


        private void setItemClickListener(OnItemClickListener itemClickListener) {
            mItemClickListener = itemClickListener;
        }

        private void setItemLongClickListener(OnItemLongClickListener itemLongClickListener) {
            mItemLongClickListener = itemLongClickListener;
        }

        public FileDownloadAdapter(List<Pair<String,String>> _list)
        {
            mList=_list;
        }

        @Override
        public FileDownloadActivity.FileDownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_download_list, parent, false);
            FileDownloadActivity.FileDownloadViewHolder viewHolder = new FileDownloadActivity.FileDownloadViewHolder(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return viewHolder;
        }

        //绑定控件
        @Override
        public void onBindViewHolder(final FileDownloadActivity.FileDownloadViewHolder holder, final int position) {
            holder.itemView.setTag(position);
            Pair<String,String> item = mList.get(position);
            holder.textFile.setText(item.first);
            holder.textUrl.setText(item.second);
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }
        public Pair<String, String> getDat(int pos) {return mList.get(pos);}

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, (Integer) view.getTag());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (mItemLongClickListener != null) {
                mItemLongClickListener.onLongItemClick(view, (Integer) view.getTag());
            }
            return true;
        }
    }
    //*/

    // 用于adapter的item点击事件
    @Override
    public void onItemClick(View view, int position){
        Toast.makeText(this, "pos = "+position, Toast.LENGTH_SHORT).show();
        NetworkUtils.DLThread th= new NetworkUtils.DLThread(adapter.getDat(position).first,adapter.getDat(position).second);
        th.start();
        Log.e("Download","Begin from "+adapter.getDat(position).second);
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(this, "Download ERROR. Throw "+ e, Toast.LENGTH_SHORT).show();
            return;
        }
        if (th.done)
            Toast.makeText(this, "下载完成，请至文件夹内查看", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Download ERROR. Bad Request.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLongItemClick(View view, int position) {
        Toast.makeText(this, "Long , pos = "+position, Toast.LENGTH_SHORT).show();
        /*
        mPosition = position;
        if (mRelativePop.getVisibility() == View.GONE) {
            mRelativePop.setVisibility(View.VISIBLE);
        }
        */
    }


}
