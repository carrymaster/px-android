package com.mercadopago.android.px.internal.datasource;

import android.support.annotation.NonNull;
import com.mercadopago.android.px.addons.ESCManagerBehaviour;
import com.mercadopago.android.px.configuration.AdvancedConfiguration;
import com.mercadopago.android.px.configuration.DiscountParamsConfiguration;
import com.mercadopago.android.px.configuration.PaymentConfiguration;
import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.internal.datasource.cache.Cache;
import com.mercadopago.android.px.internal.repository.InitRepository;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.services.CheckoutService;
import com.mercadopago.android.px.internal.util.JsonUtil;
import com.mercadopago.android.px.model.exceptions.ApiException;
import com.mercadopago.android.px.model.internal.CheckoutParams;
import com.mercadopago.android.px.model.internal.InitRequest;
import com.mercadopago.android.px.model.internal.InitResponse;
import com.mercadopago.android.px.preferences.CheckoutPreference;
import com.mercadopago.android.px.services.Callback;
import java.util.ArrayList;

import static com.mercadopago.android.px.services.BuildConfig.API_ENVIRONMENT;
import static com.mercadopago.android.px.services.BuildConfig.API_VERSION;

public class InitService implements InitRepository {

    @NonNull private final ESCManagerBehaviour escManagerBehaviour;
    @NonNull private final CheckoutService checkoutService;
    @NonNull private final String language;
    @NonNull /* default */ final PaymentSettingRepository paymentSettingRepository;
    @NonNull /* default */ final Cache<InitResponse> initCache;

    public InitService(@NonNull final PaymentSettingRepository paymentSettingRepository,
        @NonNull final ESCManagerBehaviour escManagerBehaviour, @NonNull final CheckoutService checkoutService,
        @NonNull final String language, @NonNull final Cache<InitResponse> initCache) {
        this.paymentSettingRepository = paymentSettingRepository;
        this.escManagerBehaviour = escManagerBehaviour;
        this.checkoutService = checkoutService;
        this.language = language;
        this.initCache = initCache;
    }

    @NonNull
    @Override
    public MPCall<InitResponse> init() {
        if (initCache.isCached()) {
            return initCache.get();
        } else {
            return newCall();
        }
    }

    @NonNull
    private MPCall<InitResponse> newCall() {
        return new MPCall<InitResponse>() {

            @Override
            public void enqueue(final Callback<InitResponse> callback) {
                newRequest().enqueue(getInternalCallback(callback));
            }

            @Override
            public void execute(final Callback<InitResponse> callback) {
                newRequest().execute(getInternalCallback(callback));
            }

            @NonNull /* default */ Callback<InitResponse> getInternalCallback(
                final Callback<InitResponse> callback) {
                return new Callback<InitResponse>() {
                    @Override
                    public void success(final InitResponse initResponse) {
                        paymentSettingRepository.configure(initResponse.getCheckoutPreference());
                        paymentSettingRepository.configureSite(initResponse.getSite().getId());
                        initCache.put(initResponse);
                        callback.success(initResponse);
                    }

                    @Override
                    public void failure(final ApiException apiException) {
                        callback.failure(apiException);
                    }
                };
            }
        };
    }

    @NonNull /* default */ MPCall<InitResponse> newRequest() {
        // nullable value
        final CheckoutPreference checkoutPreference = paymentSettingRepository.getCheckoutPreference();
        final PaymentConfiguration paymentConfiguration = paymentSettingRepository.getPaymentConfiguration();

        final AdvancedConfiguration advancedConfiguration = paymentSettingRepository.getAdvancedConfiguration();
        final DiscountParamsConfiguration discountParamsConfiguration =
            advancedConfiguration
                .getDiscountParamsConfiguration();

        final CheckoutParams checkoutParams = new CheckoutParams.Builder()
            .setDiscountParamsConfiguration(discountParamsConfiguration)
            .setCardWithEsc(new ArrayList<>(escManagerBehaviour.getESCCardIds()))
            .setCharges(paymentConfiguration.getCharges())
            .setSupportsSplit(paymentConfiguration.getPaymentProcessor().supportsSplitPayment(checkoutPreference))
            .setSupportsExpress(advancedConfiguration.isExpressPaymentEnabled())
            .setShouldSkipUserConfirmation(paymentSettingRepository.getPaymentConfiguration()
                .getPaymentProcessor().shouldSkipUserConfirmation())
            .setDynamicDialogLocations(advancedConfiguration.getDynamicDialogConfiguration().getSupportedLocations())
            .setDynamicViewLocations(advancedConfiguration.getDynamicFragmentConfiguration().getSupportedLocations())
            .build();

        final InitRequest initRequest = new InitRequest.Builder()
            .setCheckoutPreferenceId(paymentSettingRepository.getCheckoutPreferenceId())
            .setCheckoutPreference(checkoutPreference)
            .setCheckoutParams(checkoutParams)
            .build();

        return checkoutService.init(API_ENVIRONMENT, API_VERSION, language, paymentSettingRepository.getPublicKey(),
            paymentSettingRepository.getPrivateKey(), JsonUtil.getInstance().getMapFromObject(initRequest));
    }
}