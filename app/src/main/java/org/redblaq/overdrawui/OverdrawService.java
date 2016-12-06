package org.redblaq.overdrawui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class OverdrawService extends Service {

    static final String ARG_FILE_PATH = "file-path";

    private static final int FOREGROUND_ID = 999;

    private OverdrawView overdrawView;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        String filePath = null;
        if (intent.hasExtra(ARG_FILE_PATH)) {
            filePath = intent.getStringExtra(ARG_FILE_PATH);
        }

        initOverdraw(filePath);

        final PendingIntent pendingIntent = createPendingIntent();
        final Notification notification = createNotification(pendingIntent);

        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyOverdraw();
        stopForeground(true);

        logServiceEnded();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // TODO Refactor
    private void initOverdraw(@Nullable String filePath) {

        overdrawView = new OverdrawView(this);

        final ImageView image = overdrawView.getImage();
        Picasso.with(this).load(filePath).into(image);
        image.setAlpha(50);

        final int px = (int) (24 * Resources.getSystem().getDisplayMetrics().density);

        image.setPadding(0, -px, 0, 0);
    }

    private void destroyOverdraw() {
        overdrawView.destroy();
        overdrawView = null;
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
