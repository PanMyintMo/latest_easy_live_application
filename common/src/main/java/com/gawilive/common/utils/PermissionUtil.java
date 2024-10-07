package com.gawilive.common.utils;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import com.gawilive.common.interfaces.PermissionCallback;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.ArrayList;
import java.util.List;

/**
 * 申请权限
 */
public class PermissionUtil {

    public static void request(FragmentActivity activity, PermissionCallback callback, String... permissions) {
        // Check if the Android version is 13 (API level 33) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            List<String> updatedPermissions = getStrings(permissions);

            // Request the updated permissions
            XXPermissions.with(activity)
                    .permission(updatedPermissions.toArray(new String[0]))
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> grantedPermissions, boolean all) {
                            if (!all) {
                                ToastUtil.show("获取部分权限成功，但部分权限未正常授予");
                                return;
                            }
                            callback.onAllGranted();
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions, boolean never) {
                            if (never) {
                                ToastUtil.show("被永久拒绝授权，请手动授予必要的权限");
                                // If permanently denied, open the app's permission settings
                                XXPermissions.startPermissionActivity(activity, deniedPermissions);
                            } else {
                                ToastUtil.show("获取权限失败");
                            }
                        }
                    });
        } else {
            // For Android versions below 13, request the older permissions
            XXPermissions.with(activity)
                    .permission(permissions)
                    .interceptor(new PermissionInterceptor())
                    .request(new OnPermissionCallback() {

                        @Override
                        public void onGranted(List<String> grantedPermissions, boolean all) {
                            if (!all) {
                                ToastUtil.show("获取部分权限成功，但部分权限未正常授予");
                                return;
                            }
                            callback.onAllGranted();
                        }

                        @Override
                        public void onDenied(List<String> deniedPermissions, boolean never) {
                            if (never) {
                                ToastUtil.show("被永久拒绝授权，请手动授予必要的权限");
                                // If permanently denied, open the app's permission settings
                                XXPermissions.startPermissionActivity(activity, deniedPermissions);
                            } else {
                                ToastUtil.show("获取权限失败");
                            }
                        }
                    });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private static @NonNull List<String> getStrings(String[] permissions) {
        List<String> updatedPermissions = new ArrayList<>();

        for (String permission : permissions) {
            // Map READ_EXTERNAL_STORAGE to the new media-specific permissions for Android 13+
            if (permission.equals(android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_IMAGES);
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_VIDEO);
                updatedPermissions.add(android.Manifest.permission.READ_MEDIA_AUDIO);
            } else {
                updatedPermissions.add(permission);
            }
        }
        return updatedPermissions;
    }
}
