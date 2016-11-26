package org.jagsa.ekimaidsample;

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
    protected String error_message = "";

    // doInBackgroundメソッドをオーバーライドします
    // このメソッドは非同期に実行されます
    // このメソッドの中でUIの操作はできません
    // 引数に検索文字列を受け取り、通信で取得したJSONを返します
    @Override
    protected JSONObject doInBackground(String... strings) {
        BufferedReader reader = null;
        StringBuilder response = new StringBuilder();
        HttpURLConnection connection = null;
        try {
            // APIのリクエストのためのURLを作ります
            // ここでは駅すぱあとの簡易駅情報取得APIを呼び出します
            // APIの仕様は公式リファレンスを読んで下さい
            // 公式リファレンス： http://docs.ekispert.com/v1/le/station/light.html
            // ここでは、クエリパラメータnameに引数で受け取った検索文字を指定します
            // 駅すぱあとのAPIリクエストには、APIキーをクエリパラメータkeyに指定する必要があります
            URL api_request_url = new URL(
                    "http://api.ekispert.jp/v1/json/station/light"
                            + "?key=" + APIKEY.ekispert
                            + "&name=" + strings[0]);

            // URLに対するHTTP通信を開きます
            connection = (HttpURLConnection) api_request_url.openConnection();
            // HTTPリクエストはGETを指定します
            connection.setRequestMethod("GET");
            // 通信を接続します
            connection.connect();

            // レスポンスを取得するためのストリームを開きます
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // レスポンスを文字列に変換します
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            // 文字列に変換したレスポンスをJSONオブジェクトに変換して、戻り値として返します
            return new JSONObject(response.toString());

        } catch (JSONException | IOException e) {
            // 何らかのエラーが発生したため、スタックトレースして通信をキャンセルします
            e.printStackTrace();
            error_message = e.toString();
            cancel(true);
        } finally {
            // 通信の成否にかかわらず、ストリームが開いている場合はcloseします
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    cancel(true);
                }
            }
            // 接続を閉じます
            if(connection != null) {
                connection.disconnect();
            }
        }
        // 正常に通信を終えた場合は既にreturnしているはずなので
        // ここが実行される場合は何らかのエラーが発生しています
        // ですので、ここではnullを返すことにします
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
