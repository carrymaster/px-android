package com.mercadopago.android.px.tracking.internal.mapper;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.ArrayList;
import java.util.List;

public class FromDiscountItemToItemId extends Mapper<PaymentReward.Discount.Item, String> {

    //FIXME remove when mapper stop adding null values
    @Override
    public List<String> map(@NonNull final Iterable<PaymentReward.Discount.Item> values) {
        final List<String> returned = new ArrayList<>();
        for (final PaymentReward.Discount.Item value : values) {
            final String mappedValue = map(value);
            if (mappedValue != null) {
                returned.add(mappedValue);
            }
        }
        return returned;
    }

    @Override
    public String map(@NonNull final PaymentReward.Discount.Item discountItem) {
        return discountItem.getCampaignId();
    }
}