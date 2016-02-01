package com.lancekrogers.stormy.ui;

import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;

import com.lancekrogers.stormy.R;

public class DailyForcastActivity extends ListActivity {

    private Daily[] mDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forcast);

        DailyAdapter dayAdapter = new DailyAdapter(this, mDays);

    }

}
