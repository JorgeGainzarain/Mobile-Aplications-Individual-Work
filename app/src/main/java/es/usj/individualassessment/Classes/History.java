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
            //Log.d("JsonDebug", i + "->" + jsonDays.getJSONObject(i).toString());
            Day day = new Day(jsonDays.getJSONObject(i), calendar);

            // If there's not a isPrediction in the json (First time a data is included won't have a isPrediction)
            if(!jsonDays.getJSONObject(i).has("isPrediction")) {
                day.setPrediction(isPrediction(calendar, day));
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

    public void replaceDay(Day day, Day newDay) {
        Log.d("UpdateDebug", "Test Control 1");
        int index = days.indexOf(day);
        days.remove(index);
        days.set(index, newDay);
        Log.d("UpdateDebug", "Test Control 2");
    }

    public boolean isPrediction(Calendar calendar, Day day) {
        //Log.d("Prediction Message Test", "Prediction json not found -> " + day.getDateString());
        Calendar auxCal = (Calendar) calendar.clone();
        String auxDate = day.getDateString();

        String[] parts = auxDate.split("-");
        auxCal.set(Calendar.YEAR, Integer.parseInt(parts[0]));
        auxCal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
        auxCal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(parts[2]));

        return new CalendarComparator().compareDate(this.cal,auxCal) <= 0;
    }

    public Day getFirstPredictionDay() {

        for (Day day : days) {
            //Log.d("DebugPrediction", day.getDateString() + " -> " + day.isPrediction());
            if (day.isPrediction()) {
                return day;
            }
        }
        return null;
    }


    public Day getToday(Calendar cal1) {
        Log.d("PerformanceDebug", "getTodayLoop called");
        for (Day day : days) {
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(day.getDate());

            boolean bool = new CalendarComparator().compareDate(cal1,cal2) == 0;
            //Log.d("Control", "----->" + cal1.getTime());

            //Log.d("Control", "Control message getToday ("
            //        + cal1.get(Calendar.YEAR) + "-" + cal1.get(Calendar.MONTH) + "-" + cal1.get(Calendar.DAY_OF_MONTH) + ","
            //        + cal2.get(Calendar.YEAR) + "-" + cal2.get(Calendar.MONTH) + "-" + cal2.get(Calendar.DAY_OF_MONTH) + ")"
            //        + " -> " + bool);

            cal2.setTime(day.getDate());
            if (new CalendarComparator().compareDate(cal1,cal2) == 0) {
                return day;
            }
        }
        //Log.d("Control", "Control null return");
        throw new Error("Today not found"); // Throw an error if not found
    }

    public void addNewDays(JSONArray newJsonDays) throws JSONException, ParseException {
        for (int i = 0; i < newJsonDays.length(); i++) {
            JSONObject jsonObject = newJsonDays.getJSONObject(i);
            days.add(new Day(jsonObject, this.cal));

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

