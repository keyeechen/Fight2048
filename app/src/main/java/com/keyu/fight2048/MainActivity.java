package com.keyu.fight2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements  GameListener{
    private Game2048Layout game2048Layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game2048Layout = findViewById(R.id.game_area);
    }

    @Override
    public void onScoreChange(int score) {

    }

    @Override
    public void onGameOver() {

    }
}
