package org.redblaq.overdrawui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.redblaq.overdrawui.OverdrawPermissionsUtil.canDrawOverlays;
import static org.redblaq.overdrawui.OverdrawPermissionsUtil.createRequiredPermissionIntent;
import static org.redblaq.overdrawui.OverdrawPermissionsUtil.isPermissionDenied;

public class MainActivity extends AppCompatActivity {

    private final static int REQUIRED_PERMISSION_REQUEST_CODE = 2121;
    private final static int PICK_FILE_REQUEST_CODE = 2122;

    @Bind(R.id.file_name)
    TextView tvPath;

    @Bind(R.id.start)
    Button bStartService;

    @Bind(R.id.stop)
    Button bStopService;

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
            if (!canDrawOverlays(this)) {
                Toast.makeText(this,
                        "Required permission is not granted. Please restart the app and grant "
                                + "required "
                                + "permission.",
                        Toast.LENGTH_LONG).show();
            } else {
                startService();
            }
        } else if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            final String path = data.getDataString();
            tvPath.setText(path);
            bStartService.setVisibility(View.VISIBLE);
            bStopService.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.pick_file)
    void clickPickFile() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
    }

    @OnClick(R.id.start)
    void clickStart() {
        rxPermissions.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .subscribe(alertWindowPermissionGranted -> {
                    if (isPermissionDenied(alertWindowPermissionGranted, this)) {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        startActivityForResult(createRequiredPermissionIntent(this),
                                REQUIRED_PERMISSION_REQUEST_CODE);
                        return;
                    }

                    startService();
                }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @OnClick(R.id.stop)
    void clickStop() {
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
}
