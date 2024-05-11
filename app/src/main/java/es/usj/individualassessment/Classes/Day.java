package es.usj.individualassessment.Classes;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Day {
    private String sunrise;
    private String sunset;
    private Date date;

    public Day(JSONObject jsonObject) throws JSONException, ParseException {
        this.sunrise = jsonObject.getString("sunrise");
        this.sunset = jsonObject.getString("sunset");

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

        String dateString = jsonObject.getString("datetime");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateString);

        assert date != null;
        this.date = date;

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

    public void setDate(Date date) {
        this.date = date;
    }
}
