package tw.tcnr26.m2002;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class M2002 extends AppCompatActivity {

    private GridView gridView;
    private Intent recognizerIntent;
    private ArrayList<Object> messageList;
    private ArrayAdapter<Object> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m2002);
        setupViewComponent();
    }

    private void setupViewComponent() {
        gridView = (GridView)findViewById(R.id.gridView);
        //-------- 語音辨識檢查服務是否存在----------
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        if (!hasRecognizer()) {
            Toast.makeText(getApplicationContext(), "無語音辨識服務", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //------------------------------------------------------
        messageList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, messageList);
        gridView.setNumColumns(3);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new MyOnItemClickListener());
    }

    //--------Intent 語音辨識----------
    private boolean hasRecognizer() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(recognizerIntent,
                PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() != 0) { //檢查該Intent 是否有註冊
            return true;
        } else {
            return false;
        }
    }

    public void b001ON(View view) {
         /*--------------
        RecognizerIntent.EXTRA_LANGUAGE_MODEL   根據所選語音模型識別
        RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        設定辨識語系 如RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString()
        是以英文固定辨識
        RecognizerIntent.EXTRA_PROMPT 提示文字
        RecognizerIntent.EXTRA_MAX_RESULTS 最多答案筆數
        RecognizerIntent.EXTRA_RESULTS 辨識結果
        --------------*/
        recognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
        );

        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.JAPANESE.toString()); // 此行換語言
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "請說...");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 6);
        startActivityForResult(recognizerIntent, 1);//啟動語音辨識服務 Intent

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent it) {
        super.onActivityResult(requestCode, resultCode, it);
        messageList.clear();
        if (requestCode != 1) {
            return;
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        List<String> list = it.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        //---語音辨識結果列表
        for (String s : list) {
            messageList.add(s);
        }
        adapter.notifyDataSetChanged();
    }

    //============================inner class======================================
    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String keyword = parent.getItemAtPosition(position).toString();
            Intent web = new Intent(Intent.ACTION_WEB_SEARCH);
            web.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //-- 帶入參數到URL
            web.putExtra(SearchManager.QUERY, keyword);
            startActivity(web);
            Toast.makeText(getApplicationContext(), keyword, Toast.LENGTH_SHORT).show();
        }
    }
}