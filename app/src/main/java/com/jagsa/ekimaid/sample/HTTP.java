package com.jagsa.ekimaid.sample;

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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // URLにHTTP通信でリクエストを送るメソッドを定義します
    //
    //   引　数: URL    -> 通信したいURLです
    //   戻り値: String -> 通信で取得したレスポンスの文字列です
    //
    // --- 注意 ---------------------------------------------------------------------------
    //
    //  HTTP通信のように、Androidアプリでインターネット接続を行う場合は、AndroidManifest.xmlの
    //  manifestタグに以下のようにパーミッションを宣言する必要があります。
    //
    //    <uses-permission android:name="android.permission.INTERNET" />
    //
    //  詳しくは、AndroidManifest.xmlを見てください。
    // --------------------------------------------------------------------------------------
    //
    // 難しい話
    //   - HTTPメソッドはGETに固定しています(今回はGET以外扱いません)
    //   - エラーが発生した場合、キャッチせずにそのまま呼び出し元に投げます。
    //     例外ハンドリングは呼び出し元で行います。
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    static String request(URL url) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader    reader     = null;
        InputStream       in;
        try {
            connection = (HttpURLConnection) url.openConnection(); // URLに対するHTTP通信を開きます
            connection.setRequestMethod("GET");                    // HTTPリクエストはGETで固定します
            connection.connect();                                  // 通信を接続します


            try {
                // レスポンスを取得するためのストリームを開きます
                in = connection.getInputStream();
            } catch (IOException e){
                // ストリームが開けない場合、エラーストリームを開きます
                in = connection.getErrorStream();
            }
            // ストリームがどちらも開けなかった場合は例外を投げます
            if(in == null){
                throw new IOException("The stream could not be opened.");
            }

            // ストリームからレスポンスを読み取るために、便利なバッファリーダーを使います
            // 面倒な処理を代わりにやってくれるおまじないだと思ってください
            reader = new BufferedReader(new InputStreamReader(in));

            // レスポンスを文字列に変換します
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            // レスポンスを戻り値として返します
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
