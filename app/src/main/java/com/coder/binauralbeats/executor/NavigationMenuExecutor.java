package com.coder.binauralbeats.executor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;

import com.coder.binauralbeats.ConsIntent;
import com.coder.binauralbeats.R;
import com.coder.binauralbeats.activity.WebviewActivity;
import com.coder.binauralbeats.utils.DevicesUtils;
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
                shareApp(context);
                return true;
            case R.id.nav_send:
                sendFeedback(context);
                return true;
            case R.id.nav_rate:
                doStartPlay(context);
                break;
            case R.id.nav_about:
                Intent intent = new Intent(context, WebviewActivity.class);
                context.startActivity(intent);
                return true;
        }
        return false;
    }

    private static void shareApp(Context context) {
        String url = "https://play.google.com/store/apps/details?id" + context.getPackageName();
        Intent textIntent = new Intent(Intent.ACTION_SEND);
        textIntent.setType("text/plain");
        textIntent.putExtra(Intent.EXTRA_TEXT, "Binaural Beats Therapy App "+url);
        context.startActivity(Intent.createChooser(textIntent, "share"));
    }

    private static void sendFeedback(Context context) {
        String[] email = {"tangyuan128@gmail.com"}; // 需要注意，email必须以数组形式传入
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822"); // 设置邮件格式
        intent.putExtra(Intent.EXTRA_EMAIL, email); // 接收人
        intent.putExtra(Intent.EXTRA_SUBJECT, "Binaural Beats Therapy Version:"+ DevicesUtils.getLocalVersionName(context)); // 主题
//        intent.putExtra(Intent.EXTRA_TEXT, "Binaural Beats Therapy Feedback"); // 正文
        context.startActivity(Intent.createChooser(intent, "请选择邮件类应用"));
    }

    private static void doStartPlay(Context context) {
        String mAddress = "market://details?id=" + context.getPackageName();
        Intent marketIntent = new Intent("android.intent.action.VIEW");
        marketIntent.setData(Uri.parse(mAddress ));
        context.startActivity(marketIntent);
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
