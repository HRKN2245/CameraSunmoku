package sample.extract;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	private double[][][][] weight; //重み。日付、時間、住所重みと3つに分けられ、更にそこから行列に分けられる。
	private ArrayList<Integer> contextWeight; //周辺語重みが入るArrayList
	private ArrayList<Integer> contextLine; //周辺語重みが何行目にあるかが入るArrayList
	private ArrayList<ArrayList<Integer>> contextArray; //周辺後重みの数値と、その周辺後の行数を持つArrayList
	private String[][][] morpheme;
	private Pattern context, schedule, mean; //周辺後重み、スケジュール重み、意味重み
	
	//引数ありコンストラクタ
	Omomi(String[][][] morpheme){
		this.morpheme = morpheme;
		weight = new double[this.morpheme.length][3][][]; //まず日付、時間、住所重みの3つに分ける。
		contextWeight = new ArrayList<Integer>();
		contextLine = new ArrayList<Integer>();
		contextArray = new ArrayList<ArrayList<Integer>>();
		//weightのサイズ定義。各行の形態素の数によって列数が決まる。
		for(int i=0; i<weight.length; i++){
			System.out.println((i+1)+"枚目");
			for(int j=0; j<weight[i].length; j++){
				weight[i][j] = new double[morpheme[i].length][];
				for(int k=0; k<morpheme[i].length; k++){
					System.out.println("k="+k);
					System.out.println(morpheme[i].length);
					weight[i][j][k] = new double[morpheme[i][k].length];
				}
			}
		}
	}
	
	private void contextDecision(int id){
		switch(id){
		//日付のパターンを取得
		case 0: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.YEAR);
				mean = Pattern.compile(RegularExpression.COUNTER_WORD_DAYS);
				break;
		//時間のパターンを取得
		case 1: context = Pattern.compile(RegularExpression.EXPRESSION_TIME);
				schedule = Pattern.compile(RegularExpression.TIME);
				mean = Pattern.compile(RegularExpression.COUNTER_WORD_TIME);
				break;
		//住所のパターンを取得
		case 2: schedule = null;
				context = Pattern.compile(RegularExpression.EXPRESSION_ADDRESS);
				mean = null;
		}
	}
	
	//重みの取得
	public double[][][][] getWeight(){
		int[] c = new int[2];  //周辺語重みと、その周辺語の行インデックスが入る。
		int[] tmp = new int[2]; //参照渡しを防ぐためのダミー配列
		int s; //数字や地域表現があると重みがつく。
		int meanWeight; //スケジュール情報を意味づける表現の重み。年、月など
		Matcher m;
		//iはファイルインデックスを示す。jはどのスケジュール情報の重みかを示す。kは行、lは列を示す。
		for(int i=0; i<weight.length; i++){ //txtファイル数分ループ
			System.out.println(i+1+"回目");
			for(int j=0; j<weight[i].length; j++){ //日付、時間、住所の３つ分ループ
				contextWeight.clear();
				contextLine.clear();
				contextArray.clear();
				c[0] = 0; c[1] = 0; //初期化
				contextDecision(j);
				for(int k=0; k<morpheme[i].length; k++){  //行ループ
					meanWeight = 0;
					for(int l=0; l<morpheme[i][k].length; l++){ //列ループ
						m = context.matcher(morpheme[i][k][l]);
						if(m.find()){
							System.out.println(m.group()+"が見つかりました。");
							contextWeight.add(1);
							contextLine.add(k+1);
							contextArray.add(contextWeight);
							contextArray.add(contextLine);
						}
						if(mean != null){
							m = mean.matcher(morpheme[i][k][l]);
							if(m.find()) meanWeight++;
						}
					}
					for(int l=0; l<morpheme[i][k].length; l++){ //列ループ
						s = 0;
						if(schedule != null){
							m = schedule.matcher(morpheme[i][k][l]);
							if(m.find()) s++;
						}
						//重みの計算。
						Calc(i,j,k,l,s,meanWeight);
					}
				}
			}
		}
		return weight;
	}

	//定義式に基づいた計算を行うメソッド
	/**
	 * @param i ファイルインデックス
	 * @param j 0なら日付重み、1なら時間重み、2なら住所重みの演算をしていることを指す。
	 * @param k 行インデックス
	 * @param l 列インデックス
	 * @param s 数字や地域表現がマッチングされると+1される重み。
	 * @param meanWeight スケジュール情報だと意味づける表現が出た場合の重み。
	 */
	private void Calc(int i, int j, int k, int l, int s, int meanWeight) {
		// TODO 自動生成されたメソッド・スタブ
		double cSum = 0.0;
		for(int n=0; n<contextWeight.size(); n++){
			cSum += contextArray.get(0).get(n) * (1.0/(double)(1+((k+1)-contextArray.get(1).get(n)))); //array.get(n)[0]は重み、[1]はその行数が入っている。
		}
		weight[i][j][k][l] = (cSum + meanWeight) * s;
	}	
}
