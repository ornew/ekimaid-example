package org.jagsa.ekimaidsample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created on 12/02, 2016.
 * Copyright 2016 Arata Furukawa <old.river.new@gmail.com>.
 */

class HTTP {
    static String request(URL url) throws IOException {
        BufferedReader    reader     = null;
        InputStream       in;
        HttpURLConnection connection = null;
        try {
            // URLに対するHTTP通信を開きます
            connection = (HttpURLConnection) url.openConnection();
            // HTTPリクエストはGETを指定します
            connection.setRequestMethod("GET");
            // 通信を接続します
            connection.connect();

            // レスポンスを取得するためのストリームを開きます
            try {
                in = connection.getInputStream();
            } catch (IOException e){
                in = connection.getErrorStream();
            }
            if(in == null){
                throw new IOException("The stream could not be opened.");
            }
            reader = new BufferedReader(new InputStreamReader(in));

            // レスポンスを文字列に変換します
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();
        } finally {
            // 接続を閉じます
            if(connection != null) {
                connection.disconnect();
            }
            // 通信の成否にかかわらず、ストリームが開いている場合はcloseします
            if (reader != null) {
                reader.close();
            }
        }
    }
}
