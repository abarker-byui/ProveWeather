package byui.cs246.barker.wk06Prove;

import android.app.Activity;
import android.util.Log;
import android.widget.*;
import byui.cs246.barker.wk06Prove.objects.*;
import com.google.gson.Gson;
import java.io.*;
import java.lang.ref.WeakReference;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GetWeather implements Runnable {
    private static final String CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String FORECAST_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private static final String API_KEY = "_YOUR_API_";

    private String _city;
    private WeakReference<Activity> _caller;
    private GetWeatherType _mode;

    public GetWeather(String city, WeakReference<Activity> caller, GetWeatherType mode) {
        _city = city;
        _caller = caller;
        _mode = mode;
    }

    @Override
    public void run() {
        switch (_mode)
        {
            case Current:
                Log.d("GetWeather", "Getting current temperature for " + _city);
                getCurrentWeather();
                break;
            case Forecast:
                Log.d("GetWeather", "Getting forecast for " + _city);
                getForecast();
                break;
        }
    }

    private void getCurrentWeather() {
        Gson gson = new Gson();
        String queryStr = buildQueryString(_city);
        if (queryStr == null) {
            return;
        }

        try {
            URL url = new URL(CURRENT_URL + "?" + queryStr);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
            InputStream responseStream = connection.getInputStream();

            try (Scanner scanner = new Scanner(responseStream)) {
                String response = scanner.useDelimiter("\\A").next();

                CurrentWeather conditions = gson.fromJson(response, CurrentWeather.class);

                final Activity activity = _caller.get();
                if (activity == null) {
                    Log.d("GetWeather", "Activity reference was collected");
                    return;
                }

                Log.d("GetWeather", "Displaying current temperature for " + _city);
                final String message = String.format(activity.getString(R.string.temperature_format),
                        _city, conditions.getMeasurements().get("temp"));
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } catch (MalformedURLException e) {
            Log.d("GetWeather-current", "Error building URL");
        } catch (IOException e) {
            Log.d("GetWeather-current", "Error opening connection");
        }
    }

    private void getForecast() {
        Gson gson = new Gson();
        String queryStr = buildQueryString(_city);
        if (queryStr == null) {
            return;
        }

        try {
            URL url = new URL(FORECAST_URL + "?" + queryStr);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept-Charset", StandardCharsets.UTF_8.name());
            InputStream responseStream = connection.getInputStream();

            try (Scanner scanner = new Scanner(responseStream)) {
                String response = scanner.useDelimiter("\\A").next();
                System.out.println(response);
                Forecast forecast = gson.fromJson(response, Forecast.class);

                final Activity activity = _caller.get();
                if (activity == null) {
                    Log.d("GetWeather", "Activity reference was collected");
                    return;
                }

                Log.d("GetWeather", "Displaying forecast for " + _city);
                final List<ForecastItem> items = forecast.getItems();

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ListView listView = (ListView)activity.findViewById(R.id.listForecast);

                        List<String> itemStrings = new ArrayList<>();
                        for (ForecastItem item : items) {
                            itemStrings.add(item.toString());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                                android.R.layout.simple_list_item_1,
                                android.R.id.text1,
                                itemStrings);

                        listView.setAdapter(adapter);
                    }
                });
            }
        } catch (MalformedURLException e) {
            Log.d("GetWeather-forecast", "Error building URL");
        } catch (IOException e) {
            Log.d("GetWeather-forecast", "Error opening connection");
        }
    }

    /**
     * Build the necessary query string for connecting to the API.
     * @param city      The city to be referenced in the query string.
     * @return          The complete query string.
     */
    private static String buildQueryString(String city) {
        try {
            return String.format("q=%s&apiKey=%s&units=imperial",
                    URLEncoder.encode(city, StandardCharsets.UTF_8.name()),
                    URLEncoder.encode(API_KEY, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            System.out.println("\nError building query string");
            return null;
        }
    }
}
