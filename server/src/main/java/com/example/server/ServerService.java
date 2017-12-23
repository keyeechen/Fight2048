package com.example.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
    private final int SERVER_PORT = 10001;
    private List<Socket> socketList = new ArrayList<>();
    private ServerSocket serverSocket;
    private ExecutorService mExecutorService;
    private String sendMsg = null;
    private String receiveMsg = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            Log.i(getClass().getName(), "server starts at port:" + SERVER_PORT);
            mExecutorService = Executors.newCachedThreadPool();
            mExecutorService.execute(new Runnable() {
                Socket client;

                @Override
                public void run() {
                    while (true) {
                        try {
                            client = serverSocket.accept();
                            socketList.add(client);
                            mExecutorService.execute(new ServerThread(client));
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
        private BufferedReader in;
        private PrintWriter out;

        public ServerThread(Socket mSocket) {
            this.mSocket = mSocket;
            try {
                in = new BufferedReader(new InputStreamReader(mSocket.getInputStream(), "UTF-8"));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream(), "UTF-8")), true);
                out.println("I`m server.");
            } catch (IOException ioe) {

            }
        }


        @Override
        public void run() {

            try {
                while (true) {
                    if ((receiveMsg = in.readLine()) != null) {

                        sendMsg = " I have received: " + receiveMsg;
                        Log.i(getClass().getName(), sendMsg);
                        out.println(sendMsg);
                    }
                }
            } catch (IOException ioe) {

            }
        }
    }
}
