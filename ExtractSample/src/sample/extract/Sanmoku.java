package sample.extract;
import java.util.ArrayList;
import net.reduls.sanmoku.FeatureEx;


public class Sanmoku {
	private String[] word;
	private String[][] str;
	
	Sanmoku(ArrayList<String> word){
		this.word = word.toArray(new String[0]);
		str =  new String[this.word.length][];
	}
	
	//各形態素を取得するメソッド
	public String[][] SanmokuStart(){
		for(int i=0; i<word.length; i++){
			if(word[i].length() > 0) {
				str[i] = new String[net.reduls.sanmoku.Tagger.parse(word[i]).size()];
				int j = 0;
				for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(word[i])) {
					if(i == 0 && j == 0){
						str[i][j] = "";
						j++;
						continue;
					}
					FeatureEx fe = new FeatureEx(e);
					str[i][j] = e.surface;
					j++;
				}
			}
		}
		return str;
	}
}
