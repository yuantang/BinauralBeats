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
//        if (id == R.id.nav_activity) {
//
//        } else
        if (id == R.id.nav_theme) {
            nightMode((Activity) context);
        }  if (id == R.id.nav_share) {
            Intent textIntent = new Intent(Intent.ACTION_SEND);
            textIntent.setType("text/plain");
            textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
            context.startActivity(Intent.createChooser(textIntent, "分享"));
        } else if (id == R.id.nav_about){

        }
        return false;
    }
    private static void nightMode(final Activity activity) {
        Preferences.saveNightMode(!Preferences.isNightMode());
        activity.recreate();
    }
}
