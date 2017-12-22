package com.keyu.fight2048;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements  GameListener{
    private Game2048Layout game2048Layout;
    private Toolbar myToolbar;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        game2048Layout = findViewById(R.id.game_area);
        float mDensity = getResources().getDisplayMetrics().density;
        sp = getPreferences(Context.MODE_PRIVATE);
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.app_simple_name);
        myToolbar.setTitleTextColor(Color.WHITE);
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
    }

    @Override
    public void onScoreChange(int score) {

    }

    @Override
    public void onGameOver() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                return true;
            case R.id.action_settings:
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
}
