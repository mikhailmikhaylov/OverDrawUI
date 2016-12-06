package org.redblaq.overdrawui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class OverdrawView extends View {

    private Context context;
    private FrameLayout layout;
    private WindowManager manager;

    public OverdrawView(Context context) {
        super(context);

        this.context = context;
        this.layout = new FrameLayout(context);

        addToWindowManager();
    }

    private void addToWindowManager() {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.LEFT;

        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.addView(layout, params);

        final LayoutInflater layoutInflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        layoutInflater.inflate(R.layout.overdraw_item, layout);

        // Support dragging the image view
        final ImageView imageView = (ImageView) layout.findViewById(R.id.image);
        imageView.setOnTouchListener(new OnTouchListener() {
            private int initX, initY;
            private int initTouchX, initTouchY;

            @Override public boolean onTouch(View v, MotionEvent event) {
                int x = (int)event.getRawX();
                int y = (int)event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initX = params.x;
                        initY = params.y;
                        initTouchX = x;
                        initTouchY = y;
                        return true;

                    case MotionEvent.ACTION_UP:
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        params.x = initX + (x - initTouchX);
                        params.y = initY + (y - initTouchY);

                        // Invalidate layout
                        manager.updateViewLayout(layout, params);
                        return true;
                }
                return false;
            }
        });
    }

    public void destroy() {
        manager.removeView(layout);
    }
}
