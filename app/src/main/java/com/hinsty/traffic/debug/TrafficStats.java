package com.hinsty.traffic.debug;

/**
 * @author dz
 * @version 2015/6/20.
 */
public class TrafficStats {
    public static long rx=0,tx=0;
    public static long getMobileRxBytes(){return rx;}
    public static long getMobileTxBytes(){return tx;}
    public static void setRT(long r,long t){
        rx = r;
        tx = t;
    }
}
