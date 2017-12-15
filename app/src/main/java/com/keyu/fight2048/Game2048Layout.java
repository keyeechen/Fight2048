package com.keyu.fight2048;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.widget.RelativeLayout;

/**
 * Created by focus on 2017/12/15.
 */

public class Game2048Layout extends RelativeLayout {
    private int mColumns = 4;
    private SquareView[] gameItems;
    private int mMargin = 10;//方块的横向和纵向距离
    private int mPadding;//布局的四边周围边距
    private GestureDetector mGestureDetector;//捕捉拥护的手势
    private boolean isFirst = true;//只在第一次测量时计算小方块的位置
    private Context mContext;
    public Game2048Layout(Context context) {
        super(context);
    }

    public Game2048Layout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mMargin, getResources().getDisplayMetrics());
        mPadding = Math.min(getPaddingLeft(), getPaddingRight());
        int tempMin = Math.min(getPaddingBottom(), getPaddingTop());
        mPadding = Math.min(mPadding, tempMin);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wholeSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
        int squareWidth = (wholeSize - 2 * mPadding - (mColumns - 1) * mMargin) / mColumns;
        gameItems = new SquareView[mColumns * mColumns];
        for (int i = 0; i < mColumns * mColumns; i++) {
             SquareView square = new SquareView(mContext);
             gameItems[i] = square;
             RelativeLayout.LayoutParams lp = new LayoutParams(squareWidth, squareWidth);
             //不是最后一列，则有右边距
             //不是第一列
        }
    }
}
