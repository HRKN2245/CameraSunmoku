package jp.recognize.scenery.android;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback; //jPeg生成後に呼ばれるコールバック
import android.os.Bundle;
import android.widget.Toast;

public class CameraPreviewActivity extends Activity {
	public static final String PICTURE_FILENAME = "image.jpeg";
	public static final String RESULT_PICTURE_DATASIZE = "RESULT_PICTURE_SIZE";
	public static byte[] imageData;
	private CameraPreview cameraPreview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cameraPreview = new CameraPreview(this, new PictureCallback() {
			//Camera.takePicutureで撮影を行い、onPictureTakenをコールバックメソッドの引数として、撮影した
			//画像をbyteデータ(byte[] data)として取得
			public void onPictureTaken(byte[] data, Camera camera) {
				imageData = data;
				FileOutputStream fileOutputStream = null;
				BufferedOutputStream bufferedOutputStream = null;
				try {
					//撮影画像をPICTURE_FILENAMEで出力
					fileOutputStream = openFileOutput(PICTURE_FILENAME, MODE_PRIVATE);
					bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					bufferedOutputStream.write(data);
					bufferedOutputStream.close();
					cameraPreview.releaseCamera(); //カメラ解放
					Intent intent = new Intent(); //Intent = アプリ間の連携
					//putExtraで、第1引数に任意のKey、第2引数に格納したいデータを他のActivityに渡す
					intent.putExtra(RESULT_PICTURE_DATASIZE, imageData.length);
					//メインActivityに実行結果を通知するメソッド。
					//RESULT_OKは、Activity遷移の成功を示す。
					if(data != null)
						setResult(Activity.RESULT_OK, intent);
				} catch (IOException e) {
					//遷移のキャンセルを示す
					setResult(Activity.RESULT_CANCELED);
				} finally {
					Utils.closeSilently(bufferedOutputStream);
					Utils.closeSilently(fileOutputStream);
				}

				finish();
			}
		});

		setContentView(cameraPreview); //Viewの設定
		Toast.makeText(this, "画面をタップして撮影してください。", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraPreview.releaseCamera();
	}

	@Override
	protected void onStop() {
		super.onStop();
		cameraPreview.releaseCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		cameraPreview.releaseCamera();
	}
}