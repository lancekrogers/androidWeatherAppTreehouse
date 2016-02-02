package com.lancekrogers.stormy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lancekrogers.stormy.R;
import com.lancekrogers.stormy.weather.Daily;

/**
 * Created by lancerogers on 2/1/16.
 */
public class DayAdapter extends BaseAdapter {

    private Context mContext;
    private Daily[] mDailys;

    public DayAdapter(Context context, Daily[] dailies) {
        mContext = context;
        mDailys = dailies;
    }

    @Override
    public int getCount() {
        return mDailys.length;
    }

    @Override
    public Object getItem(int position) {
        return mDailys[position];
    }

    @Override
    public long getItemId(int position) {
        return 0; // Used to tag items for easy reference
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // brand new
            convertView = LayoutInflater.from(mContext).inflate(R.layout.daily_list_item, parent, false);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView) convertView.findViewById(R.id.iconImageView);
            holder.temperatureLabel = (TextView) convertView.findViewById(R.id.temperatureLabel);
            holder.dayLabel = (TextView) convertView.findViewById(R.id.dayNam
            convertView.setTag(holder);
        }
        else {

            holder = (ViewHolder) convertView.getTag();
        }
        Daily day = mDailys[position];
        holder.iconImageView.setImageResource(day.getIconId());
        holder.temperatureLabel.setText(day.getTemperatureMax() + "");
        //holder.dayLabel.setText(day.getDayOfTheWeek());
        if (position == 0) {
            holder.dayLabel.setText("Today");
        } else {
            holder.dayLabel.setText(day.getDayOfTheWeek());
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView; // public by default
        TextView temperatureLabel;
        TextView dayLabel;
    }
}
