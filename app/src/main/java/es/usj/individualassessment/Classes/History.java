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
    private final List<Day> days;

    public History(JSONArray jsonDays) throws JSONException, ParseException {

        this.days = new ArrayList<>();

        for (int i = 0; i < jsonDays.length() -1; i++) {
            Log.d("JsonDebug", i + "->" + jsonDays.getJSONObject(i).toString());
            days.add(new Day(jsonDays.getJSONObject(i)));
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

    public Day getToday(Calendar calendar) {
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

            boolean isDuplicate = false;
            for (Day existingDay : days) {
                if (existingDay.getDate().equals(newDay.getDate())) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                days.add(newDay);
            }
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

        if(cal1.after(cal2)) return 1;
        else if (cal1.before(cal2)) return -1;
        else return 0;
    }
}