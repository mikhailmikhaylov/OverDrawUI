package org.redblaq.overdrawui.overdraw;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import org.redblaq.overdrawui.app.App;
import org.redblaq.overdrawui.repository.Prefs;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import javax.inject.Inject;

@InjectViewState
public class OverdrawPresenter extends MvpPresenter<OverdrawView> {

    @Inject Prefs prefs;

    private final CompositeSubscription composite = new CompositeSubscription();

    OverdrawPresenter() {
        App.getAppComponent().inject(this);
    }

    void startListeningPrefs() {
        final Subscription subscription = prefs.getTransparency()
                .subscribe(v -> getViewState().updateTransparency((int) (v * 100)),
                        Timber::e);
        composite.add(subscription);
    }

    void updateTransparency(int percentile) {
        prefs.updateTransparency(percentile);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        composite.clear();
    }
}
