package com.mercadopago.android.px.internal.features.review_and_confirm;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.addons.model.SecurityValidationData;
import com.mercadopago.android.px.configuration.DynamicDialogConfiguration;
import com.mercadopago.android.px.core.DynamicDialogCreator;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.callbacks.FailureRecovery;
import com.mercadopago.android.px.internal.core.ProductIdProvider;
import com.mercadopago.android.px.internal.features.explode.ExplodeDecoratorMapper;
import com.mercadopago.android.px.internal.repository.DiscountRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.util.SecurityValidationDataFactory;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PayButtonViewModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.PostPaymentAction;
import com.mercadopago.android.px.internal.viewmodel.mappers.PayButtonViewModelMapper;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.IPaymentDescriptor;
import com.mercadopago.android.px.model.IPaymentDescriptorHandler;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.PaymentResult;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.PaymentReward;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.tracking.internal.events.ChangePaymentMethodEvent;
import com.mercadopago.android.px.tracking.internal.events.ConfirmEvent;
import com.mercadopago.android.px.tracking.internal.events.FrictionEventTracker;
import com.mercadopago.android.px.tracking.internal.views.ReviewAndConfirmViewTracker;
import java.util.Set;

/* default */ final class ReviewAndConfirmPresenter extends BasePresenter<ReviewAndConfirm.View>
    implements ReviewAndConfirm.Action {

    @NonNull private final PaymentSettingRepository paymentSettings;
    @NonNull private final UserSelectionRepository userSelectionRepository;
    @NonNull private final PaymentRewardRepository paymentRewardRepository;
    @NonNull private final ProductIdProvider productIdProvider;
    @NonNull /* default */ final PaymentRepository paymentRepository;
    private final ExplodeDecoratorMapper explodeDecoratorMapper;
    private final ReviewAndConfirmViewTracker reviewAndConfirmViewTracker;
    private final ConfirmEvent confirmEvent;
    private final PayButtonViewModel payButtonViewModel;
    private FailureRecovery recovery;

    /* default */ ReviewAndConfirmPresenter(@NonNull final PaymentRepository paymentRepository,
        @NonNull final DiscountRepository discountRepository,
        @NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final UserSelectionRepository userSelectionRepository,
        @NonNull final PaymentRewardRepository paymentRewardRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour,
        @NonNull final ProductIdProvider productIdProvider) {
        this.paymentRepository = paymentRepository;
        this.paymentSettings = paymentSettings;
        this.userSelectionRepository = userSelectionRepository;
        this.paymentRewardRepository = paymentRewardRepository;
        this.productIdProvider = productIdProvider;

        final Set<String> escCardIds = escManagerBehaviour.getESCCardIds();
        explodeDecoratorMapper = new ExplodeDecoratorMapper();
        reviewAndConfirmViewTracker =
            new ReviewAndConfirmViewTracker(escCardIds, userSelectionRepository, paymentSettings,
                discountRepository.getCurrentConfiguration());
        confirmEvent = ConfirmEvent.from(escCardIds, userSelectionRepository);
        payButtonViewModel = new PayButtonViewModelMapper().map(
            paymentSettings.getAdvancedConfiguration().getCustomStringConfiguration());
    }

    @Override
    public void attachView(final ReviewAndConfirm.View view) {
        super.attachView(view);
        paymentRepository.attach(this);
        view.setPayButtonText(payButtonViewModel);
    }

    @Override
    public void onViewResumed(final ReviewAndConfirm.View view) {
        attachView(view);
        setCurrentViewTracker(reviewAndConfirmViewTracker);
        resolveDynamicDialog(DynamicDialogConfiguration.DialogLocation.ENTER_REVIEW_AND_CONFIRM);
    }

    @Override
    public void hasFinishPaymentAnimation() {
        final IPaymentDescriptor payment = paymentRepository.getPayment();
        paymentRewardRepository.getPaymentReward(payment, paymentRepository.createPaymentResult(payment),
            this::handleResult);
    }

    private void handleResult(@NonNull final IPaymentDescriptor payment, @NonNull final PaymentResult paymentResult,
        @NonNull final PaymentReward paymentReward) {
        final String currencyId = paymentSettings.getSite().getCurrencyId();
        payment.process(new IPaymentDescriptorHandler() {
            @Override
            public void visit(@NonNull final IPaymentDescriptor payment) {
                final PaymentModel paymentModel =
                    new PaymentModel(payment, paymentResult, paymentReward, currencyId);
                getView().showResult(paymentModel);
            }

            @Override
            public void visit(@NonNull final BusinessPayment businessPayment) {
                final BusinessPaymentModel paymentModel =
                    new BusinessPaymentModel((BusinessPayment) payment, paymentResult, paymentReward, currencyId);
                getView().showResult(paymentModel);
            }
        });
    }

    @Override
    public void changePaymentMethod() {
        ChangePaymentMethodEvent.create().track();
        removeUserSelection();
        getView().finishAndChangePaymentMethod();
    }

    private void resolveDynamicDialog(@NonNull final DynamicDialogConfiguration.DialogLocation location) {
        final CheckoutPreference checkoutPreference = paymentSettings.getCheckoutPreference();
        final DynamicDialogConfiguration dynamicDialogConfiguration =
            paymentSettings.getAdvancedConfiguration().getDynamicDialogConfiguration();
        final DynamicDialogCreator.CheckoutData checkoutData =
            new DynamicDialogCreator.CheckoutData(checkoutPreference, paymentRepository.getPaymentDataList());
        if (dynamicDialogConfiguration.hasCreatorFor(location)) {
            getView().showDynamicDialog(dynamicDialogConfiguration.getCreatorFor(location), checkoutData);
        }
    }

    @Override
    public void detachView() {
        paymentRepository.detach(this);
        super.detachView();
    }

    @Override
    public void startSecuredPayment() {
        final SecurityValidationData data =
            SecurityValidationDataFactory.create(productIdProvider, paymentSettings, userSelectionRepository);
        getView().startSecurityValidation(data);
    }

    @Override
    public void onPaymentConfirm() {
        confirmEvent.track();
        pay();
    }

    @Override
    public void trackSecurityFriction() {
        // TODO Review ID
        FrictionEventTracker
            .with(ReviewAndConfirmViewTracker.PATH, FrictionEventTracker.Id.GENERIC,
                FrictionEventTracker.Style.CUSTOM_COMPONENT).track();
    }

    /* default */ void pay() {
        if (paymentRepository.isExplodingAnimationCompatible()) {
            getView().startLoadingButton(paymentRepository.getPaymentTimeout(), payButtonViewModel);
            getView().hideConfirmButton();
        }
        // TODO improve: This was added because onetap can detach this listener on its OnDestroy
        paymentRepository.attach(this);
        paymentRepository.startPayment();
    }

    @Override
    public void onCardFlowResponse() {
        getView().cancelLoadingButton();
        getView().showConfirmButton();
        pay();
    }

    @Override
    public void onCardFlowCancel() {
        // TODO do nothing
        // TODO check if it's needed
        // TODO Reset UI - Exploading button
    }

    @Override
    public void onError(@NonNull final MercadoPagoError mercadoPagoError) {
        getView().cancelLoadingButton();
        getView().showConfirmButton();
        getView().cancelCheckoutAndInformError(mercadoPagoError);
    }

    @Override
    public void onCvvRequired(@NonNull final Card card) {
        getView().cancelLoadingButton();
        getView().showConfirmButton();
        getView().showCardCVVRequired(card);
    }

    @Override
    public void onVisualPayment() {
        getView().cancelLoadingButton();
        getView().showConfirmButton();
        getView().showPaymentProcessor();
    }

    @Override
    public void onRecoverPaymentEscInvalid(final PaymentRecovery recovery) {
        getView().cancelLoadingButton();
        getView().showConfirmButton();
        getView().startPaymentRecoveryFlow(recovery);
    }

    @Override
    public void onPaymentFinished(@NonNull final IPaymentDescriptor payment) {
        getView().hideConfirmButton();
        getView().finishLoading(explodeDecoratorMapper.map(payment));
    }

    @Override
    public void onPaymentError(@NonNull final MercadoPagoError error) {
        getView().cancelLoadingButton();
        getView().showConfirmButton();

        recovery = this::pay;
        if (error.isPaymentProcessing()) {
            final String currencyId = paymentSettings.getSite().getCurrencyId();
            final PaymentResult paymentResult =
                new PaymentResult.Builder()
                    .setPaymentData(paymentRepository.getPaymentDataList())
                    .setPaymentStatus(Payment.StatusCodes.STATUS_IN_PROCESS)
                    .setPaymentStatusDetail(Payment.StatusDetail.STATUS_DETAIL_PENDING_CONTINGENCY)
                    .build();
            final PaymentModel paymentModel = new PaymentModel(null, paymentResult, PaymentReward.EMPTY, currencyId);
            getView().showResult(paymentModel);
        } else if (error.isInternalServerError() || error.isNoConnectivityError()) {
            getView().showErrorSnackBar(error);
        } else {
            getView().showErrorScreen(error);
        }
    }

    @Override
    public void recoverFromFailure() {
        if (recovery != null) {
            recovery.recover();
        }
    }

    @Override
    public void executePostPaymentAction(@NonNull final PostPaymentAction postPaymentAction) {
        postPaymentAction.execute(new PostPaymentAction.ActionController() {
            @Override
            public void recoverPayment(@NonNull final PostPaymentAction postPaymentAction) {
                getView().startPaymentRecoveryFlow(paymentRepository.createPaymentRecovery());
            }

            @Override
            public void onChangePaymentMethod() {
                // Do nothing, It is handled before.
            }
        });
    }

    public void onPayerInformationResponse() {
        getView().reloadBody();
    }

    public void removeUserSelection() {
        userSelectionRepository.reset();
    }
}