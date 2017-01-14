package org.redblaq.overdrawui.di;

import dagger.Component;
import org.redblaq.overdrawui.overdraw.OverdrawPresenter;
import org.redblaq.overdrawui.ui.main.MainPresenter;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(MainPresenter presenter);

    void inject(OverdrawPresenter presenter);
}
