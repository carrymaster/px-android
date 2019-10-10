package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.font.PxFont;
import com.mercadopago.android.px.internal.util.ViewUtils;
import com.mercadopago.android.px.internal.viewmodel.EmptyLocalized;
import com.mercadopago.android.px.internal.viewmodel.IDetailColor;
import com.mercadopago.android.px.internal.viewmodel.IDetailDrawable;
import com.mercadopago.android.px.internal.viewmodel.ILocalizedCharSequence;
import com.mercadopago.android.px.model.DiscountConfigurationModel;

import static com.mercadopago.android.px.internal.util.TextUtil.isEmpty;

public class AmountDescriptorView extends LinearLayout {

    private TextView leftLabel;
    private TextView rightLabel;
    private ImageView imageView;
    private boolean rightLabelSemiBold;
    private boolean leftLabelSemiBold;

    public static int getDesiredHeight(@NonNull final Context context) {
        final View view = inflate(context, R.layout.px_viewholder_amountdescription, null);
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    public AmountDescriptorView(final Context context) {
        this(context, null);
    }

    public AmountDescriptorView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AmountDescriptorView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface OnClickListener {
        void onDiscountAmountDescriptorClicked(@NonNull final DiscountConfigurationModel discountModel);
        void onChargesAmountDescriptorClicked(@NonNull final DynamicDialogCreator dynamicDialogCreator);
    }

    private void init() {
        inflate(getContext(), R.layout.px_view_amount_descriptor, this);
        leftLabel = findViewById(R.id.label);
        rightLabel = findViewById(R.id.amount);
        imageView = findViewById(R.id.icon_descriptor_amount);
    }

    public void update(@NonNull final AmountDescriptorView.Model model) {
        updateLeftLabel(model);
        updateRightLabel(model);
        updateDrawable(model.detailDrawable, model.detailDrawableColor);
        updateTextColor(model.detailColor);
        setOnClickListener(model.listener);
    }

    private void updateRightLabel(@NonNull final AmountDescriptorView.Model model) {
        updateLabel(model.right.get(getContext()), rightLabel, rightLabelSemiBold);
    }

    private void updateLeftLabel(@NonNull final AmountDescriptorView.Model model) {
        updateLabel(model.left.get(getContext()), leftLabel, leftLabelSemiBold);
    }

    private void updateLabel(@NonNull final CharSequence charSequence, @NonNull final TextView textView,
        final boolean isSemiBold) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(charSequence);

        if (isSemiBold) {
            ViewUtils.setFontInSpannable(getContext(), PxFont.SEMI_BOLD, spannableStringBuilder);
        }

        if (isEmpty(charSequence)) {
            textView.setVisibility(GONE);
        }

        textView.setText(spannableStringBuilder);
    }

    public void setBold(@NonNull final Position label) {
        if (Position.LEFT == label) {
            leftLabelSemiBold = true;
        } else if (Position.RIGHT == label) {
            rightLabelSemiBold = true;
        }
    }

    public void setTextSize(final int dimen) {
        final int size = (int) getContext().getResources().getDimension(dimen);
        leftLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        rightLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
    }

    public void setTextColor(final int color) {
        leftLabel.setTextColor(color);
        rightLabel.setTextColor(color);
    }

    private void updateTextColor(@NonNull final IDetailColor detailColor) {
        leftLabel.setTextColor(detailColor.getColor(getContext()));
        rightLabel.setTextColor(detailColor.getColor(getContext()));
    }

    private void updateDrawable(@Nullable final IDetailDrawable detailDrawable,
        @Nullable final IDetailColor detailColor) {
        if (detailDrawable != null) {
            imageView.setVisibility(VISIBLE);
            imageView.setImageDrawable(detailDrawable.getDrawable(getContext()));
        } else {
            imageView.setVisibility(INVISIBLE);
        }

        if (detailColor != null) {
            imageView.setColorFilter(detailColor.getColor(getContext()));
        }
    }

    public static class Model {
        /* default */ @NonNull final ILocalizedCharSequence left;
        /* default */ @NonNull final ILocalizedCharSequence right;
        /* default */ @NonNull final IDetailColor detailColor;
        /* default */ @Nullable IDetailDrawable detailDrawable;
        /* default */ @Nullable IDetailColor detailDrawableColor;
        /* default */ @Nullable View.OnClickListener listener;

        public Model(@NonNull final ILocalizedCharSequence left, @NonNull final ILocalizedCharSequence right,
            @NonNull final IDetailColor detailColor) {
            this.left = left;
            this.right = right;
            this.detailColor = detailColor;
        }

        public Model(@NonNull final ILocalizedCharSequence left, @NonNull final IDetailColor detailColor) {
            this.left = left;
            this.detailColor = detailColor;
            right = new EmptyLocalized();
        }

        public AmountDescriptorView.Model setDetailDrawable(@Nullable final IDetailDrawable detailDrawable,
            @Nullable final IDetailColor detailDrawableColor) {
            this.detailDrawable = detailDrawable;
            this.detailDrawableColor = detailDrawableColor;
            return this;
        }

        public AmountDescriptorView.Model setListener(@NonNull final View.OnClickListener listener) {
            this.listener = listener;
            return this;
        }
    }

    public enum Position {LEFT, RIGHT}
}