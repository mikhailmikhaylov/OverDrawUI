package org.redblaq.overdrawui;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;

import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.squareup.picasso.Picasso;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class OverdrawService extends Service {

    static final String ARG_FILE_PATH = "file-path";

    private static final int FOREGROUND_ID = 999;

    private OverdrawView overdrawView;
    private ImageView overdrawImage;

    private CompositeSubscription compositeSub = new CompositeSubscription();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        String filePath = null;
        if (intent.hasExtra(ARG_FILE_PATH)) {
            filePath = intent.getStringExtra(ARG_FILE_PATH);
        }

        initOverdraw(filePath);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final RxSharedPreferences rxPrefs = RxSharedPreferences.create(prefs);

        final Subscription sub = rxPrefs
                .getFloat(Constants.PREFS_TRANSPARENCY)
                .asObservable()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> overdrawImage.setAlpha(v),
                        Timber::e);
        compositeSub.add(sub);

        final PendingIntent pendingIntent = createPendingIntent();
        final Notification notification = createNotification(pendingIntent);

        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyOverdraw();
        stopForeground(true);

        compositeSub.clear();
        logServiceEnded();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // TODO Refactor
    private void initOverdraw(@Nullable String filePath) {

        overdrawView = new OverdrawView(this);

        overdrawImage = overdrawView.getImage();
        Picasso.with(this).load(filePath).into(overdrawImage);
        overdrawImage.setAlpha(0.5f);

        final int px = (int) (24 * Resources.getSystem().getDisplayMetrics().density);

        overdrawImage.setPadding(0, -px, 0, 0);
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
