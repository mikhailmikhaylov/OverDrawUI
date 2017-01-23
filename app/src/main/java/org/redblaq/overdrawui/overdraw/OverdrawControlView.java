package org.redblaq.overdrawui.overdraw;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.*;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import org.redblaq.overdrawui.R;

class OverdrawControlView extends View {

    private WindowManager manager;

    private final SelectionModel selectionModel = new SelectionModel();

    private WindowManager.LayoutParams controlParams;

    private ImageView imageView;
    private View contentView;
    private CheckBox controlLockScroll;
    private ViewGroup root;
    private ImageView head;
    private SeekBar transparencyControl;

    OverdrawControlView(Context context) {
        super(context);

        Object imageViewTag = "image-view-tag";
        this.contentView = createContentView(context, imageViewTag);
        this.imageView = (ImageView) contentView.findViewWithTag(imageViewTag);

        this.root = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.control_view, null);
        this.head = (ImageView) root.findViewById(R.id.control_view_image);
        final ImageView transparencyButton = (ImageView) root.findViewById(R.id.control_change_transparency_button);
        this.controlLockScroll = (CheckBox) root.findViewById(R.id.control_lock_scroll);
        this.transparencyControl = (SeekBar) root.findViewById(R.id.control_change_transparency);
        final ImageView scrollingButton = (ImageView) root.findViewById(R.id.control_move_button);

        final OnClickListener controlButtonsClickListener = v -> {
            switch (v.getId()) {
                case R.id.control_change_transparency_button: {
                    selectionModel.selectItem(SelectionModel.SELECTION_TRANSPARENCY);
                    break;
                }
                case R.id.control_move_button: {
                    final boolean isLocked = controlLockScroll.isChecked();
                    controlLockScroll.setChecked(!isLocked);
                    manager.updateViewLayout(contentView, getContentViewLayoutParams(controlLockScroll.isChecked()));
                    break;
                }
            }
        };
        transparencyButton.setOnClickListener(controlButtonsClickListener);
        scrollingButton.setOnClickListener(controlButtonsClickListener);

        selectionModel.selectItem(SelectionModel.SELECTION_TRANSPARENCY);

        addToWindowManager(context, this.contentView);
    }

    ImageView getImage() {
        return imageView;
    }

    void destroy() {
        manager.removeView(contentView);
        manager.removeView(root);
    }

    void updateTransparency(int percentage) {
        transparencyControl.setProgress(percentage);
        imageView.setAlpha(percentage * 0.01f);
    }

    void setTransparencyUpdateListener(SeekBar.OnSeekBarChangeListener listener) {
        transparencyControl.setOnSeekBarChangeListener(listener);
    }

    private void addToWindowManager(Context context, View contentView) {
        final WindowManager.LayoutParams imageParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, //FLAG_SPLIT_TOUCH to enable scroll
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
        manager.addView(contentView, imageParams);
        manager.addView(root, controlParams);

        // Support dragging the image view
        head.setOnTouchListener(controlTouchListener);
    }

    private View createContentView(Context context, Object imageViewTag) {
        final ScrollView scrollView = new ScrollView(context);
        final ImageView imageView = new ImageView(context);
        imageView.setTag(imageViewTag);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        scrollView.addView(imageView);
        return scrollView;
    }

    private WindowManager.LayoutParams getContentViewLayoutParams(boolean isLocked) {
        final int touchFlag = isLocked ? WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE : WindowManager.LayoutParams.FLAG_SPLIT_TOUCH;
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                touchFlag,
                PixelFormat.TRANSLUCENT);
    }

    private final OnTouchListener controlTouchListener = new OnTouchListener() {
        private int initX, initY;
        private int initTouchX, initTouchY;

        @Override public boolean onTouch(View v, MotionEvent event) {
            final int x = (int) event.getRawX();
            final int y = (int) event.getRawY();

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
