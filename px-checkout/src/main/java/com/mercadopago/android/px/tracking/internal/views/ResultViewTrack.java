package com.mercadopago.android.px.tracking.internal.views;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.tracking.internal.model.ResultViewTrackModel;
import java.util.Locale;
import java.util.Map;

public class ResultViewTrack extends ViewTracker {

    private static final String PATH = BASE_VIEW_PATH + "/result/%s";

    private static final String SUCCESS = "success";
    private static final String PENDING = "further_action_needed";
    private static final String ERROR = "error";
    private static final String UNKNOWN = "unknown";

    private final ResultViewTrackModel resultViewTrackModel;
    private final String paymentStatus;

    public ResultViewTrack(@NonNull final PaymentModel paymentModel,
        @NonNull final PaymentResultScreenConfiguration screenConfiguration,
        @NonNull final PaymentSettingRepository paymentSetting) {
        resultViewTrackModel = new ResultViewTrackModel(paymentModel, screenConfiguration, paymentSetting.getCheckoutPreference(), paymentSetting.getSite().getCurrencyId());
        paymentStatus = getMappedResult(paymentModel.getPaymentResult());
    }

    public ResultViewTrack(@NonNull final BusinessPaymentModel paymentModel,
        @NonNull final PaymentSettingRepository paymentSetting) {
        resultViewTrackModel = new ResultViewTrackModel(paymentModel, paymentSetting.getCheckoutPreference(),
            paymentSetting.getSite().getCurrencyId());
        paymentStatus = getMappedResult(paymentModel.getPaymentResult());
    }

    private String getMappedResult(@NonNull final PaymentResult payment) {
        if (payment.isApproved() || payment.isInstructions()) {
            return SUCCESS;
        } else if (payment.isRejected()) {
            return ERROR;
        } else if (payment.isPending()) {
            return PENDING;
        } else {
            return UNKNOWN;
        }
    }

    @NonNull
    @Override
    public Map<String, Object> getData() {
        return resultViewTrackModel.toMap();
    }

    @NonNull
    @Override
    public String getViewPath() {
        return String.format(Locale.US, PATH, paymentStatus);
    }
}