package com.mercadopago.android.px.model.internal;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.google.gson.annotations.SerializedName;
import java.util.Collections;
import java.util.List;

public final class PaymentReward implements Parcelable {

    public static final PaymentReward EMPTY = new PaymentReward();

    public static final Creator<PaymentReward> CREATOR = new Creator<PaymentReward>() {
        @Override
        public PaymentReward createFromParcel(final Parcel in) {
            return new PaymentReward(in);
        }

        @Override
        public PaymentReward[] newArray(final int size) {
            return new PaymentReward[size];
        }
    };

    @SerializedName("mpuntos")
    @Nullable private final Score score;
    @SerializedName("discounts")
    @Nullable private final Discount discount;
    @SerializedName("cross_selling")
    private final List<CrossSelling> crossSellings;

    private PaymentReward() {
        score = null;
        discount = null;
        crossSellings = Collections.emptyList();
    }

    /* default */ PaymentReward(final Parcel in) {
        score = in.readParcelable(Score.class.getClassLoader());
        discount = in.readParcelable(Discount.class.getClassLoader());
        crossSellings = in.createTypedArrayList(CrossSelling.CREATOR);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeParcelable(score, flags);
        dest.writeParcelable(discount, flags);
        dest.writeTypedList(crossSellings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Nullable
    public Score getScore() {
        return score;
    }

    @Nullable
    public Discount getDiscount() {
        return discount;
    }

    @NonNull
    public List<CrossSelling> getCrossSellings() {
        return crossSellings != null ? crossSellings : Collections.emptyList();
    }

    /* default */public static final class Score implements Parcelable {

        public static final Creator<Score> CREATOR = new Creator<Score>() {
            @Override
            public Score createFromParcel(final Parcel in) {
                return new Score(in);
            }

            @Override
            public Score[] newArray(final int size) {
                return new Score[size];
            }
        };

        private final Progress progress;
        private final String title;
        private final Action action;

        /* default */ Score(final Parcel in) {
            progress = in.readParcelable(Progress.class.getClassLoader());
            title = in.readString();
            action = in.readParcelable(Action.class.getClassLoader());
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeParcelable(progress, flags);
            dest.writeString(title);
            dest.writeParcelable(action, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /* default */ public static final class Progress implements Parcelable {

            public static final Creator<Progress> CREATOR = new Creator<Progress>() {
                @Override
                public Progress createFromParcel(final Parcel in) {
                    return new Progress(in);
                }

                @Override
                public Progress[] newArray(final int size) {
                    return new Progress[size];
                }
            };

            private final float percentage;
            @SerializedName("level_color")
            private final String color;
            @SerializedName("level_number")
            private final int level;

            /* default */ Progress(final Parcel in) {
                percentage = in.readFloat();
                color = in.readString();
                level = in.readInt();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeFloat(percentage);
                dest.writeString(color);
                dest.writeInt(level);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public float getPercentage() {
                return percentage;
            }

            public String getColor() {
                return color;
            }

            public int getLevel() {
                return level;
            }
        }

        public Progress getProgress() {
            return progress;
        }

        public String getTitle() {
            return title;
        }

        public Action getAction() {
            return action;
        }
    }

    /* default */ public static final class Discount implements Parcelable {

        public static final Creator<Discount> CREATOR = new Creator<Discount>() {
            @Override
            public Discount createFromParcel(final Parcel in) {
                return new Discount(in);
            }

            @Override
            public Discount[] newArray(final int size) {
                return new Discount[size];
            }
        };

        private final String title;
        private final String subtitle;
        private final Action action;
        @SerializedName("action_download")
        private final DownloadApp actionDownload;
        private final List<Item> items;

        /* default */ Discount(final Parcel in) {
            title = in.readString();
            subtitle = in.readString();
            action = in.readParcelable(Action.class.getClassLoader());
            actionDownload = in.readParcelable(DownloadApp.class.getClassLoader());
            items = in.createTypedArrayList(Item.CREATOR);
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(title);
            dest.writeString(subtitle);
            dest.writeParcelable(action, flags);
            dest.writeParcelable(actionDownload, flags);
            dest.writeTypedList(items);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        /* default */ public static final class DownloadApp implements Parcelable {

            public static final Creator<DownloadApp> CREATOR = new Creator<DownloadApp>() {
                @Override
                public DownloadApp createFromParcel(Parcel in) {
                    return new DownloadApp(in);
                }

                @Override
                public DownloadApp[] newArray(int size) {
                    return new DownloadApp[size];
                }
            };

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeString(title);
                dest.writeParcelable(action, flags);
            }

            /* default */ DownloadApp(Parcel in) {
                title = in.readString();
                action = in.readParcelable(Action.class.getClassLoader());
            }

            private final String title;
            private final Action action;

            public String getTitle() {
                return title;
            }

            public Action getAction() {
                return action;
            }
        }

        /* default */ public static final class Item implements Parcelable {

            public static final Creator<Item> CREATOR = new Creator<Item>() {
                @Override
                public Item createFromParcel(final Parcel in) {
                    return new Item(in);
                }

                @Override
                public Item[] newArray(final int size) {
                    return new Item[size];
                }
            };

            private final String title;
            private final String subtitle;
            private final String icon;
            private final String target;
            private final String campaignId;

            /* default */ Item(final Parcel in) {
                title = in.readString();
                subtitle = in.readString();
                icon = in.readString();
                target = in.readString();
                campaignId = in.readString();
            }

            @Override
            public void writeToParcel(final Parcel dest, final int flags) {
                dest.writeString(title);
                dest.writeString(subtitle);
                dest.writeString(icon);
                dest.writeString(target);
                dest.writeString(campaignId);
            }

            @Override
            public int describeContents() {
                return 0;
            }

            public String getTitle() {
                return title;
            }

            public String getSubtitle() {
                return subtitle;
            }

            public String getIcon() {
                return icon;
            }

            public String getTarget() {
                return target;
            }

            public String getCampaignId() {
                return campaignId;
            }
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }

        public Action getAction() {
            return action;
        }

        public DownloadApp getActionDownload() {
            return actionDownload;
        }

        @NonNull
        public List<Item> getItems() {
            return items != null ? items : Collections.emptyList();
        }
    }

    /* default */ public static final class Action implements Parcelable {

        public static final Creator<Action> CREATOR = new Creator<Action>() {
            @Override
            public Action createFromParcel(final Parcel in) {
                return new Action(in);
            }

            @Override
            public Action[] newArray(final int size) {
                return new Action[size];
            }
        };

        private final String label;
        private final String target;

        /* default */ Action(final Parcel in) {
            label = in.readString();
            target = in.readString();
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(label);
            dest.writeString(target);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getLabel() {
            return label;
        }

        public String getTarget() {
            return target;
        }
    }

    /* default */ public static final class CrossSelling implements Parcelable {

        public static final Creator<CrossSelling> CREATOR = new Creator<CrossSelling>() {
            @Override
            public CrossSelling createFromParcel(final Parcel in) {
                return new CrossSelling(in);
            }

            @Override
            public CrossSelling[] newArray(final int size) {
                return new CrossSelling[size];
            }
        };

        private final String title;
        private final String icon;
        private final Action action;
        private final String contentId;

        /* default */ CrossSelling(final Parcel in) {
            title = in.readString();
            icon = in.readString();
            action = in.readParcelable(Action.class.getClassLoader());
            contentId = in.readString();
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeString(title);
            dest.writeString(icon);
            dest.writeParcelable(action, flags);
            dest.writeString(contentId);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public String getTitle() {
            return title;
        }

        public String getIcon() {
            return icon;
        }

        public Action getAction() {
            return action;
        }

        public String getContentId() {
            return contentId;
        }
    }
}