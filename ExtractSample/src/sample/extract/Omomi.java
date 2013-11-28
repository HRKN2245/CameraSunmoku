package sample.extract;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	private double[][][] weight; //重み。日付、時間、住所重みと3つに分けられ、更にそこから行列に分けられる。
	private ArrayList<int[]> array; //周辺後重みの数値と、その周辺後の行数を持つArrayList
	private String[][] morpheme;
	private Pattern context, schedule, counter; //周辺後重み、スケジュール重み、助数詞重み
	
	//引数ありコンストラクタ
	Omomi(String[][] morpheme){
		this.morpheme = morpheme;
		weight = new double[3][][]; //まず日付、時間、住所重みの3つに分ける。
		array = new ArrayList<int[]>();
		//weightのサイズ定義。各行の形態素の数によって列数が決まる。
		for(int i=0; i<weight.length; i++){
			weight[i] = new double[morpheme.length][];
			for(int j=0; j<morpheme.length; j++){
				weight[i][j] = new double[morpheme[j].length];
			}
		}
	}
	
	private void contextDecision(int id){
		switch(id){
		//日付のパターンを取得
		case 0: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.YEAR);
				counter = Pattern.compile(RegularExpression.COUNTER_WORD_DAYS);
				break;
		//時間のパターンを取得
		case 1: context = Pattern.compile(RegularExpression.EXPRESSION_TIME);
				schedule = Pattern.compile(RegularExpression.TIME);
				counter = Pattern.compile(RegularExpression.COUNTER_WORD_TIME);
				break;
		//住所のパターンを取得
		case 2: context = Pattern.compile(RegularExpression.EXPRESSION_ADDRESS);
		}
	}
	
	//重みの取得
	public double[][][] getWeight(){
		int[] c = new int[2];  //周辺語重みと、その周辺語の行インデックスが入る。
		int s; //数字や地域表現があると重みがつく。
		int meanWeight; //スケジュール情報を意味づける表現の重み。年、月など
		Matcher m;
		//iはどのスケジュール情報の重みかを示す。、jは行、kは列を示す。
		for(int i=0; i<weight.length; i++){
			contextDecision(i);
			for(int j=0; j<morpheme.length; j++){
				meanWeight = 0;
				for(int k=0; k<morpheme[j].length; k++){
					m = context.matcher(morpheme[j][k]);
					if(m.find()){
						System.out.println("OK");
						c[0]++;
						c[1] = j+1;
						array.add(c);
					}
					if(counter != null){
						m = counter.matcher(morpheme[j][k]);
						if(m.find()) meanWeight++;
					}
				}
				for(int k=0; k<morpheme[j].length; k++){
					s = 0;
					if(schedule != null){
						m = schedule.matcher(morpheme[j][k]);
						if(m.find()) s++;
					}
					//重みの計算。
					Calc(i,j,k,s,meanWeight);
				}
			}
		}
		return weight;
	}

	//定義式に基づいた計算を行うメソッド
	private void Calc(int i, int j, int k, int s, int meanWeight) {
		// TODO 自動生成されたメソッド・スタブ
		double cSum = 0.0;
		for(int l=0 ;l<array.size(); l++){
			cSum += array.get(l)[0] * (1.0/(double)(1+((j+1)-array.get(l)[1]))); //array.get(k)[0]は重み、[1]はその行数が入っている。
			System.out.println(cSum);
		}
		weight[i][j][k] = (cSum + meanWeight) * s;
	}	
}
