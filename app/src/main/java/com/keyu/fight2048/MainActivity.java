package com.keyu.fight2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity implements  GameListener{
    LinearLayout ll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SquareView squareView = new SquareView(this, null);
        ll.addView(squareView);
        System.out.println("hello, world");
    }

    @Override
    public void onScoreChange(int score) {

    }
}
