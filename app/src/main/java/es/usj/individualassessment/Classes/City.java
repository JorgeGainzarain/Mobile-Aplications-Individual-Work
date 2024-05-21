package es.usj.individualassessment.Classes;

import android.annotation.SuppressLint;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import es.usj.individualassessment.Classes.Comparators.CalendarComparator;

public class City {
    private String name;
    private String province;
    private String country;
    private double latitude;
    private double longitude;
    private Calendar calendar;
    private History history;
    private int tzOffset;
    private Day today;
    private Boolean favourite;


    public City(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            this.name = jsonObject.getString("address");
            this.latitude = jsonObject.getDouble("latitude");
            this.longitude = jsonObject.getDouble("longitude");

            String[] addressParts = jsonObject.getString("resolvedAddress").split(", ");
            this.province = (addressParts.length == 3) ? addressParts[1] : null;
            this.country = (addressParts.length == 3) ? addressParts[2] : addressParts[1];

            this.tzOffset = jsonObject.getInt("tzoffset");
            this.calendar = getLocalCalendar(this.tzOffset, new Date());

            //Log.d("Prediction Message Test", this.name);
            this.history = new History(jsonObject.getJSONArray("days"), (Calendar) this.calendar.clone());
            Log.d("Control", this.name + " -> " + this.calendar.getTime() + " -> (" + this.tzOffset + ")");
            this.today = history.getToday((Calendar) this.calendar.clone());

            if(jsonObject.has("fav")) {
                this.favourite = jsonObject.getBoolean("fav");
            }
            else {
                this.favourite = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() {
        StringBuilder resolvedAddress = new StringBuilder();
        resolvedAddress.append(name);
        if (province != null) {
            resolvedAddress.append(", ").append(province);
        }
        resolvedAddress.append(", ").append(country);

        try {
            return new JSONObject()
                    .put("address", name)
                    .put("latitude", latitude)
                    .put("longitude", longitude)
                    .put("resolvedAddress", resolvedAddress.toString())
                    .put("tzoffset", calendar.getTimeZone().getRawOffset() / 3600000)
                    .put("days", history.toJSON())
                    .put("fav", favourite);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Day firstPredictionDay() {
        return history.getFirstPredictionDay();
    }

    public void addData(String jsonString) {
        try {
            this.history.addNewDays(new JSONObject(jsonString).getJSONArray("days"));
        }
        catch (Exception e) {
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
        return !before(getSunrise()) && before(getSunset());
    }

    @SuppressLint("SimpleDateFormat")
    private boolean before(String time) {
        try {
            Date date = new SimpleDateFormat("hh:mm:ss").parse(time);

            Calendar cal1 = Calendar.getInstance();
            assert date != null;
            cal1.setTime(date);

            int hour1 = cal1.get(Calendar.HOUR_OF_DAY);
            int minute1 = cal1.get(Calendar.MINUTE);
            int second1 = cal1.get(Calendar.SECOND);

            //int comparation = compareTime(this.calendar, cal1);
            int comparation = new CalendarComparator().compareTime(this.calendar,cal1);

            Log.d("DateTime", "City: " + name + " -> "
                    + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND)
                    + " -> " + hour1 + ":" + minute1 + ":" + second1 + " -> " + comparation );

            return comparation < 0; // (-1) -> Before


        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static Calendar getLocalCalendar(int tzOffset, Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, tzOffset);
        return calendar;
    }

    public String getSunrise() {
        return today.getSunrise();
    }

    public String getSunset() {
        return today.getSunset();
    }

    // Getters and setters
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

    public int getTzOffset() {
        return tzOffset;
    }
    public void setTzOffset(int tzOffset) {
        this.tzOffset = tzOffset;
    }
    public Day getToday() {return today;}

    public Double getAvgWind() {
        return history.getAvgWind();
    }

    public Double getAvgTemp() {return history.getAvgTemp();}
    public Double getAvgDew() {return history.getAvgDew();}

    public Boolean isFavourite() {return favourite;}
    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
        ListCities.instance.saveCity(this);
    }

    public void updateToday(Day newDay) {
        Log.d("UpdateDebug", "Test Control 0");
        history.replaceDay(today, newDay);
        this.today = newDay;
    }


}
