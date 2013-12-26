package jp.recognize.scenery.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	public final int SCHEDULE_ELEMENT = 6;
	private double[][][] weight; //重み。日付、時間、住所重みと3つに分けられ、更にそこから行列に分けられる。
	private Map<String, String> feature;
	private List<Integer> contextWeight; //周辺語重みが入るArrayList
	private List<Integer> contextLine; //周辺語重みが何行目にあるかが入るArrayList
	private List<List<Integer>> contextArray; //周辺後重みの数値と、その周辺後の行数を持つArrayList
	private String[][] morpheme;
	private Pattern context, schedule, preMean, aftMean; //周辺後重み、スケジュール重み
	
	//引数ありコンストラクタ
	Omomi(String[][] morpheme, Map<String, String> feature){
		this.morpheme = morpheme;
		this.feature = feature;
		weight = new double[SCHEDULE_ELEMENT][][]; //まず年、月、日、時、分、住所の６つ
		contextWeight = new ArrayList<Integer>();
		contextLine = new ArrayList<Integer>();
		contextArray = new ArrayList<List<Integer>>();
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
				break;
		//住所のパターンの取得
		case 5: context = Pattern.compile(RegularExpression.EXPRESSION_ADDRESS);
				schedule = Pattern.compile("地域");
				preMean = null;
				aftMean = null;
		}
	}
	
	//重みの取得
	public double[][][] getWeight(){
		setWeight();
		return weight;
	}

	private void setWeight(){
		int[] c = new int[2];  //周辺語重みと、その周辺語の行インデックスが入る。
		int s; //数字や地域表現があると重みがつく。
		int meanWeight; //スケジュール情報を意味づける表現の重み。年、月など
		//iはファイルインデックスを示す。jはどのスケジュール情報の重みかを示す。kは行、lは列を示す。
		for(int i=0; i<weight.length; i++){  //重みのコンテキスト数分ループ
			System.out.println("コンテキスト"+i);
			weight[i] = new double[morpheme.length][];
			contextWeight.clear();
			contextLine.clear();
			contextArray.clear();
			c[0] = 0; c[1] = 0; //初期化
			contextDecision(i);
			for(int j=0; j<morpheme.length; j++){  //行ループ
				weight[i][j] = new double[morpheme[j].length];
				meanWeight = 0;
				//スケジュール周辺語重み。
				for(int k=0; k<morpheme[j].length; k++){ //列ループ
					if(context.matcher(morpheme[j][k]).find()){
						//System.out.println(m.group()+"が見つかりました。");
						if(!contextLine.isEmpty() && j+1 != contextLine.get(contextLine.size()-1)){
							contextWeight.clear();
							contextLine.clear();
						}
						contextWeight.add(1);
						contextLine.add(j+1);
						contextArray.add(contextWeight);
						contextArray.add(contextLine);
					}
				}
				//意味重みづけと、数値や地域表現に重みづけ。
				for(int k=0; k<morpheme[j].length; k++){ //列ループ
					s = 0;  //初期化
					meanWeight = 0; //初期化
					boolean frag = false;
					if(i < 5){  //住所重み以外の場合
						if(k != 0){
							if(morpheme[j][k-1] != null && preMean != null && preMean.matcher(morpheme[j][k-1]).find()){
								meanWeight = meanWeight + 2;
								frag = true;
							}
						}
						if(k != morpheme[j].length-1){
							if(morpheme[j][k+1] != null && aftMean.matcher(morpheme[j][k+1]).find()){
								meanWeight = meanWeight + 2;
								frag = true;
							}
						}
						if(schedule.matcher(morpheme[j][k]).find() && frag) s++;
					}
					else{  //住所重みの場合
						if(feature.containsKey(morpheme[j][k])){
							if(schedule.matcher(feature.get(morpheme[j][k])).find()) s=s+3;
						}
					}
					//重みの計算。
					Calc(i,j,k,s,meanWeight);
				}
				//System.out.println();
			}
			//System.out.println();
		}
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
	private void Calc(int i, int j, int k, int s, int meanWeight) {
		// TODO 自動生成されたメソッド・スタブ
		double cSum = 0.0;
		for(int n=0; n<contextWeight.size(); n++){
			cSum += contextArray.get(0).get(n) * (1.0/(double)(1+((j+1)-contextArray.get(1).get(n)))); //contextArray.get(0)は重み、(1)はその行数が入っている。
		}
		weight[i][j][k] = (cSum + meanWeight) * s;
	}	
}
