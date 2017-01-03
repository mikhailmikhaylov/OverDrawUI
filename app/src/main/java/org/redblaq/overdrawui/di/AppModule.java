package org.redblaq.overdrawui.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.redblaq.overdrawui.app.App;

import javax.inject.Singleton;

/**
 * @author Stanislav Shamilov
 */

@Singleton
@Module
public class AppModule {

    private App mApp;

    public AppModule(App app) {
        mApp = app;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }

}
