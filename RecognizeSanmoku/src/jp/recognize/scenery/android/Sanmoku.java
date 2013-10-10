package jp.recognize.scenery.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.reduls.sanmoku.FeatureEx;

import android.text.SpannableString;

public class Sanmoku{
	private String recognizeData;
	private String[] exWord;
	
	//引数ありコンストラクタ
	Sanmoku(String recognizeData){
		this.recognizeData = recognizeData;
	}
	
    public String[] SanmokuStart(){
    	//文章の解析部分
    	if(recognizeData.length() > 0) { 
    		for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(recognizeData)) {
    			FeatureEx fe = new FeatureEx(e);
    			SpannableString spannable = DataFormatter.format("<"+e.surface+">\n"+e.feature+","+fe.baseform+","+fe.pronunciation+","+fe.reading);
    		}
    	}
    	
    	ExtractWord(recognizeData);
    	return exWord;
    }

    	//日付、時間の抜き出し部分
    private void ExtractWord(String recognizeData){
    	String[] word = recognizeData.split("[ \n]+");
    	String time = "((午前|午後|ＡＭ|ａｍ|ＰＭ|ｐｍ)?[０-１0-1]?[０-９0-9]|[２2][０-３0-3])[時：:]([０-５0-5]?[０-９0-9][分]?)?";
    	String[] strTmp = new String[3];
		Pattern pYear = Pattern.compile("(平成|昭和|Ｈ)?([０-９]{1,4})[年．／]");
		Pattern pMonth = Pattern.compile("([０-９]?[０-９]|[１][０-２])[月．／]");
		Pattern pDay = Pattern.compile("([０-２]?[０-９]|[３][０-１])[日]?");
		Pattern pTime = Pattern.compile(time);
		Matcher m;
		int ArrayIndex = 0;
		
    	if(recognizeData.length() > 0){
			int num;
			for(int i=0; i<word.length; i++){
				
				//年の部分の抽出
				m = pYear.matcher(word[i]);
				if(m.find()){
					strTmp[0] = m.group(2);
					ArrayIndex++;
				}

				//月の部分の抽出
				m = pMonth.matcher(word[i]);
				if(m.find()){
					strTmp[1] = m.group(0);
					ArrayIndex++;
				}

				//日にちの部分の抽出
				m = pDay.matcher(word[i]);
				if(m.find()){
					strTmp[2] = m.group(0);
					ArrayIndex++;
				}

				//時間の部分の抽出
				m = pTime.matcher(word[i]);
				num = 0;
				while(true){
					if(m.find(num)){
						num = m.end();
					}
					else break;
				}
			}
			exWord = new String[ArrayIndex];
			for(int i=0; i<exWord.length; i++){
				exWord[i] = strTmp[i];
			}
			
    	}

    }
    
}