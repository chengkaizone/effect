package effect.cheng.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import effect.cheng.widget.CircleProgress;

/**
 * 风格环形进度条
 * 
 * @author Administrator
 * 
 */
public class CircleProgressDemo extends Activity implements OnClickListener {

	private Button mBtnAddMain; // 增加进度值
	private Button mBtnAddSub; // 减少进度值
	private Button mImageBtn; // 清除进度值

	private CircleProgress progress1;
	private CircleProgress progress2;

	private int progress = 0;
	private int subProgress = 0;

	private Button mBtnStartButton; // 开启动画
	private Button mBtnSTopButton; // 结束动画
	private CircleProgress progress4;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_progress);
		initView();
	}

	public void initView() {
		mBtnAddMain = (Button) findViewById(R.id.buttonAddMainPro);
		mBtnAddSub = (Button) findViewById(R.id.buttonAddSubPro);
		mImageBtn = (Button) findViewById(R.id.buttonImage);

		mBtnStartButton = (Button) findViewById(R.id.buttonStart);
		mBtnSTopButton = (Button) findViewById(R.id.buttonStop);

		mBtnAddMain.setOnClickListener(this);
		mBtnAddSub.setOnClickListener(this);
		mImageBtn.setOnClickListener(this);

		mBtnStartButton.setOnClickListener(this);
		mBtnSTopButton.setOnClickListener(this);

		progress1 = (CircleProgress) findViewById(R.id.roundBar1);
		progress2 = (CircleProgress) findViewById(R.id.roundBar2);
		progress4 = (CircleProgress) findViewById(R.id.roundBar4);
		progress1.setSideInterval(4);
		progress1.setPaintColor(Color.argb(0xff, 0, 0, 0xff));
		progress1.setPaintWidth(6);
		progress1.setFill(false);
		progress1.setMaxProgress(100);
		progress2.setPaintColor(Color.argb(0xff, 0xaa, 0x55, 0));
		progress2.setFill(true);
		progress2.setMaxProgress(100);
		progress4.setPaintColor(Color.argb(0xff, 0, 0, 0xff));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.buttonAddMainPro:
			add();
			break;
		case R.id.buttonAddSubPro:
			Sub();
			break;
		case R.id.buttonImage:
			clear();
			break;
		case R.id.buttonStart:
			start();
			break;
		case R.id.buttonStop:
			stop();
			break;
		}
	}

	public void add() {
		progress += 5;
		if (progress > 100) {
			progress = 0;
		}
		progress1.setMainProgress(progress);
		progress2.setMainProgress(progress);
	}

	public void Sub() {
		subProgress += 5;
		if (subProgress > 100) {
			subProgress = 0;
		}
		progress1.setSubProgress(subProgress);
		progress2.setSubProgress(subProgress);
	}

	public void clear() {
		progress = 0;
		subProgress = 0;
		progress1.setMainProgress(0);
		progress2.setMainProgress(0);
		progress1.setSubProgress(0);
		progress2.setSubProgress(0);
	}

	public void start() {
		progress4.startCartoom(10);
	}

	public void stop() {
		progress4.stopCartoom();
	}
}