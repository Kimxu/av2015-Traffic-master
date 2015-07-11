package com.hinsty.traffic.service.object;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.hinsty.traffic.Common;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class SettingData implements Parcelable{

    @Override
    public int describeContents() {
        return SettingData.class.hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(startDay);
        dest.writeInt(dataPlan);
        dest.writeInt(dayAlert);
        dest.writeInt(monthAlert);
        dest.writeBooleanArray(new boolean[]{dayAlertEnable,monthAlertEnable});
        dest.writeInt(collectFrequency.value());
    }

    public static final Parcelable.Creator<SettingData> CREATOR = new Parcelable.Creator<SettingData>() {
        public SettingData createFromParcel(Parcel source) {
            SettingData data = new SettingData();
            data.startDay = source.readInt();
            data.dataPlan = source.readInt();
            data.dayAlert = source.readInt();
            data.monthAlert = source.readInt();
            boolean[] b = new boolean[2];
            source.readBooleanArray(b);
            data.collectFrequency = CollectFrequency.fromValue(source.readInt());
            data.dayAlertEnable = b[0];
            data.monthAlertEnable = b[1];
            return data;
        }

        @Override
        public SettingData[] newArray(int size) {
            return new SettingData[size];
        }
    };

    public static final int NORMAL_VALUE = 5000;
    public static final int SAVE_POWER_VALUE = 10000;
    public static enum CollectFrequency {
        NORMAL(NORMAL_VALUE), SAVE_POWER(SAVE_POWER_VALUE);

        CollectFrequency(int i){
            v=i;
        }
        int v;
        public int value(){
            return v;
        }

        public static CollectFrequency fromValue(int value){
            switch (value){
                case NORMAL_VALUE:
                    return NORMAL;
                case SAVE_POWER_VALUE:
                    return SAVE_POWER;
                default:
                    return null;
            }
        }
        public static CollectFrequency nameOf(String name){
            CollectFrequency c=null;
            try{
                c = CollectFrequency.valueOf(name);
            }catch (Exception e){
                //do nothing
            }
            if(c==null){
                switch (name){
                    case "普通":
                        return NORMAL;
                    case "省电":
                        return SAVE_POWER;
                }
            }
            return c;
        }
    }

    public int startDay = 1;
    public int dataPlan = 0;
    public int dayAlert = 0;
    public int monthAlert = 0;
    public boolean dayAlertEnable = true;
    public boolean monthAlertEnable = true;
    public CollectFrequency collectFrequency = CollectFrequency.NORMAL;

    public SettingData() {}

    public void load(SharedPreferences sp){
        startDay = sp.getInt(Common.SP.KEY_START_DAY, startDay);
        dataPlan = sp.getInt(Common.SP.KEY_DATA_PLAN, dataPlan);
        dayAlert = sp.getInt(Common.SP.KEY_DAY_ALERT_TRAFFIC, dayAlert);
        monthAlert = sp.getInt(Common.SP.KEY_MONTH_ALERT_TRAFFIC, monthAlert);
        dayAlertEnable = sp.getBoolean(Common.SP.KEY_DAY_ALERT_ENABLE, dayAlertEnable);
        monthAlertEnable = sp.getBoolean(Common.SP.KEY_MONTH_ALERT_ENABLE, monthAlertEnable);
        collectFrequency = CollectFrequency.fromValue(sp.getInt(Common.SP.KEY_COLLECT_FREQUENCY,
                CollectFrequency.NORMAL.value()));
    }

    public void save(SharedPreferences sp,SettingData sm){
        startDay = sm.startDay;
        dataPlan = sm.dataPlan;
        dayAlert = sm.dayAlert;
        monthAlert = sm.monthAlert;
        dayAlertEnable = sm.dayAlertEnable;
        monthAlertEnable = sm.dayAlertEnable;
        collectFrequency = sm.collectFrequency;
        sp.edit()
                .putBoolean(Common.SP.KEY_DAY_ALERT_ENABLE,dayAlertEnable)
                .putBoolean(Common.SP.KEY_MONTH_ALERT_ENABLE,monthAlertEnable)
                .putInt(Common.SP.KEY_START_DAY,startDay)
                .putInt(Common.SP.KEY_DATA_PLAN,dataPlan)
                .putInt(Common.SP.KEY_DAY_ALERT_TRAFFIC,dayAlert)
                .putInt(Common.SP.KEY_MONTH_ALERT_TRAFFIC,monthAlert)
                .putInt(Common.SP.KEY_COLLECT_FREQUENCY,collectFrequency.value()).apply();
    }
}
