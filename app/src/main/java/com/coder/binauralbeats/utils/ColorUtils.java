package com.coder.binauralbeats.utils;

import android.content.Context;
import android.util.TypedValue;

import com.coder.binauralbeats.R;

/**
 * Created by yuant on 2018/3/19.
 */

public class ColorUtils {
    /**
     * 获取主题颜色
     * @return
     */
    public static int getColorPrimary(Context context){
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取主题颜色
     * @return
     */
    public static int getDarkColorPrimary(Context context){
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    public static int getAccentColor(Context context){
        TypedValue typedValue = new  TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }
}
