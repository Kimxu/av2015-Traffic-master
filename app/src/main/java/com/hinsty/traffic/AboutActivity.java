package com.hinsty.traffic;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

/**
 * @author dz
 * @version 2015/6/17.
 */
public class AboutActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setRootPaddingTop();
    }

    public void onBack(View view) {
        finish();
    }
}
