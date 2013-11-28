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
		String[][] morpheme;
		double[][][] weight; 
		
		morpheme = new Sanmoku(new TxtFileInput().input()).SanmokuStart();
		weight = new Omomi(morpheme).getWeight();
		new Excel(morpheme, weight).createXLS();
	}

}
