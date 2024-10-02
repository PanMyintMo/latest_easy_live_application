package com.gawilive.common.utils;

import androidx.fragment.app.FragmentActivity;
import com.gawilive.common.interfaces.PermissionCallback;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * 申请权限
 */
public class PermissionUtil {


    public static void request(FragmentActivity activity,PermissionCallback callback, String... permission){
        XXPermissions.with(activity)
                // 申请单个权限
                .permission(permission)
                .interceptor(new PermissionInterceptor())
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (!all) {
                            ToastUtil.show("获取部分权限成功，但部分权限未正常授予");
                            return;
                        }
                        callback.onAllGranted();
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            ToastUtil.show("被永久拒绝授权，请手动授予录音和日历权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(activity, permissions);
                        } else {
                            ToastUtil.show("获取录音和日历权限失败");
                        }
                    }
                });
    }

}
