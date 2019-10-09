package com.mercadopago.android.px.internal.features.checkout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.base.MvpView;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.exceptions.CheckoutPreferenceException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;

/* default */ interface Checkout {
    /* default */ interface View extends MvpView {
        void startExpressPaymentRecoveryFlow(@NonNull final PaymentRecovery paymentRecovery);

        void showError(final MercadoPagoError error);

        void showProgress();

        void showReviewAndConfirm(final boolean isUniquePaymentMethod);

        void showPaymentMethodSelection();

        void showPaymentResult(final PaymentModel paymentModel);

        void finishWithPaymentResult();

        void finishWithPaymentResult(final Integer customResultCode);

        void finishWithPaymentResult(final Payment payment);

        void finishWithPaymentResult(final Integer customResultCode, final Payment payment);

        void cancelCheckout();

        void cancelCheckout(final MercadoPagoError mercadoPagoError);

        void cancelCheckout(final Integer customResultCode, final Boolean paymentMethodEdited);

        void startPaymentRecoveryFlow(final PaymentRecovery paymentRecovery);

        void showPaymentProcessor();

        void showPaymentProcessorWithAnimation();

        boolean isActive();

        void showBusinessResult(final BusinessPaymentModel model);

        void showOneTap();

        void hideProgress();

        void exitCheckout(final int resCode);

        void transitionOut();

        void showSavedCardFlow(final Card card);

        void showNewCardFlow();

        void showReviewAndConfirmAndRecoverPayment(final boolean isUniquePaymentMethod,
            @NonNull final PostPaymentAction postPaymentAction);

        void startPayment();

        void showCheckoutExceptionError(final CheckoutPreferenceException checkoutPreferenceException);

        void fetchFonts();

        void showFailureRecoveryError();
    }

    /* default */ interface Actions {
        void initialize();

        void onErrorCancel(@Nullable final MercadoPagoError mercadoPagoError);

        void onPaymentMethodSelectionError(final MercadoPagoError mercadoPagoError);

        void onPaymentMethodSelectionCancel();

        void onReviewAndConfirmCancel();

        void onReviewAndConfirmError(final MercadoPagoError mercadoPagoError);

        void onPaymentResultResponse();

        void onCardFlowResponse();

        void onTerminalError(@NonNull final MercadoPagoError mercadoPagoError);

        void onCardFlowCancel();

        void onCustomReviewAndConfirmResponse(final Integer customResultCode);

        void recoverFromFailure();

        void setFailureRecovery(final FailureRecovery failureRecovery);

        void onCustomPaymentResultResponse(final Integer customResultCode);

        void cancelCheckout();

        void exitWithCode(final int resCode);

        boolean isUniquePaymentMethod();

        //TODO separate with better navigation when we have a proper driver.
        void onChangePaymentMethodFromReviewAndConfirm();
    }
}
