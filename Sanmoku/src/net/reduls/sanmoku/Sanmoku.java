package net.reduls.sanmoku;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.util.Log;
import android.text.Html;
import android.content.Intent;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.SpannableString;
import java.util.List;
//TextWatcherは、EditTextの内容をリアルタイムで反映させるもの
public class Sanmoku extends Activity implements TextWatcher
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //テキストフィールド
        EditText searchBar = (EditText)findViewById(R.id.search_bar);
        //リスナーに登録
        searchBar.addTextChangedListener(this);

        String key = getIntent().getStringExtra("search.key");
        getIntent().putExtra("search.key","");
        if(key!=null && key.equals("")==false)
        	searchBar.setText(key);
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    public void afterTextChanged(Editable s) {
        String key = s.toString();
        LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
        //removeAllViews():全ての子ビューを削除するメソッド
        resultArea.removeAllViews();
        
        //文章の解析部分
        if(key.length() > 0) { 
            for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(key)) {
                TextView txt = new TextView(this);
                //SpannableString 文字の装飾
                SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature);
                txt.setText(spannable, TextView.BufferType.SPANNABLE);
                resultArea.addView(txt);
            }
        }
    }
}