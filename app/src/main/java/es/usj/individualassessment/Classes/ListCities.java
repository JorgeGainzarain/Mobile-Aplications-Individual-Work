package es.usj.individualassessment.Classes;

import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

public class ListCities extends ArrayList<City> {

    public static ListCities instance;
    private final File citiesDirectory;
    private final File copyDirectory;
    private final Boolean loadCopy;

    private final List<String> cities = Arrays.asList(
            "Los Angeles", "New York City", "Rio de Janeiro", "Zaragoza", "Paris", "Rome",
            "Cape Town", "Istanbul", "Tokyo", "Sydney", "Buenos Aires", "Alofi", "Majuro",
            "Santa Fe", "Jujuy", "Salta", "Tucumán", "Tierra del Fuego", "Berlin", "CanBerra"
    );

    private final String apiKey = "YSM2B6WBW6W4GYST2A5PL4PM8";

    public ListCities(File citiesDir, File copyDir) {
        this(citiesDir, copyDir, false);
    }
    public ListCities(File citiesDir, File copyDir, Boolean loadcopy) {
        this.loadCopy = loadcopy;
        this.citiesDirectory = citiesDir;
        this.copyDirectory = copyDir;

        if (!citiesDirectory.exists()) {
            if(!citiesDirectory.mkdir()) {
                throw new RuntimeException("Failed to create the citiesDirectory");
            }
        }

        if (!copyDirectory.exists()) {
            if(!copyDirectory.mkdir()) {
                throw new RuntimeException("Failed to create the copyDirectory");
            }
        }
    }

    public CompletableFuture<Void> loadCities() {
        return CompletableFuture.runAsync (() -> {

            for (String cityName : cities) {
                try {
                    this.add(loadCity(cityName).join());
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }
                Log.d("CityLoad", "City " + cityName + " loaded");
            }
            instance = this;
        });

    }

    private CompletableFuture<City> loadCity(String cityName) throws IOException, JSONException {
        return CompletableFuture.supplyAsync( () -> {

            String jsonString = loadCityString(cityName).join();
            City city = getCity(cityName, jsonString).join();

            // Now we have the city itself loaded, let's check if we need to update it considering today's date
            // let's check the city lastHist Time
            Calendar calendar = city.getCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            String monthString = (month < 9) ? "0" + (month + 1) : String.valueOf(month + 1);
            String dayString = (day < 10) ? "0" + day : String.valueOf(day);
            String dateString = year + "-" + monthString + "-" + dayString;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            LocalDate todayDate = LocalDate.parse(dateString, formatter);
            LocalDate lastHistDay = LocalDate.parse(city.firstPredictionDay().getDateString(), formatter);

            // Check if today is after the last day marked as history
            if (todayDate.isAfter(lastHistDay)) {
                String cityData = fetchWeatherForCity(
                        city.getName(),
                        lastHistDay.plusDays(1),
                        todayDate,
                        false)
                        .join();

                city.addData(cityData);

                saveCityData(new File(citiesDirectory, city.getName()), city.toJSON().toString());
            }

            return city;
        });
    }


    public CompletableFuture<City> getCity(String cityName, String jsonString) {
        return CompletableFuture.supplyAsync( () -> {

            AtomicReference<City> cityRef = new AtomicReference<>();
            try {
                City city = new City(jsonString);
                cityRef.set(city);
            } catch (Exception e) {
                copyLoad(cityName).thenAccept(newJsonString -> {
                    try {
                        City city = new City(newJsonString);
                        cityRef.set(city);
                    } catch (Exception ex) {
                        // If an exception occurs again, complete the future exceptionally with the error
                        throw new RuntimeException(e);
                    }
                }).join(); // Ensure the completion of copyLoad before continuing
            }
            // Get the reference of city
            City city = cityRef.get();
            if (city == null) {
                throw new RuntimeException(cityName + " == null");
            }
            else {
                return city;
            }
        });
    }
    private CompletableFuture<String> loadCityString(String city) {
        return CompletableFuture.supplyAsync(() -> {

            if (this.loadCopy) {
                return copyLoad(city).join();
            }

            if (!citiesDirectory.exists() || !new File(citiesDirectory, city + ".json").exists()) {
                if (copyDirectory.exists() && new File(copyDirectory, city + ".json").exists()) {
                    return copyLoad(city).join();
                } else {
                    return firstLoad(city).join();
                }
            } else {
                return loadFromJson(city).join();
            }

        });
    }

