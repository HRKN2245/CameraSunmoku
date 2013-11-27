package sample.extract;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		File file = new File("sample.txt");
		String str;
		String[][] morpheme;
		double[][][] weight = new double[3][][]; 
		ArrayList<String> word = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while((str = br.readLine()) != null){
				word.add(str);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("ファイルが見つかりません");
		} catch (IOException ie){
			ie.printStackTrace();
		}
		//各形態素を取得する。
		Sanmoku sanmoku = new Sanmoku(word);
		morpheme = sanmoku.SanmokuStart();
		
		for(int i=0; i<weight.length; i++){
			weight[i] = new double[morpheme.length][];
			for(int j=0; j<morpheme.length; j++){
				weight[i][j] = new double[morpheme[j].length];
			}
		}
		
		Omomi[] w = new Omomi[3];
		for(int i=0; i<w.length; i++){
			w[i] = new Omomi(weight[i],morpheme,i);
			weight[i] = w[i].getWeight();
		}
		
		
		Excel excel = new Excel(morpheme, weight);
		excel.createXLS();
		/*for(int i=0; i<weight.length; i++){
			System.out.println((i+1)+"つ目");
			for(int j=0; j<morpheme.length; j++){
				for(int k=0; k<morpheme[j].length; k++){
					System.out.print(morpheme[j][k]+"→"+weight[i][j][k]+"  ");
				}
				System.out.println();
			}
		}*/

	}

}
