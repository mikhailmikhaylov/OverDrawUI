package org.redblaq.overdrawui.overdraw;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import org.redblaq.overdrawui.R;
import org.redblaq.overdrawui.app.App;
import org.redblaq.overdrawui.repository.Prefs;
import org.redblaq.overdrawui.ui.main.MainActivity;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

public class OverdrawService extends Service {

    public static final String ARG_FILE_PATH = "file-path";

    private static final int FOREGROUND_ID = 999;

    private OverdrawView overdrawView;
    private ImageView overdrawImage;

    private CompositeSubscription composite = new CompositeSubscription();

    @Inject
    Prefs prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        App.getAppComponent().inject(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logServiceStarted();

        String filePath = null;
        if (intent.hasExtra(ARG_FILE_PATH)) {
            filePath = intent.getStringExtra(ARG_FILE_PATH);
        }

        initOverdraw(filePath);

        final Subscription sub = prefs
                .getTransparency()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> overdrawImage.setAlpha(v),
                        Timber::e);
        composite.add(sub);

        final PendingIntent pendingIntent = createPendingIntent();
        final Notification notification = createNotification(pendingIntent);

        startForeground(FOREGROUND_ID, notification);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        destroyOverdraw();
        stopForeground(true);
        composite.clear();
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
