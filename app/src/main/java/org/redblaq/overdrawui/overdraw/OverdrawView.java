package org.redblaq.overdrawui.overdraw;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.*;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.redblaq.overdrawui.R;

class OverdrawView extends View {

    private Context context;
    private WindowManager manager;

    private SelectionModel selectionModel = new SelectionModel();

    private WindowManager.LayoutParams imageParams;
    private WindowManager.LayoutParams controlParams;

    private ImageView imageView;
    private ViewGroup controlContainer;
    private ImageView controlView;
    private ImageView brightnessButton;
    private SeekBar brightnessControl;

    OverdrawView(Context context) {
        super(context);

        this.context = context;
        this.imageView = new ImageView(context);
        this.controlContainer = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.control_view, null);
        this.controlView = (ImageView) controlContainer.findViewById(R.id.control_view_image);
        this.brightnessButton = (ImageView) controlContainer.findViewById(R.id.control_change_transparency_button);
        this.brightnessControl = (SeekBar) controlContainer.findViewById(R.id.control_change_transparency);

        brightnessButton.setOnClickListener(controlButtonsClickListener);

        addToWindowManager();
    }

    ImageView getImage() {
        return imageView;
    }

    void destroy() {
        manager.removeView(imageView);
        manager.removeView(controlContainer);
    }

    private void addToWindowManager() {
        imageParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);

        controlParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT);

        imageParams.gravity = Gravity.START;
        controlParams.gravity = Gravity.END;

        manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.addView(imageView, imageParams);
        manager.addView(controlContainer, controlParams);

        // Support dragging the image view
        controlView.setOnTouchListener(controlTouchListener);
    }

    private final OnClickListener controlButtonsClickListener = v -> {
        switch (v.getId()) {
            case R.id.control_change_transparency_button: {
                selectionModel.selectItem(SelectionModel.SELECTION_BRIGHTNESS);
                break;
            }
        }
    };

    private final OnTouchListener controlTouchListener = new OnTouchListener() {
        private int initX, initY;
        private int initTouchX, initTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initX = controlParams.x;
                    initY = controlParams.y;
                    initTouchX = x;
                    initTouchY = y;
                    return true;

                case MotionEvent.ACTION_UP:
                    return true;

                case MotionEvent.ACTION_MOVE:
                    controlParams.x = initX + (x - initTouchX);
                    controlParams.y = initY + (y - initTouchY);

                    // Invalidate layout
                    manager.updateViewLayout(controlContainer, controlParams);
                    return true;
            }
            return false;
        }
    };

    private class SelectionModel {
        private static final int SELECTION_NONE = -1;
        private static final int SELECTION_BRIGHTNESS = 0;

        private int selection = SELECTION_BRIGHTNESS;

        void selectItem(int position) {
            // remove all buttons selections and select the appropriate one
            brightnessControl.setVisibility(GONE);

            if (position == selection) {
                selection = SELECTION_NONE;
                return;
            }

            selection = position;
            switch (selection) {
                case SELECTION_BRIGHTNESS: {
                    brightnessControl.setVisibility(VISIBLE);
                }
            }
        }
    }
}
