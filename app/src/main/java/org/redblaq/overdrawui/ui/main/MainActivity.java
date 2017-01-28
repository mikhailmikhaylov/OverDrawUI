package org.redblaq.overdrawui.ui.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.redblaq.overdrawui.R;
import org.redblaq.overdrawui.overdraw.OverdrawService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ir.sohreco.androidfilechooser.FileChooserDialog;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.PICK_FILE_REQUEST_CODE;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.REQUIRED_PERMISSION_REQUEST_CODE;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.canDrawOverlays;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.createRequiredPermissionIntent;
import static org.redblaq.overdrawui.util.OverdrawPermissionsUtil.isPermissionDenied;

public class MainActivity extends MvpAppCompatActivity implements MainView {

    @BindView(R.id.file_name) TextView tvPath;
    @BindView(R.id.start) Button bStartService;
    @BindView(R.id.stop) Button bStopService;
    @BindView(R.id.transparency) SeekBar sbTransparency;
    @BindView(R.id.btn_pick_folder) Button btnPickFolder;

    private RxPermissions rxPermissions;
    private CompositeSubscription composite = new CompositeSubscription();
    private FileChooserDialog fileChooserDialog;

    @InjectPresenter
    MainPresenter presenter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        rxPermissions = new RxPermissions(this);
        sbTransparency.setOnSeekBarChangeListener(seekBarListener);
        presenter.startListeningPrefs();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        composite.clear();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
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

    @Override public void updateTransparencyRepresentation(int transparencyPercentile) {
        sbTransparency.setProgress(transparencyPercentile);
    }

    @OnClick(R.id.pick_file) void clickPickFile() {
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FILE_REQUEST_CODE);
    }

    @OnClick(R.id.start) void clickStart() {
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
        composite.add(sub);
    }

    @OnClick(R.id.stop) void clickStop() {
        stopService();
    }

    @OnClick(R.id.btn_pick_folder) void pickFolder() {
        if (fileChooserDialog == null) {
            fileChooserDialog = new FileChooserDialog.Builder(FileChooserDialog.ChooserType.DIRECTORY_CHOOSER, presenter).build();
        }
        fileChooserDialog.show(getSupportFragmentManager(), null);
    }



    private void startService() {
        final String path = tvPath.getText().toString();
        final String noPath = getResources().getString(R.string.pick_a_file);

        if (path.equals(noPath)) {
            Toast.makeText(this, R.string.please_select_file, Toast.LENGTH_SHORT).show();
            return;
        }
        final Intent serviceIntent = new Intent(this, OverdrawService.class);
        serviceIntent.putExtra(OverdrawService.ARG_FILE_PATH, path);
        startService(serviceIntent);
    }

    private void stopService() {
        presenter.stopService(this);
    }

    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                presenter.updateTransparency(i);
            }
        }

        @Override public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
