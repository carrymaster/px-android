package com.mercadopago.android.px.internal.features.express.slider;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.di.Session;
import com.mercadopago.android.px.internal.features.disable_payment_method.DisabledPaymentMethodDetailDialog;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.drawables.DrawableFragmentItem;

public abstract class PaymentMethodFragment extends Fragment implements PaymentMethod.View {

    protected static final String ARG_MODEL = "ARG_MODEL";
    protected static final String ARG_PM_TYPE = "ARG_PM_TYPE";

    private View badge;
    private CardView card;
    private String paymentMethodType;
    private PaymentMethodPresenter presenter;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments == null || !arguments.containsKey(ARG_MODEL) || !arguments.containsKey(ARG_PM_TYPE)) {
            throw new IllegalStateException("PaymentMethodFragment does not contain model info");
        }
        paymentMethodType = arguments.getString(ARG_PM_TYPE);
        presenter = new PaymentMethodPresenter(
            Session.getInstance().getConfigurationModule().getDisabledPaymentMethodRepository(),
            (DrawableFragmentItem) arguments.getSerializable(ARG_MODEL));
    }

    @CallSuper
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        badge = view.findViewById(R.id.px_disabled_badge);
        card = view.findViewById(R.id.payment_method);
        presenter.attachView(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewResumed();
    }

    @Override
    public void disable() {
        badge.setVisibility(View.VISIBLE);
        ViewUtils.grayScaleViewGroup(card);
        card.setOnClickListener(
            v -> DisabledPaymentMethodDetailDialog.showDialog(getChildFragmentManager(), paymentMethodType));
    }

    protected void tintBackground(@NonNull final ImageView background, @NonNull final String color) {
        final int backgroundColor = Color.parseColor(color);

        final int alpha = Color.alpha(backgroundColor);
        final int blue = Color.blue(backgroundColor);
        final int green = Color.green(backgroundColor);
        final int red = Color.red(backgroundColor);

        final int lighterBackgroundColor =
            Color.argb((int) (alpha * 0.7f), (int) (red * 0.8f), (int) (green * 0.8f), (int) (blue * 0.8f));
        Color.argb(0, 0, 0, 0);
        final int[] ints = { backgroundColor, lighterBackgroundColor };
        final GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BL_TR,
            ints);

        gradientDrawable.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.px_xs_margin));
        gradientDrawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setDither(true);

        background.setImageDrawable(gradientDrawable);
    }
}