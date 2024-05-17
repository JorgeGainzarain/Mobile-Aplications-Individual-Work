package es.usj.individualassessment.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Hour {
    private final String datetime;
    private final double temp;
    private final double dew;
    private final double humidity;
    private final double precip;
    private final double precipProb;
    private final JSONArray precipType;
    private final int snow;
    private final int snowDepth;
    private final double windSpeed;
    private final double windDir;
    private final double pressure;
    private final double cloudCover;
    private final double visibility;
    private final double solarRad;
    private final double solarEnergy;
    private final double moonPhase;
    private final String condition;
    private final String icon;

    public Hour(JSONObject jsonObject) throws JSONException {
        this.datetime = jsonObject.getString("datetime");
        this.temp = jsonObject.optDouble("temp", 0.0);
        this.dew = jsonObject.optDouble("dew", 0.0);
        this.humidity = jsonObject.optDouble("humidity", 0.0);
        this.precip = jsonObject.optDouble("precip", 0.0);
        this.precipProb = jsonObject.optDouble("precipprob", 0.0);
        this.precipType = jsonObject.optJSONArray("preciptype");
        this.snow = jsonObject.optInt("snow", 0);
        this.snowDepth = jsonObject.optInt("snowdepth", 0);
        this.windSpeed = jsonObject.optDouble("windspeed", 0.0);
        this.windDir = jsonObject.optDouble("winddir", 0.0);
        this.pressure = jsonObject.optDouble("pressure", 0.0);
        this.cloudCover = jsonObject.optDouble("cloudcover", 0.0);
        this.visibility = jsonObject.optDouble("visibility", 0.0);
        this.solarRad = jsonObject.optDouble("solarradiation", 0.0);
        this.solarEnergy = jsonObject.optDouble("solarenergy", 0.0);
        this.moonPhase = jsonObject.optDouble("moonphase", 0.0);
        this.condition = jsonObject.optString("conditions", "");
        this.icon = jsonObject.optString("icon", "");
    }

    public JSONObject toJSON() throws JSONException {

        return new JSONObject()
                .put("datetime", datetime)
                .put("temp", temp)
                .put("dew", dew)
                .put("humidity", humidity)
                .put("precip", precip)
                .put("precipprob", precipProb)
                .put("preciptype", precipType)
                .put("snow", snow)
                .put("snowdepth", snowDepth)
                .put("windspeed", windSpeed)
                .put("winddir", windDir)
                .put("pressure", pressure)
                .put("cloudcover", cloudCover)
                .put("visibility", visibility)
                .put("solarradiation", solarRad)
                .put("solarenergy", solarEnergy)
                .put("moonphase", moonPhase)
                .put("conditions", condition)
                .put("icon", icon);
    }

    public String getDatetime() {
        return datetime;
    }

    public double getTemp() {
        return temp;
    }

    public double getDew() {
        return dew;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPrecip() {
        return precip;
    }

    public double getPrecipProb() {
        return precipProb;
    }

    public JSONArray getPrecipType() {
        return precipType;
    }

    public int getSnow() {
        return snow;
    }

    public int getSnowDepth() {
        return snowDepth;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDir() {
        return windDir;
    }

    public double getPressure() {
        return pressure;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getSolarRad() {
        return solarRad;
    }

    public double getSolarEnergy() {
        return solarEnergy;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public String getCondition() {
        return condition;
    }

    public String getIcon() {
        return icon;
    }
}
