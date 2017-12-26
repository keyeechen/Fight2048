package com.example.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.keyu.fight2048.bean.Message2048;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 局域网的服务器端
 * Created by focus on 2017/12/23.
 */

public class ServerService extends Service {
    private final int SERVER_PORT = 6666;
    private List<Socket> socketList = new ArrayList<>();
    private List<ServerThread> threadList = new ArrayList<>();
    private ServerSocket serverSocket;
    private ExecutorService mExecutorService;
    private String sendMsg = null;
    private Message2048 receiveMsg = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            Log.i(getClass().getName(), "server starts at port:" + SERVER_PORT);
            mExecutorService = Executors.newCachedThreadPool();
            mExecutorService.execute(new Runnable() {

                @Override
                public void run() {
                    while (true) {
                        try {
                            Socket client = serverSocket.accept();
                            Log.i(getClass().getName(), "new client.");
                            socketList.add(client);
                            ServerThread serverThread = new ServerThread(client);
                            threadList.add(serverThread);
                            mExecutorService.execute(serverThread);

                        } catch (IOException ioe) {
                        }

                    }
                }
            });


        } catch (IOException ios) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 处理与客户端的通信
     */
    class ServerThread implements Runnable {
        private Socket mSocket;
        private ObjectInputStream in = null;
        private ObjectOutputStream out = null;

        public ServerThread(Socket mSocket) {
            this.mSocket = mSocket;
            try {
                out = new ObjectOutputStream(mSocket.getOutputStream());
                in = new ObjectInputStream(new BufferedInputStream(mSocket.getInputStream()));
                Message2048 msg = new Message2048(4);
                msg.setUserName("server");
                out.writeObject(msg);
                out.flush();
            } catch (IOException ioe) {

            }
        }


        @Override
        public void run() {

            try {
                while (true) {
                    receiveMsg = (Message2048) in.readObject();
                    if (receiveMsg != null) {
                        Log.i(getClass().getName(), receiveMsg.toString());
                        for (ServerThread st : threadList) {
                            st.sendMsg();
                        }
                    }
                }
            } catch (Exception ioe) {

            }
        }

        /**
         * 转发消息给各客户端
         */
        public void sendMsg() {
            try {
                out.writeObject(receiveMsg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
