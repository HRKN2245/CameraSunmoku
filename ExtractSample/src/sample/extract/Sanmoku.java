package sample.extract;
import net.reduls.sanmoku.FeatureEx;


public class Sanmoku {
	private String[][] word;
	private String[][][] morpheme;
	
	Sanmoku(String[][] word){
		this.word = word;
		morpheme =  new String[this.word.length][][];
		for(int i=0; i<morpheme.length; i++){
			morpheme[i] = new String[this.word[i].length][];
		}
	}
	
	//各形態素を取得するメソッド
	public String[][][] SanmokuStart(){
		for(int i=0; i<word.length; i++){
			for(int j=0; j<word[i].length; j++){
				if(word[i][j].length() > 0) {
					morpheme[i][j] = new String[net.reduls.sanmoku.Tagger.parse(word[i][j]).size()];
					int k = 0;
					for(net.reduls.sanmoku.Morpheme e : net.reduls.sanmoku.Tagger.parse(word[i][j])) {
						if(j == 0 && k == 0){
							morpheme[i][j][k] = "";
							k++;
							continue;
						}
						FeatureEx fe = new FeatureEx(e);
						morpheme[i][j][k] = e.surface;
						k++;
					}
				}
			}
		}
		return morpheme;
	}
}
