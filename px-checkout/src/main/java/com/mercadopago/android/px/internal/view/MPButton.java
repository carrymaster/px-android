package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;

public class MPButton extends AppCompatButton {

    public MPButton(final Context context) {
        this(context, null);
    }

    public MPButton(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPButton(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            FontHelper.setFont(this, PxFont.REGULAR);
        }
    }
}