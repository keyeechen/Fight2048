package com.keyu.fight2048;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.keyu.fight2048.bean.Message2048;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements  GameListener{
    private Game2048Layout game2048Layout;
    private Toolbar myToolbar;
    private SharedPreferences sp;
    private static  TextView tv_score;
    private TextView tv_best_score;
    private int highestScore;
    private static GameCallBack mGameCallBack;
    private ExecutorService executorService;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private final String SERVER_IP = "10.0.2.2";
    private final int SERVER_PORT = 6665;
    private int mColumns = 4;
    private String userName;
    private final int SLEEP_TIME = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game2048Layout = findViewById(R.id.game_area);
        float mDensity = getResources().getDisplayMetrics().density;
        sp = getPreferences(Context.MODE_PRIVATE);
        highestScore = sp.getInt("highestScore", 0);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.app_simple_name);
        myToolbar.setTitleTextColor(Color.WHITE);
        tv_score = findViewById(R.id.tv_score);
        tv_best_score = findViewById(R.id.tv_best_score);
        tv_best_score.setText(highestScore + "");
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.icon);
        int width = icon.getWidth();
        int height = icon.getHeight();
        float scale = 64.0f * mDensity / width;
        Log.i(getLocalClassName(), "width = " + width);
        Log.i(getLocalClassName(), "height = " + width);
        Log.i(getLocalClassName(), "mDensity = " + mDensity);
        Log.i(getLocalClassName(), "scale = " + scale);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(icon, 0, 0, width, height, matrix, true);
        Drawable drawable = new BitmapDrawable(resizedBitmap);
        myToolbar.setLogo(drawable);
        setSupportActionBar(myToolbar);
        executorService = Executors.newCachedThreadPool();


    }

    @Override
    public void onScoreChange(int score) {
        tv_score.setText(score + "");
        if (highestScore < score) {
            highestScore = score;
            sp.edit().putInt("highestScore", highestScore).commit();
            tv_best_score.setText(highestScore + "");
        }
    }

    @Override
    public void onGameOver(GameCallBack callBack) {
        mGameCallBack = callBack;
        GameOverDialog gameOverDialog = new GameOverDialog();
        gameOverDialog.show(getSupportFragmentManager(), "GameOver");
    }

    @Override
    public void onGameWin(GameCallBack callBack) {
        mGameCallBack = callBack;
        GameWinDialog winDialog = new GameWinDialog();
        winDialog.show(getSupportFragmentManager(), "GameWin");
    }

    @Override
    public void onNumsChange(int[] gameNums) {
        //executorService.execute(new SendThread(gameNums));
    }

    @Override
    public void onNumsSetup(int[] gameNums) {
        //executorService.execute(new ConnectThread(gameNums));
    }

    @Override
    public void onNumsChange1(ArrayList itemNumList) {
        executorService.execute(new SendThread(itemNumList));
    }

    @Override
    public void onNumsSetup1(ArrayList itemNumList) {
        executorService.execute(new ConnectThread(itemNumList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Start Over ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                game2048Layout.startOver();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create().show();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, ConnectActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    public static class GameOverDialog extends DialogFragment {

        public GameOverDialog() {
        }



        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_game_over_dialog, null);
            Button btn_try_again = view.findViewById(R.id.btn_try_again);
            btn_try_again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                     if(mGameCallBack != null){
                         tv_score.setText("0");
                         mGameCallBack.doAfterJob();
                         GameOverDialog.this.dismiss();
                     }
                }
            });
            return view;
        }
    }

    public static class GameWinDialog extends DialogFragment {
        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frag_game_win_dialog, null);
            Button btn_continue= view.findViewById(R.id.btn_continue);
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mGameCallBack != null){
                        tv_score.setText("0");
                        mGameCallBack.doAfterJob();
                        GameWinDialog.this.dismiss();
                    }
                }
            });
            return view;
        }
    }

    class ConnectThread implements Runnable {
        public ConnectThread(int[] gameNums) {
            this.gameNums = gameNums;
        }

        private int gameNums[];
        private ArrayList<Integer> itemNumList;

        public ConnectThread(ArrayList<Integer> itemNumList) {
            this.itemNumList = itemNumList;
        }


        @Override
        public void run() {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                userName = "user" + new Random().nextInt(100);
                Message2048 msg = new Message2048(mColumns);
                msg.setUserName(userName);
                msg.setType(new Random().nextInt(5));
                msg.setItemNums(gameNums);
                msg.setItemNumList(itemNumList);
                out.writeObject(msg);
                out.flush();
                receiveMsg();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendThread implements Runnable {
        public SendThread(int[] gameNums) {
            this.gameNums = gameNums;
        }
        private int gameNums[];

        public SendThread(ArrayList<Integer> itemNumList) {
            this.itemNumList = itemNumList;
        }

        private ArrayList<Integer> itemNumList;

        @Override
        public void run() {
            try {
                Message2048 msg = new Message2048(mColumns);
                msg.setUserName(userName);
                msg.setItemNums(gameNums);
                msg.setItemNumList(itemNumList);
                msg.setType(new Random().nextInt(5));
                out.writeObject(msg);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }


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
                            // tv_from_server.setText(msg.toString() +"\n" + tv_from_server.getText());
                            game2048Layout.updateBoard1(msg.getItemNumList());
                        }
                    });
                }
                Thread.sleep(SLEEP_TIME);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
