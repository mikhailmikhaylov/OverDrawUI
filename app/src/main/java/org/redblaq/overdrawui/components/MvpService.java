package org.redblaq.overdrawui.components;

import android.app.Service;
import com.arellomobile.mvp.MvpDelegate;
import timber.log.Timber;

public abstract class MvpService extends Service {

    private MvpDelegate<? extends MvpService> mMvpDelegate;

    @Override public void onCreate() {
        super.onCreate();

        Timber.d("MvpService create");

        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
    }

    @Override public void onDestroy() {
        super.onDestroy();

        Timber.d("MvpService destroy");

        getMvpDelegate().onDetach();
        getMvpDelegate().onDestroy();
    }

    private MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
        }
        return mMvpDelegate;
    }
}
