package tech.bfitzsimmons.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getWeather(View view){
        EditText input = (EditText) findViewById(R.id.input);

        //hide keyboard when we press button
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

        String city = input.getText().toString().toLowerCase();

        String encodedCity;
        try {
            encodedCity = URLEncoder.encode(city, "UTF-8");
            System.out.println(encodedCity);
            new WeatherData().execute("http://api.openweathermap.org/data/2.5/weather?q="+encodedCity+"&appid=633afad2bbe22922f50ed2969c9669f1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class WeatherData extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder result = new StringBuilder();
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1){
                    result.append((char) data);
                    data = reader.read();
                }

                return result.toString();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response) {
            try{
                JSONObject jsonObject = new JSONObject(response);

                String weather = jsonObject.getString("weather");
                JSONArray weatherArr = new JSONArray(weather);
                String main = weatherArr.getJSONObject(0).getString("main");
                TextView description = (TextView) findViewById(R.id.weatherDescription);
                description.setText("Weather: "+main);

                String tempData = jsonObject.getString("main");
                JSONObject temps = new JSONObject(tempData);
                String temp = temps.getString("temp");
                TextView tempDescription = (TextView) findViewById(R.id.temperature);
                tempDescription.setText("Temperature: "+temp);
            } catch (Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "That city isn't in the database", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
