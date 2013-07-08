package jp.recognize.scenery.android;

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
    		TextView txt = new TextView(this);
    		//SpannableString 文字の装飾
    		SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature);
    		txt.setText(spannable, TextView.BufferType.SPANNABLE);
    		resultArea.addView(txt);
    	}
    }
}
}