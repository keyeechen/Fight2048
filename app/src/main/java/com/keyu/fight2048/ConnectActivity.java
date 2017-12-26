package com.keyu.fight2048;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.keyu.fight2048.bean.Message2048;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_from_server;
    private Button btn_connect;
    private Button btn_send;
    private Button btn_disconnect;
    private ExecutorService executorService;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String CHAR_SET = "UTF-8";
    private final String SERVER_IP = "10.0.2.2";
    private final int SERVER_PORT = 6665;
    private int mColumns = 4;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect2);
        tv_from_server = findViewById(R.id.tv_from_server);
        btn_send = findViewById(R.id.btn_send);
        btn_connect = findViewById(R.id.btn_connect);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        executorService = Executors.newCachedThreadPool();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_connect:
                executorService.execute(new ConnectThread());
                break;
            case R.id.btn_send:
                executorService.execute(new SendThread());
                break;
            case R.id.btn_disconnect:
                executorService.execute(new DisconnectThread());
                break;
        }
    }

    class ConnectThread implements Runnable {


        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
                userName = "user" + new Random().nextInt(100);
                Message2048 msg = new Message2048(mColumns);
                msg.setUserName(userName);
                msg.setType(new Random().nextInt(5));
                out.writeObject(msg);
                out.flush();
                receiveMsg();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendThread implements Runnable {
        @Override
        public void run() {
            try {
                Message2048 msg = new Message2048(mColumns);
                msg.setUserName(userName);
                msg.setType(new Random().nextInt(5));
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    class DisconnectThread implements Runnable {
        @Override
        public void run() {

        }
    }
    /**
     * 接收消息的方法
     */
    private void receiveMsg() {
        while (true) {
            try {
                final Message2048 msg = (Message2048) in.readObject();
                if (msg != null && !userName.equals(msg.getUserName())) {//只接收别的客户端的信息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_from_server.setText(msg.toString());
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
