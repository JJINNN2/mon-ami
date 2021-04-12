package com.dlab.monami;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Fragment4 extends Fragment {
    public Fragment4() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Fragment4","onCreateView");
        final View v = inflater.inflate(R.layout.fragment4, container, false);

        CalendarView calendarView = v.findViewById(R.id.calendarView);
        List<EventDay> events = new ArrayList<>();

        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
//                calendarView.setDate(calendar);
                Log.d("Calendar","clicked");
                Log.d("Calendar", clickedDayCalendar.getTime().toString());
                //calendarView.setHighlightedDays(clickedDayCalendar);


                try {
                    calendarView.setDate(clickedDayCalendar);
                    Log.d("Date","set"+calendarView.getSelectedDates());
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                events.add(new EventDay(calendar, R.drawable.ic_black_circle, Color.parseColor("#228B22")));
                //Log.d("Calendar events",events.toString());
                calendarView.setEvents(events);
            }
        });

        return v;
    }


}
