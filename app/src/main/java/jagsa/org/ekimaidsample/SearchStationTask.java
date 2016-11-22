package jagsa.org.ekimaidsample;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created on 11/22, 2016.
 * Copyright 2016 Arata Furukawa <old.river.new@gmail.com>.
 */

public class SearchStationTask extends AsyncTask<String, Void, JSONObject> {
    @Override
    protected JSONObject doInBackground(String... strings) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        try {
            URL api_request_url = new URL(
                    "http://api.ekispert.jp/v1/json/station/light"
                            + "?key=" + APIKEY.ekispert
                            + "&name=" + strings[0]);

            HttpURLConnection connection = (HttpURLConnection) api_request_url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(api_request_url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return new JSONObject(response.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            cancel(true);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(JSONObject results) {
        super.onPostExecute(results);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
