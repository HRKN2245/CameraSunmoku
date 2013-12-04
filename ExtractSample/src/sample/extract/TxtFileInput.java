package sample.extract;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TxtFileInput {
	private File[] files;
	private File dir;
	private ArrayList<String> list;
	private String[][] word;
	public TxtFileInput() {
		// TODO 自動生成されたコンストラクター・スタブ
		dir = new File("txt");
		files = dir.listFiles();
		list = new ArrayList<String>();
		word = new String[files.length][];
	}
	
	public String[][] input(){
		String str;
		for(int i=0; i<files.length; i++){
			try {
				BufferedReader br = new BufferedReader(new FileReader(files[i]));
				while((str = br.readLine()) != null){
					if(str.length() > 0)
						list.add(str);
				}
				br.close();
			} catch (FileNotFoundException e) {
				// TODO 自動生成された catch ブロック
				System.out.println("ファイルが見つかりません");
			} catch (IOException ie){
				ie.printStackTrace();
			}
			word[i] = list.toArray(new String[0]);
			list.clear();
		}
		return word;
	}
}
