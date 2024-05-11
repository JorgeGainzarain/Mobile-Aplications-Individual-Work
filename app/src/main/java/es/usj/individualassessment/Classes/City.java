package es.usj.individualassessment.Classes;

import static es.usj.individualassessment.UtilityFunctionsKt.compareTime;

import android.util.Log;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class City {
    private String name;
    private String province;
    private String country;
    private double latitude;
    private double longitude;
    //private Forecast currConditions;
    private Calendar calendar;
    private History history;


    public City(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.name = jsonObject.getString("resolvedAddress").split(", ")[0];
            this.latitude = jsonObject.getDouble("latitude");
            this.longitude = jsonObject.getDouble("longitude");
            //this.currConditions = new Forecast(jsonObject.getJSONObject("currentConditions"));

            String[] addressParts = jsonObject.getString("resolvedAddress").split(", ");
            this.province = (addressParts.length == 3) ? addressParts[1] : null;
            this.country = (addressParts.length == 3) ? addressParts[2] : addressParts[1];


            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.HOUR_OF_DAY, jsonObject.getInt("tzoffset"));
            this.calendar = cal;

            this.history = new History(jsonObject.getJSONArray("days"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getTimeString() {
        int hour = this.calendar.get(Calendar.HOUR_OF_DAY);
        int minute = this.calendar.get(Calendar.MINUTE);
        String hourString = (hour < 10) ? "0" + hour : String.valueOf(hour);
        String minuteString = (minute < 10) ? "0" + minute : String.valueOf(minute);
        return hourString + " : " + minuteString;
    }

    public boolean isDay() {

        String[] currentTimeParts = getTimeString().split(" : ");
        int currentHour = Integer.parseInt(currentTimeParts[0]);
        int currentMinute = Integer.parseInt(currentTimeParts[1]);

        Log.d("DateTime", "City: " + name + " -> " + currentHour + " : " + currentMinute + " -> "
                + "(" + getSunrise() + " , " + getSunset() + ")" );
        return !before(getSunrise()) && before(getSunset());
    }


    private boolean before(String time) {
        try {
            Date date = new SimpleDateFormat("hh:mm:ss").parse(time);

            Calendar cal1 = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            assert date != null;
            cal1.setTime(date);

            return compareTime(calendar, cal1) == -1;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    public String getSunrise() {
        return getToday().getSunrise();
    }

    public String getSunset() {
        return getToday().getSunset();
    }

    // Getters and setters
    public Day getToday() {
        Log.d("TestLog", name);
        return history.getToday(calendar);
    }
    public String getName() {
        return name;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
