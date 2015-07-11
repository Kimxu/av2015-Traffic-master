package com.hinsty.traffic;
import com.hinsty.traffic.service.object.TrafficData;
import com.hinsty.traffic.service.object.MainActivityData;
import com.hinsty.traffic.service.object.AppTrafficMap;
import com.hinsty.traffic.service.object.SettingData;

interface IServiceBinder{
    TrafficData getTodayTraffic();
    AppTrafficMap getTodayAppTraffic();
    TrafficData getMonthTraffic();
    MainActivityData getMainData();
    SettingData getSettingData();
    void useFastCheck();
    void cancelFastCheck();
    void updateSetting(in SettingData data);
}