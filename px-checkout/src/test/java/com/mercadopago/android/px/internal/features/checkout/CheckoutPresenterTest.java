package com.mercadopago.android.px.internal.features.checkout;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.core.SplitPaymentProcessor;
import com.mercadopago.android.px.internal.configuration.InternalConfiguration;
import com.mercadopago.android.px.internal.features.Constants;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentRepository;
import com.mercadopago.android.px.internal.repository.PaymentRewardRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.repository.PluginRepository;
import com.mercadopago.android.px.internal.repository.UserSelectionRepository;
import com.mercadopago.android.px.internal.viewmodel.CheckoutStateModel;
import com.mercadopago.android.px.internal.viewmodel.PaymentModel;
import com.mercadopago.android.px.internal.viewmodel.RecoverPaymentPostPaymentAction;
import com.mercadopago.android.px.mocks.PaymentMethodSearchs;
import com.mercadopago.android.px.mocks.Payments;
import com.mercadopago.android.px.model.BusinessPayment;
import com.mercadopago.android.px.model.Card;
import com.mercadopago.android.px.model.Payment;
import com.mercadopago.android.px.model.PaymentMethod;
import com.mercadopago.android.px.model.PaymentMethodSearch;
import com.mercadopago.android.px.model.PaymentRecovery;
import com.mercadopago.android.px.model.Setting;
import com.mercadopago.android.px.model.Sites;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.exceptions.MercadoPagoError;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.preferences.PaymentPreference;
import com.mercadopago.android.px.utils.StubFailMpCall;
import com.mercadopago.android.px.utils.StubSuccessMpCall;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceOneItem;
import static com.mercadopago.android.px.utils.StubCheckoutPreferenceUtils.stubPreferenceOneItemAndPayer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPresenterTest {

    private static final String DEFAULT_CARD_ID = "260077840";
    private static final String DEBIT_CARD_DEBCABAL = "debcabal";
    public static final int CUSTOM_RESULT_CODE = 1;

    @Mock private Checkout.View checkoutView;
    @Mock private PaymentSettingRepository paymentSettingRepository;
    @Mock private UserSelectionRepository userSelectionRepository;
    @Mock private InitRepository initRepository;
    @Mock private PluginRepository pluginRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentRewardRepository paymentRewardRepository;
    @Mock private InternalConfiguration internalConfiguration;

    private CheckoutPresenter presenter;
    private CheckoutStateModel checkoutStateModel;

    @Before
    public void setUp() {
        checkoutStateModel = new CheckoutStateModel();
        presenter = getPresenter(checkoutStateModel);
    }

    @Test
    public void whenCheckoutInitializedAndPaymentMethodSearchFailsThenShowError() {
        final ApiException apiException = mock(ApiException.class);
        when(initRepository.init()).thenReturn(new StubFailMpCall<>(apiException));

        presenter.initialize();

        verify(checkoutView).showProgress();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showError(any(MercadoPagoError.class));
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenChoHasCompletePrefSetDoNotCallRepositoryToGetPreference() {
        final CheckoutPreference preference = stubPreferenceOneItem();
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(preference);
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getInitResponse()));

        presenter.initialize();

        verifyInitializeWithPreference();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenChoHasPreferenceAndPaymentMethodRetrievedShowPaymentMethodSelection() {
        final CheckoutPreference preference = stubPreferenceOneItemAndPayer();
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(preference);
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getInitResponse()));
        presenter.initialize();

        verify(initRepository).init();
        verifyInitializeWithPreference();
        verifyNoMoreInteractions(checkoutView);
        verifyNoMoreInteractions(initRepository);
    }

    @Test
    public void whenAPaymentMethodIsSelectedThenShowReviewAndConfirmIfPaymentProcessorShouldNotSkipUserConfirmation() {
        final PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
        final SplitPaymentProcessor paymentProcessor = mock(SplitPaymentProcessor.class);

        when(paymentSettingRepository.getPaymentConfiguration()).thenReturn(paymentConfiguration);
        when(paymentConfiguration.getPaymentProcessor()).thenReturn(paymentProcessor);
        when(paymentProcessor.shouldSkipUserConfirmation()).thenReturn(false);

        presenter.onPaymentMethodSelected();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showReviewAndConfirm(false);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenAPaymentMethodIsSelectedThenShowVisualPaymentProcessorIfItShouldSkipShowUserConfirmation() {
        final PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
        final SplitPaymentProcessor paymentProcessor = mock(SplitPaymentProcessor.class);

        when(paymentSettingRepository.getPaymentConfiguration()).thenReturn(paymentConfiguration);
        when(paymentConfiguration.getPaymentProcessor()).thenReturn(paymentProcessor);
        when(paymentProcessor.shouldSkipUserConfirmation()).thenReturn(true);

        presenter.onPaymentMethodSelected();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentProcessorWithAnimation();
        verify(checkoutView, never()).showReviewAndConfirm(anyBoolean());
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenDefaultCardIdValidSelectedThenShowSecurityCode() {
        final InitResponse search = mockPaymentMethodSearchForDriver(true);
        presenter.startFlow(search);
        verify(checkoutView).showSavedCardFlow(any(Card.class));
    }

    @Test
    public void whenDefaultCardIdInvalidSelectedThenShowPaymentVault() {
        final InitResponse search = mockPaymentMethodSearchForDriver(false);
        presenter.startFlow(search);
        verify(checkoutView).showPaymentMethodSelection();
    }

    @Test
    public void whenDefaultCardIdIsNullAndDefaultPaymentTypeIsValidThenShowNewCardFlow() {
        final InitResponse search = mockPaymentMethodSearchForNewCardDriver();
        presenter.startFlow(search);
        verify(checkoutView).showNewCardFlow();
    }

    @Test
    public void whenCardFlowCanceledAndThereIsValidCardThenCancelCheckout() {
        final InitResponse paymentMethodSearch = mockPaymentMethodSearchForDriver(true);
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.onCardFlowCancel();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCardFlowCancelAndDefaultCardIdIsNullAndDefaultPaymentTypeIsValidThenCancelCheckout() {
        final InitResponse paymentMethodSearch = mockPaymentMethodSearchForNewCardDriver();
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.onCardFlowCancel();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCardFlowCanceledAndThereIsInValidCardThenShowPaymentMethodSelection() {
        final InitResponse paymentMethodSearch = mockPaymentMethodSearchForDriver(false);
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(paymentMethodSearch));

        presenter.onCardFlowCancel();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentMethodSelection();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCardFlowCanceledAndPaymentMethodSearchFailsThenShowPaymentMethodSelection() {
        final ApiException apiException = mock(ApiException.class);
        when(initRepository.init()).thenReturn(new StubFailMpCall<>(apiException));

        presenter.onCardFlowCancel();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentMethodSelection();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentMethodCanceledThenCancelCheckout() {
        presenter.onPaymentMethodSelectionCancel();
        verify(checkoutView).cancelCheckout();
    }

    @Test
    public void whenPaymentResultWithCreatedPaymentThenFinishCheckoutWithPaymentResult() {
        final Payment payment = mock(Payment.class);

        when(paymentRepository.hasPayment()).thenReturn(true);
        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onPaymentResultResponse();

        verify(checkoutView).finishWithPaymentResult(payment);
    }

    @Test
    public void whenPaymentResultWithoutCreatedPaymentThenFinishCheckoutWithoutPaymentResult() {
        presenter.onPaymentResultResponse();
        verify(checkoutView).finishWithPaymentResult();
    }

    @Test
    public void whenPaymentIsCanceledBecauseUserWantsToSelectOtherPaymentMethodThenShowPaymentMethodSelection() {
        presenter.onChangePaymentMethod();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).transitionOut();
        verify(checkoutView).showPaymentMethodSelection();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenUserSelectChangePaymentMethodFromPaymentResultAndExitOnIsTrueThenNotShowPaymentMethodSelection() {
        when(paymentRepository.getPayment()).thenReturn(mock(Payment.class));
        when(internalConfiguration.shouldExitOnPaymentMethodChange()).thenReturn(true);

        presenter.onChangePaymentMethod();

        verify(checkoutView).transitionOut();
        verify(checkoutView)
            .finishWithPaymentResult(Constants.RESULT_CHANGE_PAYMENT_METHOD, (Payment) paymentRepository.getPayment());
    }

    @Test
    public void whenUserSelectChangePaymentMethodWithoutPaymentAndExitOnIsTrueThenFinishWithPaymentResultChangePaymentMethodCode() {
        when(internalConfiguration.shouldExitOnPaymentMethodChange()).thenReturn(true);

        presenter.onChangePaymentMethod();

        verify(checkoutView).transitionOut();
        verify(checkoutView).finishWithPaymentResult(Constants.RESULT_CHANGE_PAYMENT_METHOD);
    }

    @Test
    public void whenPaymentNeedsRecoveryFromReviewAndConfirmThenShowReviewAndConfirmAndRecoverPayment() {
        final RecoverPaymentPostPaymentAction action = new RecoverPaymentPostPaymentAction();
        action.execute(presenter);

        verify(checkoutView).showReviewAndConfirmAndRecoverPayment(false, action);
    }

    @Test
    public void whenPaymentNeedsRecoveryFromOneTapThenDoNothing() {
        checkoutStateModel.isExpressCheckout = true;

        final RecoverPaymentPostPaymentAction action = new RecoverPaymentPostPaymentAction();
        action.execute(presenter);

        verify(checkoutView).fetchFonts();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentHasInvalidEscThenStartPaymentRecoveryFlow() {
        final PaymentRecovery paymentRecovery = mock(PaymentRecovery.class);

        presenter.onRecoverPaymentEscInvalid(paymentRecovery);

        verify(checkoutView).startPaymentRecoveryFlow(paymentRecovery);
    }

    @Test
    public void whenOneTapPaymentHasInvalidEscThenStartPaymentRecoveryFlow() {
        checkoutStateModel.isExpressCheckout = true;

        final PaymentRecovery paymentRecovery = mock(PaymentRecovery.class);

        presenter.onRecoverPaymentEscInvalid(paymentRecovery);

        verify(checkoutView).startExpressPaymentRecoveryFlow(paymentRecovery);
    }

    @Test
    public void whenCardFlowResponseHasRecoverableTokenProcessThenCreatePaymentInOneTap() {
        final PaymentRecovery paymentRecovery = mock(PaymentRecovery.class);
        when(paymentRepository.hasPayment()).thenReturn(true);
        when(paymentRepository.createPaymentRecovery()).thenReturn(paymentRecovery);
        when(paymentRecovery.isTokenRecoverable()).thenReturn(true);

        presenter.onCardFlowResponse();

        verify(checkoutView).startPayment();
    }

    @Test
    public void whenCardFlowResponseHasNotRecoverableTokenProcessAndThereIsNoAvailableHooksThenShowReviewAndConfirmIfPaymentProcessorShouldNotSkipUserConfirmation() {
        final PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
        final SplitPaymentProcessor paymentProcessor = mock(SplitPaymentProcessor.class);

        when(paymentRepository.hasPayment()).thenReturn(false);

        when(paymentSettingRepository.getPaymentConfiguration()).thenReturn(paymentConfiguration);
        when(paymentConfiguration.getPaymentProcessor()).thenReturn(paymentProcessor);
        when(paymentProcessor.shouldSkipUserConfirmation()).thenReturn(false);

        presenter.onCardFlowResponse();

        verify(paymentRepository).hasPayment();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showReviewAndConfirm(false);
        verifyNoMoreInteractions(checkoutView);
        verifyNoMoreInteractions(paymentRepository);
    }

    @Test
    public void whenCardFlowResponseHasNotRecoverableTokenProcessAndThereIsNoAvailableHooksThenShowVisualPaymentProcessorIfItShouldSkipUserConfirmation() {
        final PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
        final SplitPaymentProcessor paymentProcessor = mock(SplitPaymentProcessor.class);

        when(paymentRepository.hasPayment()).thenReturn(false);

        when(paymentSettingRepository.getPaymentConfiguration()).thenReturn(paymentConfiguration);
        when(paymentConfiguration.getPaymentProcessor()).thenReturn(paymentProcessor);
        when(paymentProcessor.shouldSkipUserConfirmation()).thenReturn(true);

        presenter.onCardFlowResponse();

        verify(paymentRepository).hasPayment();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentProcessorWithAnimation();
        verify(checkoutView, never()).showReviewAndConfirm(anyBoolean());
        verifyNoMoreInteractions(checkoutView);
        verifyNoMoreInteractions(paymentRepository);
    }

    //Backs
    @Test
    public void whenCheckoutisInitializedAndUserPressesBackThenCancelCheckout() {
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getInitResponse()));

        presenter.initialize();

        verify(checkoutView).showPaymentMethodSelection();

        presenter.onPaymentMethodSelectionCancel();

        verify(checkoutView).cancelCheckout();
    }

    @Test
    public void whenPaymentMethodEditionIsRequestedAndUserPressesBackThenCancelCheckout() {
        final PaymentConfiguration paymentConfiguration = mock(PaymentConfiguration.class);
        final SplitPaymentProcessor paymentProcessor = mock(SplitPaymentProcessor.class);

        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getInitResponse()));
        when(paymentSettingRepository.getPaymentConfiguration()).thenReturn(paymentConfiguration);
        when(paymentConfiguration.getPaymentProcessor()).thenReturn(paymentProcessor);
        when(paymentProcessor.shouldSkipUserConfirmation()).thenReturn(false);

        presenter.initialize();

        verify(checkoutView).showPaymentMethodSelection();

        presenter.onPaymentMethodSelected();
        verify(checkoutView).showReviewAndConfirm(anyBoolean());

        presenter.onChangePaymentMethodFromReviewAndConfirm();
        verify(checkoutView, atLeastOnce()).showPaymentMethodSelection();

        presenter.onPaymentMethodSelectionCancel();
        verify(checkoutView).cancelCheckout();
    }

    @Test
    public void whenReviewAndConfirmCanceledAndOnlyOnePaymentMethodThenCancelCheckout() {
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        when(initRepository.init())
            .thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getPaymentMethodSearchWithOnlyAccountMoneyMLA()));

        presenter.initialize();
        presenter.onReviewAndConfirmCancel();

        verify(checkoutView).cancelCheckout();
    }

    @Test
    public void whenReviewAndConfirmCanceledAndThereIsMoreThanOnePaymentMethodThenShowPaymentMethodSelection() {
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(stubPreferenceOneItem());
        when(initRepository.init()).thenReturn(new StubSuccessMpCall<>(PaymentMethodSearchs.getInitResponse()));

        presenter.initialize();
        presenter.onReviewAndConfirmCancel();

        verifyInitializeWithPreference();
        verify(checkoutView, atLeastOnce()).showPaymentMethodSelection();
        verify(checkoutView).transitionOut();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentSelectionErrorAndPaymentMethodChangeRequestedFromReviewAndConfirmOnBackExitCheckout() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);

        presenter.onChangePaymentMethodFromReviewAndConfirm();
        presenter.onPaymentMethodSelectionError(mercadoPagoError);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).transitionOut();
        verify(checkoutView).showPaymentMethodSelection();
        verify(checkoutView).cancelCheckout(mercadoPagoError);

        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentSelectionErrorAndPaymentMethodChangeNotRequestedFromReviewAndConfirmThenCancelCheckout() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);

        presenter.onPaymentMethodSelectionError(mercadoPagoError);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout(mercadoPagoError);

        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenErrorShownAndInvalidIdentificationThenGoBackToPaymentMethodSelection() {
        final ApiException apiException = Payments.getInvalidIdentificationPayment();
        final MercadoPagoError mpException = new MercadoPagoError(apiException, "");

        presenter.onErrorCancel(mpException);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentMethodSelection();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenErrorShownAndValidIdentificationThenCancelCheckout() {
        final ApiException apiException = mock(ApiException.class);
        final MercadoPagoError mpException = new MercadoPagoError(apiException, "");

        presenter.onErrorCancel(mpException);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenNoDefaultPaymentMethodsAndIsExpressCheckoutThenShowExpressCheckout() {
        final PaymentMethodSearch search = mock(PaymentMethodSearch.class);
        when(search.hasExpressCheckoutMetadata()).thenReturn(true);

        presenter.noDefaultPaymentMethods(search);

        verifyShowOneTap();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCancelExpressCheckoutThenHideProgress() {
        final PaymentMethodSearch search = mock(PaymentMethodSearch.class);
        final CheckoutPreference checkoutPreference = mock(CheckoutPreference.class);

        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(checkoutPreference.getPaymentPreference()).thenReturn(null);
        when(search.hasExpressCheckoutMetadata()).thenReturn(true);

        presenter.startFlow(search);
        presenter.cancelCheckout();

        verifyShowOneTap();
        verify(checkoutView, atLeastOnce()).hideProgress();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCancelRegularCheckoutThenCancelCheckout() {
        presenter.cancelCheckout();

        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentErrorIsPaymentProcessingThenShowPaymentResult() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);
        when(mercadoPagoError.isPaymentProcessing()).thenReturn(true);
        when(paymentRepository.getPaymentDataList()).thenReturn(mock(List.class));
        final CheckoutPreference checkoutPreference = mock(CheckoutPreference.class);
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        when(checkoutPreference.getSite()).thenReturn(Sites.ARGENTINA);
        presenter.onPaymentError(mercadoPagoError);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).hideProgress();
        verify(checkoutView).showPaymentResult(any(PaymentModel.class));
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenPaymentErrorIsInternalServerErrorThenShowError() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);
        when(mercadoPagoError.isInternalServerError()).thenReturn(true);

        presenter.onPaymentError(mercadoPagoError);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).hideProgress();
        verify(checkoutView).showError(any(MercadoPagoError.class));
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenMercadoPagoErrorIsNotInternalServerErrorOrPaymentProcessingThenShowError() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);

        presenter.onPaymentError(mercadoPagoError);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).hideProgress();
        verify(checkoutView).showError(any(MercadoPagoError.class));
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenTerminalErrorThenCancelCheckout() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);
        presenter.onTerminalError(mercadoPagoError);
        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout(mercadoPagoError);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenReviewAndConfirmErrorThenCancelCheckout() {
        final MercadoPagoError mercadoPagoError = mock(MercadoPagoError.class);
        presenter.onReviewAndConfirmError(mercadoPagoError);
        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout(mercadoPagoError);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomReviewAndConfirmResponseThenCancelCheckout() {
        presenter.onCustomReviewAndConfirmResponse(CUSTOM_RESULT_CODE);
        verify(checkoutView).fetchFonts();
        verify(checkoutView).cancelCheckout(eq(CUSTOM_RESULT_CODE), anyBoolean());
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasPaymentThenFinishWithPaymentResult() {
        final Payment payment = mock(Payment.class);
        when(paymentRepository.hasPayment()).thenReturn(true);
        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onCustomPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE, payment);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasNotPaymentThenFinishWithPaymentResult() {
        when(paymentRepository.hasPayment()).thenReturn(false);

        presenter.onCustomPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCustomPaymentResultResponseHasBusinessPaymentThenFinishWithPaymentResult() {
        final BusinessPayment payment = mock(BusinessPayment.class);
        when(paymentRepository.hasPayment()).thenReturn(true);
        when(paymentRepository.getPayment()).thenReturn(payment);

        presenter.onCustomPaymentResultResponse(CUSTOM_RESULT_CODE);

        verify(checkoutView).fetchFonts();
        verify(checkoutView).finishWithPaymentResult(CUSTOM_RESULT_CODE);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenFailureRecoveryNotSetThenShowFailureRecoveryError() {
        presenter.recoverFromFailure();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showFailureRecoveryError();
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenExitWithCodeThenExitCheckout() {
        presenter.exitWithCode(CUSTOM_RESULT_CODE);
        verify(checkoutView).fetchFonts();
        verify(checkoutView).exitCheckout(CUSTOM_RESULT_CODE);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenCvvRequiredThenShowSavedCardFlow() {
        final Card card = mock(Card.class);
        presenter.onCvvRequired(card);
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showSavedCardFlow(card);
        verifyNoMoreInteractions(checkoutView);
    }

    @Test
    public void whenVisualPaymentConfiguredThenShowPaymentProcessor() {
        presenter.onVisualPayment();
        verify(checkoutView).fetchFonts();
        verify(checkoutView).showPaymentProcessor();
        verifyNoMoreInteractions(checkoutView);
    }

// --------- Helper methods ----------- //

    @NonNull
    private CheckoutPresenter getBasePresenter(
        final Checkout.View view, final CheckoutStateModel checkoutStateModel) {

        presenter = new CheckoutPresenter(checkoutStateModel, paymentSettingRepository, userSelectionRepository,
            initRepository, pluginRepository, paymentRepository, paymentRewardRepository, internalConfiguration);

        presenter.attachView(view);
        return presenter;
    }

    @NonNull
    private CheckoutPresenter getPresenter(final CheckoutStateModel checkoutStateModel) {
        return getBasePresenter(checkoutView, checkoutStateModel);
    }

    private void verifyInitializeWithPreference() {
        verify(checkoutView).showProgress();
        verify(checkoutView).fetchFonts();
        verify(checkoutView, atLeastOnce()).showPaymentMethodSelection();
    }

    @NonNull
    private InitResponse mockPaymentMethodSearchForDriver(boolean isValidCard) {
        final InitResponse search = mock(InitResponse.class);
        final PaymentMethod paymentMethod = mock(PaymentMethod.class);
        when(paymentMethod.getPaymentTypeId()).thenReturn("debit_card");
        final ArrayList settingsList = mock(ArrayList.class);
        final Setting setting = mock(Setting.class);
        when(setting.getSecurityCode()).thenReturn(null);
        when(settingsList.get(any(int.class))).thenReturn(setting);
        when(paymentMethod.getSettings()).thenReturn(settingsList);
        when(search.getPaymentMethodById(any(String.class))).thenReturn(paymentMethod);
        if (isValidCard) {
            when(search.getCardById(any(String.class))).thenReturn(new Card());
        } else {
            when(search.getCardById(any(String.class))).thenReturn(null);
        }
        final CheckoutPreference checkoutPreference = mock(CheckoutPreference.class);
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        final PaymentPreference paymentPreference = mock(PaymentPreference.class);
        when(checkoutPreference.getPaymentPreference()).thenReturn(paymentPreference);
        when(paymentSettingRepository.getCheckoutPreference().getPaymentPreference().getDefaultCardId()).thenReturn(
            DEFAULT_CARD_ID);
        when(paymentSettingRepository.getCheckoutPreference().getPaymentPreference().getDefaultPaymentMethodId())
            .thenReturn(DEBIT_CARD_DEBCABAL);
        return search;
    }

    @NonNull
    private InitResponse mockPaymentMethodSearchForNewCardDriver() {
        final InitResponse search = mock(InitResponse.class);
        final CheckoutPreference checkoutPreference = mock(CheckoutPreference.class);
        when(checkoutPreference.getPaymentPreference()).thenReturn(mock(PaymentPreference.class));
        when(checkoutPreference.getPaymentPreference().getDefaultCardId()).thenReturn(null);
        when(checkoutPreference.getPaymentPreference().getDefaultPaymentTypeId()).thenReturn("debit_card");
        when(paymentSettingRepository.getCheckoutPreference()).thenReturn(checkoutPreference);
        return search;
    }

    private void verifyShowOneTap() {
        verify(checkoutView).fetchFonts();
        verify(checkoutView, atLeastOnce()).hideProgress();
        verify(checkoutView).showOneTap();
    }
}