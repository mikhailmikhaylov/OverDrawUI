package org.redblaq.overdrawui.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.redblaq.overdrawui.app.Constants;
import rx.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Prefs {

    private final SharedPreferences sharedPreferences;
    private RxSharedPreferences rxPrefs;

    @Inject Prefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        rxPrefs = RxSharedPreferences.create(sharedPreferences);
    }

    public void updateTransparency(int percentile) {
        updateTransparency(percentile / 100f);
    }

    public Observable<Float> getTransparency() {
        return rxPrefs.getFloat(Constants.PREFS_TRANSPARENCY)
                .asObservable();
    }

    private void updateTransparency(float value) {
        sharedPreferences.edit().putFloat(Constants.PREFS_TRANSPARENCY, value).apply();
    }
}
