package es.usj.individualassessment.Classes;

import static es.usj.individualassessment.UtilityFunctionsKt.compareDate;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class History {
    private List<Day> days;

    public History(JSONArray jsonDays) throws JSONException, ParseException {

        this.days = new ArrayList<>();

        for (int i = 0; i < jsonDays.length(); i++) {
            days.add(new Day(jsonDays.getJSONObject(i)));
        }

    }

    public Day getToday(Calendar calendar) {
        for (Day day : days) {
            Log.d("TestLog", day.getDate() + " -> " +  calendar.getTime());
            if (compareDate(day.getDate(), calendar.getTime()) == 0) {
                return day;
            }
        }
        throw new Error("Day " + calendar.getTime() + "Not Found");
    }
}
