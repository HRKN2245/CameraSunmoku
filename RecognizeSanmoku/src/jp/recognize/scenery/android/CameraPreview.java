package jp.recognize.scenery.android;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * CameraのPreview表示をするViewです。
 * Previewを表示するためには、サーフェスビュー(SurfaceView)を用いる。
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, AutoFocusCallback {
	private final String TAG = CameraPreview.class.getSimpleName();
	private Camera camera;
	private PictureCallback pictureCallback;

	//引数ありコンストラクタ
	public CameraPreview(Context aContext, PictureCallback pictureCallback) {
		super(aContext);

		this.pictureCallback = pictureCallback;
		//getHolder()で、surfaceHolderオブジェクトを取得。
		//addCallbackメソッドにて、surfaceViewが変化したときに呼び出されるsurfaceHolder.callback
		//インターフェースを実装するクラスを指定。
		getHolder().addCallback(this);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	//surfaceChanged:サーフェスが変更されたときのイベント処理を記述
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		Log.d(TAG, "surfaceChanged");

		if (camera == null) {
			return;
		}

		//プレビューの開始
		camera.startPreview();
	}
	
	//surfaceCreated:サーフェスが作成されたとき
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		Log.d(TAG, "surfaceCreated");

		try {
			//カメラオープン
			camera = Camera.open();
			//カメラのパラメータを取得し、parametersに代入
			Parameters parameters = camera.getParameters();
			Size previewSize = max(parameters.getSupportedPreviewSizes());
			parameters.setPreviewSize(previewSize.width, previewSize.height);
			//パラメータのセット
			camera.setParameters(parameters);
			//surfaceHolderを設定する。
			camera.setPreviewDisplay(surfaceHolder);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Size max(List<Size> supportedPreviewSizes) {
		Log.d(TAG, "supported preview size");
		int maxArea = Integer.MIN_VALUE;
		Size maxSize = null;
		for (Size size : supportedPreviewSizes) {
			int area = size.width * size.height;
			if (maxArea < area) {
				maxArea = area;
				maxSize = size;
			}
		}

		return maxSize;
	}

	//surfaceDestroyed:サーフェスが破棄されたとき。
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		Log.d(TAG, "surfaceDestroyed");
		releaseCamera();
	}

	public void releaseCamera() {
		if (camera == null) {
			return;
		}

		camera.setPreviewCallback(null);
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	//オートフォーカスを行う
	public void onAutoFocus(boolean success, Camera camera) {
		if (!success) {
			return;
		}

		camera.takePicture(null, null, pictureCallback);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		camera.autoFocus(this);
		return super.onTouchEvent(event);
	}
}