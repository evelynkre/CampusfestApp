package com.example.campusfestapp.ui.timetable;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.campusfestapp.Timetable;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Gibt ein TimetableFragment mit dem entsprechenden Inhalt anhand des offenen Tabs zurueck.
 */
public class TimetablePagerAdapter extends FragmentPagerAdapter {
    private final JSONArray timeTableStages;
    private String[] mTabTitles;
    private final Timetable mContext;

    public TimetablePagerAdapter(Timetable context, FragmentManager fm, JSONArray timeTableStages) {
        super(fm);
        mContext = context;
        this.timeTableStages = timeTableStages;
        String[] tabTitles = new String[timeTableStages.length()];
        for(int i=0; i < timeTableStages.length(); i++){
            try {
                tabTitles[i] = timeTableStages.getJSONObject(i).getString("stage");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mTabTitles = tabTitles;
    }

    @Override
    public Fragment getItem(int position) {
        try {
            JSONArray timetable = timeTableStages
                    .getJSONObject(position)
                    .getJSONArray("timetable");
            return TimetableFragment.newInstance(position + 1, timetable, this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    /**
     * TextClick wird in jeder Klasse des Timetables entgegengenommen und an die Timetableklasse
     * weitergegeben, weil ich nicht weiss, wo genau der Klick ankommt und nur die Timetableklasse
     * den Click verarbeiten kann
     * @param v
     */
    public void textClick(View v){
        mContext.textClick(v);
    }
}