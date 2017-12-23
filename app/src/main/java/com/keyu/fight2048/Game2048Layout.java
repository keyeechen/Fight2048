package com.keyu.fight2048;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by focus on 2017/12/15.
 */

public class Game2048Layout extends RelativeLayout {
    private int mColumns = 4;
    private  final  int LIMIT = 2048;
    private SquareView[] gameItems;
    private int mMargin = 10;//方块的横向和纵向距离
    private int mPadding;//布局的四边周围边距
    private GestureDetector mGestureDetector;//捕捉拥护的手势
    private boolean isFirst = true;//只在第一次测量时计算小方块的位置
    private boolean isMoveHappen = false;//是否发生移动
    private boolean isMergeHappen = false;//是否发生合并
    private GameListener gameListener;
    private Context mContext;
    private int mScore;
    private boolean win = false;
    private boolean newNumFlag = false;
    private enum ACTION {
        LEFT, RIGHT, UP, DOWN
    }

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
        mGestureDetector = new GestureDetector(context, new MyGestureDetectorListener());
        if (mContext instanceof GameListener) {
            gameListener = (GameListener) mContext;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wholeSize = Math.min(getMeasuredHeight(), getMeasuredWidth());
        int squareWidth = (wholeSize - 2 * mPadding - (mColumns - 1) * mMargin) / mColumns;
        if (isFirst) {
            gameItems = new SquareView[mColumns * mColumns];
            for (int i = 0; i < mColumns * mColumns; i++) {
                SquareView square = new SquareView(mContext);
                gameItems[i] = square;
                square.setId(i + 1);
                RelativeLayout.LayoutParams lp = new LayoutParams(squareWidth, squareWidth);
                //不是最后一列，则有右边距
                if ((i + 1) % mColumns != 0) {
                    lp.rightMargin = mMargin;
                }
                //如果不是第一列，需要设置RIGHT OF
                if (i % mColumns != 0) {
                    lp.addRule(RelativeLayout.RIGHT_OF, gameItems[i - 1].getId());
                }
                //不是第一行的处理, 要把方块放在上方方块之下
                if (i / mColumns != 0) {
                    lp.topMargin = mMargin;
                    lp.addRule(RelativeLayout.BELOW, gameItems[i - mColumns].getId());
                }
                addView(square, lp);//添加小方块到父控件中
            }
            generateNum();
        }
        isFirst = false;
        setMeasuredDimension(wholeSize, getMeasuredHeight());//修改大方块的尺寸
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    private void actionMove(ACTION action) {
        Log.i(getClass().getName(), "action = " + action);
        for (int i = 0; i < mColumns; i++) {
            List<SquareView> row = new ArrayList<>();
            for (int j = 0; j < mColumns; j++) {
                int index = getIndexByAction(action, i, j);
                SquareView squareView = gameItems[index];
                if (squareView.getNumber() != 0) {
                    row.add(squareView);
                }
            }
            //判断是否有移动
            for (int j = 0; j < mColumns && j < row.size(); j++) {
                int index = getIndexByAction(action, i, j);
                if (row.get(j).getNumber() != gameItems[index].getNumber()) {
                    isMoveHappen = true;
                    break;
                }
            }
            //合并方块

            mergeItems(row);
            for (int j = 0; j < mColumns; j++) {
                int index = getIndexByAction(action, i, j);
                if(j < row.size()){
                    gameItems[index].setNumber(row.get(j).getNumber());
                }
                else{
                    gameItems[index].setNumber(0);
                }
            }

        }
        if (win) {
            if (gameListener != null) {
                gameListener.onGameWin(new GameCallBack() {
                    @Override
                    public void doAfterJob() {
                        startOver();
                    }
                });
            }
        } else {
            generateNum();
        }

    }

    private class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {

        float mDensity = getResources().getDisplayMetrics().density;
        float MIN_DISTANCE = mDensity * 8;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float xDelta = e2.getX() - e1.getX();
            float yDelta = e2.getY() - e1.getY();
            if (xDelta > MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {//move right
                actionMove(ACTION.RIGHT);
            } else if (xDelta < -MIN_DISTANCE && Math.abs(velocityX) > Math.abs(velocityY)) {//move left
                actionMove(ACTION.LEFT);
            } else if (yDelta > MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {//move down
                actionMove(ACTION.DOWN);
            } else if (yDelta < -MIN_DISTANCE && Math.abs(velocityX) < Math.abs(velocityY)) {//move up
                actionMove(ACTION.UP);
            }

            return true;
        }
    }

    private int getIndexByAction(ACTION action, int i, int j) {
        switch (action) {
            case LEFT:
                return i * mColumns + j;
            case RIGHT:
                return i * mColumns + mColumns - j - 1;
            case UP:
                return j * mColumns + i;
            case DOWN:
                return (mColumns - 1 - j) * mColumns + i;
        }
        return i * mColumns + j;
    }


    /**
     * 合并临近且数字相同的方块
     */
    private void mergeItems(List<SquareView> row) {
        if (row.size() <= 1) return;
        for (int i = 0; i < row.size() - 1; i++) {
            SquareView item1 = row.get(i);
            SquareView item2 = row.get(i + 1);
            if (item1.getNumber() == item2.getNumber()) {
                isMergeHappen = true;
                int newNum = 2 * item1.getNumber();
                item1.setNumber(newNum);
                mScore += newNum;
                if (gameListener != null) {
                    gameListener.onScoreChange(mScore);
                }
                if (newNum == LIMIT) {
                    win = true;
                }
                //将后续元素向前移动
                for (int j = i + 1; j < row.size() - 1; j++) {
                    row.get(j).setNumber(row.get(j + 1).getNumber());
                }
                row.get(row.size() - 1).setNumber(0);
            }

        }
    }

    /**
     * 检查是否填满数字
     *
     * @return 数字填满：true，否则：false
     */
    private boolean isFull() {
        for (int i = 0; i < gameItems.length; i++) {
            if (gameItems[i].getNumber() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查游戏是否结束
     *
     * @return
     */
    private boolean checkOver() {
        if (!isFull()) {
            return false;
        }
        for (int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mColumns; j++) {
                int ind = i * mColumns + j;
                SquareView curView = gameItems[ind];
                //判断与左边是否相同
                if (ind % mColumns > 0) {
                    SquareView leftView = gameItems[ind - 1];
                    if (curView.getNumber() == leftView.getNumber()) {
                        return false;
                    }
                }
                //判断与右边是否相同
                if ((ind + 1) % mColumns != 0) {
                    SquareView rightView = gameItems[ind + 1];
                    if (curView.getNumber() == rightView.getNumber()) {
                        return false;
                    }
                }
                //判断与上边是否相同
                if (ind / mColumns > 0) {
                    Log.i(getClass().getName(), "up ind = " + ind);
                    SquareView upView = gameItems[ind - mColumns];
                    if (curView.getNumber() == upView.getNumber()) {
                        return false;
                    }
                }
                //判断与下边是否相同
                if (ind / mColumns < mColumns - 1) {
                    Log.i(getClass().getName(), "down ind = " + ind);
                    SquareView downView = gameItems[ind + mColumns];
                    if (curView.getNumber() == downView.getNumber()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 在空的格子上产生数字
     */
    private void generateNum() {

        if (checkOver()) {
            Log.i(getClass().getSimpleName(), "Game Over");
            if (gameListener != null) {
                gameListener.onGameOver(new GameCallBack() {
                    @Override
                    public void doAfterJob() {
                        startOver();
                    }
                });
            }
        }
        newNumFlag = false;
        if (!isFull()) {
            if (isMergeHappen || isMoveHappen || isFirst) {
                Random rand = new Random();
                int nextInd = rand.nextInt(mColumns * mColumns);
                SquareView squareView = gameItems[nextInd];
                while (squareView.getNumber() != 0) {
                    nextInd = rand.nextInt(mColumns * mColumns);
                    squareView = gameItems[nextInd];
                    newNumFlag = true;
                }
                squareView.setNumber(Math.random() > 0.5 ? 2 : 4);
                isMergeHappen = isMoveHappen = false;
            }
        }

        if (newNumFlag && checkOver()) {
            Log.i(getClass().getSimpleName(), "Game Over");
            if (gameListener != null) {
                gameListener.onGameOver(new GameCallBack() {
                    @Override
                    public void doAfterJob() {
                        startOver();
                    }
                });
            }
        }
    }

    /**
     * 重新开始游戏
     */
    public void startOver() {
        for (int i = 0; i < mColumns; i++) {
            for (int j = 0; j < mColumns; j++) {
                int ind = i * mColumns + j;
                gameItems[ind].setNumber(0);
            }
        }
        win = false;
        isFirst = true;
        mScore = 0;
        generateNum();
    }

}
