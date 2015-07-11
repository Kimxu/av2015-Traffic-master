package com.hinsty.traffic;

/**
 * @author dz
 * @version 2015/6/16.
 */
public interface Common {

    public static final String DB_FILE_NAME = "traffic.db";

    static interface SP {
        //setting
        public static final String FILE_NAME = "data";
        public static final String KEY_DATA_PLAN = "mDataPlanView";
        public static final String KEY_DAY_ALERT_TRAFFIC = "day_alert_traffic";
        public static final String KEY_DAY_ALERT_ENABLE = "day_alert_traffic_enable";
        public static final String KEY_MONTH_ALERT_ENABLE = "month_overflow_limit";
        public static final String KEY_MONTH_ALERT_TRAFFIC = "alert_traffic";
        public static final String KEY_START_DAY = "start_day";
        public static final String KEY_COLLECT_FREQUENCY = "collect_frequency";

        //traffic data
        static final String KEY_SUB = "sub";
        static final String KEY_BOOT_TIME = "boot_time";
        static final String KEY_DAY = "day";
        static final String KEY_DAY_ALERT_WORKED = "day_alert_worked";
        static final String KEY_MONTH_ALERT_WORkED = "month_alert_worked";
        static final String KEY_DATA_PLAN_ALERT_WORKED = "data_plan_worked";

        static final String KEY_FIRST_BOOT = "first_boot";

    }
    static final String ACTION_UI_NEED_UPDATE = "com.hinsty.traffic.action.ui_update";

    static final String MONTH = "month";
    static final String YEAR = "year";
    static final String DAY = "day";

}
