package jp.recognize.scenery.android;

import java.util.HashMap;
import java.util.Map;

public class Sanmoku{
	private String recognizeData;
	private String[][] morpheme;
	private Map<String, String> feature = new HashMap<String, String>();
	
	//引数ありコンストラクタ
	Sanmoku(String recognizeData){
		this.recognizeData = recognizeData;
	}
	
	public String[][] getMorpheme(){
		setMorpheme();
		return morpheme;
	}

	private void setMorpheme(){
		String[] word = recognizeData.split("[ \n]+");
		morpheme = new String[word.length][];
		for(int i=0; i<word.length; i++){
			if(word[i].length() > 0) {
				morpheme[i] = new String[net.reduls.sanmoku.Tagger.parse(word[i]).size()];
				int j = 0;
				for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(word[i])) {
					if(i == 0 && j == 0){
						morpheme[i][j] = "";
						j++;
						continue;
					}
					morpheme[i][j] = e.surface;
					feature.put(morpheme[i][j], e.feature);  //Mapの作成。（形態素、その属性（地域など））のように、各形態素の属性がマッピングされる。
					j++;
				}
			}
		}
	}
	
	//マッピングデータを取得するメソッド
	public Map<String, String> getFeature(){   //マッピングしたデータを返すメソッド。
		return feature;
	}
}