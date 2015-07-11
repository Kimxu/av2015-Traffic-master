package com.hinsty.traffic.service.object;

import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.hinsty.traffic.dao.Traffic;

import java.text.DecimalFormat;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class TrafficData implements Parcelable {
    public long tx ;
    public long rx ;

    public TrafficData(){
        tx=0;
        rx=0;
    }

    public TrafficData(long rx,long tx){
        this.tx=tx;
        this.rx=rx;
    }

    public void saveToSP(SharedPreferences.Editor editor,String name){
        editor.putLong(name+"_RX",rx).putLong(name+"_Tx",tx);
    }
    public TrafficData loadFromSP(SharedPreferences sp,String name,TrafficData t){
        rx = sp.getLong(name+"_RX",t.rx);
        tx = sp.getLong(name+"_TX",t.tx);
        return this;
    }

    public TrafficData loadFromSP(SharedPreferences sp,String name){
        rx = sp.getLong(name+"_RX",0);
        tx = sp.getLong(name+"_TX",0);
        return this;
    }

    public TrafficData set(long rx,long tx){
        this.rx=rx;
        this.tx=tx;
        return this;
    }
    public TrafficData set(TrafficData data){
        this.rx=data.rx;
        this.tx=data.tx;
        return this;
    }
    public TrafficData add(long rx,long tx){
        this.rx+=rx;
        this.tx+=tx;
        return this;
    }
    public TrafficData add(TrafficData data){
        tx+=data.tx;
        rx+=data.rx;
        return this;
    }
    public TrafficData add(Traffic t){
        tx+=t.getTx();
        rx+=t.getRx();
        return this;
    }

    public TrafficData sub(TrafficData data){
        rx-=data.rx;
        tx-=data.tx;
        return this;
    }
    public long sum(){
        return tx+rx;
    }

    public static String formatByte(long b){

//        if(b>1L<<20){
//            return String.format("%dM",(1f*(b>>17)/8));
//        }else if(b>1L<<10){
//            return String.format("%dK",b>>10);
//        }else{
//            return String.format("%dB",b);
        DecimalFormat formater = new DecimalFormat("#.#");
        double size1 = (double) b;
        if (size1 < 1024 * 1024) {
            double kSize = (size1 / 1024);
            return formater.format(kSize) + "KB";
        } else if (size1 < 1024 * 1024 * 1024) {
            double mSize = size1 / (1024 * 1024);
            return formater.format(mSize) + "MB";
        } else {
            double gSize = size1 / (1024 * 1024 * 1024);
            return formater.format(gSize) + "GB";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(rx);
        dest.writeLong(tx);
    }

    @Override
    public String toString() {
        return "[rx "+tx+" tx "+tx+"]";
    }

    public static final Parcelable.Creator<TrafficData> CREATOR = new Parcelable.Creator<TrafficData>() {

        @Override
        public TrafficData createFromParcel(Parcel source) {
            TrafficData data = new TrafficData();
            data.rx = source.readLong();
            data.tx = source.readLong();
            return data;
        }

        @Override
        public TrafficData[] newArray(int size) {
            return new TrafficData[size];
        }
    };
}
