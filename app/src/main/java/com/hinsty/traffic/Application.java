package com.hinsty.traffic;

import com.hinsty.traffic.dao.DaoMaster;
import com.hinsty.traffic.dao.DaoSession;

/**
 * @author dz
 * @version 2015/6/28.
 */
public class Application extends android.app.Application {
    DaoMaster daoMaster;

    @Override
    public void onCreate() {
        daoMaster = new DaoMaster(new DaoMaster.DevOpenHelper(this,Common.DB_FILE_NAME,null).getWritableDatabase());
        super.onCreate();
    }

    public DaoSession daoSession(){
        return daoMaster.newSession();
    }
}
