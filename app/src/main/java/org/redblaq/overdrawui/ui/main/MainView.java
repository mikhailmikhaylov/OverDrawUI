package org.redblaq.overdrawui.ui.main;

import com.arellomobile.mvp.MvpView;

import org.redblaq.overdrawui.model.ImagePreview;

import java.util.List;

public interface MainView extends MvpView {
    void updateTransparencyRepresentation(int transparencyPercentile);

    void showImagePreviews(List<ImagePreview> imagePreviews);
}
