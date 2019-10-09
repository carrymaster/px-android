package com.mercadopago.android.px.model;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;

public class AccountMoneyDisplayInfo implements Serializable, Parcelable {

    public String message;

    public String sliderTitle;

    protected AccountMoneyDisplayInfo(Parcel in) {
        message = in.readString();
        sliderTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(message);
        dest.writeString(sliderTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AccountMoneyDisplayInfo> CREATORA = new Creator<AccountMoneyDisplayInfo>() {
        @Override
        public AccountMoneyDisplayInfo createFromParcel(Parcel in) {
            return new AccountMoneyDisplayInfo(in);
        }

        @Override
        public AccountMoneyDisplayInfo[] newArray(int size) {
            return new AccountMoneyDisplayInfo[size];
        }
    };
}
