package com.mercadopago.android.px.internal.features.business_result;

import android.support.annotation.NonNull;
import com.mercadolibre.android.mlbusinesscomponents.common.MLBusinessSingleItem;
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppData;
import com.mercadolibre.android.mlbusinesscomponents.components.common.downloadapp.MLBusinessDownloadAppView;
import com.mercadolibre.android.mlbusinesscomponents.components.crossselling.MLBusinessCrossSellingBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.discount.MLBusinessDiscountBoxData;
import com.mercadolibre.android.mlbusinesscomponents.components.loyalty.MLBusinessLoyaltyRingData;
import com.mercadopago.android.px.internal.viewmodel.mappers.Mapper;
import com.mercadopago.android.px.model.internal.PaymentReward;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;

public class PaymentRewardMapper extends Mapper<PaymentReward, PaymentRewardViewModel> {

    @Override
    public PaymentRewardViewModel map(@NonNull final PaymentReward paymentReward) {
        final PaymentReward.Discount discount = paymentReward.getDiscount();
        return new PaymentRewardViewModel(getLoyaltyData(paymentReward.getScore()),
            getDiscountBoxData(discount), getShowAllDiscount(discount), getDownloadAppData(discount),
            getCrossSellingBoxData(paymentReward.getCrossSellings()));
    }

    @Nullable
    private MLBusinessLoyaltyRingData getLoyaltyData(@Nullable final PaymentReward.Score score) {

        if (score == null) {
            return null;
        }

        final PaymentReward.Score.Progress progress = score.getProgress();
        final PaymentReward.Action action = score.getAction();

        return new MLBusinessLoyaltyRingData() {
            @Override
            public String getRingHexaColor() {
                return progress.getColor();
            }

            @Override
            public int getRingNumber() {
                return progress.getLevel();
            }

            @Override
            public float getRingPercentage() {
                return progress.getPercentage();
            }

            @Override
            public String getTitle() {
                return score.getTitle();
            }

            @Override
            public String getButtonTitle() {
                return action.getLabel();
            }

            @Override
            public String getButtonDeepLink() {
                return action.getTarget();
            }
        };
    }

    @Nullable
    private MLBusinessDiscountBoxData getDiscountBoxData(
        @Nullable final PaymentReward.Discount discount) {

        if (discount == null) {
            return null;
        }

        return new MLBusinessDiscountBoxData() {
            @Nullable
            @Override
            public String getTitle() {
                return discount.getTitle();
            }

            @Nullable
            @Override
            public String getSubtitle() {
                return discount.getSubtitle();
            }

            @NonNull
            @Override
            public List<MLBusinessSingleItem> getItems() {
                return getDisCountItems(discount.getItems());
            }
        };
    }

    @NonNull
    private List<MLBusinessSingleItem> getDisCountItems(
        @NonNull List<PaymentReward.Discount.Item> items) {

        List<MLBusinessSingleItem> singleItems = new LinkedList<>();

        for (PaymentReward.Discount.Item item : items) {
            singleItems.add(new MLBusinessSingleItem() {
                @Override
                public String getImageUrl() {
                    return item.getIcon();
                }

                @Override
                public String getTitleLabel() {
                    return item.getTitle();
                }

                @Override
                public String getSubtitleLabel() {
                    return item.getSubtitle();
                }

                @Nullable
                @Override
                public String getDeepLinkItem() {
                    return item.getTarget();
                }

                @Nullable
                @Override
                public String getTrackId() {
                    return item.getCampaignId();
                }
            });
        }
        return singleItems;
    }

    @Nullable
    private PaymentReward.Action getShowAllDiscount(@Nullable final PaymentReward.Discount discount) {
        final PaymentReward.Action showAllDiscount;
        if (discount == null || (showAllDiscount = discount.getAction()) == null) {
            return null;
        }

        return showAllDiscount;
    }

    @Nullable
    private MLBusinessDownloadAppData getDownloadAppData(@Nullable final PaymentReward.Discount discount) {
        final PaymentReward.Discount.DownloadApp downloadApp;
        if (discount == null || (downloadApp = discount.getActionDownload()) == null) {
            return null;
        }

        return new MLBusinessDownloadAppData() {
            @NonNull
            @Override
            public MLBusinessDownloadAppView.AppSite getAppSite() {

                //TODO: Logica para saber en que app estoy.
                return MLBusinessDownloadAppView.AppSite.MP;
            }

            @NonNull
            @Override
            public String getTitle() {
                return downloadApp.getTitle();
            }

            @NonNull
            @Override
            public String getButtonTitle() {
                return downloadApp.getAction().getLabel();
            }

            @NonNull
            @Override
            public String getButtonDeepLink() {
                return downloadApp.getAction().getTarget();
            }
        };
    }

    @NonNull
    private List<MLBusinessCrossSellingBoxData> getCrossSellingBoxData(
        List<PaymentReward.CrossSelling> crossSellingList) {

        final List<MLBusinessCrossSellingBoxData> crossSellingBoxData = new LinkedList<>();

        for (PaymentReward.CrossSelling crossSellingItem : crossSellingList) {

            PaymentReward.Action action = crossSellingItem.getAction();
            crossSellingBoxData.add(new MLBusinessCrossSellingBoxData() {
                @NonNull
                @Override
                public String getIconUrl() {
                    return crossSellingItem.getIcon();
                }

                @NonNull
                @Override
                public String getText() {
                    return crossSellingItem.getTitle();
                }

                @NonNull
                @Override
                public String getButtonTitle() {
                    return action.getLabel();
                }

                @NonNull
                @Override
                public String getButtonDeepLink() {
                    return action.getTarget();
                }
            });
        }

        return crossSellingBoxData;
    }
}
