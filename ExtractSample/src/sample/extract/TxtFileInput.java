package sample.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TxtFileInput {
	private File file;
	private ArrayList<String> word;
	public TxtFileInput() {
		// TODO 自動生成されたコンストラクター・スタブ
		file = new File("sample.txt");
		word = new ArrayList<String>();
	}
	
	public ArrayList<String> input(){
		String str;
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
		return word;
	}
}
