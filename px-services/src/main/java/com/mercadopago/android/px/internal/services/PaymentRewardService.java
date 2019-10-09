package com.mercadopago.android.px.internal.services;

import com.mercadopago.android.px.internal.callbacks.MPCall;
import com.mercadopago.android.px.model.internal.PaymentReward;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PaymentRewardService {

    @GET("/{version}/px_mobile/congrats")
    MPCall<PaymentReward> getPaymentReward(
        @Path(value = "version", encoded = true) String version,
        @Header("Accept-Language") String locale,
        @Query("access_token") String accessToken,
        @Query("payment_ids") String paymentIds,
        @Query("platform") String platform,
        @Query("campaign_id") String campaignId);
}