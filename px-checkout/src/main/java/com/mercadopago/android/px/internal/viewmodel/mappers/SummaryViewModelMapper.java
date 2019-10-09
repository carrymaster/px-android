package com.mercadopago.android.px.internal.viewmodel.mappers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import com.mercadopago.android.px.internal.repository.AmountRepository;
import com.mercadopago.android.px.internal.repository.ChargeRepository;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.view.AmountDescriptorView;
import com.mercadopago.android.px.internal.view.ElementDescriptorView;
import com.mercadopago.android.px.internal.view.SummaryDetailDescriptorFactory;
import com.mercadopago.android.px.internal.view.SummaryView;
import com.mercadopago.android.px.internal.viewmodel.AmountLocalized;
import com.mercadopago.android.px.internal.viewmodel.SummaryViewDefaultColor;
import com.mercadopago.android.px.internal.viewmodel.TotalLocalized;
import com.mercadopago.android.px.model.CardMetadata;
import com.mercadopago.android.px.model.DiscountConfigurationModel;
import com.mercadopago.android.px.model.commission.PaymentTypeChargeRule;
import com.mercadopago.android.px.model.internal.ExpressPaymentMethod;
import com.mercadopago.android.px.model.internal.SummaryInfo;
import java.util.Collection;
import java.util.List;

public class SummaryViewModelMapper extends CacheableMapper<ExpressPaymentMethod, SummaryView.Model,
    Pair<DiscountConfigurationModel, PaymentTypeChargeRule>> {

    @NonNull private final String currencyId;
    @NonNull private final DiscountRepository discountRepository;
    @NonNull private final AmountRepository amountRepository;
    @NonNull private final ElementDescriptorView.Model elementDescriptorModel;
    @NonNull private final AmountDescriptorView.OnClickListener listener;
    @NonNull private final SummaryInfo summaryInfo;
    @NonNull private final ChargeRepository chargeRepository;

    public SummaryViewModelMapper(@NonNull final String currencyId,
        @NonNull final DiscountRepository discountRepository, @NonNull final AmountRepository amountRepository,
        @NonNull final ElementDescriptorView.Model elementDescriptorModel,
        @NonNull final AmountDescriptorView.OnClickListener listener,
        @NonNull final SummaryInfo summaryInfo, @NonNull final ChargeRepository chargeRepository) {
        this.currencyId = currencyId;
        this.discountRepository = discountRepository;
        this.amountRepository = amountRepository;
        this.elementDescriptorModel = elementDescriptorModel;
        this.listener = listener;
        this.summaryInfo = summaryInfo;
        this.chargeRepository = chargeRepository;
    }

    @Override
    protected Pair<DiscountConfigurationModel, PaymentTypeChargeRule> getKey(
        @NonNull final ExpressPaymentMethod expressPaymentMethod) {
        return new Pair<>(discountRepository.getConfigurationFor(expressPaymentMethod.getCustomOptionId()),
            chargeRepository.getChargeRule(expressPaymentMethod.getPaymentTypeId()));
    }

    @Override
    public SummaryView.Model map(@NonNull final ExpressPaymentMethod expressPaymentMethod) {
        return createModel(expressPaymentMethod.getPaymentTypeId(),
            discountRepository.getConfigurationFor(expressPaymentMethod.getCustomOptionId()));
    }

    //TODO remove when add card node comes from backend
    @Override
    public List<SummaryView.Model> map(@NonNull final Iterable<ExpressPaymentMethod> val) {
        if (val instanceof Collection) {
            ((Collection<ExpressPaymentMethod>) val).add(getAddCardNode());
        }
        return super.map(val);
    }

    @NonNull
    private SummaryView.Model createModel(@NonNull final String paymentTypeId,
        @NonNull final DiscountConfigurationModel discountModel) {
        final PaymentTypeChargeRule chargeRule = chargeRepository.getChargeRule(paymentTypeId);
        final List<AmountDescriptorView.Model> summaryDetailList =
            new SummaryDetailDescriptorFactory(listener, discountModel, amountRepository, summaryInfo, currencyId,
                chargeRule).create();

        final AmountDescriptorView.Model totalRow = new AmountDescriptorView.Model(
            new TotalLocalized(),
            new AmountLocalized(amountRepository.getAmountToPay(paymentTypeId, discountModel), currencyId),
            new SummaryViewDefaultColor());

        return new SummaryView.Model(elementDescriptorModel, summaryDetailList, totalRow);
    }

    @NonNull
    private ExpressPaymentMethod getAddCardNode() {
        return new ExpressPaymentMethod() {
            @Override
            public String getPaymentMethodId() {
                return "";
            }

            @Override
            public String getPaymentTypeId() {
                return "";
            }

            @Nullable
            @Override
            public CardMetadata getCard() {
                return null;
            }

            @Override
            public boolean isCard() {
                return false;
            }

            @NonNull
            @Override
            public String getCustomOptionId() {
                return getPaymentMethodId();
            }
        };
    }
}