package es.usj.individualassessment.Classes;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Day {
    private String sunrise;
    private String sunset;
    private Date date;
    private String icon;
    //private List<Hour> hours;
    private boolean prediction;

    public Day(JSONObject jsonObject) throws JSONException, ParseException {

        //hours = new ArrayList<>();

        if(jsonObject.optBoolean("isPrediction")) {
            this.prediction = jsonObject.getBoolean("isPreciction");
        }
        else {
            this.prediction = false;
        }


        this.icon = jsonObject.getString("icon");
        this.sunrise = jsonObject.getString("sunrise");
        this.sunset = jsonObject.getString("sunset");

        /*
        JSONArray jsonHours = jsonObject.getJSONArray("hours");
        for (int i = 0; i < jsonHours.length(); i++) {
            hours.add(new Hour(jsonHours.getJSONObject(i)));
        }
        */


        String dateString = jsonObject.getString("datetime");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);

        assert date != null;
        this.date = date;

    }

    public JSONObject toJSON() throws JSONException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);

        JSONObject jsonObject = new JSONObject()
                .put("sunrise", sunrise)
                .put("sunset", sunset)
                .put("datetime", dateString)
                .put("isPrediction", isPrediction())
                .put("icon", icon);

        /*
        JSONArray jsonHours = new JSONArray();
        for (Hour hour : hours) {
            jsonHours.put(hour.toJSON());
        }

        jsonObject.put("hours", jsonHours);
        */


        return jsonObject;
    }

    public String getSunrise() {
        return sunrise;
    }

    public void setSunrise(String sunrise) {
        this.sunrise = sunrise;
    }

    public String getSunset() {
        return sunset;
    }

    public void setSunset(String sunset) {
        this.sunset = sunset;
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isPrediction() {
        return prediction;
    }

    public void setPrediction(boolean prediction) {
        this.prediction = prediction;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
