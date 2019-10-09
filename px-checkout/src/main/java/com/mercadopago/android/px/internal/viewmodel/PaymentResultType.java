package com.mercadopago.android.px.internal.viewmodel;

import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.model.BusinessPayment;

public enum PaymentResultType {
    APPROVED("APPROVED", R.color.ui_components_success_color, R.color.px_green_status_bar,
        R.drawable.px_badge_check, 0),
    REJECTED("REJECTED", R.color.ui_components_error_color, R.color.px_red_status_bar,
        R.drawable.px_badge_error, R.string.px_rejection_label),
    PENDING("PENDING", R.color.ui_components_warning_color, R.color.px_orange_status_bar,
        R.drawable.px_badge_pending_orange, 0);

    public final String name;
    public final int resColor;
    public final int statusBarColor;
    public final int badge;
    public final int message;

    PaymentResultType(@NonNull final String name, @ColorRes final int resColor, @ColorRes final int statusBarColor,
        @DrawableRes final int badge, @StringRes final int message) {
        this.name = name;
        this.resColor = resColor;
        this.statusBarColor = statusBarColor;
        this.badge = badge;
        this.message = message;
    }

    public static PaymentResultType from(final BusinessPayment.Decorator decorator) {
        switch (decorator) {
        case PENDING:
            return PENDING;
        case APPROVED:
            return APPROVED;
        case REJECTED:
            return REJECTED;
        default:
            throw new IllegalStateException("Invalid decorator");
        }
    }
}