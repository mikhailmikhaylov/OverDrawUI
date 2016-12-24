package org.redblaq.overdrawui.ui.main;

import android.content.Context;
import android.content.Intent;
import org.redblaq.overdrawui.app.App;
import org.redblaq.overdrawui.di.Container;
import org.redblaq.overdrawui.overdraw.OverdrawService;
import org.redblaq.overdrawui.repository.Prefs;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Should be migrated to Moxy presenter
 */
class MainPresenter {

    private final Prefs prefs;

    private final CompositeSubscription composite = new CompositeSubscription();

    private MainView view;

    MainPresenter(Context context) {
        final App applicationContext = (App) context.getApplicationContext();
        final Container container = applicationContext.getContainer();

        prefs = container.getPrefs();
    }

    void startListeningPrefs() {
        final MainView view = getView();

        final Subscription sub = prefs.getTransparency()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(v -> view.updateTransparencyRepresentation((int) (v * 100)),
                        Timber::e);
        composite.add(sub);
    }

    void updateTransparency(int percentile) {
        prefs.updateTransparency(percentile);
    }

    void startService(Context context, String path) {
        final Intent serviceIntent = new Intent(context, OverdrawService.class);
        serviceIntent.putExtra(OverdrawService.ARG_FILE_PATH, path);
        context.startService(serviceIntent);
    }

    void stopService(Context context) {
        context.stopService(new Intent(context, OverdrawService.class));
    }

    void injectView(MainView view) {
        this.view = view;
    }

    void release() {
        composite.clear();
    }

    private MainView getView() {
        return view;
    }
}
