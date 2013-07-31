package jp.recognize.scenery.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jp.recognize.common.RecognitionResult.Word;
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
					Word[] words = recognize(jpegData);
					String input = null;
					Intent intent = new Intent(sampleActivity.this,Sanmoku.class);
					//文字を取得し、sanmokuActivityにデータを送信。
					input = getWords(words);
					intent.putExtra("RecognizeData", input);
					startActivity(intent);
					finish();
					
					//bitmap: 認識結果を描くための変数
					//final Bitmap bitmap = drawRecognizeResult(jpegData, words);
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

	//重要
	private Word[] recognize(byte[] jpegData) throws MalformedURLException, InterruptedException {
		// SceneryRecognizerインスタンスを生成します
		SceneryRecognizer recognizer = new HttpSceneryRecognizer(new URL(Constants.RECOGNITION_URL));

		// 認識を実行し認識ジョブを生成します
		SceneryRecognitionJob job = recognizer.recognize(new HttpSceneryRecognitionRequest
				(Constants.API_KEY, Constants.CHARACTERS, Constants.WORDS, Constants.ANALYSIS, 
						new InputStreamImageContent(ImageContentType.IMAGE_JPEG, new ByteArrayInputStream(jpegData)), 
						new HttpSceneryRecognitionHint(null, 0, LetterColor.Unknown, false)));
		// 認識ジョブが終了するまで待ちます
		job.waitFor();

		// 認識結果を取得します
		return job.getResultAsWords(100);
	}

	//認識文字を取得し、正しい文章になるように並び替えるメソッド
	private String getWords(Word[] words){
		String input = null;
		Word tmp = null;
		
		//y軸順に並び替える。この時点ではx軸順から見て誤っている可能性がある。
		for(int i=0; i<words.length; i++){
			for(int j=i; 0<j; j--){
				if(words[j].getShape().getBounds().getTop() < words[j-1].getShape().getBounds().getTop()){
					tmp = words[j];
					words[j] = words[j-1];
					words[j-1] = tmp;
				}
			}
		}
		
		//y軸順に並び替えた単語配列の中で、同じ行にある単語をx軸順に並び替える。
		for(int i=0; i<words.length; i++){
			for(int j=i; 0<j; j--){
				if(SameArrayJudge(words, j) && (words[j].getShape().getBounds().getLeft() < words[j-1].getShape().getBounds().getLeft())){
					tmp = words[j];
					words[j] = words[j-1];
					words[j-1] = tmp;
				}
			}
		}
		for(int i=0; i<words.length; i++){
			System.out.println(words[i].getText()+","+words[i].getShape().getBounds().getLeft()+","+words[i].getShape().getBounds().getTop()
					+","+words[i].getShape().getBounds().getBottom()+","+words[i].getShape().getBounds().getHeight());
			if(i==0) input = words[i].getText();
			else input += words[i].getText();
		}
		return input;
	}
	
	//単語が同じ行にあるかを判断するメソッド
	private boolean SameArrayJudge(Word[] words, int j){
		int WordsBottom = 0 ,WordsBottomBefore = 0;
		//前の単語が後の単語より少しだけ低い位置にあると認識された場合
		if((WordsBottomBefore = words[j-1].getShape().getBounds().getBottom()) > (WordsBottom = words[j].getShape().getBounds().getBottom())){
			if(words[j-1].getShape().getBounds().getTop() < words[j].getShape().getBounds().getTop())
				return true;
			else 
				return words[j-1].getShape().getBounds().getHeight() - (WordsBottomBefore - WordsBottom) > words[j].getShape().getBounds().getHeight() / 5 
						? true : false;
		}
		//前の単語が後の単語より少しだけ高い位置にあると認識された場合
		else{
			if(words[j-1].getShape().getBounds().getTop() > words[j].getShape().getBounds().getTop())
				return true;
			else 
				return words[j].getShape().getBounds().getHeight() - (WordsBottom - WordsBottomBefore) > words[j-1].getShape().getBounds().getHeight() / 5 
						? true : false;
		}
	}
	
	//認識結果を描画するメソッド
	/*
	private Bitmap drawRecognizeResult(byte[] pictureData, Word[] words) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
		final Bitmap mutableBitmap = bitmap.copy(bitmap.getConfig(), true);
		bitmap.recycle();
		Canvas canvas = new Canvas(mutableBitmap);

		for (Word word : words) {
			Rectangle bounds = word.getShape().getBounds();
			Rect rect = new Rect(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
			canvas.drawRect(rect, fillPaint);
			canvas.drawRect(rect, framePaint);
		}
		
		for (Word word : words) {
			Rectangle bounds = word.getShape().getBounds();
			Rect rect = new Rect(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
			textPaint.setTextSize(rect.height() > rect.width() ? rect.width() : rect.height());
			canvas.drawText(word.getText(), rect.left, rect.top, textPaint);
		}

		return mutableBitmap;
	}*/
}
