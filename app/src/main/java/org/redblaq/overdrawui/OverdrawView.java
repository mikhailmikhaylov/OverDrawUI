package org.redblaq.overdrawui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class OverdrawView extends View {

    private Context context;
    private WindowManager manager;

    private ImageView imageView;
    private ImageView controlView;

    public OverdrawView(Context context) {
        super(context);

        this.context = context;
        this.imageView = new ImageView(context);
        this.controlView = (ImageView) LayoutInflater.from(context).inflate(R.layout.control_view, null);

        addToWindowManager();
    }

    ImageView getImage() {
        return imageView;
    }

    View getControlView() {
        return controlView;
    }

    void destroy() {
        manager.removeView(imageView);
        manager.removeView(controlView);
    }

    private void addToWindowManager() {
        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        final WindowManager.LayoutParams controlParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.START;
        controlParams.gravity = Gravity.END;

        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.addView(imageView, imageParams);
        manager.addView(controlView, controlParams);
    }
}
