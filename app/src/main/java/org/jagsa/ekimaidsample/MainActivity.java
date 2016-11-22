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
    private EditText          edit_search_word; // 検索する文字を入力するエディットテキストビューです
    private TextView          text_output;      // プログラムからの出力を表示するためのテキストビューです
    private Button            button_search;    // 検索ボタンのビューです
    private SearchStationTask task;             // 検索をする非同期タスクです

    // 出力を表示するためのテキストビュー(text_output)に文字列を追記する関数です
    private void print(String text) {
        text_output.setText(text_output.getText() + text);
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
                // 検索ボタンを無効にして、連打されて何度も通信が発生してしまうことを防ぎます
                button_search.setEnabled(false);
                // 出力を空にします
                text_output.setText("");

                // 非同期検索タスクを作成します
                task = new SearchStationTask() {
                    // onPostExecuteメソッドをオーバーライドします
                    // このメソッドはタスクの実行が終わった後に実行されます
                    // 引数は通信で取得したJSONオブジェクトです(nullの場合はエラーです)
                    @Override
                    protected void onPostExecute(JSONObject results) {
                        super.onPostExecute(results);

                        // エラーの場合はnullになるようにしてあるため、nullでないかチェックをします
                        if (results != null) {
                            // null出ない場合は結果が入っていますので、ここで中身を取得・操作します
                            // JSONオブジェクトの操作に失敗するとJSONExceptionという例外が発生するので
                            // try-catch構文で例外処理をする必要があります
                            try {
                                //==========================================
                                // 解析はせずにJSONをそのまま表示します
                                print(results.toString(2));
                                //==========================================
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // 通信の成功・失敗にかかわらず、ボタンを再び有効にします
                        button_search.setEnabled(true);
                    }

                    @Override
                    protected void onCancelled() {
                        super.onCancelled();
                        print("エラーが発生しました。");
                    }
                };

                // エディットに入力されている文字を取得します
                String serach_word = edit_search_word.getText().toString();
                if(!serach_word.isEmpty()) {
                    // 入力が空でなければ非同期検索タスクを実行します
                    // 引数は検索する文字列にしてあります(詳しくは、SearchTaskを見てください)
                    task.execute(serach_word);
                } else {
                    // 入力が空だった場合は検索を実行せずにメッセージを出力します
                    print("検索する文字を入力してください。");
                    // ボタンを再び有効にします
                    button_search.setEnabled(true);
                }
            }
        });

        // ネットワークの接続状態を確認します
        // ACCESS_NETWORK_STATEパーミッションが必要です
        // !! このサンプルでは起動時にしか確認しません
        // !! 実際には起動後にネットワーク状態が変化したことをチェックすべきです
        ConnectivityManager connectivity =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivity.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            // ネットワークに接続できていない場合はボタンを無効にしてメッセージを表示します
            button_search.setEnabled(false);
            print("ネットワークに接続していません。ネットワークに接続して、アプリを再起動してください。");
        }

    }
}
