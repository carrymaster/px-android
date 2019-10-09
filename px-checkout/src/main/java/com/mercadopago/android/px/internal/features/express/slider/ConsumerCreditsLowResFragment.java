package com.mercadopago.android.px.internal.features.express.slider;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.mercadopago.android.px.R;
import com.mercadopago.android.px.internal.view.LinkableTextView;
import com.mercadopago.android.px.internal.viewmodel.drawables.ConsumerCreditsDrawableFragmentItem;
import com.mercadopago.android.px.model.ConsumerCreditsDisplayInfo;
import com.mercadopago.android.px.model.PaymentTypes;

public class ConsumerCreditsLowResFragment extends ConsumerCreditsFragment {

    @SuppressWarnings("TypeMayBeWeakened")
    @NonNull
    public static Fragment getInstance(@NonNull final ConsumerCreditsDrawableFragmentItem item) {
        final ConsumerCreditsLowResFragment creditsFragment = new ConsumerCreditsLowResFragment();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_MODEL, item);
        bundle.putString(ARG_PM_TYPE, PaymentTypes.DIGITAL_CURRENCY);
        creditsFragment.setArguments(bundle);
        return creditsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container,
        @Nullable final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.px_fragment_consumer_credits_low_res, container, false);
    }

    @Override
    protected void showDisplayInfo(final View view, @NonNull final ConsumerCreditsDisplayInfo displayInfo) {
        ((LinkableTextView) view.findViewById(R.id.bottom_text)).updateModel(displayInfo.bottomText);
    }
}