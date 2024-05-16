package es.usj.individualassessment.Classes;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class History {
    private final List<Day> days;
    private final Calendar cal;

    public History(JSONArray jsonDays, Calendar calendar) throws JSONException, ParseException {
        this.cal = calendar;

        this.days = new ArrayList<>();

        for (int i = 0; i < jsonDays.length(); i++) {
            Log.d("JsonDebug", i + "->" + jsonDays.getJSONObject(i).toString());
            Day day = new Day(jsonDays.getJSONObject(i));

            /*
            Calendar auxCal = (Calendar) calendar.clone();
            Date auxDate = day.getDate();
            auxCal.setTime(auxDate);

            if(!auxCal.before(calendar)) {
                day.setPrediction(true);
            }
            */

            days.add(day);

        }
        sortDays();

    }

    public JSONArray toJSON() {
        JSONArray jsonArray = new JSONArray();
        for (Day day : days) {
            jsonArray.put(day);
        }
        return jsonArray;
    }

    public List<Day> getDays() {
        return days;
    }

    public Day getLastHistoryDay() {
        Day lastDay = null;
        for (Day day : days) {
            //Log.d("LogicDebug" , day.getDate() + " -> " + !day.isPrediction());
            if (!day.isPrediction()) {
                lastDay = day;
            }
        }
        return lastDay;
    }


    public Day getToday(Calendar cal1) {
        Log.d("PerformanceDebug", "getTodayLoop called");
        Comparator<Calendar> dateComparator = new CalendarDateComparator();
        for (Day day : days) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(day.getDate());
            if (dateComparator.compare(cal1, cal2) == 0) {
                return day;
            }
        }
        return null; // Return null if today is not found
    }

    public void addNewDays(JSONArray newJsonDays) throws JSONException, ParseException {
        for (int i = 0; i < newJsonDays.length(); i++) {
            JSONObject jsonObject = newJsonDays.getJSONObject(i);
            Day newDay = new Day(jsonObject);

            days.add(newDay);

        }
        sortDays();
    }

    private void sortDays() {
        days.sort(new DaysComparator());
    }

    public Calendar getCal() {
        return cal;
    }
}

