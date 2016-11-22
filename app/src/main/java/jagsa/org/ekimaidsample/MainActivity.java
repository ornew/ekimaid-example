package jagsa.org.ekimaidsample;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText edit_search_word;
    private TextView text_output;
    private Button button_search;
    private SearchStationTask task;

    // 出力を表示するためのテキストビューに文字列を追記する関数です
    private void print(String text) {
        text_output.setText(text_output.getText() + text);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 検索する文字を入力するエディットビューを取得します
        edit_search_word = (EditText) findViewById(R.id.edit_search_word);
        // 出力を表示するためのテキストビューを取得します
        text_output = (TextView) findViewById(R.id.text_output);
        // 検索ボタンを取得します
        button_search = (Button) findViewById(R.id.button_search);

        // 検索ボタンがタップされたときの動作を記述します
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 検索ボタンをオフにします
                // 連打されて何度も通信が発生してしまうことを防ぎます
                button_search.setEnabled(false);
                // 出力をクリアします
                text_output.setText("");
                // 非同期検索タスクを作成します
                task = new SearchStationTask() {
                    // onPostExecuteメソッドをオーバーライドします
                    // このメソッドはタスクの実行が終わった後に実行されます
                    @Override
                    protected void onPostExecute(JSONObject results) {
                        super.onPostExecute(results);
                        if (results != null) {
                            // resultsがnullでないときは結果が入っているので
                            // 中身を取得します
                            try {
                                // 解析はせずにJSONをそのまま表示します
                                print(results.toString(2));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        // 通信の成功・失敗にかかわらず、ボタンを再びオンにします
                        button_search.setEnabled(true);
                    }

                    @Override
                    protected void onCancelled() {
                        super.onCancelled();
                        print("エラーが発生しました。");
                    }
                };
                // 入力エディットに入力されている文字を取得します
                String serach_word = edit_search_word.getText().toString();
                if(serach_word != null && !serach_word.isEmpty()) {
                    // 入力が空でなければ非同期検索タスクを実行します
                    // 引数は検索する文字列にしてあります(詳しくは、SearchTaskを見てください)
                    task.execute(serach_word);
                } else {
                    // 入力が空だった場合は実行せずにメッセージを出力します
                    print("検索する文字を入力してください。");
                    // ボタンを再びオンにします
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
            // ネットワークに接続で来ていない場合はボタンをオフにして、メッセージをだす
            button_search.setEnabled(false);
            print("ネットワークに接続していません。ネットワークに接続して、アプリを再起動してください。");
        }

    }
}