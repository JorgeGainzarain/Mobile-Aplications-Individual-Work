package es.usj.individualassessment.Classes;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Day {
    private final String sunrise;
    private final String sunset;
    private  final Date date;
    private final String icon;
    private final List<Hour> hours = new ArrayList<>();
    private boolean prediction;
    private final Calendar cal;
    private final double tempmax;
    private final double tempmin;
    private final double temp;
    private final double feelslike;
    private final double dew;
    private final double precipprob;
    private final int snow;
    private final double windspeed;
    private final double winddir;
    private final double visibility;
    private final double moonphase;
    private final String description;


    public Day(JSONObject jsonObject, Calendar calendar) throws JSONException, ParseException {

        this.cal = calendar;

        if(jsonObject.optBoolean("isPrediction")) {
            this.prediction = jsonObject.getBoolean("isPrediction");
        }
        else {
            this.prediction = false;
        }


        this.icon = jsonObject.getString("icon");
        this.sunrise = jsonObject.getString("sunrise");
        this.sunset = jsonObject.getString("sunset");
        this.tempmax = jsonObject.getDouble("tempmax");
        this.tempmin = jsonObject.getDouble("tempmin");
        this.temp = jsonObject.getDouble("temp");
        this.feelslike = jsonObject.getDouble("feelslike");
        this.dew = jsonObject.getDouble("dew");
        this.precipprob = jsonObject.getDouble("precipprob");
        this.snow = jsonObject.getInt("snow");
        this.windspeed = jsonObject.getDouble("windspeed");
        this.winddir = jsonObject.getDouble("winddir");
        this.visibility = jsonObject.getDouble("visibility");
        this.moonphase = jsonObject.getDouble("moonphase");
        this.description = jsonObject.getString("description");


        JSONArray jsonHours = jsonObject.optJSONArray("hours");
        if(jsonHours != null) {
            for (int i = 0; i < jsonHours.length(); i++) {
                hours.add(new Hour(jsonHours.getJSONObject(i)));
            }
        }


        String dateString = jsonObject.getString("datetime");
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);

        assert date != null;
        this.date = date;

    }

    public JSONObject toJSON() throws JSONException {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        JSONObject jsonObject = new JSONObject()
                .put("sunrise", sunrise)
                .put("sunset", sunset)
                .put("datetime", dateString)
                .put("isPrediction", isPrediction())
                .put("icon", icon)
                .put("tempmax", tempmax)
                .put("tempmin", tempmin)
                .put("temp", temp)
                .put("feelslike", feelslike)
                .put("dew", dew)
                .put("precipprob", precipprob)
                .put("snow", snow)
                .put("windspeed", windspeed)
                .put("winddir", winddir)
                .put("visibility", visibility)
                .put("moonphase", moonphase)
                .put("description", description);

        // If there's hours stored put them in the json
        if(hasHours()) {
            JSONArray jsonHours = new JSONArray();
            for (Hour hour : hours) {
                jsonHours.put(hour.toJSON());
            }
            jsonObject.put("hours", jsonHours);
        }

        return jsonObject;
    }

    public String getIcon() {
        if(hasHours()) {
            for(Hour hour : hours) {
                if(cal.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(hour.getDatetime().split(":")[0])) {
                    return hour.getIcon();
                }
            }
        }
        else {
            return icon;
        }
        return null;
    }

    public Boolean hasHours() {
        return ! this.hours.isEmpty();
    }


    public String getSunrise() {
        return sunrise;
    }

    public String getSunset() {
        return sunset;
    }


    public Date getDate() {
        return date;
    }

    public String getDateString() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public boolean isPrediction() {
        return prediction;
    }

    public void setPrediction(boolean prediction) {
        this.prediction = prediction;
    }
    public Calendar getCal() {
        return cal;
    }

    public double getTempmax() {
        return tempmax;
    }

    public double getTempmin() {
        return tempmin;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public double getDew() {
        return dew;
    }

    public double getPrecipprob() {
        return precipprob;
    }

    public int getSnow() {
        return snow;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public double getWinddir() {
        return winddir;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getMoonphase() {
        return moonphase;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Day) {
            return equals((Day) o);
        }
        else {
            return false;
        }
    }
    private boolean equals(Day d) {
        return date == d.date;
    }
}
