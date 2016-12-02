package org.jagsa.ekimaidsample;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created on 11/22, 2016.
 * Copyright 2016 Arata Furukawa <old.river.new@gmail.com>.
 */

public class MainActivity extends AppCompatActivity {
    private EditText            edit_search_word;    // 検索する文字を入力するエディットテキストビューです
    private TextView            text_output;         // 出力を表示するためのテキストビューです
    private Button              button_search;       // 検索ボタンのビューです
    private MySearchStationTask task_search_station; // 検索をする非同期タスクです

    // 出力を表示するためのテキストビュー(text_output)に文字列を追記する関数です
    private void print(String text) {
        text_output.setText(text_output.getText() + text);
    }
    private void println(String text) {
        print(text + '\n');
    }

    // タスクの実行中にエラーが発生した場合に行う表示とボタンの復帰処理です
    private void errorOnTask(){
        println("エラーが発生しました。");
        println("詳細はスタックトレースをAndroid StudioのLogcatで確認してください。");
        button_search.setEnabled(true);
    }

    // 駅を検索する非同期タスクを継承してこのアクティビティ用にカスタマイズします
    private class MySearchStationTask extends SearchStationTask {
        // onPostExecuteメソッドをオーバーライドします
        // このメソッドはタスクの実行が終わった後に実行されます
        // 引数は通信で取得したJSONオブジェクトです(nullの場合はエラーです)
        @Override
        protected void onPostExecute(JSONObject results) {
            super.onPostExecute(results);

            // 通信エラーの場合はnullになるようにしてあるため、nullでないかチェックをします
            if (results != null) {
                // nullでない場合は結果が入っていますので、ここで中身を取得・操作します
                // JSONオブジェクトの操作に失敗するとJSONExceptionという例外が発生するので
                // try-catch構文で例外処理をする必要があります
                try {
                    //==========================================
                    // 解析はせずにJSONをそのまま表示します
                    println(results.toString(2));
                    //==========================================
                } catch (JSONException e) {
                    e.printStackTrace();
                    errorOnTask();
                }
            } else {
                // nullの場合はエラーが発生しています
                errorOnTask();
            }
            // 成功・失敗にかかわらず、ボタンを有効に戻します
            button_search.setEnabled(true);
        }
    }

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
                text_output.setText("");          // 出力を空にします

                // 駅を検索する非同期タスクを作成します
                task_search_station = new MySearchStationTask();

                // エディットに入力されている文字を取得します
                String serach_word = edit_search_word.getText().toString();

                if(serach_word.isEmpty()) {
                    // 入力が空だった場合は検索を実行せずにメッセージを出力します
                    print("検索する文字を入力してください。");
                    button_search.setEnabled(true); // ボタンを有効に戻します
                } else {
                    // 入力が空でなければ非同期検索タスクを実行します
                    // 引数は検索する文字列にしてあります(詳しくは、SearchStationTaskを見てください)
                    task_search_station.execute(serach_word);
                }
            }
        });
    }
}
