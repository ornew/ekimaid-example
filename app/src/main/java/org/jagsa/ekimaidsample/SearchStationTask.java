package org.jagsa.ekimaidsample;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

/**
 * Created on 11/22, 2016.
 * Copyright 2016 Arata Furukawa <old.river.new@gmail.com>.
 */

class SearchStationTask extends AsyncTask<String, Void, JSONObject> {
    // doInBackgroundメソッドをオーバーライドします
    // このメソッドは非同期に実行されます
    // このメソッドの中でUIの操作はできません
    // 引数に検索文字列を受け取り、通信で取得したJSONを返します
    @Override
    protected JSONObject doInBackground(String... strings) {
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

            String response = HTTP.request(api_request_url);

            // 文字列に変換したレスポンスをJSONオブジェクトに変換して、戻り値として返します
            return new JSONObject(response);

        } catch (JSONException | IOException e) {
            // エラーが発生しました
            e.printStackTrace();

            // 今回は、通信エラーが発生した場合はnullを返すようにします
            return null;
        }
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
