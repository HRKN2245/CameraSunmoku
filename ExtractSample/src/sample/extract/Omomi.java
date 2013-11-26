package sample.extract;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Omomi {
	private double[][] weight;
	private ArrayList<int[]> array;
	private String[][] morpheme;
	private Pattern context, schedule = null;
	private Matcher m;
	
	Omomi(double[][] weight, String[][] morpheme, int id){
		this.weight = weight;
		this.morpheme = morpheme;
		array = new ArrayList<int[]>();
		System.out.println((id+1)+"回目");
		switch(id){
		case 0: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.YEAR);
				break;
		case 1: context = Pattern.compile(RegularExpression.EXPRESSION_DAYS);
				schedule = Pattern.compile(RegularExpression.TIME);
				break;
		case 2: context = Pattern.compile(RegularExpression.EXPRESSION_ADDRESS);
				
		}
	}
	
	public double[][] getWeight(){
		int[] c = new int[2];
		int s;
		for(int i=0; i<morpheme.length; i++){
			for(int j=0; j<morpheme[i].length; j++){
				s = 0;
				m = context.matcher(morpheme[i][j]);
				if(m.find()){
					System.out.println("OK");
					c[0]++;
					c[1] = i+1;
					array.add(c);
				}
				if(schedule != null){
					m = schedule.matcher(morpheme[i][j]);
					if(m.find()) s++;
				}
				Calc(i,j,s);
			}
		}
		return weight;
	}

	private void Calc(int i, int j, int s) {
		// TODO 自動生成されたメソッド・スタブ
		double cSum = 0.0;
		for(int k=0 ;k<array.size(); k++){
			cSum += array.get(k)[0] * (1.0/(double)(1+((i+1)-array.get(k)[1])));
			System.out.println(cSum);
		}
		weight[i][j] = cSum * s;
	}
	
}
