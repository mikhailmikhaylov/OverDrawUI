package org.redblaq.overdrawui.overdraw;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.*;
import android.widget.ImageView;
import android.widget.SeekBar;
import org.redblaq.overdrawui.R;

class OverdrawControlView extends View {

    private Context context;
    private WindowManager manager;

    private SelectionModel selectionModel = new SelectionModel();

    private WindowManager.LayoutParams controlParams;

    private ImageView imageView;
    private ViewGroup root;
    private ImageView head;
    private SeekBar transparencyControl;

    OverdrawControlView(Context context) {
        super(context);

        this.context = context;
        this.imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        this.root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.control_view, null);
        this.head = (ImageView) root.findViewById(R.id.control_view_image);
        final ImageView transparencyButton = (ImageView) root.findViewById(R.id.control_change_transparency_button);
        this.transparencyControl = (SeekBar) root.findViewById(R.id.control_change_transparency);
        final ImageView scrollingButton = (ImageView) root.findViewById(R.id.control_move_button);

        final OnClickListener controlButtonsClickListener = v -> {
            switch (v.getId()) {
                case R.id.control_change_transparency_button: {
                    selectionModel.selectItem(SelectionModel.SELECTION_TRANSPARENCY);
                    break;
                }
                case R.id.control_move_button: {
                    selectionModel.selectItem(SelectionModel.SELECTION_TRANSPARENCY);
                    break;
                }
            }
        };
        transparencyButton.setOnClickListener(controlButtonsClickListener);
        scrollingButton.setOnClickListener(controlButtonsClickListener);

        selectionModel.selectItem(SelectionModel.SELECTION_TRANSPARENCY);

        addToWindowManager();
    }

    ImageView getImage() {
        return imageView;
    }

    void destroy() {
        manager.removeView(imageView);
        manager.removeView(root);
    }

    void updateTransparency(int percentage) {
        transparencyControl.setProgress(percentage);
        imageView.setAlpha(percentage * 0.01f);
    }

    void setTransparencyUpdateListener(SeekBar.OnSeekBarChangeListener listener) {
        transparencyControl.setOnSeekBarChangeListener(listener);
    }

    private void addToWindowManager() {
        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
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
        manager.addView(root, controlParams);

        // Support dragging the image view
        head.setOnTouchListener(controlTouchListener);
    }

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
                    controlParams.x = initX - (x - initTouchX);
                    controlParams.y = initY + (y - initTouchY);

                    // Invalidate layout
                    manager.updateViewLayout(root, controlParams);
                    return true;
            }
            return false;
        }
    };

    private class SelectionModel {
        private static final int SELECTION_NONE = -1;
        private static final int SELECTION_TRANSPARENCY = 0;
        private static final int SELECTION_SCROLLING = 1;

        private int selection = SELECTION_TRANSPARENCY;

        void selectItem(int position) {
            // remove all buttons selections and select the appropriate one
            transparencyControl.setVisibility(GONE);

            if (position == selection) {
                selection = SELECTION_NONE;
                return;
            }

            selection = position;

            transparencyControl.setVisibility(GONE);

            // add button selections
            switch (selection) {
                case SELECTION_TRANSPARENCY: {
                    transparencyControl.setVisibility(VISIBLE);
                    break;
                }
            }
        }
    }
}
