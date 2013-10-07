package jp.recognize.scenery.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.reduls.sanmoku.FeatureEx;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.text.SpannableString;
import sample.calendar.EditActivity;
import sample.calendar.R;

public class Sanmoku extends Activity{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
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
    			FeatureEx fe = new FeatureEx(e);
    			//TextView txt = new TextView(this);
    			//SpannableString 文字の装飾
    			SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature+","+fe.baseform+","+fe.pronunciation+","+fe.reading);
    			//txt.setText(spannable, TextView.BufferType.SPANNABLE);
    			//resultArea.addView(txt);
    		}
    	}

    	//日付、時間の抜き出し部分
    	String[] word = RecognizeData.split("[ \n]+");
    	if(RecognizeData.length() > 0){
    		String[] exWord = new String[3];
    		String time = "((午前|午後|ＡＭ|ａｍ|ＰＭ|ｐｍ)?[０-１0-1]?[０-９0-9]|[２2][０-３0-3])[時：:]([０-５0-5]?[０-９0-9][分]?)?";
    		Pattern pYear = Pattern.compile("(平成|昭和|Ｈ)?([０-９]{1,4})[年．／]");
    		Pattern pMonth = Pattern.compile("([０-９]?[０-９]|[１][０-２])[月．／]");
    		Pattern pDay = Pattern.compile("([０-２]?[０-９]|[３][０-１])[日]");
    		Pattern pTime = Pattern.compile(time);
			Matcher m;
			int num;
			for(int i=0; i<word.length; i++){
				
				//年の部分の抽出
				m = pYear.matcher(word[i]);
				if(m.find()){
					TextView txt = new TextView(this);
					txt.setText(m.group(2));
					resultArea.addView(txt);
					exWord[0] = m.group(2);
				}

				//月の部分の抽出
				m = pMonth.matcher(word[i]);
				if(m.find()){
					TextView txt = new TextView(this);
					txt.setText(m.group(1));
					resultArea.addView(txt);
					exWord[1] = m.group(1);
				}

				//日にちの部分の抽出
				m = pDay.matcher(word[i]);
				if(m.find()){
					TextView txt = new TextView(this);
					txt.setText(m.group(1));
					resultArea.addView(txt);
					exWord[2] = m.group(1);
				}

				//時間の部分の抽出
				m = pTime.matcher(word[i]);
				num = 0;
				while(true){
					if(m.find(num)){
						TextView txt = new TextView(this);
						txt.setText(m.group());
						resultArea.addView(txt);
						num = m.end();
					}
					else break;
				}
				
				Intent intent = new Intent();
				//文字を取得し、EditActivityにデータを送信。
				intent.putExtra("str", exWord);
				setResult(RESULT_OK,intent);
				finish();
			}
    		
    	}

    }
}