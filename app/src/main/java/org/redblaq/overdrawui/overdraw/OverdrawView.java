package org.redblaq.overdrawui.overdraw;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

class OverdrawView extends View {

    private Context context;
    private WindowManager manager;

    private ImageView imageView;

    OverdrawView(Context context) {
        super(context);

        this.context = context;
        this.imageView = new ImageView(context);

        addToWindowManager();
    }

    ImageView getImage() {
        return imageView;
    }

    void destroy() {
        manager.removeView(imageView);
    }

    private void addToWindowManager() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.START;

        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.addView(imageView, params);
    }
}
