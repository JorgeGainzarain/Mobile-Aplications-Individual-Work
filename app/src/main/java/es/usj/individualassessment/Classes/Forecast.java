package es.usj.individualassessment.Classes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Forecast {
    private double temp;
    private double dew;
    private double humidity;
    private double precip;
    private double precipProb;
    private JSONArray precipType;
    private int snow;
    private int snowDepth;
    private double windSpeed;
    private double windDir;
    private double pressure;
    private double cloudCover;
    private double visibility;
    private double solarRad;
    private double solarEnergy;
    private double moonPhase;
    private String condition;
    private String icon;

    public Forecast(JSONObject jsonObject) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject()
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

        return jsonObject;
    }


    public double getPrecip() {
        return precip;
    }

    public void setPrecip(double precip) {
        this.precip = precip;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getDew() {
        return dew;
    }

    public void setDew(double dew) {
        this.dew = dew;
    }

    public double getPrecipProb() {
        return precipProb;
    }

    public void setPrecipProb(double precipProb) {
        this.precipProb = precipProb;
    }

    public JSONArray getPrecipType() {
        return precipType;
    }

    public void setPrecipType(JSONArray precipType) {
        this.precipType = precipType;
    }

    public int getSnow() {
        return snow;
    }

    public void setSnow(int snow) {
        this.snow = snow;
    }

    public int getSnowDepth() {
        return snowDepth;
    }

    public void setSnowDepth(int snowDepth) {
        this.snowDepth = snowDepth;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public double getWindDir() {
        return windDir;
    }

    public void setWindDir(double windDir) {
        this.windDir = windDir;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getCloudCover() {
        return cloudCover;
    }

    public void setCloudCover(double cloudCover) {
        this.cloudCover = cloudCover;
    }

    public double getVisibility() {
        return visibility;
    }

    public void setVisibility(double visibility) {
        this.visibility = visibility;
    }

    public double getSolarRad() {
        return solarRad;
    }

    public void setSolarRad(double solarRad) {
        this.solarRad = solarRad;
    }

    public double getSolarEnergy() {
        return solarEnergy;
    }

    public void setSolarEnergy(double solarEnergy) {
        this.solarEnergy = solarEnergy;
    }

    public double getMoonPhase() {
        return moonPhase;
    }

    public void setMoonPhase(double moonPhase) {
        this.moonPhase = moonPhase;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
