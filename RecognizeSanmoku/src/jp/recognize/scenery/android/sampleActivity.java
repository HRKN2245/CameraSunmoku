package jp.recognize.scenery.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import jp.recognize.common.RecognitionResult.Word;
import jp.recognize.common.RecognitionResult.SegmentGraph;
import jp.recognize.common.RecognitionResult.SegmentGraph.*;
import jp.recognize.common.RecognitionResult.SegmentGraph.Segment.Candidate;
import jp.recognize.common.SceneryRecognitionRequest.SceneryRecognitionHint.LetterColor;
import jp.recognize.common.ImageContentType;
import jp.recognize.common.SceneryRecognitionJob;
import jp.recognize.common.SceneryRecognizer;
import jp.recognize.common.client.HttpSceneryRecognitionRequest;
import jp.recognize.common.client.HttpSceneryRecognizer;
import jp.recognize.common.client.HttpSceneryRecognitionRequest.HttpSceneryRecognitionHint;
import jp.recognize.common.client.HttpSceneryRecognitionRequest.InputStreamImageContent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class sampleActivity extends Activity {
	private static final int REQUEST_CODE = 1;

	private Handler handler = new Handler();
	private ImageView imageView;
	private Paint fillPaint;
	private Paint framePaint;
	private Paint textPaint;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("起動！！");

		fillPaint = new Paint();
		fillPaint.setStyle(Style.FILL);
		fillPaint.setColor(Color.MAGENTA);
		fillPaint.setAlpha(128);

		framePaint = new Paint();
		framePaint.setStyle(Style.STROKE);
		framePaint.setColor(Color.MAGENTA);

		textPaint = new Paint();
		textPaint.setStyle(Style.STROKE);
		textPaint.setColor(Color.CYAN);
		textPaint.setTextSize(12);

		imageView = new ImageView(this);
		imageView.setScaleType(ScaleType.FIT_XY);

		setContentView(imageView);

		Intent intent = new Intent(this, CameraPreviewActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode != REQUEST_CODE) {
			return;
		}

		if (resultCode != Activity.RESULT_OK) {
			Toast.makeText(this, "画像の取得に失敗しました。", Toast.LENGTH_LONG).show();
			return;
		}

		final int pictureDataSize = data.getIntExtra(CameraPreviewActivity.RESULT_PICTURE_DATASIZE, -1);
		if (pictureDataSize == -1) {
			Toast.makeText(this, "画像サイズの取得に失敗しました。", Toast.LENGTH_LONG).show();
			return;
		}

		final ProgressDialog progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("認識中です。");
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {
				try {
					byte[] jpegData = CameraPreviewActivity.imageData;
					//Word[] words = recognize(jpegData);
					SegmentGraph[] segmentGraphtmp = recognize2(jpegData), //断片グラフを取得する。
									segmentGraph = null;  //正しく直された断片グラフ
					int[] wordCount, //文字数
							puttern; //パターン数
					String input = "";
					
					//断片グラフの設定
					setSegmentGraph(segmentGraphtmp);
					
					//設定した断片グラフのうち、null要素を全て削った新しい断片グラフ配列の宣言
					for(int i=0; i<segmentGraphtmp.length; i++){
						if(segmentGraphtmp[i] == null){
							segmentGraph = new SegmentGraph[i];
							break;
						}
					}
					//配列のコピー
					System.arraycopy(segmentGraphtmp, 0, segmentGraph, 0, segmentGraph.length);
					
					//文字数の取得
					wordCount = new int[segmentGraph.length];
					puttern = new int[segmentGraph.length];
					setStates(segmentGraph, wordCount);
					
					//認識結果の単語を入れる配列を宣言。
					String word[][][] = new String[segmentGraph.length][][];
					for(int i=0; i<segmentGraph.length; i++){
						word[i] = new String[wordCount[i]][2];
					}

					//全てのパターンを配列に代入する。
					word = getString(wordCount, puttern, segmentGraph);
					
					//文字列を取得する。
					input = getWords(word, wordCount, puttern);
					
					Intent intent = new Intent(sampleActivity.this,Sanmoku.class);
					//文字を取得し、sanmokuActivityにデータを送信。
					intent.putExtra("RecognizeData", input);
					startActivity(intent);
					finish();
				
					handler.post(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							//imageView.setImageBitmap(bitmap);
						}
					});
				} catch (final Exception e) {
					handler.post(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(sampleActivity.this, "認識に失敗しました。", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	//重要
	//撮影した画像のデータを返すメソッド
	private byte[] getPictureData(int pictureDataSize) throws IOException {
		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;

		try {
			byte[] jpegData = new byte[pictureDataSize];
			fileInputStream = openFileInput(CameraPreviewActivity.PICTURE_FILENAME);
			bufferedInputStream = new BufferedInputStream(fileInputStream, pictureDataSize);
			bufferedInputStream.read(jpegData);
			return jpegData;
		} finally {
			Utils.closeSilently(bufferedInputStream);
			Utils.closeSilently(fileInputStream);
		}
	}

	private SegmentGraph[] recognize2(byte[] jpegData) throws MalformedURLException, InterruptedException, UnsupportedEncodingException {
		// SceneryRecognizerインスタンスを生成します
		SceneryRecognizer recognizer = new HttpSceneryRecognizer(new URL(Constants.RECOGNITION_URL));

		// 認識を実行し認識ジョブを生成します 
		System.out.println(Constants.WORDS);
		SceneryRecognitionJob job = recognizer.recognize(new HttpSceneryRecognitionRequest
				(Constants.API_KEY, Constants.CHARACTERS, Constants.WORDS, Constants.ANALYSIS, 
						new InputStreamImageContent(ImageContentType.IMAGE_JPEG, new ByteArrayInputStream(jpegData)), 
						new HttpSceneryRecognitionHint(null, 0, LetterColor.Unknown, false)));
		// 認識ジョブが終了するまで待ちます
		job.waitFor();

		// 認識結果を取得します
		return job.getResultAsSegmentGraphs();
	}
	
	//断片グラフを設定するメソッド。
	//複数行の断片グラフを取得した場合、同じ結果が複数出ることがあるので、その同じ結果の断片グラフ配列除去
	//断片グラフの要素がない配列も除去。
	//読み取り結果が認識すべき文字数と一致しない断片グラフ配列も除去
	//断片グラフの無駄な配列を全て除去し、
	private void setSegmentGraph(SegmentGraph[] segmentGraphtmp) {
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
	
	//文字数、スコアを取得するメソッド
	//statesの配列化は、参照渡しをさせるため。
	private void setStates(SegmentGraph[] segmentGraph, int[] wordCount){
		Candidate[] segArray;
		for(int i=0; i<segmentGraph.length; i++){
			double TotalScore = 0.0;
			Segment seg = segmentGraph[i].getFirstSegment();
			while(seg != null){
				segArray = seg.getCandidates();
				TotalScore += segArray[0].getScore();
				seg = seg.getNextSegment();
				wordCount[i]++;
			}
			System.out.println(TotalScore);
			//一番有効な文字のトータルスコアが設定値以上だと、認識失敗とする。
			//ピントが合わない状態で撮影した場合の処置。
			if(TotalScore > 5.0)
				Toast.makeText(sampleActivity.this, "認識に失敗しました。", Toast.LENGTH_LONG).show();
		}
	}
	
	//全パターンを配列に代入するメソッド
	private String[][][] getString(int[] wordCount, int[] puttern, SegmentGraph[] segmentGraph){
		Candidate[] segArray;
		String word[][][] = new String[segmentGraph.length][][];
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
		//}
		return word;
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
		default: return null;
		}
	}
	
	//全パターンを出力し、文字列として取得するメソッド
	private String getWords(String[][][] word, int[] wordCount, int[] puttern){
		String input = "";
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
