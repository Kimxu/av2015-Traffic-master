package com.hinsty.traffic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.hinsty.traffic.chart.ChartActivity;
import com.hinsty.traffic.report.DayReportActivity;
import com.hinsty.traffic.report.MonthReportActivity;
import com.hinsty.traffic.service.BindHelper;
import com.hinsty.traffic.service.object.SettingData;

/**
 * @author dz
 * @version 2015/6/23.
 */
public class SettingActivity extends BaseActivity {
    LayoutInflater mInflater;
    BindHelper mBindHelper;
    SettingData mSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(R.color.main_fore);
        setContentView(R.layout.activity_setting);
        setRootPaddingTop();
        mInflater = getLayoutInflater();
        mBindHelper = new BindHelper(){
            public void onConnect() {
                try {
                    mSetting = binder.getSettingData();
                } catch (RemoteException e) {
                    mSetting = new SettingData();
                }
            }
        };
        mBindHelper.bind(this);
    }

    @Override
    protected void onDestroy() {
        mBindHelper.unBind(this);
        super.onDestroy();
    }

    public void save(){
        if(mBindHelper.binder!=null) try {
            mBindHelper.binder.updateSetting(mSetting);
        } catch (RemoteException e) {
            //do nothing
        }
    }

    public void onBack(View view){
        finish();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.traffic_set:
                showDataPlanDialog();
                break;
            case R.id.change_date:
                setStartDateDialog();
                break;
            case R.id.change_collect_frequency:
                showCollectFrequency();
                break;
            case R.id.traffic_alert:
                showTrafficAlertDialog();
                break;
            case R.id.chart:
                startActivity(new Intent(this, ChartActivity.class));
                break;
            case R.id.day_report:
                startActivity(new Intent(this, DayReportActivity.class));
                break;
            case R.id.month_report:
                startActivity(new Intent(this, MonthReportActivity.class));
                break;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    private void showDataPlanDialog() {
        View view = mInflater.inflate(R.layout.dialog_data_plan, null);
        final EditText edit = (EditText) view.findViewById(R.id.edit);
        edit.setHint(mSetting.dataPlan + "M");
        new AlertDialog.Builder(this).setTitle(R.string.data_plan).setView(view)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = edit.getText().toString();
                        if (s != null && s.length() > 0) {
                            mSetting.dataPlan=(Integer.valueOf(s));
                            save();
                        }
                    }
                }).create().show();
    }

    private void setStartDateDialog() {
        View view = mInflater.inflate(R.layout.dialog_start_day, null);
        final NumberPicker numberPicker = (NumberPicker) view.findViewById(R.id.picker);
        numberPicker.setMaxValue(31);
        numberPicker.setMinValue(1);
        numberPicker.setValue(mSetting.startDay);
        new AlertDialog.Builder(this).setTitle(R.string.start_day).setView(view)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSetting.startDay =(numberPicker.getValue());
                        save();
                    }
                }).create().show();
    }

    private void showCollectFrequency() {
        View view = mInflater.inflate(R.layout.dialog_collect_frequency, null);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        ((RadioButton) view.findViewById(mSetting.collectFrequency == SettingData
                .CollectFrequency.NORMAL ? R.id.radio_1 : R.id.radio_2)).setChecked(true);
        new AlertDialog.Builder(this).setTitle(R.string.collect_frequency).setView(view)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String str = ((RadioButton) radioGroup.findViewById(radioGroup
                                .getCheckedRadioButtonId())).getText().toString();
                        mSetting.collectFrequency = SettingData.CollectFrequency.nameOf(str);
                        save();
                    }
                }).create().show();
    }

    private void showTrafficAlertDialog() {
        final View view = mInflater.inflate(R.layout.dialog_alert_setting, null);
        ((EditText) view.findViewById(R.id.month_overflow_edit)).setHint(String.format("%dM," +
                ""/* + "(不超过%dM)"*/, mSetting.monthAlert,mSetting.dataPlan));
        ((EditText) view.findViewById(R.id.day_overflow_edit)).setHint(String.format("%dM," +
                ""/* + "(不超过%dM)"*/, mSetting.dayAlert, mSetting.dataPlan));
        ((CheckBox) view.findViewById(R.id.month_alert_check_box)).setChecked(mSetting.monthAlertEnable);
        ((CheckBox) view.findViewById(R.id.day_alert_check_box)).setChecked(mSetting.dayAlertEnable);
        new AlertDialog.Builder(this).setTitle(R.string.alert_traffic).setView(view)
                .setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String monthAlertTrafficStr = ((EditText) view.findViewById(R.id
                                .month_overflow_edit)).getText().toString();
                        if (null != monthAlertTrafficStr && monthAlertTrafficStr.length() != 0) {
                            mSetting.monthAlert = Integer.valueOf(monthAlertTrafficStr);
                        }
                        String dayAlertTrafficStr = ((EditText) view.findViewById(R.id
                                .day_overflow_edit)).getText().toString();
                        if (null != dayAlertTrafficStr && dayAlertTrafficStr.length() != 0) {
                            mSetting.dayAlert = Integer.valueOf(dayAlertTrafficStr);
                        }
                       mSetting.monthAlertEnable =(((CheckBox) view.findViewById(R.id
                                .month_alert_check_box)).isChecked());
                        mSetting.dayAlertEnable =(((CheckBox) view.findViewById(R.id
                                .day_alert_check_box)).isChecked());
                        save();
                    }
                }).create().show();
    }

}

