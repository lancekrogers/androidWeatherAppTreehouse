package com.lancekrogers.stormy.ui;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lancekrogers.stormy.R;
import com.lancekrogers.stormy.adapters.DayAdapter;
import com.lancekrogers.stormy.weather.Daily;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DailyForcastActivity extends Activity {

    private static final String TAG = DailyForcastActivity.class.getSimpleName();
    private Daily[] mDays;

    @Bind(android.R.id.list) ListView mListView;
    @Bind(android.R.id.empty) TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forcast);
        ButterKnife.bind(this);

        Intent intent = getIntent();


        Parcelable[] parcelable = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);

        mDays = Arrays.copyOf(parcelable, parcelable.length, Daily[].class);

        DayAdapter dayAdapter = new DayAdapter(this, mDays);
        mListView.setAdapter(dayAdapter);

        mListView.setEmptyView(mEmptyTextView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dayOfTheWeek = mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();
                String highTemp = mDays[position].getTemperatureMax() + "";
                String message = String.format("On %s the high will be %s and it will be %S",
                        dayOfTheWeek, highTemp, conditions);

                Toast.makeText(DailyForcastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

    }
/*
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String dayOfTheWeek = mDays[position].getDayOfTheWeek();
        String conditions = mDays[position].getSummary();
        String highTemp = mDays[position].getTemperatureMax() + "";
        String message = String.format("On %s the high will be %s and it will be %S",
                dayOfTheWeek, highTemp, conditions);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    } */
}
