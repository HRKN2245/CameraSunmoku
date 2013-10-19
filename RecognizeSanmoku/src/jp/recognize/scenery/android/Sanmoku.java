package jp.recognize.scenery.android;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sample.calendar.RegularExpression;

import net.reduls.sanmoku.FeatureEx;

import android.text.SpannableString;

public class Sanmoku{
	private String recognizeData;
	private String[] exWord, strTmp;
	private int ArrayIndex = 0;
	private static final int STR_TMP = 30;
	
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
    	String[] strTmp = new String[STR_TMP];
		
    	if(recognizeData.length() > 0){
    		DayExtract(word);
    		TimeExtract(word);
		}
		exWord = new String[ArrayIndex];
		for(int i=0; i<exWord.length; i++){
			exWord[i] = strTmp[i];
		}
			
    }
    
    //日付抽出をするメソッド
    private void DayExtract(String[] word){
    	Pattern pYear = Pattern.compile(RegularExpression.YEAR);
    	Pattern pDay = Pattern.compile(RegularExpression.MONTH+RegularExpression.DAY);
    	Matcher m = null;
    	for(int i=0; i<word.length; i++){
    		for(int j=0; j<2; j++){ //年、月日の2回という意味
    			switch(j){
    			case 0: m = pYear.matcher(word[i]);
    					Check(m, 2);
    					word[i] = m.replaceAll("");
    					break;
    			case 1: m = pDay.matcher(word[i]);
    					Check(m, 1);
    					Check(m, 3);
    			}
    			System.out.println("ok");
    		}
    	}
    }

    //時間抽出するメソッド
    private void TimeExtract(String[] word){
    	Pattern pTime = Pattern.compile(RegularExpression.TIME);
    	Pattern pExpression = Pattern.compile(RegularExpression.END_TIME_EXPRESSION);
    	Matcher m;
    	int offset;
    	boolean flag = false;
    	for(int i=0; i<word.length; i++){
    		m = pExpression.matcher(word[i]);
    		offset = 0;
    		if(m.find(offset)){
    			offset = m.end();
    			flag = true;
    		}
    		m =pTime.matcher(word[i]);
    		if(flag) m.region(0, offset); //正規検索エンジンの設定○○：○○　～　××：××　の　「～」の場所から左と右に分ける。
    		Check(m, 1); //○時の取得
    		Check(m, 4); //○分の取得
    		Check(m, offset, 1); //終了時間の○時取得
    		Check(m, offset, 4); //終了時間の○分取得
    		
    	}
    }
    
    //正規表現パターンと文字列のチェックを行うメソッド
    private void Check(Matcher m, int groupNumber){
    	if(m.find()){
    		strTmp[ArrayIndex++] = m.group(groupNumber);
    	}
    }
    
    //おーばーろーど
    private void Check(Matcher m, int start, int groupNumber){
    	if(m.find(start)){
    		strTmp[ArrayIndex++] = m.group(groupNumber);
    	}
    }
    
}