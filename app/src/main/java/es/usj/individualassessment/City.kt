package es.usj.individualassessment

import org.json.JSONObject

data class City(
    val name: String,
    val province: String,
    val country: String,
    val latitude: Double,
    val longitude: Double
) {
    constructor(jsonString: String) : this(
        JSONObject(jsonString).getString("resolvedAddress").split(", ")[0],
        JSONObject(jsonString).getString("resolvedAddress").split(", ")[1],
        JSONObject(jsonString).getString("resolvedAddress").split(", ")[2],
        JSONObject(jsonString).getDouble("latitude"),
        JSONObject(jsonString).getDouble("longitude")
    )
}
