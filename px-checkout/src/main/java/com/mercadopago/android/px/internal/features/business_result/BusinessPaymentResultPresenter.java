package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.mercadopago.android.px.internal.base.BasePresenter;
import com.mercadopago.android.px.internal.repository.PaymentSettingRepository;
import com.mercadopago.android.px.internal.view.ActionDispatcher;
import com.mercadopago.android.px.internal.view.BusinessActions;
import com.mercadopago.android.px.internal.viewmodel.BusinessPaymentModel;
import com.mercadopago.android.px.model.Action;
import com.mercadopago.android.px.model.ExitAction;
import com.mercadopago.android.px.model.internal.PrimaryExitAction;
import com.mercadopago.android.px.model.internal.SecondaryExitAction;
import com.mercadopago.android.px.tracking.internal.events.AbortEvent;
import com.mercadopago.android.px.tracking.internal.events.CrossSellingEvent;
import com.mercadopago.android.px.tracking.internal.events.DiscountItemEvent;
import com.mercadopago.android.px.tracking.internal.events.DownloadAppEvent;
import com.mercadopago.android.px.tracking.internal.events.PrimaryActionEvent;
import com.mercadopago.android.px.tracking.internal.events.ScoreEvent;
import com.mercadopago.android.px.tracking.internal.events.SecondaryActionEvent;
import com.mercadopago.android.px.tracking.internal.events.SeeAllDiscountsEvent;
import com.mercadopago.android.px.tracking.internal.views.ResultViewTrack;

/* default */ class BusinessPaymentResultPresenter extends BasePresenter<BusinessPaymentResultContract.View>
    implements ActionDispatcher, BusinessPaymentResultContract.Presenter, BusinessActions {

    private final BusinessPaymentModel model;
    private final ResultViewTrack viewTracker;

    /* default */ BusinessPaymentResultPresenter(@NonNull final PaymentSettingRepository paymentSettings,
        @NonNull final BusinessPaymentModel model) {
        this.model = model;
        viewTracker = new ResultViewTrack(model, paymentSettings);
    }

    @Override
    public void attachView(final BusinessPaymentResultContract.View view) {
        super.attachView(view);
        mapPaymentModel();
    }

    @Override
    public void onFreshStart() {
        viewTracker.track();
    }

    @Override
    public void onAbort() {
        new AbortEvent(viewTracker).track();
        getView().processCustomExit();
    }

    @Override
    public void dispatch(final Action action) {
        if (action instanceof ExitAction) {
            // Hack for tracking
            if (action instanceof PrimaryExitAction) {
                new PrimaryActionEvent(viewTracker).track();
            } else if (action instanceof SecondaryExitAction) {
                new SecondaryActionEvent(viewTracker).track();
            }
            getView().processCustomExit((ExitAction) action);
        } else {
            throw new UnsupportedOperationException("this Action class can't be executed in this screen");
        }
    }

    private void mapPaymentModel() {
        final BusinessPaymentResultViewModel viewModel = new BusinessPaymentResultMapper().map(model);
        getView().configureViews(viewModel, this);
        getView().setStatusBarColor(viewModel.headerModel.getStatusBarColor());
    }

    @Override
    public void OnClickDownloadAppButton(@NonNull final String deepLink) {
        new DownloadAppEvent(viewTracker).track();
        getView().processBusinessAction(deepLink);
    }

    @Override
    public void OnClickCrossSellingButton(@NonNull final String deepLink) {
        new CrossSellingEvent(viewTracker).track();
        getView().processCrossSellingBusinessAction(deepLink);
    }

    @Override
    public void onClickDiscountItem(final int index, @Nullable final String deepLink, @Nullable final String trackId) {
        new DiscountItemEvent(viewTracker, index, trackId).track();
        if (deepLink != null) {
            getView().processBusinessAction(deepLink);
        }
    }

    @Override
    public void onClickLoyaltyButton(@NonNull final String deepLink) {
        new ScoreEvent(viewTracker).track();
        getView().processBusinessAction(deepLink);
    }

    @Override
    public void onClickShowAllDiscounts(@NonNull final String deepLink) {
        new SeeAllDiscountsEvent(viewTracker).track();
        getView().processBusinessAction(deepLink);
    }
}