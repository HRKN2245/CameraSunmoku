package sample.extract;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	private final int SCHEDULE_ELEMENT = 5;
	private double[][][][] weight; //重み。日付、時間、住所重みと3つに分けられ、更にそこから行列に分けられる。
	private ArrayList<Integer> contextWeight; //周辺語重みが入るArrayList
	private ArrayList<Integer> contextLine; //周辺語重みが何行目にあるかが入るArrayList
	private ArrayList<ArrayList<Integer>> contextArray; //周辺後重みの数値と、その周辺後の行数を持つArrayList
	private String[][][] morpheme;
	private Pattern context, schedule, preMean, aftMean; //周辺後重み、スケジュール重み
	
	//引数ありコンストラクタ
	Omomi(String[][][] morpheme){
		this.morpheme = morpheme;
		weight = new double[this.morpheme.length][SCHEDULE_ELEMENT][][]; //まず年、月、日、時、分、住所の６つ
		contextWeight = new ArrayList<Integer>();
		contextLine = new ArrayList<Integer>();
		contextArray = new ArrayList<ArrayList<Integer>>();
		//weightのサイズ定義。各行の形態素の数によって列数が決まる。
		for(int i=0; i<weight.length; i++){
			System.out.println((i+1)+"枚目");
			for(int j=0; j<weight[i].length; j++){
				weight[i][j] = new double[morpheme[i].length][];
				for(int k=0; k<morpheme[i].length; k++){
					weight[i][j][k] = new double[morpheme[i][k].length];
				}
			}
		}
	}
	
	private void contextDecision(int id){
		switch(id){
		//年のパターンを取得
		case 0: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.YEAR);
				preMean = Pattern.compile(RegularExpression.CONTEXT_PREWORD_YEAR);
				aftMean = Pattern.compile(RegularExpression.CONTEXT_AFTWORD_YEAR);
				break;
		//月のパターンを取得
		case 1: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.MONTH);
				preMean = Pattern.compile(RegularExpression.CONTEXT_PREWORD_MONTH);
				aftMean = Pattern.compile(RegularExpression.CONTEXT_AFTWORD_MONTH);
				break;
		//日のパターンを取得
		case 2: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.DAYS);
				preMean = Pattern.compile(RegularExpression.CONTEXT_PREWORD_DAYS);
				aftMean = Pattern.compile(RegularExpression.CONTEXT_AFTWORD_DAYS);
				break;
		//時のパターン取得		
		case 3: context = Pattern.compile(RegularExpression.EXPRESSION_TIME);
				schedule = Pattern.compile(RegularExpression.HOUR);
				preMean = null;
				aftMean = Pattern.compile(RegularExpression.CONTEXT_WORD_HOUR);
				break;
		//分のパターン取得		
		case 4: context = Pattern.compile(RegularExpression.EXPRESSION_TIME);
				schedule = Pattern.compile(RegularExpression.MINUTE);
				preMean = Pattern.compile(RegularExpression.CONTEXT_PREWORD_MINUTE);
				aftMean = Pattern.compile(RegularExpression.CONTEXT_AFTWORD_MINUTE);
		
		}
	}
	
	//重みの取得
	public double[][][][] getWeight(){
		int[] c = new int[2];  //周辺語重みと、その周辺語の行インデックスが入る。
		int s; //数字や地域表現があると重みがつく。
		int meanWeight; //スケジュール情報を意味づける表現の重み。年、月など
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
						if(context.matcher(morpheme[i][k][l]).find()){
							//System.out.println(m.group()+"が見つかりました。");
							contextWeight.add(1);
							contextLine.add(k+1);
							contextArray.add(contextWeight);
							contextArray.add(contextLine);
						}
						/*if(mean != null){
							m = mean.matcher(morpheme[i][k][l]);
							if(m.find()) meanWeight++;
						}*/
					}
					for(int l=0; l<morpheme[i][k].length; l++){ //列ループ
						s = 0;  //初期化
						meanWeight = 0; //初期化
						boolean frag = false;
						if(schedule != null){
							if(l != 0){
								if(morpheme[i][k][l-1] != null){
									if(preMean != null && preMean.matcher(morpheme[i][k][l-1]).find()){
										meanWeight = meanWeight + 4;
										frag = true;
									}
								}
							}
							if(l != morpheme[i][k].length-1){
								if(morpheme[i][k][l+1] != null){
									if(aftMean.matcher(morpheme[i][k][l+1]).find()){
										meanWeight = meanWeight + 4;
										frag = true;
									}
								}
							}
							if(schedule.matcher(morpheme[i][k][l]).find() && frag) s++;
						}
						//重みの計算。
						Calc(i,j,k,l,s,meanWeight);
					}
					System.out.println();
				}
				System.out.println();
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
		System.out.print(weight[i][j][k][l]+" ");
	}	
}
