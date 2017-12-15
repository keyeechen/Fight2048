package com.keyu.fight2048;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2017/11/1.
 */

public class SquareView extends View {
    private String mText;
    public SquareView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SquareView, 0, 0);
        try {
            mText = typedArray.getString(R.styleable.SquareView_setText);
        }
        finally {
//            typedArray.recycle();
        }

    }

}
