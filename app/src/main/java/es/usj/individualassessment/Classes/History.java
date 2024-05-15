package es.usj.individualassessment.Classes;

import static es.usj.individualassessment.UtilityFunctionsKt.compareDate;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History {
    private final List<Day> days;

    public History(JSONArray jsonDays, Calendar calendar) throws JSONException, ParseException {

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


    public Day getToday(Calendar calendar) {
        Log.d("PerformanceDebug", "getTodayLoop called");
        for (Day day : days) {
            if (compareDate(day.getDate(), calendar.getTime()) == 0) {
                return day;
            }
        }
        throw new Error("Day " + calendar.getTime() + "Not Found");
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

}

class DaysComparator implements java.util.Comparator<Day> {
    @Override
    public int compare(Day a, Day b) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(a.getDate());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(b.getDate());

        if (cal1.after(cal2)) return 1;
        else if (cal1.before(cal2)) return -1;
        else return 0;
    }
}

