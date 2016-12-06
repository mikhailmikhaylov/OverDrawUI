package org.redblaq.overdrawui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class OverdrawService extends Service {

    private final static int FOREGROUND_ID = 999;

    private OverdrawView overdrawView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        initOverdraw();

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

    private void initOverdraw() {
        overdrawView = new OverdrawView(this);
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
