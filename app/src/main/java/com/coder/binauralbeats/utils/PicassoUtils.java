package com.coder.binauralbeats.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import com.coder.binauralbeats.MyApp;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PicassoUtils {

    public static Bitmap getBitmap(final int id) {
//        final Bitmap[] bitmap = new Bitmap[1];
//        Handler uiHandler = new Handler(Looper.getMainLooper());
//        uiHandler.post(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    bitmap[0] =  Picasso.get().load(id).get();
//                } catch (IOException e) {
//                    e.printStackTrace();
//
//                }
//            }
//        });
        return  BitmapFactory.decodeResource(MyApp.getInstance().getResources(), id);
    }
}
