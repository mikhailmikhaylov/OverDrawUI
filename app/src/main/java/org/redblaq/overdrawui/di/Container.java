package org.redblaq.overdrawui.di;

import org.redblaq.overdrawui.repository.Prefs;

public class Container {

    private Prefs prefs;

    public void register(Prefs prefs) {
        this.prefs = prefs;
    }

    public Prefs getPrefs() {
        if (prefs == null) {
            throwIllegalInjectionException();
        }
        return prefs;
    }

    private void throwIllegalInjectionException() {
        throw new IllegalStateException("Illegal injection");
    }

    public void release() {
        this.prefs = null;
    }
}
