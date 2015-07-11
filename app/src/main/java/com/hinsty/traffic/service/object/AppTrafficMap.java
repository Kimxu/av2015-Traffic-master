package com.hinsty.traffic.service.object;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class AppTrafficMap implements Parcelable {

    public Map<String,TrafficData> map;

    public AppTrafficMap(Map<String,TrafficData> map) {
        this.map = map;
    }

    public AppTrafficMap(){
        map =new HashMap<>();}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(map.size());
        for(Map.Entry<String,TrafficData> entry: map.entrySet()){
            dest.writeString(entry.getKey());
            entry.getValue().writeToParcel(dest,flags);
        }
    }

    public static final Parcelable.Creator<AppTrafficMap> CREATOR = new Parcelable.Creator<AppTrafficMap>() {

        @Override
        public AppTrafficMap createFromParcel(Parcel source) {
            AppTrafficMap data = new AppTrafficMap();
            int size = source.readInt();
            data.map =new HashMap<>(size);
            while(--size>=0){
                String key = source.readString();
                TrafficData value = TrafficData.CREATOR.createFromParcel(source);
                data.map.put(key,value);
            }
            return data;
        }

        @Override
        public AppTrafficMap[] newArray(int size) {
            return new AppTrafficMap[size];
        }
    };
}
