package com.mercadopago.android.px.internal.features.payment_result;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.configuration.PaymentResultScreenConfiguration;
import com.mercadopago.android.px.core.MercadoPagoCheckout;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.features.payment_result.mappers.PaymentResultViewModelMapper;
import com.mercadopago.android.px.internal.features.payment_result.viewmodel.PaymentResultViewModel;
import com.mercadopago.android.px.internal.features.review_and_confirm.components.actions.ChangePaymentMethodAction;
import com.mercadopago.android.px.internal.repository.InstructionsRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.util.ApiUtil;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.internal.view.CopyAction;
import com.mercadopago.android.px.internal.view.LinkAction;
import com.mercadopago.android.px.internal.view.NextAction;
import com.mercadopago.android.px.internal.view.RecoverPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.Instruction;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.services.Callback;
import com.mercadopago.android.px.tracking.internal.events.AbortEvent;
import com.mercadopago.android.px.tracking.internal.events.ChangePaymentMethodEvent;
import com.mercadopago.android.px.tracking.internal.events.ContinueEvent;
import com.mercadopago.android.px.tracking.internal.events.CrossSellingEvent;
import com.mercadopago.android.px.tracking.internal.events.DiscountItemEvent;
import com.mercadopago.android.px.tracking.internal.events.DownloadAppEvent;
import com.mercadopago.android.px.tracking.internal.events.ScoreEvent;
import com.mercadopago.android.px.tracking.internal.events.SeeAllDiscountsEvent;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;
import java.util.List;

/* default */ class PaymentResultPresenter extends BasePresenter<PaymentResultContract.View>
    implements ActionDispatcher, PaymentResultContract.Presenter, BusinessActions {

    private final PaymentModel paymentModel;
    private final InstructionsRepository instructionsRepository;
    private final ResultViewTrack resultViewTrack;
    private final PaymentResultScreenConfiguration screenConfiguration;

    private FailureRecovery failureRecovery;

    /* default */ PaymentResultPresenter(@NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final InstructionsRepository instructionsRepository, @NonNull final PaymentModel paymentModel) {
        this.paymentModel = paymentModel;
        this.instructionsRepository = instructionsRepository;
        screenConfiguration =
            paymentSettings.getAdvancedConfiguration().getPaymentResultScreenConfiguration();
        resultViewTrack =
            new ResultViewTrack(paymentModel, screenConfiguration, paymentSettings);
    }

    @Override
    public void attachView(final PaymentResultContract.View view) {
        super.attachView(view);

        if (paymentModel.getPaymentResult().isOffPayment()) {
            getInstructions();
        } else {
            configureView(null);
        }
    }

    @Override
    public void onFreshStart() {
        setCurrentViewTracker(resultViewTrack);
    }

    @Override
    public void onAbort() {
        new AbortEvent(resultViewTrack).track();
        getView().finishWithResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
    }

    /* default */ void getInstructions() {
        instructionsRepository.getInstructions(paymentModel.getPaymentResult()).enqueue(
            new Callback<List<Instruction>>() {
                @Override
                public void success(final List<Instruction> instructions) {
                    resolveInstructions(instructions);
                }

                @Override
                public void failure(final ApiException apiException) {
                    if (isViewAttached()) {
                        getView().showApiExceptionError(apiException, ApiUtil.RequestOrigin.GET_INSTRUCTIONS);
                        setFailureRecovery(() -> getInstructions());
                    }
                }
            });
    }

    public void recoverFromFailure() {
        if (failureRecovery != null) {
            failureRecovery.recover();
        }
    }

    /* default */ void setFailureRecovery(final FailureRecovery failureRecovery) {
        this.failureRecovery = failureRecovery;
    }

    /* default */ void resolveInstructions(final List<Instruction> instructions) {
        final Instruction instruction = getInstruction(instructions);
        if (isViewAttached() && instruction == null) {
            getView().showInstructionsError();
        } else {
            configureView(instruction);
        }
    }

    @Nullable
    private Instruction getInstruction(@NonNull final List<Instruction> instructions) {
        if (instructions.size() == 1) {
            return instructions.get(0);
        } else {
            return getInstructionForType(instructions,
                paymentModel.getPaymentResult().getPaymentData().getPaymentMethod().getPaymentTypeId());
        }
    }

    @Nullable
    private Instruction getInstructionForType(final Iterable<Instruction> instructions, final String paymentTypeId) {
        for (final Instruction instruction : instructions) {
            if (instruction.getType().equals(paymentTypeId)) {
                return instruction;
            }
        }
        return null;
    }

    private void configureView(@Nullable final Instruction instruction) {
        final PaymentResultViewModel viewModel = new PaymentResultViewModelMapper(screenConfiguration, instruction)
            .map(paymentModel);
        getView().configureViews(viewModel, this);
        getView().setStatusBarColor(viewModel.headerModel.getStatusBarColor());
    }

    @Override
    public void dispatch(@NonNull final Action action) {
        if (!isViewAttached()) {
            return;
        }

        if (action instanceof NextAction) {
            new ContinueEvent(resultViewTrack).track();
            getView().finishWithResult(MercadoPagoCheckout.PAYMENT_RESULT_CODE);
        } else if (action instanceof ChangePaymentMethodAction) {
            ChangePaymentMethodEvent.with(resultViewTrack).track();
            getView().changePaymentMethod();
        } else if (action instanceof RecoverPaymentAction) {
            getView().recoverPayment();
        } else if (action instanceof LinkAction) {
            getView().openLink(((LinkAction) action).url);
        } else if (action instanceof CopyAction) {
            getView().copyToClipboard(((CopyAction) action).content);
        }
    }

    @Override
    public void OnClickDownloadAppButton(@NonNull final String deepLink) {
        new DownloadAppEvent(resultViewTrack).track();
        getView().processBusinessAction(deepLink);
    }

    @Override
    public void OnClickCrossSellingButton(@NonNull final String deepLink) {
        new CrossSellingEvent(resultViewTrack).track();
        getView().processCrossSellingBusinessAction(deepLink);
    }

    @Override
    public void onClickDiscountItem(final int index, @Nullable final String deepLink, @Nullable final String trackId) {
        new DiscountItemEvent(resultViewTrack, index, trackId).track();
        if (deepLink != null) {
            getView().processBusinessAction(deepLink);
        }
    }

    @Override
    public void onClickLoyaltyButton(@NonNull final String deepLink) {
        new ScoreEvent(resultViewTrack).track();
        getView().processBusinessAction(deepLink);
    }

    @Override
    public void onClickShowAllDiscounts(@NonNull final String deepLink) {
        new SeeAllDiscountsEvent(resultViewTrack).track();
        getView().processBusinessAction(deepLink);
    }
}