package effect.cheng.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import effect.cheng.widget.CaptureView;

public class CropDemo extends Activity {
	private ImageView ivImage;
	private ImageView ivCrop;
	private CaptureView mCaptureView;
	private Button btnCrop;

	private Bitmap mBitmap;
	private final Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// ����ͼ��ʾ�����Ͻ�
			ivCrop.setImageBitmap((Bitmap) msg.obj);
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crop_main);
		ivImage = (ImageView) this.findViewById(R.id.iv_image);
		ivCrop = (ImageView) this.findViewById(R.id.iv_corp);
		mBitmap = BitmapFactory
				.decodeResource(getResources(), R.drawable.image);
		ivImage.setImageBitmap(mBitmap);
		mCaptureView = (CaptureView) this.findViewById(R.id.capture);
		btnCrop = (Button) this.findViewById(R.id.btn_crop);
		btnCrop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Runnable crop = new Runnable() {
					public void run() {
						Message msg = Message.obtain(mHandler);
						msg.obj = cropImage();
						msg.sendToTarget();
					}
				};
				startBackgroundJob("��ͼ", "���ڱ���", crop, mHandler);
			}
		});
	}

	private Bitmap cropImage() {
		Rect cropRect = mCaptureView.getCaptureRect();
		int width = cropRect.width();
		int height = cropRect.height();
		Bitmap croppedImage = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(croppedImage);
		Rect dstRect = new Rect(0, 0, width, height);
		// ����ͼƬ��ʾ����
		mBitmap = regulationBitmap(mBitmap);
		canvas.drawBitmap(mBitmap, cropRect, dstRect, null);
		return croppedImage;
	}

	// ImageView�е�ͼ���Ǹ�ʵ�ʵ�ͼƬ�б������ţ������Ҫ����ͼƬ����
	private Bitmap regulationBitmap(Bitmap bitmap) {
		int ivWidth = ivImage.getWidth();
		int ivHeight = ivImage.getHeight();
		int bmpWidth = bitmap.getWidth();
		int bmpHeight = bitmap.getHeight();
		// ��͸ߵı���
		float scaleWidth = (float) ivWidth / bmpWidth;
		float scaleHeight = (float) ivHeight / bmpHeight;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bmpWidth,
				bmpHeight, matrix, true);
		return resizeBmp;
	}

	public void startBackgroundJob(String title, String message, Runnable job,
			Handler handler) {
		ProgressDialog dialog = ProgressDialog.show(this, title, message, true,
				false);
		new Thread(new CropJob(job, dialog)).start();
	}

	private class CropJob implements Runnable {
		private final ProgressDialog mDialog;
		private final Runnable mJob;

		public CropJob(Runnable job, ProgressDialog dialog) {
			mDialog = dialog;
			mJob = job;
		}

		public void run() {
			try {
				mJob.run();
			} finally {
				if (mDialog.getWindow() != null)
					mDialog.dismiss();
			}
		}
	}
}