    private CompletableFuture<String> loadFromJson(String city) {
        return CompletableFuture.supplyAsync(() -> {
            Log.d("LoadDebug", "Loading from JSON");
            File file = new File(citiesDirectory, city + ".json");
            try {
                return new String(Files.readAllBytes(file.toPath()));
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    private CompletableFuture<String> copyLoad(String city) {
        return CompletableFuture.supplyAsync(() -> {
            Log.d("LoadDebug", "Loading from Copy");
            File copyFile = new File(copyDirectory, city + ".json");
            File targetFile = new File(citiesDirectory, city + ".json");
            try {
                // Read the file content from the copy directory
                String content = new String(Files.readAllBytes(copyFile.toPath()));

                // Copy the file to the cities directory, replacing the existing one if necessary
                Files.copy(copyFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                return content;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<String> firstLoad(String city) {
        return CompletableFuture.supplyAsync(() -> {

            LocalDate currDate = LocalDate.now();
            LocalDate firstDay = currDate.withDayOfMonth(1);
            LocalDate lastDay = currDate.withDayOfMonth(currDate.lengthOfMonth());
            String jsonString = fetchWeatherForCity(city, firstDay, lastDay, false).join();

            saveCityData(new File(copyDirectory, city + ".json"), jsonString).join(); // Wait until the copy is saved


            return jsonString;
        });
    }
    public CompletableFuture<String> fetchWeatherForCity(String city, LocalDate startDay, LocalDate endDay, boolean today) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.d("LoadDebug", "Loading from API (" + startDay + " , " + endDay + ")");

                String defaultUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                        city + "/" + startDay + "/" + endDay +
                        "?unitGroup=metric&include=days&key=" + apiKey + "&contentType=json";

                String todayUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" +
                        city + "/today?unitGroup=metric&include=days%2Chours&key=" + apiKey + "&contentType=json";

                URL url = new URL(today ? todayUrl : defaultUrl);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

                Log.d("APIDebug", connection.getURL().toString());
                Log.d("APIDebug", "");

                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    return stringBuilder.toString();
                } else {
                    return "Failed to fetch weather data for "
                            + city + ". Response code: " + responseCode;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private CompletableFuture<Void> saveCityData(File file, String data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Log.d("Debug", "Saving city " + file.getName());

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());

                outputStream.close();

                Log.d("Debug", "City: " + file.getName() + " saved successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

    public CompletableFuture<Void> saveCity(City city) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String data = city.toJSON().toString();
                File file = new File(citiesDirectory, city.getName() + ".json");
                Log.d("Debug", "Saving city " + file.getName());

                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(data.getBytes());

                outputStream.close();

                Log.d("Debug", "City: " + file.getName() + " updated successfully");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        });
    }

}

    /*
    private fun loadCities(): CompletableFuture<Void> {
        return CompletableFuture.runAsync {
            Log.d("Control", "Loading cities")

            val directory = File(applicationContext.filesDir, "cities")
            val copyDirectory = File(applicationContext.filesDir, "cities-copy")

            if (!directory.exists() && copyDirectory.exists() && !copyDirectory.listFiles().isNullOrEmpty()) {
                Log.d("LoadingDebug", "Loading copy of cities")
                loadCopy()
            }

            if (directory.exists() && !directory.listFiles().isNullOrEmpty()) {
                Log.d("CityLoading", "Loading Custom")
                listCities = getCities(applicationContext)

                for (city in listCities) {
                    Log.d("Control", "Loading city ${city.name}")

                    val year = city.calendar.get(Calendar.YEAR)
                    val month = city.calendar.get(Calendar.MONTH)
                    val day = city.calendar.get(Calendar.DAY_OF_MONTH)
                    val monthString = if (month < 9) "0${month + 1}" else "${month + 1}"
                    val dayString = if (day < 10) "0$day" else "$day"
                    val dateString = "$year-$monthString-$dayString"
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val todayDate = LocalDate.parse(dateString, formatter)

                    val lastHistDay = LocalDate.parse(city.firstPredictionDay().dateString, formatter)
                    Log.d("LogicDebug", "Today: $todayDate -> Last History Day: $lastHistDay")
                    if (todayDate.isAfter(lastHistDay)) {
                        Log.d("LogicDebug", "City ${city.name}: todayDate is After the lastHistDay")

                        val startDay = lastHistDay.plusDays(1)
                        val cityData = fetchWeatherForCity(city.name, startDay, todayDate)

                        city.addData(cityData)
                        saveCityData(city.name, city.toJSON().toString())
                    }
                }
            } else {
                Log.d("Control", "Loading Full month")
                val currentDate = LocalDate.now()
                val startDay = currentDate.withDayOfMonth(1)
                val endDay = currentDate.withDayOfMonth(currentDate.lengthOfMonth())

                directory.mkdirs()

                for (city in cities) {
                    Log.d("Control", "Saving city $city")
                    saveCityData(city, fetchWeatherForCity(city, startDay, endDay))
                }

                listCities = getCities(applicationContext)
            }

            saveCopy()

            Log.d("Control", "Control message 1")

            for (city in listCities) {
                val today = city.today
                val bool = today.hasHours()

                Log.d("ShouldLoadAPI", "${city.name} -> $bool")

                if (!bool) {
                    val jsonString = fetchWeatherForCity(city.name)
                    val jsonObject = JSONObject(jsonString)
                    val daysObject = jsonObject.getJSONArray("days").get(0) as JSONObject
                    val newDay = Day(daysObject, city.today.cal)

                    city.updateToday(newDay)
                }
                Log.d("Control", "Control message END")
                saveCityData(city.name, city.toJSON().toString())
                Log.d("LogicDebug", "City ${city.name} loading finished")
            }
            Log.d("LogicDebug", "All cities loaded")
            Log.d("LogicDebug", "")
        }
    }
    */
