package jp.recognize.scenery.android;

import android.content.Context;
import android.widget.Toast;
import jp.recognize.common.RecognitionResult.SegmentGraph;
import jp.recognize.common.RecognitionResult.SegmentGraph.Segment;
import jp.recognize.common.RecognitionResult.SegmentGraph.Segment.Candidate;

public class Words{
	private SegmentGraph[] segmentGraphtmp, segmentGraph = null;  //正しく直された断片グラフ
	private int[] wordCount, puttern; //文字数、パターン数
	private String[][][] word;
	
	Words(SegmentGraph[] segmentGraphtmp){
		this.segmentGraphtmp = segmentGraphtmp;
		setSegmentGraph();
	}
	
	//断片グラフを設定するメソッド。
	//複数行の断片グラフを取得した場合、同じ結果が複数出ることがあるので、その同じ結果の断片グラフ配列除去
	//断片グラフの要素がない配列も除去。
	private void setSegmentGraph() {
		// TODO 自動生成されたメソッド・スタブ
		SegmentGraph stmp = null;
		
		//ただの出力
		for(int i=0; i<segmentGraphtmp.length; i++){
			if(segmentGraphtmp[i].getFirstSegment() == null) continue;
			System.out.println("ループ"+i);
			Segment seg = segmentGraphtmp[i].getFirstSegment();
			while(seg != null){
				System.out.println(seg);
				seg = seg.getNextSegment();
			}
		}
		
		//同じ行のものをnullにする。
		for(int i=0; i<segmentGraphtmp.length-1; i++){
			if(segmentGraphtmp[i] != null){
				for(int j=i+1; j<segmentGraphtmp.length; j++){
					if(segmentGraphtmp[j] != null){
						if(segmentGraphtmp[j].getFirstSegment() == null)
							segmentGraphtmp[j] = null;
						else if(Math.abs(segmentGraphtmp[i].getFirstSegment().getShape().getBounds().getTop() - 
								segmentGraphtmp[j].getFirstSegment().getShape().getBounds().getTop()) < 50){
							if(getScore(segmentGraphtmp[i].getFirstSegment()) > getScore(segmentGraphtmp[j].getFirstSegment()))
								segmentGraphtmp[i] = segmentGraphtmp[j];
							segmentGraphtmp[j] = null;
						}
					}
				}
			}
		}
		
		//並び替え
		for(int i=0; i<segmentGraphtmp.length; i++){
			if(segmentGraphtmp[i] != null){
				for(int j=i; 0<j; j--){
					if(segmentGraphtmp[j-1] == null){
						stmp = segmentGraphtmp[j];
						segmentGraphtmp[j] = segmentGraphtmp[j-1];
						segmentGraphtmp[j-1] = stmp;
					}
					else if(segmentGraphtmp[j].getFirstSegment().getShape().getBounds().getTop() < 
							segmentGraphtmp[j-1].getFirstSegment().getShape().getBounds().getTop()){
						stmp = segmentGraphtmp[j];
						segmentGraphtmp[j] = segmentGraphtmp[j-1];
						segmentGraphtmp[j-1] = stmp;
					}
				}
			}
		}
		
		for(int i=0; i<segmentGraphtmp.length; i++){
			if(segmentGraphtmp[i] == null){
				segmentGraph = new SegmentGraph[i];
				break;
			}
		}
		//配列のコピー
		System.arraycopy(segmentGraphtmp, 0, segmentGraph, 0, segmentGraph.length);
		
		//再出力
		System.out.println("ここより下が本番");
		for(int i=0; i<segmentGraphtmp.length; i++){
			System.out.println("ループ"+i);
			if(segmentGraphtmp[i] == null) break;
			Segment seg = segmentGraphtmp[i].getFirstSegment();
			while(seg != null){
				System.out.println(seg);
				seg = seg.getNextSegment();
			}
		}
	}
	
