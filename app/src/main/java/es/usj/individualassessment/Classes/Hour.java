package es.usj.individualassessment.Classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Hour {
    private String datetime;
    private Forecast forecast;

    public Hour(JSONObject jsonObject) throws JSONException {
        this.datetime = jsonObject.getString("datetime");
        this.forecast = new Forecast(jsonObject);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject jsonObject = forecast.toJSON()
                .put("datetime", datetime);

        return jsonObject;
    }
}
