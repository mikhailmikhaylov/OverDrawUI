package org.redblaq.overdrawui.ui.main;

import android.content.Context;
import android.content.Intent;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.redblaq.overdrawui.app.App;
import org.redblaq.overdrawui.overdraw.OverdrawService;
import org.redblaq.overdrawui.repository.Prefs;

import javax.inject.Inject;

import ir.sohreco.androidfilechooser.FileChooserDialog;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements FileChooserDialog.ChooserListener {

    @Inject
    Prefs prefs;

    private final CompositeSubscription composite = new CompositeSubscription();

    MainPresenter() {
        App.getAppComponent().inject(this);
    }

    void startListeningPrefs() {
        final Subscription subscription = prefs.getTransparency()
                .subscribe(v -> getViewState().updateTransparencyRepresentation((int) (v * 100)),
                        Timber::e);
        composite.add(subscription);
    }

    void updateTransparency(int percentile) {
        prefs.updateTransparency(percentile);
    }

    void stopService(Context context) {
        context.stopService(new Intent(context, OverdrawService.class));
    }

    @Override public void onDestroy() {
        super.onDestroy();
        composite.clear();
    }

    @Override
    public void onSelect(String path) {

    }
}
