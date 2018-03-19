package com.coder.binauralbeats.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by GreendaMi on 2016/11/25.
 */

@SuppressLint("AppCompatCustomView")
public class IconFontTextView extends TextView {
    public IconFontTextView(Context context) {
        super(context);
    }

    public IconFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "iconfont/iconfont.ttf");
        setTypeface(typeface);
    }


}
