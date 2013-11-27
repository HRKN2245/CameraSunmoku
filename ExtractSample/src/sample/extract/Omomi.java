package sample.extract;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	private double[][] weight; //重み。２次元の理由は、行列であるため。
	private ArrayList<int[]> array; //周辺後重みの数値と、その周辺後の行数を持つArrayList
	private String[][] morpheme;
	private Pattern context, schedule, counter; //周辺後重み、スケジュール重み、助数詞重み
	private Matcher m;
	
	Omomi(double[][] weight, String[][] morpheme, int id){
		this.weight = weight;
		this.morpheme = morpheme;
		array = new ArrayList<int[]>();
		System.out.println((id+1)+"回目");
		switch(id){
		case 0: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.YEAR);
				counter = Pattern.compile(RegularExpression.COUNTER_WORD_DAYS);
				break;
		case 1: context = Pattern.compile(RegularExpression.EXPRESSION_TIME);
				schedule = Pattern.compile(RegularExpression.TIME);
				counter = Pattern.compile(RegularExpression.COUNTER_WORD_TIME);
				break;
		case 2: context = Pattern.compile(RegularExpression.EXPRESSION_ADDRESS);
				
		}
	}
	
	public double[][] getWeight(){
		int[] c = new int[2];
		int s;
		int counterWeight;
		//iは行、jは列を表している。
		for(int i=0; i<morpheme.length; i++){
			counterWeight = 0;
			for(int j=0; j<morpheme[i].length; j++){
				m = context.matcher(morpheme[i][j]);
				if(m.find()){
					System.out.println("OK");
					c[0]++;
					c[1] = i+1;
					array.add(c);
				}
				if(counter != null){
					m = counter.matcher(morpheme[i][j]);
					if(m.find()) counterWeight++;
				}
			}
			for(int j=0; j<morpheme[i].length; j++){
				s = 0;
				if(schedule != null){
					m = schedule.matcher(morpheme[i][j]);
					if(m.find()) s++;
				}
				Calc(i,j,s,counterWeight);
			}
		}
		return weight;
	}

	private void Calc(int i, int j, int s, int counterWeight) {
		// TODO 自動生成されたメソッド・スタブ
		double cSum = 0.0;
		for(int k=0 ;k<array.size(); k++){
			cSum += array.get(k)[0] * (1.0/(double)(1+((i+1)-array.get(k)[1]))); //array.get(k)[0]は重み、[1]はその行数が入っている。
			System.out.println(cSum);
		}
		weight[i][j] = (cSum + counterWeight) * s;
	}
	
}
