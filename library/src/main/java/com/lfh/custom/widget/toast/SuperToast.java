package com.lfh.custom.widget.toast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


@SuppressWarnings({"UnusedDeclaration", "deprecation"})
public class SuperToast {
    private static final String TAG = "SuperToast";
    private static final String ERROR_CONTEXT_NULL = " - You cannot use a null context.";
    private static final String ERROR_DURATION_TOO_LONG =
            " - You should NEVER specify a duration greater than four and a half seconds for a SuperToast.";

    /**
     * Custom OnClickListener to be used with SuperActivityToasts/SuperCardToasts. Note that
     * SuperActivityToasts/SuperCardToasts must use this with an
     */
    public interface OnClickListener {
        void onClick(View view, Parcelable token);
    }

    /**
     * Custom OnDismissListener to be used with any type of SuperToasts. Note that
     * SuperActivityToasts/SuperCardToasts must use this with an
     */
    public interface OnDismissListener {
        void onDismiss(View view);
    }

    /**
     * Animations for all types of SuperToasts.
     */
    public enum Animations {
        FADE,
        FLY_IN,
        SCALE,
        POPUP
    }

    /**
     * Durations for all types of SuperToasts.
     */
    public static class Duration {
        public static final int VERY_SHORT = (1500);
        public static final int SHORT = (2000);
        public static final int MEDIUM = (2750);
        public static final int LONG = (3500);
        public static final int EXTRA_LONG = (4500);
    }

    /**
     * Text sizes for all types of SuperToasts.
     */
    public static class TextSize {
        public static final int EXTRA_SMALL = (12);
        public static final int SMALL = (14);
        public static final int MEDIUM = (16);
        public static final int LARGE = (18);
    }

    /**
     * Types for SuperActivityToasts and SuperCardToasts.
     */
    public enum Type {
        /**
         * Standard type used for displaying messages.
         */
        STANDARD,

        /**
         * Progress type used for showing progress.
         */
        PROGRESS,

        /**
         * Progress type used for showing progress.
         */
        PROGRESS_HORIZONTAL,

        /**
         * Button type used for receiving click actions.
         */
        BUTTON
    }

    /**
     * Positions for icons used in all types of SuperToasts.
     */
    public enum IconPosition {
        /**
         * Set the icon to the left of the text.
         */
        LEFT,

        /**
         * Set the icon to the right of the text.
         */
        RIGHT,

        /**
         * Set the icon on top of the text.
         */
        TOP,

        /**
         * Set the icon on the bottom of the text.
         */
        BOTTOM
    }

    private Animations mAnimations = Animations.FADE;
    private Context mContext;
    private int mGravity = Gravity.BOTTOM | Gravity.CENTER;
    private int mDuration = Duration.SHORT;
    private int mTypefaceStyle;
    private int mBackground;
    private int mXOffset = 0;
    private int mYOffset = 0;
    private OnDismissListener mOnDismissListener;
    private TextView mMessageTextView;
    private View mToastView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mWindowManagerParams;

    /**
     * Instantiates a new {@value #TAG}.
     *
     * @param context {@link Context}
     */
    @SuppressLint("InflateParams")
    private SuperToast(Context context) {
        if (context == null) {
            throw new IllegalArgumentException(TAG + ERROR_CONTEXT_NULL);
        }

        this.mContext = context;
        mYOffset = context.getResources().getDimensionPixelSize(R.dimen.super_toast_bottom_padding);
        final LayoutInflater layoutInflater = LayoutInflater.from(context);
        mToastView = layoutInflater.inflate(R.layout.super_toast_view, null);
        mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        mMessageTextView = (TextView) mToastView.findViewById(R.id.tv_toast_message);
    }

    /**
     * Shows the {@value #TAG}. If another {@value #TAG} is showing than
     * this one will be added to a queue and shown when the previous {@value #TAG}
     * is dismissed.
     */
    public void show() {
        mWindowManagerParams = new WindowManager.LayoutParams();
        mWindowManagerParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManagerParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowManagerParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mWindowManagerParams.format = PixelFormat.TRANSLUCENT;
        mWindowManagerParams.windowAnimations = getAnimation();
        mWindowManagerParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        mWindowManagerParams.gravity = mGravity;
        mWindowManagerParams.x = mXOffset;
        mWindowManagerParams.y = mYOffset;

        ManagerSuperToast.getInstance().add(this);

    }

