package sample.extract;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		String[][][] morpheme;
		double[][][][] weight; 
		
		morpheme = new Sanmoku(new TxtFileInput().input()).SanmokuStart();
		weight = new Omomi(morpheme).getWeight();
		new Excel(morpheme, weight).createXLS();
	}

}
