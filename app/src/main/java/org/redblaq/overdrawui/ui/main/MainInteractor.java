package org.redblaq.overdrawui.ui.main;

import org.redblaq.overdrawui.model.ImagePreview;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;

public class MainInteractor {

    @Inject
    public MainInteractor() {

    }

    public Observable<List<ImagePreview>> createImagePreview(List<String> filePathes) {
        return Observable.from(filePathes)
                .map(ImagePreview::new)
                .toList();
    }
}
