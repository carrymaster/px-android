package com.mercadopago.android.px.internal.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.font.FontHelper;
import com.mercadopago.android.px.internal.font.PxFont;

public class MPTextView extends AppCompatTextView {

    public MPTextView(final Context context) {
        this(context, null);
    }

    public MPTextView(final Context context, @Nullable final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MPTextView(final Context context, @Nullable final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MPTextView);
        final PxFont font = PxFont.from(a.getInt(R.styleable.MPTextView_customStyle, PxFont.REGULAR.id));
        a.recycle();

        if (!isInEditMode()) {
            FontHelper.setFont(this, font);
        }
    }
}