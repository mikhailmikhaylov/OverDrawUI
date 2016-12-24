package org.redblaq.overdrawui.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import com.tbruyelle.rxpermissions.RxPermissions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import org.redblaq.overdrawui.app.App;
import org.redblaq.overdrawui.di.Container;
import org.redblaq.overdrawui.overdraw.OverdrawService;
import org.redblaq.overdrawui.R;
import org.redblaq.overdrawui.repository.Prefs;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.canDrawOverlays;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.createRequiredPermissionIntent;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.isPermissionDenied;

public class MainActivity extends AppCompatActivity {

    private final static int REQUIRED_PERMISSION_REQUEST_CODE = 2121;
    private final static int PICK_FILE_REQUEST_CODE = 2122;

    @Bind(R.id.file_name) TextView tvPath;
    @Bind(R.id.start) Button bStartService;
    @Bind(R.id.stop) Button bStopService;
    @Bind(R.id.transparency) SeekBar sbTransparency;

    private RxPermissions rxPermissions;

    private Prefs prefs;

    private CompositeSubscription compositeSub = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);

        sbTransparency.setOnSeekBarChangeListener(seekBarListener);

        inject();
        bindToPrefs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeSub.clear();
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
            sbTransparency.setVisibility(View.VISIBLE);
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
        clickStop();
        final Subscription sub = rxPermissions.request(Manifest.permission.SYSTEM_ALERT_WINDOW)
                .subscribe(alertWindowPermissionGranted -> {
                    if (isPermissionDenied(alertWindowPermissionGranted, this)) {
                        Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        startActivityForResult(createRequiredPermissionIntent(this),
                                REQUIRED_PERMISSION_REQUEST_CODE);
                        return;
                    }
                    startService();
                }, error -> Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show());
        compositeSub.add(sub);
    }

    @OnClick(R.id.stop)
    void clickStop() {
        stopService();
    }

    private void bindToPrefs() {
        final Subscription sub = prefs.getTransparency()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> sbTransparency.setProgress((int) (v * 100)),
                        Timber::e);
        compositeSub.add(sub);
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

    private void inject() {
        final App applicationContext = (App) getApplicationContext();
        final Container diContainer = applicationContext.getContainer();

        prefs = diContainer.getPrefs();
    }

    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                prefs.updateTransparency(i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
}
