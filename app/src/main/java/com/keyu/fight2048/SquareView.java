package com.keyu.fight2048;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Administrator on 2017/11/1.
 */

public class SquareView extends View {
    private String mText;//数字对应的字符串

    private int mNumber;//数字
    private Paint mPaint;
    private Context mContext;
    private Rect mBounds;//数字所占的矩形框，需要测量
    private float mDensity;
    public SquareView(Context context) {
        this(context, null);
    }

    public int getNumber() {
        return mNumber;
    }

    public void setNumber(int number) {
        mNumber = number;
        mText = mNumber + "";
        float scaledDensity = mContext.getResources().getDisplayMetrics().scaledDensity;
        mPaint.setTextSize(36 * scaledDensity);
        mPaint.setAntiAlias(true);
        mBounds = new Rect();//数字所占矩形框，用于控制数字的显示位置
        mPaint.getTextBounds(mText, 0, mText.length(), mBounds);
        invalidate();//重新执行onDraw
    }


    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mPaint = new Paint();
        mDensity = mContext.getResources().getDisplayMetrics().density;
        }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int bgColor;
        switch (mNumber) {
            case 0:
                bgColor = R.color.color_num_0;
                break;
            case 2:
                bgColor = R.color.color_num_2;

                break;
            case 4:
                bgColor = R.color.color_num_4;

                break;
            case 8:
                bgColor = R.color.color_num_8;

                break;
            case 16:
                bgColor = R.color.color_num_16;
                break;
            case 32:
                bgColor = R.color.color_num_32;
                break;
            case 64:
                bgColor = R.color.color_num_64;
                break;
            case 128:
                bgColor = R.color.color_num_128;
                break;
            case 256:
                bgColor = R.color.color_num_256;
                break;
            case 512:
                bgColor = R.color.color_num_512;
                break;
            case 1024:
                bgColor = R.color.color_num_1024;
                break;
            case 2048:
                bgColor = R.color.color_num_2048;
                break;
            default:
                bgColor = R.color.color_num_default;
                break;
        }
        mPaint.setColor(mContext.getResources().getColor(bgColor));
        mPaint.setStyle(Paint.Style.FILL);
        //canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 4 * mDensity, 4 * mDensity, mPaint);
        if (mNumber != 0) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        int textColor;
        switch (mNumber) {

            case 2:
            case 4:
                textColor = R.color.color_text_2_4;
                break;
            case 8:
            case 16:
            case 32:
            case 64:
            case 128:
            case 256:
            case 512:
            case 1024:
            case 2048:
                textColor = R.color.color_text_others;
                break;
            default:
                textColor = R.color.color_text_2_4;
                break;
        }
        mPaint.setColor(mContext.getResources().getColor(textColor));
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(2 * mDensity);
        float x = getWidth() / 2 - mBounds.width() / 2;
        float y = getHeight() / 2 + mBounds.height() / 2;//字的坐标比较特殊
        canvas.drawText(mText, x, y, mPaint);
    }

}

