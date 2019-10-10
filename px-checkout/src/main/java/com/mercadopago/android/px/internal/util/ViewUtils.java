package com.mercadopago.android.px.internal.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewCompat;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.view.MPEditText;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public final class ViewUtils {

    private static final float DARKEN_FACTOR = 0.04f;
    private static final int HSV_LENGTH = 3;
    private static final float[] hsv = new float[HSV_LENGTH];

    private ViewUtils() {
    }

    public static boolean shouldVisibleAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != View.VISIBLE;
    }

    public static boolean shouldGoneAnim(@NonNull final View viewToAnimate) {
        return hasEndedAnim(viewToAnimate) && viewToAnimate.getVisibility() != View.GONE;
    }

    public static boolean hasEndedAnim(@NonNull final View viewToAnimate) {
        return viewToAnimate.getAnimation() == null ||
            (viewToAnimate.getAnimation() != null && viewToAnimate.getAnimation().hasEnded());
    }

    public static void loadOrCallError(final String imgUrl, final ImageView logo, final Callback callback) {
        if (!TextUtil.isEmpty(imgUrl)) {
            Picasso.with(logo.getContext())
                .load(imgUrl)
                .into(logo, callback);
        } else {
            callback.onError();
        }
    }

    public static void loadOrGone(@Nullable final CharSequence text, @NonNull final TextView textView) {
        if (TextUtil.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        }
    }

    public static void loadOrGone(@StringRes final int resId, @NonNull final TextView textView) {
        final CharSequence value = resId == 0 ? TextUtil.EMPTY : textView.getContext().getString(resId);
        loadOrGone(value, textView);
    }

    public static void loadOrGone(@DrawableRes final int resId, final ImageView imageView) {
        if (resId == 0) {
            imageView.setVisibility(View.GONE);
        } else {
            imageView.setImageResource(resId);
            imageView.setVisibility(View.VISIBLE);
        }
    }

    public static void setMarginBottomInView(@NonNull final View view, final int marginBottom) {
        setMarginInView(view, 0, 0, 0, marginBottom);
    }

    public static void setMarginInView(@NonNull final View button, final int leftMargin, final int topMargin,
        final int rightMargin, final int bottomMargin) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
        button.setLayoutParams(params);
    }

    public static void hideKeyboard(final Activity activity) {
        try {
            final MPEditText editText = (MPEditText) activity.getCurrentFocus();
            final InputMethodManager imm = (InputMethodManager) activity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        } catch (final Exception ex) {
        }
    }

    public static void openKeyboard(final View view) {
        view.requestFocus();
        final InputMethodManager imm =
            (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void showProgressLayout(final Activity activity) {
        showLayout(activity, true, false);
    }

    public static void showRegularLayout(final Activity activity) {
        showLayout(activity, false, true);
    }

    private static void showLayout(final Activity activity, final boolean showProgress, final boolean showLayout) {
        final View form = activity.findViewById(R.id.mpsdkRegularLayout);
        final View progress = activity.findViewById(R.id.mpsdkProgressLayout);

        if (progress != null) {
            progress.setVisibility(showLayout ? View.GONE : View.VISIBLE);
        }

        if (form != null) {
            form.setVisibility(showProgress ? View.GONE : View.VISIBLE);
        }
    }

    public static void resizeViewGroupLayoutParams(final ViewGroup viewGroup, final int height, final int width) {
        final ViewGroup.LayoutParams params = viewGroup.getLayoutParams();
        final Context context = viewGroup.getContext();
        params.height = (int) context.getResources().getDimension(height);
        params.width = (int) context.getResources().getDimension(width);
        viewGroup.setLayoutParams(params);
    }

    public static void setColorInSpannable(final int color, final int indexStart, final int indexEnd,
        @NonNull final Spannable spannable) {
        if (color != 0) {
            spannable.setSpan(new ForegroundColorSpan(color), indexStart, indexEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void setFontInSpannable(@NonNull final Context context, @NonNull final PxFont font,
        @NonNull final Spannable spannable) {
        setFontInSpannable(context, font, spannable, 0, spannable.length());
    }

    public static void setFontInSpannable(@NonNull final Context context, @NonNull final PxFont font,
        @NonNull final Spannable spannable, final int indexStart, final int indexEnd) {
        FontHelper.getFont(context, font, new ResourcesCompat.FontCallback() {
            @Override
            public void onFontRetrieved(@NonNull final Typeface typeface) {
                spannable.setSpan(new StyleSpan(typeface.getStyle()), indexStart, indexEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            @Override
            public void onFontRetrievalFailed(final int i) {
                spannable.setSpan(new StyleSpan(font.fallbackStyle), indexStart, indexEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        });
    }

    public static void stretchHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );
        view.setLayoutParams(params);
    }

    public static void wrapHeight(@NonNull final View view) {
        final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(params);
    }

    @NonNull
    public static View inflate(@NonNull final ViewGroup parent, @LayoutRes final int layout) {
        return LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
    }

    @NonNull
    public static View compose(@NonNull final ViewGroup container, @NonNull final View child) {
        container.addView(child);
        return container;
    }

    @NonNull
    public static LinearLayout createLinearContainer(final Context context) {
        final LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT));
        return linearLayout;
    }

    public static void cancelAnimation(@NonNull final View targetView) {
        final Animation animation = targetView.getAnimation();
        if (animation != null) {
            animation.cancel();
        }
    }

    public static void grayScaleView(@NonNull final ImageView targetView) {
        final ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        targetView.setColorFilter(filter);
    }

    public static void grayScaleViewGroup(@NonNull final ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            final View view = viewGroup.getChildAt(i);
            if (view instanceof ImageView) {
                grayScaleView((ImageView) view);
            } else if (view instanceof ViewGroup) {
                grayScaleViewGroup((ViewGroup) view);
            }
        }
    }

    public static void runWhenViewIsFullyMeasured(@NonNull final View view, @NonNull final Runnable runnable) {
        if (ViewCompat.isLaidOut(view)) {
            runnable.run();
        } else {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(final View v, final int left, final int top, final int right,
                    final int bottom, final int oldLeft, final int oldTop, final int oldRight, final int oldBottom) {
                    view.removeOnLayoutChangeListener(this);
                    runnable.run();
                }
            });
        }
    }

    /**
     * Paint the status bar
     *
     * @param color the color to use. The color will be darkened by {@link #DARKEN_FACTOR} percent
     */
    @SuppressLint({ "InlinedApi" })
    public static void setStatusBarColor(@ColorInt final int color, @NonNull final Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            Color.RGBToHSV(Color.red(color), Color.green(color), Color.blue(color), hsv);
            hsv[2] = Math.max(hsv[2] * (1 - DARKEN_FACTOR), 0);
            window.setStatusBarColor(Color.HSVToColor(hsv));
        }
    }
}