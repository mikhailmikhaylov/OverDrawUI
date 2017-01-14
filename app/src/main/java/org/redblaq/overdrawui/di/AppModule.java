package org.redblaq.overdrawui.di;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import org.redblaq.overdrawui.app.App;

import javax.inject.Singleton;

@Singleton
@Module
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return app.getApplicationContext();
    }
}
