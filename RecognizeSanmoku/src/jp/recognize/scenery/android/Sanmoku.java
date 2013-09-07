package jp.recognize.scenery.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.text.SpannableString;
import jp.recognize.scenery.android.sample.R;

public class Sanmoku extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);

    	//テキストフィールド
    	TextView tv = (TextView)findViewById(R.id.textView);

    	String RecognizeData = getIntent().getStringExtra("RecognizeData");
    	tv.setText(RecognizeData);

    	LinearLayout resultArea = (LinearLayout)findViewById(R.id.search_result_area);
    	//removeAllViews():全ての子ビューを削除するメソッド
    	resultArea.removeAllViews();

    	//文章の解析部分
    	if(RecognizeData.length() > 0) { 
    		for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(RecognizeData)) {
    			//TextView txt = new TextView(this);
    			//SpannableString 文字の装飾
    			SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature);
    			//txt.setText(spannable, TextView.BufferType.SPANNABLE);
    			//resultArea.addView(txt);
    		}
    	}

    	//日付の抜き出し部分
    	if(RecognizeData.length() > 0){
    		Pattern pYear = Pattern.compile("[０-９]{4}[年．／]");
    		Pattern pMonth = Pattern.compile("１?[０-９][月．／]");
    		Pattern pDay = Pattern.compile("[１-３]?[０-９][日]");

    		//年の部分の抽出
    		Matcher m = pYear.matcher(RecognizeData);
    		if(m.find()){
    			TextView txt = new TextView(this);
    			txt.setText(m.group());
    			resultArea.addView(txt);
    		}

    		//月の部分の抽出
    		m = pMonth.matcher(RecognizeData);
    		if(m.find()){
    			TextView txt = new TextView(this);
    			txt.setText(m.group());
    			resultArea.addView(txt);
    		}
    		
    		//日にちの部分の抽出
    		m = pDay.matcher(RecognizeData);
    		if(m.find()){
    			TextView txt = new TextView(this);
    			txt.setText(m.group());
    			resultArea.addView(txt);
    		}
    	}

    }
}