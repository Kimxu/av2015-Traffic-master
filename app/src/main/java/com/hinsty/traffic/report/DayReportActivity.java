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
public class DayReportActivity extends ReportBaseActivity {
    public void onClick(View view) {
        View v = getLayoutInflater().inflate(R.layout.dialog_chose_day, null);
        final NumberPicker mYearView = (NumberPicker) v.findViewById(R.id.year);
        final NumberPicker mMonthView = (NumberPicker) v.findViewById(R.id.month);
        final NumberPicker mDayView = (NumberPicker) v.findViewById(R.id.day);
        mYearView.setMinValue(1900);
        mYearView.setMaxValue(mCalendar.get(Calendar.YEAR));
        mYearView.setValue(mCalendar.get(Calendar.YEAR));
        mMonthView.setMinValue(1);
        mMonthView.setMaxValue(12);
        mMonthView.setValue(mCalendar.get(Calendar.MONTH) + 1);
        mDayView.setMinValue(1);
        onDateChange(mYearView, mMonthView, mDayView);
        mDayView.setValue(mCalendar.get(Calendar.DAY_OF_MONTH));

        mYearView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onDateChange(mYearView, mMonthView, mDayView);
            }
        });

        mMonthView.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                onDateChange(mYearView, mMonthView, mDayView);
            }
        });
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.chose_date).setView
                (v).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoaderManager lm = getLoaderManager();
                if (isLoaderWord) {
                    lm.destroyLoader(LOADER_ID);
                }
                Bundle bundle = new Bundle();
                bundle.putInt(Common.YEAR, mYearView.getValue());
                bundle.putInt(Common.MONTH, mMonthView.getValue() - 1);
                bundle.putInt(Common.DAY, mDayView.getValue());
                lm.initLoader(LOADER_ID, bundle, mReportView).forceLoad();
            }
        }).create();
        dialog.show();
    }

    static final boolean[] MONTH_DAY = new boolean[]{true, false, true, false, true, false, true,
            true, false, true, false, true};

    private void onDateChange(NumberPicker year, NumberPicker month, NumberPicker day) {
        int y = year.getValue();
        int m = month.getValue();
        if (m == 2) {
            day.setMaxValue((y % 400 == 0 || y % 4 == 0) ? 29 : 28);
        } else {
            day.setMaxValue(MONTH_DAY[m] ? 31 : 30);
        }
    }

    @Override
    protected WhereCondition getCondition(QueryBuilder<AppDayTraffic> queryBuilder, int year, int month, int day) {
        return queryBuilder.and(AppDayTrafficDao.Properties.Year.eq(year),AppDayTrafficDao.Properties.Month.eq(month),AppDayTrafficDao.Properties.Day.eq(day));
    }

}