    /**
     * Sets the message text of the {@value #TAG}.
     *
     * @param text {@link CharSequence}
     */
    public void setText(CharSequence text) {
        mMessageTextView.setText(text);
    }

    /**
     * Returns the message text of the {@value #TAG}.
     *
     * @return {@link CharSequence}
     */
    public CharSequence getText() {
        return mMessageTextView.getText();
    }

    public void setPadding() {
        int lineCount = mMessageTextView.getLineCount();

        int leftPadding = 0;
        int rightPadding = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            leftPadding = mMessageTextView.getPaddingStart();
            rightPadding = mMessageTextView.getPaddingEnd();
        } else {
            leftPadding = mMessageTextView.getPaddingLeft();
            rightPadding = mMessageTextView.getPaddingRight();
        }

        int topPadding = mMessageTextView.getPaddingTop();
        int bottomPadding = mMessageTextView.getPaddingBottom();

        if (1 < lineCount) {
            topPadding = dip2px(mContext, mContext.getResources().getDimension(R.dimen.super_toast_vertical_multi_line_padding));
            bottomPadding = dip2px(mContext, mContext.getResources().getDimension(R.dimen.super_toast_vertical_multi_line_padding));
        }

        mMessageTextView.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMessageTextView.setPaddingRelative(leftPadding, topPadding, rightPadding, bottomPadding);
        }
    }

    /**
     * Sets the message typeface style of the {@value #TAG}.
     *
     * @param typeface {@link android.graphics.Typeface} int
     */
    public void setTypefaceStyle(int typeface) {
        mTypefaceStyle = typeface;
        mMessageTextView.setTypeface(mMessageTextView.getTypeface(), typeface);
    }

    /**
     * Returns the message typeface style of the {@value #TAG}.
     *
     * @return {@link android.graphics.Typeface} int
     */
    public int getTypefaceStyle() {
        return mTypefaceStyle;
    }

    /**
     * Sets the message text color of the {@value #TAG}.
     *
     * @param textColor {@link android.graphics.Color}
     */
    public void setTextColor(int textColor) {
        mMessageTextView.setTextColor(textColor);
    }

    /**
     * Returns the message text color of the {@value #TAG}.
     *
     * @return int
     */
    public int getTextColor() {
        return mMessageTextView.getCurrentTextColor();
    }

    /**
     * Sets the text size of the {@value #TAG} message.
     *
     * @param textSize int
     */
    public void setTextSize(int textSize) {
        mMessageTextView.setTextSize(textSize);
    }

    /**
     * Returns the text size of the {@value #TAG} message in pixels.
     *
     * @return float
     */
    public float getTextSize() {
        return mMessageTextView.getTextSize();
    }

    /**
     * Sets the duration that the {@value #TAG} will show.
     *
     * @param duration {@link com.lfh.custom.widget.toast.SuperToast.Duration}
     */
    public void setDuration(int duration) {
        if (duration > Duration.EXTRA_LONG) {
            Log.e(TAG, TAG + ERROR_DURATION_TOO_LONG);
            this.mDuration = Duration.EXTRA_LONG;
        } else {
            this.mDuration = duration;
        }
    }

    /**
     * Returns the duration of the {@value #TAG}.
     *
     * @return int
     */
    public int getDuration() {
        return this.mDuration;
    }

    /**
     * Sets an icon resource to the {@value #TAG} with a specified position.
     *
     * @param iconResource toast图标资源
     * @param iconPosition {@link IconPosition}
     */
    public void setIcon(int iconResource, IconPosition iconPosition) {
        Drawable drawable = ContextCompat.getDrawable(mContext, iconResource);
        if (drawable == null) {
            drawable = mContext.getResources().getDrawable(iconResource);
        }
        if (iconPosition == IconPosition.BOTTOM) {
            mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    null, drawable);
        } else if (iconPosition == IconPosition.LEFT) {
            mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mMessageTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            }
        } else if (iconPosition == IconPosition.RIGHT) {
            mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    drawable, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mMessageTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, drawable, null);
            }
        } else if (iconPosition == IconPosition.TOP) {
            mMessageTextView.setCompoundDrawablesWithIntrinsicBounds(null,
                    drawable, null, null);
        }
    }

    /**
     * Sets the background resource of the {@value #TAG}.
     *
     * @param background 背景
     */
    public void setBackground(int background) {
        this.mBackground = background;
        mMessageTextView.setBackgroundResource(background);
    }

    /**
     * Returns the background resource of the {@value #TAG}.
     *
     * @return int
     */
    public int getBackground() {
        return this.mBackground;
    }

    /**
     * Sets the gravity of the {@value #TAG} along with x and y offsets.
     *
     * @param gravity {@link Gravity} int
     * @param xOffset int
     * @param yOffset int
     */
    public void setGravity(int gravity, int xOffset, int yOffset) {
        this.mGravity = gravity;
        this.mXOffset = xOffset;
        this.mYOffset = yOffset;
    }

    /**
     * Sets the show/hide animations of the {@value #TAG}.
     *
     * @param animations {@link com.lfh.custom.widget.toast.SuperToast.Animations}
     */
    public void setAnimations(Animations animations) {
        this.mAnimations = animations;
    }

    /**
     * Returns the show/hide animations of the {@value #TAG}.
     *
     * @return {@link com.lfh.custom.widget.toast.SuperToast.Animations}
     */
    public Animations getAnimations() {
        return this.mAnimations;
    }

    /**
     * Sets an OnDismissListener defined in this library
     * to the {@value #TAG}. Does not require wrapper.
     *
     * @param onDismissListener {@link com.lfh.custom.widget.toast.SuperToast.OnDismissListener}
     */
    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
    }

    /**
     * Returns the OnDismissListener set to the {@value #TAG}.
     */
    public OnDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    /**
     * Dismisses the {@value #TAG}.
     */
    public void dismiss() {
        ManagerSuperToast.getInstance().removeSuperToast(this);
    }

    /**
     * Returns the {@value #TAG} message textview.
     *
     * @return {@link TextView}
     */
    public TextView getTextView() {
        return mMessageTextView;
    }

    /**
     * Returns the {@value #TAG} view.
     *
     * @return {@link View}
     */
    public View getView() {
        return mToastView;
    }

    /**
     * Returns true if the {@value #TAG} is showing.
     *
     * @return boolean
     */
    public boolean isShowing() {
        return mToastView != null && mToastView.isShown();
    }

    /**
     * Returns the window manager that the {@value #TAG} is attached to.
     *
     * @return {@link WindowManager}
     */
    public WindowManager getWindowManager() {
        return mWindowManager;
    }

    /**
     * Returns the window manager layout params of the {@value #TAG}.
     *
     * @return {@link WindowManager.LayoutParams}
     */
    public WindowManager.LayoutParams getWindowManagerParams() {
        return mWindowManagerParams;
    }

    /**
     * Private method used to return a specific animation for a animations enum
     */
    private int getAnimation() {
        if (mAnimations == Animations.FLY_IN) {
            return android.R.style.Animation_Translucent;
        } else if (mAnimations == Animations.SCALE) {
            return android.R.style.Animation_Dialog;
        } else if (mAnimations == Animations.POPUP) {
            return android.R.style.Animation_InputMethod;
        } else {
            return android.R.style.Animation_Toast;
        }
    }

    /**
     * Returns a standard {@value #TAG}.
     *
     * @param context          {@link Context}
     * @param textCharSequence {@link CharSequence}
     * @param durationInteger  {@link com.lfh.custom.widget.toast.SuperToast.Duration}
     * @return {@link SuperToast}
     */
    public static SuperToast create(Context context, CharSequence textCharSequence,
                                    int durationInteger) {
        SuperToast superToast = new SuperToast(context);
        superToast.setText(textCharSequence);
        superToast.setPadding();
        superToast.setDuration(durationInteger);

        return superToast;

    }

    /**
     * Returns a standard {@value #TAG} with specified animations.
     *
     * @param context          {@link Context}
     * @param textCharSequence {@link CharSequence}
     * @param durationInteger  {@link com.lfh.custom.widget.toast.SuperToast.Duration}
     * @param animations       {@link com.lfh.custom.widget.toast.SuperToast.Animations}
     * @return {@link SuperToast}
     */
    public static SuperToast create(Context context, CharSequence textCharSequence,
                                    int durationInteger, Animations animations) {
        final SuperToast superToast = new SuperToast(context);
        superToast.setText(textCharSequence);
        superToast.setDuration(durationInteger);
        superToast.setAnimations(animations);

        return superToast;

    }

    /**
     * Dismisses and removes all showing/pending {@value #TAG}.
     */
    public static void cancelAllSuperToasts() {
        ManagerSuperToast.getInstance().cancelAllSuperToasts();
    }

    private int dip2px(Context context, float dipValue) {
        return (int) (0.5F + dipValue * context.getResources().getDisplayMetrics().density);
    }
}