package org.redblaq.overdrawui.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.f2prateek.rx.preferences.RxSharedPreferences;

import org.redblaq.overdrawui.app.Constants;
import rx.Observable;

public class Prefs {

    private final SharedPreferences sharedPreferences;
    private RxSharedPreferences rxPrefs;

    public Prefs(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        rxPrefs = RxSharedPreferences.create(sharedPreferences);
    }

    public Observable<Float> getTransparency() {
        return rxPrefs.getFloat(Constants.PREFS_TRANSPARENCY)
                .asObservable();
    }
}
