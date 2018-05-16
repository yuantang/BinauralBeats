package com.coder.binauralbeats.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/***
 * ================================================
 * @时间  2017/9/25 on 17:02
 * @作者  Yuan
 * @类名  PermissionReq
 * @描述  运行时权限
 * ================================================
 */
public class PermissionReq {
    //获取权限的结果
    private PermissionResult mResult;
    //权限
    private String [] mPermissions;
    private static int sRequestCode = 0;
    private static SparseArray<PermissionResult> sResultArray = new SparseArray<>();

    private Context mContext;
    private PermissionReq(Context context){
        this.mContext=context;
    }

    public static PermissionReq with(@NonNull Activity activity) {
        return new PermissionReq(activity);
    }
    /**
     * 设置回调
     * @param result
     * @return
     */
    public PermissionReq result(@Nullable PermissionResult result) {
        mResult = result;
        return this;
    }
    /**
     * 请求权限
     * @param permissions
     * @return
     */
    public PermissionReq permissions(@NonNull String... permissions) {
        mPermissions = permissions;
        return this;
    }
    /**
     * 执行
     */
    public void request(){

        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.M){
            if (mResult!=null){
                mResult.onGranted();
            }
            return;
        }
        List<String> deniedPermissionList=getDeniedPermissions(mContext, mPermissions);
        if (deniedPermissionList.isEmpty()) {
            if (mResult != null) {
                mResult.onGranted();
            }
            return;
        }
        int requestCode = genRequestCode();
        String[] deniedPermissions = deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
        requestPermissions(mContext, deniedPermissions, requestCode);
        sResultArray.put(requestCode, mResult);

    }
    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Context context,String[] permissions, int requestCode) {
        ((Activity) context).requestPermissions(permissions, requestCode);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionResult result = sResultArray.get(requestCode);
        if (result == null) {
            return;
        }
        sResultArray.remove(requestCode);
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                result.onDenied();
                return;
            }
        }
        result.onGranted();
    }


    private static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permission);
            }
        }
        return deniedPermissionList;
    }
    private static Activity getActivity(Object object) {
        if (object != null) {
            if (object instanceof Activity) {
                return (Activity) object;
            } else if (object instanceof Fragment) {
                return ((Fragment) object).getActivity();
            }
        }
        return null;
    }

    private static int genRequestCode() {
        return ++sRequestCode;
    }
}
