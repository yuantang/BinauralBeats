package com.coder.binauralbeats.executor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.utils.Preferences;

/**
 * Created by TUS on 2017/9/25.
 */

public class NavigationMenuExecutor {
    public static boolean onNavigationItemSelected(MenuItem item, Context context) {


        int id = item.getItemId();
        switch (id){
            case R.id.nav_theme:
                nightMode((Activity) context);
                 break;
            case R.id.nav_share:
                Intent textIntent = new Intent(Intent.ACTION_SEND);
                textIntent.setType("text/plain");
                textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
                context.startActivity(Intent.createChooser(textIntent, "分享"));
                return true;
            case R.id.nav_send:

                return true;
            case R.id.nav_about:

                return true;
        }
        return false;
    }
    private static void nightMode(final Activity activity) {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.recreate();
            }
        });
    }
}
