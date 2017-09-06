package com.example.ikram.weatherapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ikram.weatherapp.*;


import org.json.JSONException;
import org.json.JSONObject;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends Activity {

    TextView weatherIcon;
    EditText cityName;
    TextView resultTextView;
    TextView tempTextView;
    TextView textViewCity;
    Typeface weatherFont;
    TextView textView;
    Button button;
    ImageView buttonBack;
    private static final String OPEN_WEATHER_MAP_API = "b0c3d978ca00e07b8f8e0b4769dfd099";
    public void back (View view){

        cityName.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        button.setVisibility(View.VISIBLE);
        weatherIcon.setVisibility(View.INVISIBLE);
        tempTextView.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.INVISIBLE);
        textViewCity.setVisibility(View.INVISIBLE);
        buttonBack.setVisibility(View.INVISIBLE);
        cityName.setText(null);
        weatherIcon.setText(null);
        tempTextView.setText(null);
        resultTextView.setText(null);
        textViewCity.setText(null);



    }
    public void findWeather(View view) {

        cityName.setVisibility(View.INVISIBLE);
        textView.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);
        weatherIcon.setVisibility(View.VISIBLE);
        tempTextView.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
        textViewCity.setVisibility(View.VISIBLE);
        buttonBack.setVisibility(View.VISIBLE);

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);

        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");

            DownloadTask task = new DownloadTask();
            task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName +"&units=metric");


        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

        }



    }

    public static String setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = "&#xf00d;";
            } else {
                icon = "&#xf02e;";
            }
        } else {
            switch(id) {
                case 2 : icon = "&#xf01e;";
                    break;
                case 3 : icon = "&#xf01c;";
                    break;
                case 7 : icon = "&#xf014;";
                    break;
                case 8 : icon = "&#xf013;";
                    break;
                case 6 : icon = "&#xf01b;";
                    break;
                case 5 : icon = "&#xf019;";
                    break;
            }
        }
        return icon;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        weatherFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/weathericons-regular-webfont.ttf");
        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
        tempTextView = (TextView) findViewById(R.id.tempTextView);
        textViewCity = (TextView) findViewById(R.id.textViewCity);
        weatherIcon = (TextView) findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        buttonBack = (ImageView) findViewById(R.id.imageView3);

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.addRequestProperty("x-api-key", OPEN_WEATHER_MAP_API);

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {


                JSONObject jsonObject = new JSONObject(result);
                JSONObject details = jsonObject.getJSONArray("weather").getJSONObject(0);
                String city = jsonObject.getString("name").toUpperCase(Locale.US) + ", " + jsonObject.getJSONObject("sys").getString("country");
                String description = details.getString("description").toUpperCase(Locale.US);
                JSONObject main = jsonObject.getJSONObject("main");
                String temperature = String.format("%.0f", main.getDouble("temp"))+ "°C";
                String iconText = setWeatherIcon(details.getInt("id"),
                        jsonObject.getJSONObject("sys").getLong("sunrise") * 1000,
                        jsonObject.getJSONObject("sys").getLong("sunset") * 1000);

                textViewCity.setText(city);
                if (description != "") {

                    resultTextView.setText(description);
                    weatherIcon.setText(Html.fromHtml(iconText));

                } else {

                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

                }


                if (temperature != "") {

                    tempTextView.setText(temperature);

                } else {

                    Toast.makeText(getApplicationContext(), "Could not find temp", Toast.LENGTH_LONG);

                }

            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }



        }
    }


}
