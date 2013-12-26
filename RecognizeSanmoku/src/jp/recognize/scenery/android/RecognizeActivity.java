package jp.recognize.scenery.android;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import jp.recognize.common.RecognitionResult.SegmentGraph;
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
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class RecognizeActivity extends Activity {
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
					SegmentGraph[] segmentGraphtmp = recognize(jpegData); //断片グラフを取得する。
					
					System.out.println("OK");
					//文字列を取得し、形態素解析を行う。	
					Sanmoku sanmoku = new Sanmoku(new Words(segmentGraphtmp).getWords());
					Intent intent = new Intent();
					intent.putExtra("str", sanmoku.getMorpheme());
					//intent.putExtra("flag", sanmoku.exFlag);
					setResult(RESULT_OK, intent);
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
							Toast.makeText(RecognizeActivity.this, "認識に失敗しました。", Toast.LENGTH_LONG).show();
						}
					});
				}
			}
		}).start();
	}

	//重要
	//撮影した画像のデータを返すメソッド
	/*private byte[] getPictureData(int pictureDataSize) throws IOException {
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
	}*/

	private SegmentGraph[] recognize(byte[] jpegData) throws MalformedURLException, InterruptedException, UnsupportedEncodingException {
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

}
