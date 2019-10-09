package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.util.ScaleUtil;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.GenericLocalized;
import com.squareup.picasso.Picasso;

public class PaymentResultHeader extends ConstraintLayout {

    public PaymentResultHeader(final Context context) {
        this(context, null);
    }

    public PaymentResultHeader(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaymentResultHeader(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setModel(@NonNull final Model model) {
        if (model.isSuccess) {
            inflate(getContext(), R.layout.px_payment_result_header_success, this);
        } else {
            inflate(getContext(), R.layout.px_payment_result_header, this);
        }
        final int background = ContextCompat.getColor(getContext(), model.background);
        final MPTextView title = findViewById(R.id.title);
        final MPTextView label = findViewById(R.id.label);
        final ImageView icon = findViewById(R.id.icon);
        final ImageView badge = findViewById(R.id.badge);

        if (model.dynamicHeight) {
            ViewUtils.stretchHeight(this);
        } else {
            ViewUtils.wrapHeight(this);
        }

        setBackgroundColor(background);
        ViewUtils.loadOrGone(model.title.get(getContext()), title);
        if (label != null) {
            ViewUtils.loadOrGone(model.label.get(getContext()), label);
        }
        renderIcon(icon, model);
        ViewUtils.loadOrGone(model.badgeImage, badge);
    }

    private void renderIcon(@NonNull final ImageView icon, @NonNull final Model model) {
        final int size = ScaleUtil.getPxFromDp(90, getContext());
        Picasso.with(getContext())
            .load(TextUtil.isNotEmpty(model.iconUrl) ? model.iconUrl : null)
            .transform(new CircleTransform())
            .resize(size, size)
            .centerInside()
            .noFade()
            .placeholder(model.iconImage)
            .into(icon);
    }

    public static final class Model {

        /* default */ final boolean dynamicHeight;
        /* default */ final int background;
        /* default */ final int statusBarColor;
        /* default */ final int iconImage;
        /* default */ final int badgeImage;
        /* default */ final String iconUrl;
        /* default */ final GenericLocalized title;
        /* default */ final GenericLocalized label;
        /* default */ final boolean isSuccess;

        /* default */ Model(@NonNull final Builder builder) {
            dynamicHeight = builder.dynamicHeight;
            background = builder.background;
            statusBarColor = builder.statusBarColor;
            iconImage = builder.iconImage;
            iconUrl = builder.iconUrl;
            badgeImage = builder.badgeImage;
            title = builder.title;
            label = builder.label;
            isSuccess = builder.isSuccess;
        }

        @ColorRes
        public int getStatusBarColor() {
            return statusBarColor;
        }

        public static class Builder {
            /* default */ boolean dynamicHeight;
            /* default */ int background;
            /* default */ int statusBarColor;
            /* default */ int iconImage;
            /* default */ int badgeImage;
            /* default */ String iconUrl;
            /* default */ GenericLocalized title;
            /* default */ GenericLocalized label;
            /* default */ boolean isSuccess;

            public Builder setBackground(@DrawableRes final int background) {
                this.background = background;
                return this;
            }

            public Builder setStatusBarColor(@ColorRes final int statusBarColor) {
                this.statusBarColor = statusBarColor;
                return this;
            }

            public Builder setIconImage(@DrawableRes final int iconImage) {
                this.iconImage = iconImage;
                return this;
            }

            public Builder setIconUrl(final String iconUrl) {
                this.iconUrl = iconUrl;
                return this;
            }

            public Builder setBadgeImage(@DrawableRes final int badgeImage) {
                this.badgeImage = badgeImage;
                return this;
            }

            public Builder setDynamicHeight(final boolean dynamicHeight) {
                this.dynamicHeight = dynamicHeight;
                return this;
            }

            public Builder setTitle(@NonNull final GenericLocalized title) {
                this.title = title;
                return this;
            }

            public Builder setLabel(@NonNull final GenericLocalized label) {
                this.label = label;
                return this;
            }

            public Builder setSuccess(final boolean isSuccess) {
                this.isSuccess = isSuccess;
                return this;
            }

            public Model build() {
                return new Model(this);
            }
        }
    }
}