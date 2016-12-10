package org.redblaq.overdrawui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;

final class OverdrawPermissionsUtil {

    private OverdrawPermissionsUtil() {
    }

    static Intent createRequiredPermissionIntent(Context context) {
        if (isMarshmallowOrHigher()) {
            return new Intent(ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
        }
        return null;
    }

    static boolean isPermissionDenied(boolean rxPermissionGranted, Context context) {
        if (isMarshmallowOrHigher()) {
            return !canDrawOverlays(context);
        }
        return !rxPermissionGranted;
    }

    @TargetApi(Build.VERSION_CODES.M)
    static boolean canDrawOverlays(Context context) {
        return !isMarshmallowOrHigher() || Settings.canDrawOverlays(context);
    }

    private static boolean isMarshmallowOrHigher() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
