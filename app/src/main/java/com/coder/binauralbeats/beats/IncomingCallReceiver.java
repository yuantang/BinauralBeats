package com.coder.binauralbeats.beats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class IncomingCallReceiver extends BroadcastReceiver {

	private static  String TAG = "BBT-CALL";
	
    @Override
    public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            
            if(null == bundle)
                    return;
//
//            BBeatActivity app = BBeatActivity.getInstance();
//
//            Log.i(TAG,bundle.toString());
//
//            String state = bundle.getString(TelephonyManager.EXTRA_STATE);
//
//            Log.i(TAG, "State: "+ state);
//
//            if (app == null)
//            	return;
//
//            if (!app.isInProgram())
//            	return;
//
//            if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING) ||
//            		state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//            	if (app.isPaused() == false)
//            		app.pauseOrResume();
//            } else if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_IDLE)) {
//            	if (app.isPaused() == true)
//            		app.pauseOrResume();
//            }

    }

}