	//文字数を取得するメソッド
	private void setWordCount(){
		wordCount = new int[segmentGraph.length];
		for(int i=0; i<segmentGraph.length; i++){
			Segment seg = segmentGraph[i].getFirstSegment();
			while(seg != null){
				seg = seg.getNextSegment();
				wordCount[i]++;
			}
			//一番有効な文字のトータルスコアが設定値以上だと、認識失敗とする。
			//ピントが合わない状態で撮影した場合の処置。
			//if(getScore(seg) > 50.0){
				//context = new RecognizeActivity();
				//Toast.makeText(context, "認識に失敗しました。", Toast.LENGTH_LONG).show();
			//}
		}
	}
	
	//スコアを取得するメソッド
	private double getScore(Segment seg){
		double score = 0.0;
		Candidate[] segArray;
			while(seg != null){
				segArray = seg.getCandidates();
				score += segArray[0].getScore();
				seg = seg.getNextSegment();
			}
			System.out.println(score);
		return score;
	}
	
	//全パターンを配列に代入するメソッド
	private void setString(){
		setWordCount();
		Candidate[] segArray;
		word= new String[segmentGraph.length][][];
		puttern = new int[segmentGraph.length];
		for(int i=0; i<segmentGraph.length; i++){
			Segment seg = segmentGraph[i].getFirstSegment();
			word[i] = new String[wordCount[i]][2];
			for(int j=0; j<wordCount[i]; j++){
				segArray = seg.getCandidates();
				word[i][j][0] = segArray[0].getText();
				word[i][j][1] = getToUpperString(word[i][j][0]);
				if(word[i][j][1] != null) puttern[i]++; 
				seg = seg.getNextSegment();
			}
		}
	}
	
	//大文字を小文字にするメソッド。
	private String getToUpperString(String word) {
		// TODO 自動生成されたメソッド・スタブ
		switch(word.toCharArray()[0]){
		case 'あ': return "ぁ"; 
		case 'ア': return "ァ";
		case 'い': return "ぃ"; 
		case 'イ': return "ィ";
		case 'う': return "ぅ"; 
		case 'ウ': return "ゥ";
		case 'え': return "ェ"; 
		case 'お': return "ぉ";
		case 'オ': return "ォ";
		case 'つ': return "っ"; 
		case 'ツ': return "ッ";
		case 'や': return "ゃ"; 
		case 'ヤ': return "ャ";
		case 'ゆ': return "ゅ"; 
		case 'ユ': return "ュ";
		case 'よ': return "ょ"; 
		case 'ヨ': return "ョ";
		case 'へ': return "ヘ";
		case 'べ': return "ベ";
		case 'ぺ': return "ペ";
		case 'ｏ': return "０";
		case 'Ｏ': return "０";
		default: return null;
		}
	}
	
	//全パターンを出力し、文字列として取得するメソッド
	public String getWords(){
		String input = "";
		setString();
		for(int i=0; i<word.length; i++){
			int[] index = new int[wordCount[i]];
			for(int j=0; j<Math.pow(2, puttern[i]); j++){
				for(int k=0; k<wordCount[i]; k++){
					//k文字目というカウント
					input += word[i][k][index[k]];
					if(k == wordCount[i]-1) index[k]++;
					if(index[k] > word[i][k].length-1 || word[i][k][index[k]] == null)
						index = getIndex(word,index,i,k);
				}
				input += " ";
			}
			input += "\n";
		}
		return input;
	}
	
	//全パターンを出力するために、インデックスを取得するメソッド
	private int[] getIndex(String[][][] word, int[] index, int i, int k){
		if(k-1 >= 0){
			System.out.println("getIndex");
			index[k-1]++;
			for(int l=k; l<index.length; l++){
				index[l] = 0;
			}
			if(index[k-1] > word[i][k-1].length-1 || word[i][k-1][index[k-1]] == null)
				return getIndex(word,index,i,k-1);
			return index;
		}
		else return index;
	}

}
