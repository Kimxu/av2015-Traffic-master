package com.hinsty.traffic.report;

import android.graphics.drawable.Drawable;

/**
 * @author dz
 * @version 2015/6/15.
 */
public class ReportItemData {

    public Drawable icon;
    public long tx = 0;
    public long rx = 0;

    public String getTx(){
        if(tx>1<<20){
            return String.format("%dM",(int)(tx>>20));
        }else{
            return String.format("%dK",(int)(tx>>10));
        }
    }
    public String getRx(){
        if(rx>1<<20){
            return String.format("%dM",(int)(rx>>20));
        }else{
            return String.format("%dK",(int)(rx>>10));
        }
    }
    public long total(){
        return tx+rx;
    }
    public String getTotal(){
        long total = rx+tx;
        if(total>1<<20){
            return String.format("%dM",(int)(total>>20));
        }else{
            return String.format("%dK",(int)(total>>10));
        }
    }
}
