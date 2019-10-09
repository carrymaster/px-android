package com.mercadopago.android.px.tracking.internal.model;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.util.PaymentDataHelper;
import com.mercadopago.android.px.internal.util.TextUtil;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Campaign;
import com.mercadopago.android.px.model.PaymentData;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.mapper.FromDiscountItemToItemId;
import java.math.BigDecimal;
import java.util.Map;

public final class ResultViewTrackModel extends TrackingMapModel {

    private final String style;
    private final Long paymentId;
    private final String paymentStatus;
    private final String paymentStatusDetail;
    private final String currencyId;
    private final boolean hasSplitPayment;
    private final BigDecimal preferenceAmount;
    private final BigDecimal discountCouponAmount;
    private final String paymentMethodId;
    private final String paymentMethodType;
    private final Integer scoreLevel;
    private final int discountsCount;
    private final String campaignsIds;
    private final String campaignId;

    private boolean hasBottomView;
    private boolean hasTopView;
    private boolean hasImportantView;

    private enum Style {
        GENERIC("generic"),
        CUSTOM("custom");

        @NonNull public final String value;

        Style(@NonNull final String value) {
            this.value = value;
        }
    }

    public ResultViewTrackModel(@NonNull final PaymentModel paymentModel,
        @NonNull final PaymentResultScreenConfiguration screenConfiguration,
        @NonNull final CheckoutPreference checkoutPreference, @NonNull final String currencyId) {
        this(Style.GENERIC, paymentModel, checkoutPreference, currencyId);
        hasBottomView = screenConfiguration.hasBottomFragment();
        hasTopView = screenConfiguration.hasTopFragment();
        hasImportantView = false;
    }

    public ResultViewTrackModel(@NonNull final BusinessPaymentModel paymentModel,
        @NonNull final CheckoutPreference checkoutPreference, @NonNull final String currencyId) {
        this(Style.CUSTOM, paymentModel, checkoutPreference, currencyId);
        hasBottomView = paymentModel.getPayment().hasBottomFragment();
        hasTopView = paymentModel.getPayment().hasTopFragment();
    }

    private ResultViewTrackModel(@NonNull final Style style, @NonNull final PaymentModel paymentModel,
        @NonNull final CheckoutPreference checkoutPreference, @NonNull final String currencyId) {
        final PaymentResult paymentResult = paymentModel.getPaymentResult();
        final PaymentReward paymentReward = paymentModel.getPaymentReward();
        final PaymentData paymentData = paymentResult.getPaymentData();
        final PaymentReward.Discount discount = paymentReward.getDiscount();
        final Campaign campaign = paymentData != null ? paymentData.getCampaign() : null;
        final PaymentMethod paymentMethod = paymentData != null ? paymentData.getPaymentMethod() : null;
        this.style = style.value;
        this.currencyId = currencyId;
        paymentId = paymentResult.getPaymentId();
        paymentStatus = paymentResult.getPaymentStatus();
        paymentStatusDetail = paymentResult.getPaymentStatusDetail();
        hasSplitPayment = PaymentDataHelper.isSplitPayment(paymentResult.getPaymentDataList());
        preferenceAmount = checkoutPreference.getTotalAmount();
        discountCouponAmount = PaymentDataHelper.getTotalDiscountAmount(paymentResult.getPaymentDataList());
        scoreLevel = paymentReward.getScore() != null ? paymentReward.getScore().getProgress().getLevel() : null;
        discountsCount = discount != null ? discount.getItems().size() : 0;
        campaignsIds = discount != null ? TextUtil.join(new FromDiscountItemToItemId().map(discount.getItems())) : null;
        campaignId = campaign != null ? campaign.getId() : null;
        paymentMethodId = paymentMethod != null ? paymentMethod.getId() : null;
        paymentMethodType = paymentMethod != null ? paymentMethod.getPaymentTypeId() : null;
    }

    @NonNull
    @Override
    protected Map<String, Object> sanitizeMap(@NonNull final Map<String, Object> map) {
        map.put("payment_id", paymentId);
        return map;
    }
}