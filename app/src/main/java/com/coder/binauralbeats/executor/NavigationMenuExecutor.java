package com.coder.binauralbeats.executor;

import android.content.Context;
import android.view.MenuItem;

/**
 * Created by TUS on 2017/9/25.
 */

public class NavigationMenuExecutor {
    public static boolean onNavigationItemSelected(MenuItem item, Context context) {
        int id = item.getItemId();
//        if (id == R.id.nav_activity) {
//
//        } else if (id == R.id.nav_history) {
//            context.startActivity(new Intent(context, HistoryActivity.class));
//        } else if (id == R.id.nav_statistics) {
//            context.startActivity(new Intent(context, StatisticsActivity.class));
//        } else if (id == R.id.nav_story) {
//            context.startActivity(new Intent(context, StoreRunningActivity.class));
//        } else if (id == R.id.nav_share) {
//            Intent textIntent = new Intent(Intent.ACTION_SEND);
//            textIntent.setType("text/plain");
//            textIntent.putExtra(Intent.EXTRA_TEXT, "这是一段分享的文字");
//            context.startActivity(Intent.createChooser(textIntent, "分享"));
//        } else if (id == R.id.nav_setting) {
//            context.startActivity(new Intent(context, SettingsActivity.class));
//        }else if (id == R.id.nav_about){
//
//        }
        return false;
    }
}
