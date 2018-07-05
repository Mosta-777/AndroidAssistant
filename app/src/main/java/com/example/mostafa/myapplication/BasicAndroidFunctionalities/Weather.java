package com.example.mostafa.myapplication.BasicAndroidFunctionalities;

import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.mostafa.myapplication.CommunicationInterfaces;
import com.example.mostafa.myapplication.IntentAnalyzerAndRecognizer;
import com.example.mostafa.myapplication.POJOS.Entity;
import com.example.mostafa.myapplication.POJOS.Forecast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

/**
 * Created by Mahmoud Salah on 7/4/2018.
 */

public class Weather {

    public static final String LOG_TAG = Weather.class.getName();
    private CommunicationInterfaces.MainActivityFunctionalityClassesInterface analyzerinterface;
    private static String date = null;
    private static String winningSentence = null;
    private final String url =
            "https://query.yahooapis.com/v1/public/yql?u=c&lang=ar&q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22cairo%2C%20eg%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

    private static HashMap<String, String> translateMap = new HashMap<String, String>() {{
        put("Jan","يناير");
        put("Feb","فبراير");
        put("Mar","مارس");
        put("April","ابريل");
        put("May","مايو");
        put("Jun","يونيو");
        put("Jul","يوليو");
        put("Aug","اغسطس");
        put("Sep","سبتمبر");
        put("Oct","اكتوبر");
        put("Nov","نوفمبر");
        put("Dec","ديسمبر");
        put("Sunny","مشمس");
        put("Sat","السبت");
        put("Sun","الأحد");
        put("Mon","الأثنين");
        put("Tue","الثلاثاء");
        put("Wed","الأربعاء");
        put("Thu","الخميس");
        put("Fri","الجمعة");
    }};

    public Weather(CommunicationInterfaces.MainActivityFunctionalityClassesInterface intentAnalyzerAndRecognizer,
                   ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        analyzerinterface = intentAnalyzerAndRecognizer;
        findDate(theWinningSentences);
        if(winningSentence!=null)
            analyzerinterface.onChoosingTheWinningSentence(winningSentence);
        else
            analyzerinterface.onChoosingTheWinningSentence(
                    IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(0)));
        analyzerinterface.onWeatherSucceeded(url);

    }

    private static boolean findDate(ArrayList<ArrayList<Entity>> theWinningSentences)
    {
        for(int i=0; i<theWinningSentences.size(); i++)
        {
            if((Alarm.isThereOnlyOne(IntentAnalyzerAndRecognizer.DATETIME_ENTITY,theWinningSentences.get(i))))
            {
                int dateTimeEntity = IntentAnalyzerAndRecognizer.
                        containsEntity(IntentAnalyzerAndRecognizer.DATETIME_ENTITY, theWinningSentences.get(i));
                if(dateTimeEntity != -1)
                {
                    winningSentence = IntentAnalyzerAndRecognizer.extractTextFromSentence(theWinningSentences.get(i));
                    date = (String) theWinningSentences.get(i).get(dateTimeEntity).getValue();
                    return true;
                }
            }
        }
        return false;
    }

    public static String getWeather(ArrayList<Forecast> weather)
    {
        String selectedWeather = null;
        if(date==null)
        {
            selectedWeather = translateWeather(weather.get(0));
        }
        else {
            Calendar currentDate = new GregorianCalendar();
            // reset hour, minutes, seconds and millis
            currentDate.set(Calendar.HOUR_OF_DAY, 0);
            currentDate.set(Calendar.MINUTE, 0);
            currentDate.set(Calendar.SECOND, 0);
            currentDate.set(Calendar.MILLISECOND, 0);
            Calendar maxDate = new GregorianCalendar();
            maxDate.set(Calendar.HOUR_OF_DAY, 0);
            maxDate.set(Calendar.MINUTE, 0);
            maxDate.set(Calendar.SECOND, 0);
            maxDate.set(Calendar.MILLISECOND, 0);
            maxDate.add(Calendar.DAY_OF_YEAR, 10);
            int userDateYear = Integer.parseInt(date.substring(0, 4));
            int userDateMonth = Integer.parseInt(date.substring(5, 7));
            int userDateDay = Integer.parseInt(date.substring(8, 10));

            if (weather != null) {
                Calendar userDate = Calendar.getInstance();
                userDate.set(userDateYear, userDateMonth - 1, userDateDay);
                if (currentDate.getTimeInMillis() <= userDate.getTimeInMillis() && userDate.getTimeInMillis() <= maxDate.getTimeInMillis()) {
                    for (int i = 0; i < weather.size(); i++) {
                        if (date.substring(8, 10).equals(weather.get(i).getDate().substring(0, 2))) {
                            selectedWeather = translateWeather(weather.get(i));
                            break;
                        }

                    }
                }
            }
        }
        date = null;
        return selectedWeather;
    }

    private static String translateWeather(Forecast weather)
    {
        String selectedWeather = "";
        selectedWeather = translateMap.get(weather.getDay()) + ", " + weather.getDate().substring(0,3)
                + translateMap.get(weather.getDate().substring(3,6)) +" "+ weather.getDate().substring(7) + "\n"
                + "حالة الجو: "+translateMap.get(weather.getCondition()) + "\n" + "العظمي: " + "℃" + weather.getHighTemp()
                + "\n" + "الصغري: " + "℃" + weather.getLowTemp();
        return selectedWeather;
    }

    public static ArrayList<Forecast> fetchWeatherData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        ArrayList<Forecast> weather = parseJson(jsonResponse);
        return weather;
        // Extract relevant fields from the JSON response and create an {@link Event} object

        // Return the {@link Event}
    }
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("URL", "Error with creating URL ", e);
        }
        return url;
    }


    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<Forecast> parseJson(String jsonResponse)
    {
        ArrayList<Forecast> weather = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONObject resultsJson = baseJsonResponse.getJSONObject("query").getJSONObject("results")
                    .getJSONObject("channel").getJSONObject("item");
            JSONArray weatherForecastJson = resultsJson.getJSONArray("forecast");
            for(int i=0;i<weatherForecastJson.length();i++)
            {
                JSONObject forecast = weatherForecastJson.getJSONObject(i);
                String date = forecast.getString("date");
                String day = forecast.getString("day");
                String condition = forecast.getString("text");
                String high = forecast.getString("high");
                String low = forecast.getString("low");

                weather.add(new Forecast(date, day, low, high, condition));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally {
            return weather;
        }
    }
    public static class fetchForecastData extends AsyncTask<String, Void, ArrayList<Forecast>>
    {

        @Override
        protected ArrayList<Forecast> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            ArrayList<Forecast> json = fetchWeatherData(urls[0]);
            return json;
        }

    }

}

