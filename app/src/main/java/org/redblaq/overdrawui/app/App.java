package org.redblaq.overdrawui.app;

import android.app.Application;
import org.redblaq.overdrawui.di.AppComponent;
import org.redblaq.overdrawui.di.AppModule;
import org.redblaq.overdrawui.di.DaggerAppComponent;
import timber.log.Timber;

public class App extends Application {

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }
}
