package com.jagsa.ekimaid.example;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/22, 2016.
 * Copyright 2016 Arata Furukawa <old.river.new@gmail.com>.
 */

public class MainActivity extends AppCompatActivity {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // メンバ変数を宣言します
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private EditText               edit_search_word;    // 検索する文字を入力するためのエディットビューです
    private TextView               text_output;         // 出力を表示するためのテキストビューです
    private Button                 button_search;       // 検索ボタンのビューです
    private AsyncSearchStationTask task_search_station; // 駅を検索をする非同期タスクです
    private static final String KEYWORD = "橙幻郷+メイド喫茶";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // アプリが実行されたときに行う処理を定義します
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画面上のビューを取得してメンバ変数に保存します
        edit_search_word = (EditText) findViewById(R.id.edit_search_word);
        text_output      = (TextView) findViewById(R.id.text_output);
        button_search    = (Button)   findViewById(R.id.button_search);

        // 検索ボタンがタップされたときの動作を登録します
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_search.setEnabled(false);  // 検索ボタンを無効にして連打されないようにします
                text_output.setText("");          // 画面に表示される文字を空にします

                // 入力欄(テキストエディット)に入力されている文字を取得します
                String serach_word = edit_search_word.getText().toString();

                if(serach_word.isEmpty()) {
                    // 入力が空だった場合は検索を実行せずにメッセージを出力します
                    print("検索する文字を入力してください。");
                    button_search.setEnabled(true); // ボタンを有効に戻します
                } else {
                    // 入力が空でなければ非同期に駅を検索するタスクを作成します
                    task_search_station = new AsyncSearchStationTask(MainActivity.this);

                    // タスクを実行します
                    // 引数は検索する文字列にしてあります(詳しくは、AsyncSearchStationTaskを見てください)
                    task_search_station.execute(serach_word);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 駅を検索する非同期タスクのクラス(AsyncSearchStationTask)を定義します
    //
    // 非同期タスクを作るために、AsyncTaskを継承します。
    // AsyncTaskのあとの<から>までには、カンマ区切りで順番に
    //   1. タスクの引数の型
    //   2. タスクのonProgressUpdateに渡されるの型(今回は使いません)
    //   3. タスクのonPostExecuteに渡される型
    // を指定します。
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private class AsyncSearchStationTask extends AsyncTask<String, Void, String> {
        private String error_message = ""; // 通信中にエラーが起きた場合に保存するメッセージ文字列です
        private Context context; // ダイアログを表示するためのコンテキストです

        AsyncSearchStationTask(Context context){
            this.context = context;
        }

        //------------------------------------------------------------------------------------------
        // doInBackgroundメソッドをオーバーライドします
        //
        //   引　数: String... -> 検索したい文字列
        //   戻り値: String    -> 通信で取得したレスポンスの文字列
        //
        // このメソッドは非同期に実行されます。
        // このメソッドの中でUIに関わる操作はできません。
        // 引　数の型は、AsyncTaskに指定した１つ目の型と同じにする必要があります。
        // 戻り値の型は、AsyncTaskに指定した３つ目の型と同じにする必要があります。
        //------------------------------------------------------------------------------------------
        @Override
        protected String doInBackground(String... strings) {
            try {
                // APIのリクエストのためのURLを作ります
                // ここでは駅すぱあとの簡易駅情報取得APIを呼び出します
                // APIの仕様は公式リファレンスを読んで下さい
                // 公式リファレンス： http://docs.ekispert.com/v1/le/station/light.html
                // ここでは、クエリパラメータnameに引数で受け取った検索文字を指定します
                // 駅すぱあとのAPIリクエストには、APIキーをクエリパラメータkeyに指定する必要があります
                URL api_request_url = new URL(
                        "http://api.ekispert.jp/v1/json/station/light"
                                + "?key=" + APIKEY.ekispert // APIを利用するために発行されたAPIキーです
                                + "&type=train"             // 電車の駅に限定します
                                + "&name=" + strings[0]);   // 引数で渡された文字で検索します

                // URLにHTTP通信でリクエストを送信します
                // HTTP.requestメソッドはHTTP.javaに定義してあります
                String response = HTTP.request(api_request_url);

                // レスポンスの文字列を戻り値として返します
                return response;

            } catch (IOException e) {
                // 通信中にエラーが発生しました
                e.printStackTrace();
                error_message = e.toString();

                // 今回は、通信エラーが発生した場合はnullを返すようにします
                return null;
            }
        }

        //------------------------------------------------------------------------------------------
        // onPostExecuteメソッドをオーバーライドします
        //
        //   引　数: String -> 通信で取得したレスポンスの文字列
        //                    (通信中にエラーが発生した場合はnullが渡されます)
        //   戻り値: なし
        //
        // このメソッドはタスクの実行が終わった後に実行されます。
        // このメソッドの中ではUIに関わる操作を行うことができます。
        //------------------------------------------------------------------------------------------
        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);

            // 通信エラーの場合はnullになるようにしてあるため、nullでないかチェックをします
            if (results != null) {
                // nullでない場合は結果が入っていますので、ここで中身を取得・操作します
                // JSONオブジェクトの操作に失敗するとJSONExceptionという例外が発生するので
                // try-catch構文で例外処理をする必要があります
                try {
                    // レスポンスの文字をJSONとして解析します
                    JSONObject json = new JSONObject(results);

                    // ルートのResultSetを取得します
                    JSONObject resultSet = json.getJSONObject("ResultSet");

                    // 検索結果があればResultSetにPointが入っていますので、確認します
                    if(resultSet.has("Point")) {
                        // 実際にPointを取得します
                        JSONArray points = resultSet.optJSONArray("Point");
                        // 結果が一件だった場合、optJSONArrayで取得するとNullになるのでgetJSONObjectで
                        // 取得し直します
                        if (points == null) {
                            points = new JSONArray();
                            points.put(resultSet.getJSONObject("Point"));
                        }

                        final List<String> stations = new ArrayList<>();
                        final List<Integer> codes = new ArrayList<>();
                        for (int i = 0; i < points.length(); ++i) {
                            // Pointの中のi番目のStationを取得します
                            JSONObject station = points
                                    .getJSONObject(i)
                                    .getJSONObject("Station");
                            // Stationに入っているNameとcodeをそれぞれ取得して配列に追加します
                            stations.add(station.getString("Name"));
                            codes.add(station.getInt("code"));
                        }
                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                        dialog.setTitle("駅を選んでください。");
                        dialog.setItems(
                                stations.toArray(new CharSequence[stations.size()]),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        int code = codes.get(which);
                                        println("選択された駅 " + stations.get(which));
                                        println("選択された駅のコード " + code);

                                        // 駅コードから駅付近のメイドカフェを検索する非同期タスクを実行します。
                                        new AsyncSearchMaidCafeTask().execute(code);
                                    }
                                });
                        dialog.create().show();
                    } else {
                        println("検索結果は0件でした。");
                    }
                } catch (JSONException e) {
                    // JSONの解析中にエラーが発生しました
                    e.printStackTrace();
                    errorOnTask(e.toString());
                }
            } else {
                // resultsがnullの場合は、通信中にエラーが発生しましたので、メッセージを表示します
                // エラーを示す文字はerror_messageに入れるようになっています。
                errorOnTask(error_message);
            }
            // 成功・失敗にかかわらず、ボタンを有効に戻します
            // 失敗した場合もちゃんとボタンを有効に戻さないと、二度と検索できなくなりますね
            button_search.setEnabled(true);
        }
    }


    /////////////////////////////////////////////////////////////////////////////////
    //
    // AsyncSearchMaidCafeTaskを定義します
    //
    /////////////////////////////////////////////////////////////////////////////////
    private class AsyncSearchMaidCafeTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... code) {
            // ここで通信を行います
            return null;
        }
        @Override
        protected void onPostExecute(String response) {
            super.onPostExecute(response);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // アプリの画面に文字を表示するメソッドを定義します
    //
    //   引　数: String -> 表示したい文字列
    //   戻り値: なし
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void print(String text) {
        text_output.setText(text_output.getText() + text);
    }
    private void println(String text) {
        print(text + '\n');
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // エラーが発生した時に表示するためのメソッドを定義します
    //
    //   引　数: String -> エラーメッセージ
    //   戻り値: なし
    //
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void errorOnTask(String message){
        println("エラーが発生しました: ");
        println(message);
        println("より詳しい情報については、Android StudioのLogcatからスタックトレースを確認してください。");

        // 念のため、ボタンを有効に戻します
        button_search.setEnabled(true);
    }
}
