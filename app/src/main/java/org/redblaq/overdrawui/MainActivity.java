package org.redblaq.overdrawui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION;

public class MainActivity extends AppCompatActivity {

    private final static int REQUIRED_PERMISSION_REQUEST_CODE = 2121;
    private final static int PICK_FILE_REQUEST_CODE = 2122;
    @Bind(R.id.file_name)
    TextView tvPath;
    private RxPermissions rxPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUIRED_PERMISSION_REQUEST_CODE) {
            if (!canDrawOverlays()) {
                Toast.makeText(this,
                               "Required permission is not granted. Please restart the app and grant required "
                                       + "permission.",
                               Toast.LENGTH_LONG).show();
            } else {
                startService();
            }
        } else if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            final String path = data.getDataString();
            tvPath.setText(path);
        }
    }

    @OnClick(R.id.pick_file)
    public void clickPickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @OnClick(R.id.start)
    public void clickStart() {
        rxPermissions.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .subscribe(granted -> {
                    final boolean canDraw = canDrawOverlays();
                    Timber.d("%s - %s", granted, canDraw);

                    if (!granted || !canDrawOverlays()) {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        startActivityForResult(createRequiredPermissionIntent(), REQUIRED_PERMISSION_REQUEST_CODE);
                        return;
                    }

                    startService();
                }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.stop)
    public void clickStop() {
        stopService();
    }

    private void startService() {
        final String path = tvPath.getText().toString();
        final String noPath = getResources().getString(R.string.pick_a_file);

        final Intent serviceIntent = new Intent(this, OverdrawService.class);

        if (!path.equals(noPath)) {
            serviceIntent.putExtra(OverdrawService.ARG_FILE_PATH, path);
        }

        startService(serviceIntent);
    }

    private void stopService() {
        stopService(new Intent(this, OverdrawService.class));
    }

    private Intent createRequiredPermissionIntent() {
        if (isMarshmallowOrHigher()) {
            return new Intent(ACTION_MANAGE_OVERLAY_PERMISSION,
                              Uri.parse("package:" + this.getPackageName()));
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean canDrawOverlays() {
        return !isMarshmallowOrHigher() || Settings.canDrawOverlays(this);
    }

    private boolean isMarshmallowOrHigher() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}
