package org.redblaq.overdrawui.overdraw;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.picasso.Picasso;
import org.redblaq.overdrawui.R;
import org.redblaq.overdrawui.components.MvpService;
import org.redblaq.overdrawui.ui.main.MainActivity;

public class OverdrawService extends MvpService implements OverdrawView {

    public static final String ARG_FILE_PATH = "file-path";

    private static final int FOREGROUND_ID = 999;

    private OverdrawControlView overdrawControlView;

    @InjectPresenter
    OverdrawPresenter presenter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        String filePath = null;
        if (intent.hasExtra(ARG_FILE_PATH)) {
            filePath = intent.getStringExtra(ARG_FILE_PATH);
        }

        initOverdraw(filePath);
        presenter.startListeningPrefs();

        final PendingIntent pendingIntent = createPendingIntent();
        final Notification notification = createNotification(pendingIntent);

        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        destroyOverdraw();
        stopForeground(true);
        logServiceEnded();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void updateTransparency(int percentage) {
        overdrawControlView.updateTransparency(percentage);
    }

    private void initOverdraw(@Nullable String filePath) {
        overdrawControlView = new OverdrawControlView(this);

        final ImageView overdrawImage = overdrawControlView.getImage();
        Picasso.with(this).load(filePath).into(overdrawImage);
        overdrawImage.setAlpha(0.5f);

        final int px = (int) (24 * Resources.getSystem().getDisplayMetrics().density);

        // toolbar height fix for proper screenshot displaying
        overdrawImage.setPadding(0, -px, 0, 0);

        overdrawControlView.setTransparencyUpdateListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    presenter.updateTransparency(i);
                }
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void destroyOverdraw() {
        overdrawControlView.destroy();
        overdrawControlView = null;
    }

    private PendingIntent createPendingIntent() {
        final Intent intent = new Intent(this, MainActivity.class);
        return PendingIntent.getActivity(this, 0, intent, 0);
    }

    private Notification createNotification(PendingIntent intent) {
        return new Notification.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_body))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(intent)
                .build();
    }

    private void logServiceStarted() {
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    private void logServiceEnded() {
        Toast.makeText(this, "Service ended", Toast.LENGTH_SHORT).show();
    }
}
