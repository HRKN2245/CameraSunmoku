package jp.recognize.scenery.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import net.reduls.sanmoku.FeatureEx;

import android.text.SpannableString;

public class Sanmoku{
	private String recognizeData;
	private String[] exWord, strTmp;
	private int ArrayIndex = 0, FlagIndex = 0;
	private static final int STR_TMP = 30;
	public int[] exFlag ={0,0,0,0,0,0,0}; //年、月、日、開始時、開始分、終了時、終了分
	
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
    	strTmp = new String[STR_TMP];
		
    	if(recognizeData.length() > 0){
    		//DayExtract(word);
    		//TimeExtract(word);
    		AddressExtract(word);
		}
		exWord = new String[ArrayIndex];
		for(int i=0; i<exWord.length; i++){
			exWord[i] = strTmp[i];
		}
			
    }
    
    private void AddressExtract(String[] word){
    	try{
    		File dir = new File("sdcard/data/data/jp.recognize.scenery.android/files/Adress");
    		File[] files = dir.listFiles();
    		String str;
    		Matcher m = null;
    		boolean flag = false;
    		for(int i=0; i<word.length; i++){
    			for(int j=0; j<files.length; j++){
    				BufferedReader br = new BufferedReader(new FileReader(files[j]));
    				System.out.println(files[j].getName());
    				while((str = br.readLine()) != null){
    					if(word[i].equals(str)){
    						strTmp[ArrayIndex++] = str;
    						flag = true;
    						break;
    					}
    				}
    				br.close();
    				if(flag) break;
    			}
    			if(flag) break;
    		}
    		if(!flag) System.out.println("抽出できません");
    	}catch(FileNotFoundException fe){
    		System.out.println("Fileが見つかりません。");
    	}catch(IOException ie){}
    }
    
    //日付抽出をするメソッド
    private void DayExtract(String[] word){
    	Pattern pYear = Pattern.compile(RegularExpression.YEAR);
    	Pattern pDay = Pattern.compile(RegularExpression.MONTH+RegularExpression.DAY);
    	Matcher m = null;
    	for(int i=0; i<word.length; i++){
    		for(int j=0; j<2; j++){ //年、月日の2回という意味
    			if(j == 0){
    				m = pYear.matcher(word[i]);
    				if(Check(m, 2)){
    					exFlag[FlagIndex++] = 1;
    					word[i] = m.replaceAll("");
    				}
    			}
    			else{
    				m = pDay.matcher(word[i]);
    				if(Check(m, 2))
    					exFlag[FlagIndex++] = 2;
    				if(Check(m, 4))
    					exFlag[FlagIndex++] = 3;
    			}
    		}
    		System.out.println("Dayok");
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
    		System.out.println("Timeok");
    		m = pExpression.matcher(word[i]);
    		offset = 0;
    		if(m.find(offset)){
    			System.out.println(m.group());
    			offset = m.end();
    			flag = true;
    		}
    		m =pTime.matcher(word[i]);
    		if(flag) m.region(0, offset); //正規検索エンジンの設定○○：○○　～　××：××　の　「～」の場所から左と右に分ける。
    		if(Check(m, 3)){
    			exFlag[FlagIndex++] = 4; //○時の取得
    			System.out.println("ok");
    		}
    		if(Check(m, 5))
    			exFlag[4] = 5; //○分の取得
    			System.out.println("ok");
    		if(flag){
    			if(Check(m, offset, 3))
    				exFlag[FlagIndex++] = 6; //終了時間の○時取得
    			if(Check(m, offset, 5))
    				exFlag[FlagIndex++] = 7;//終了時間の○分取得
    		}
    		
    	}
    }
    
    //正規表現パターンと文字列のチェックを行うメソッド
    private boolean Check(Matcher m, int groupNumber){
    	if(m.find(0)){
    		strTmp[ArrayIndex++] = m.group(groupNumber);
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //おーばーろーど
    private boolean Check(Matcher m, int start, int groupNumber){
    	if(m.find(start)){
    		strTmp[ArrayIndex++] = m.group(groupNumber);
    		return true;
    	}
    	return false;
    }
}