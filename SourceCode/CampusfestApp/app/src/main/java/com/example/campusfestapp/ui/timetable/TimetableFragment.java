package com.example.campusfestapp.ui.timetable;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.campusfestapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TimetableFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private JSONArray timeTable;
    private TimetablePagerAdapter context;

    public static TimetableFragment newInstance(int index, JSONArray timeTable, TimetablePagerAdapter context) {
        TimetableFragment fragment = new TimetableFragment();
        fragment.timeTable = timeTable;
        fragment.context = context;
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_timetable, container, false);
        JSONObject obj;
        for(int i=0; i < timeTable.length(); i++){
            try{
                obj = timeTable.getJSONObject(i);
                String time = obj.getString("time")
                        .split(" ")[1];
                time = time.substring(0,time.length()-3);
                String bandName = obj.getString("band");
                final LinearLayout timetableTable = root.findViewById(R.id.timetableTable);
                final LinearLayout row = (LinearLayout) getLayoutInflater().inflate(R.layout.template_timetable_row, null);
                TextView timeText = row.findViewById(R.id.time);
                TextView bandNameText = row.findViewById(R.id.bandName);
                timeText.setText(time);
                bandNameText.setText(bandName);
                timetableTable.addView(row);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    /**
     * TextClick wird in jeder Klasse des Timetables entgegengenommen und an die Timetableklasse
     * weitergegeben, weil ich nicht weiss, wo genau der Klick ankommt und nur die Timetableklasse
     * den Click verarbeiten kann
     * @param v
     */
    public void textClick(View v){
        context.textClick(v);
    }
}