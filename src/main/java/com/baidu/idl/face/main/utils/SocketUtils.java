package com.baidu.idl.face.main.utils;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SocketUtils extends Thread {

    private String HOST = "120.26.177.246";
    private int PORT = 8081;
    private Socket socket = null;
    private Handler handler;
    private BufferedReader bufferedReader = null;
    private OutputStream output = null;
    private int state = -1;
    public SocketUtils(Handler handler){
        Log.e("SocketUtils","socket Build.");
        this.handler = handler;
    }

    public void run() {
        while (true) {
            try {
                this.socket = new Socket(HOST, PORT);
                socket.setKeepAlive(true);
                //socket.setSoTimeout(5000);
                if (socket.isConnected()) {
                    Log.e("SocketUtils", "Conn Success!");
                    state = 1;
                } else {
                    Log.e("SocketUtils", "Conn Fail!");
                    state = 0;
                }
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String content = null;
                while((content = bufferedReader.readLine()) != null) {
                    Log.e("SocketUtils", "content = " + content);
                    Message msg = new Message();
                    msg.what = 200;
                    msg.obj = content;
                    handler.sendMessage(msg);
                }
                //}
            } catch (Exception e) {
                Log.e("SocketUtils", "socket catch Exception" + e);
                state = 0;
                e.printStackTrace();
                try {
                    sleep(3000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
    public boolean isConnected()
    {
        if (socket == null) return false;
        return socket.isConnected();
    }
    public int getstate()
    {
        return  state;
    }
}