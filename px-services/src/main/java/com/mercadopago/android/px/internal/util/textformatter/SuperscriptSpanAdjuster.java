package com.mercadopago.android.px.internal.util.textformatter;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public final class SuperscriptSpanAdjuster extends MetricAffectingSpan {

    private double rate = 0.5;

    public SuperscriptSpanAdjuster(final double rate) {
        this.rate = rate;
    }

    @Override
    public void updateDrawState(final TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * rate);
    }

    @Override
    public void updateMeasureState(final TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * rate);
    }
}
