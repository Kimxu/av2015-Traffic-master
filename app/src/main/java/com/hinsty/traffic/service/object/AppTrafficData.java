package com.hinsty.traffic.service.object;

import android.content.SharedPreferences;

/**
 * @author dz
 * @version 2015/6/27.
 */
public class AppTrafficData {
    public int uid;
    public TrafficData realTraffic;
    public TrafficData subTraffic;
    public AppTrafficData(){
        uid=0;
        realTraffic=new TrafficData();
        subTraffic = new TrafficData();
    }
    public AppTrafficData(int uid,long rx,long tx){
        this.uid=uid;
        this.realTraffic = new TrafficData();
        this.realTraffic.rx=rx;
        this.realTraffic.tx=tx;
        subTraffic = new TrafficData();
    }
    public void loadSubTraffic(SharedPreferences sp){
        subTraffic.rx = sp.getLong("rx"+uid,0L);
        subTraffic.tx = sp.getLong("tx"+uid,0L);
    }

    public void saveSubTraffic(SharedPreferences.Editor editor){
        editor.putLong("tx"+uid,subTraffic.tx);
        editor.putLong("rx"+uid,subTraffic.rx);
    }
}
