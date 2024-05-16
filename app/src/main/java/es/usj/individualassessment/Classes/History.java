package es.usj.individualassessment.Classes;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import es.usj.individualassessment.Classes.Comparators.CalendarComparator;
import es.usj.individualassessment.Classes.Comparators.DaysComparator;

public class History {
    private final List<Day> days;
    private final Calendar cal;

    public History(JSONArray jsonDays, Calendar calendar) throws JSONException, ParseException {
        this.cal = calendar;

        this.days = new ArrayList<>();

        for (int i = 0; i < jsonDays.length(); i++) {
            Log.d("JsonDebug", i + "->" + jsonDays.getJSONObject(i).toString());
            Day day = new Day(jsonDays.getJSONObject(i));

            if(!jsonDays.getJSONObject(i).optBoolean("isPrediction")) {
                Log.d("Prediction", "Prediction json not found");
                Calendar auxCal = (Calendar) calendar.clone();
                String auxDate = day.getDateString();

                String[] parts = auxDate.split("-");
                auxCal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
                auxCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
                auxCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));

                if( new CalendarComparator().compareDate(this.cal,auxCal) <= 0) {
                    day.setPrediction(true);
                }
            }
            else {
                Log.d("Prediction", "Prediction json found");
            }

            days.add(day);

        }
        sortDays();

    }

    public JSONArray toJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Day day : days) {
            jsonArray.put(day.toJSON());
        }
        return jsonArray;
    }

    public List<Day> getDays() {
        return days;
    }

    public Day getFirstPredictionDay() {
        Day lastDay = null;

        for (Day day : days) {
            Log.d("DebugPrediction", day.getDateString() + " -> " + day.isPrediction());
            if (day.isPrediction()) {
                return day;
            }
        }
        return lastDay;
    }


    public Day getToday(Calendar cal1) {
        Log.d("PerformanceDebug", "getTodayLoop called");
        for (Day day : days) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(day.getDate());
            if (new CalendarComparator().compareDate(cal1,cal2) == 0) {
                return day;
            }
        }
        return null; // Return null if today is not found
    }

    public void addNewDays(JSONArray newJsonDays) throws JSONException, ParseException {
        for (int i = 0; i < newJsonDays.length(); i++) {
            JSONObject jsonObject = newJsonDays.getJSONObject(i);
            days.add(new Day(jsonObject));

        }
        sortDays();

        days.forEach((day) -> {
            Calendar auxCal = (Calendar) this.cal.clone();
            Date auxDate = day.getDate();
            auxCal.setTime(auxDate);

            // Set the new prediction value considering the currentDay
            day.setPrediction(new CalendarComparator().compareDate(this.cal, auxCal) >= 0);
        });
    }

    private void sortDays() {
        days.sort(new DaysComparator());
    }

    public Calendar getCal() {
        return cal;
    }
}

