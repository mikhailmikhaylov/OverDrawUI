package org.redblaq.overdrawui.app;

import android.app.Application;

import org.redblaq.overdrawui.di.Container;
import org.redblaq.overdrawui.repository.Prefs;
import timber.log.Timber;

public class App extends Application {

    private Container container;

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        this.container = new Container();
        initializeContainer();
    }

    @Override
    public void onTerminate() {
        container.release();

        super.onTerminate();
    }

    public Container getContainer() {
        return container;
    }

    private void initializeContainer() {
        container.register(new Prefs(this));
    }
}
