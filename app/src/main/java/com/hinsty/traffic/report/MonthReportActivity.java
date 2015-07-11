package com.hinsty.traffic.report;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import com.hinsty.traffic.Common;
import com.hinsty.traffic.R;
import com.hinsty.traffic.dao.AppDayTraffic;
import com.hinsty.traffic.dao.AppDayTrafficDao;

import java.util.Calendar;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * @author dz
 * @version 2015/6/24.
 */
public class MonthReportActivity extends ReportBaseActivity {
    public void onClick(View view) {
        View v = getLayoutInflater().inflate(R.layout.dialog_chose_month, null);
        final NumberPicker mYearView = (NumberPicker) v.findViewById(R.id.year);
        final NumberPicker mMonthView = (NumberPicker) v.findViewById(R.id.month);
        mYearView.setMinValue(1900);
        mYearView.setMaxValue(mCalendar.get(Calendar.YEAR));
        mMonthView.setMinValue(1);
        mMonthView.setMaxValue(12);
        mYearView.setValue(mCalendar.get(Calendar.YEAR));
        mMonthView.setValue(mCalendar.get(Calendar.MONTH) + 1);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.chose_month).setView
                (v).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoaderManager lm = getLoaderManager();
                if (isLoaderWord) {
                    lm.destroyLoader(LOADER_ID);
                }
                Bundle bundle = new Bundle();
                int year = mYearView.getValue();
                int month = mMonthView.getValue()-1;
                if(mCalendar.get(Calendar.MONTH)==month && mCalendar.get(Calendar.YEAR)==year){
                    return;
                }
                bundle.putInt(Common.YEAR, year);
                bundle.putInt(Common.MONTH, month);
                lm.initLoader(LOADER_ID, bundle, mReportView).forceLoad();
            }
        }).create();
        dialog.show();
    }


    @Override
    protected WhereCondition getCondition(QueryBuilder<AppDayTraffic> queryBuilder, int year,
                                          int month, int day) {
        return queryBuilder.and(AppDayTrafficDao.Properties.Year.eq(year),
                AppDayTrafficDao.Properties.Month.eq(month), AppDayTrafficDao.Properties.Day.eq
                        (day));
    }

}
