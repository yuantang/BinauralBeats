package com.coder.binauralbeats.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.coder.binauralbeats.MyApp;

import java.io.IOException;

public class PicassoUtils {

    public static Bitmap getBitmap(final int id) {
        return  BitmapFactory.decodeResource(MyApp.getInstance().getResources(), id);
    }
}
