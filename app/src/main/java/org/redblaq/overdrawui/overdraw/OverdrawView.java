package org.redblaq.overdrawui.overdraw;

import com.arellomobile.mvp.MvpView;

public interface OverdrawView extends MvpView {

    void updateTransparency(int percentage);
}
