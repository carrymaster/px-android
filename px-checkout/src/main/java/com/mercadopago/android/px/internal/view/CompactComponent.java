package com.mercadopago.android.px.internal.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

public abstract class CompactComponent<P, A> {

    @NonNull protected P props;
    @Nullable private final A actions;

    public CompactComponent(@NonNull final P props) {
        this(props, null);
    }

    public CompactComponent(@NonNull final P props, @Nullable final A callBack) {
        actions = callBack;
        this.props = props;
    }

    public abstract View render(@NonNull final ViewGroup parent);

    @Nullable
    protected A getActions() {
        return actions;
    }
}