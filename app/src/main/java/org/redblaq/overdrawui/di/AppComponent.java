package org.redblaq.overdrawui.di;

import dagger.Component;
import org.redblaq.overdrawui.overdraw.OverdrawService;
import org.redblaq.overdrawui.ui.main.MainPresenter;

import javax.inject.Singleton;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {

    void inject(OverdrawService service);

    void inject(MainPresenter presenter);
}
