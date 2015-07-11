package com.hinsty.traffic.service.object;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class MainActivityData implements Parcelable {
    public int dataPlan;
    public int balanceDay;
    public String todayTraffic;
    public String monthTraffic;
    public int userPercent=-1;
    @Override
    public int describeContents() {
        return getClass().hashCode();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(dataPlan);
        dest.writeInt(balanceDay);
        dest.writeString(todayTraffic);
        dest.writeString(monthTraffic);
        dest.writeInt(userPercent);
    }

    public static final Parcelable.Creator<MainActivityData> CREATOR = new Parcelable.Creator<MainActivityData>() {

        @Override
        public MainActivityData createFromParcel(Parcel source) {
            MainActivityData data = new MainActivityData();
            data.dataPlan = source.readInt();
            data.balanceDay = source.readInt();
            data.todayTraffic = source.readString();
            data.monthTraffic = source.readString();
            data.userPercent = source.readInt();
            return data;
        }

        @Override
        public MainActivityData[] newArray(int size) {
            return new MainActivityData[size];
        }
    };

}
