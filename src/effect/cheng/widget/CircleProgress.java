package effect.cheng.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义环形进度条
 * 
 * @author Administrator
 * 
 */
public class CircleProgress extends View {

	private static final int DEFAULT_MAX_VALUE = 100; // 默认进度条最大值
	private static final int DEFAULT_PAINT_WIDTH = 10; // 默认画笔宽度
	private static final int DEFAULT_PAINT_COLOR = 0xffffcc00; // 默认画笔颜色
	private static final boolean DEFAULT_FILL_MODE = true; // 默认填充模式
	private static final int DEFAULT_INSIDE_VALUE = 2; // 默认缩进距离

	private CircleAttribute mCircleAttribute; // 圆形进度条基本属性

	private int maxProgress; // 进度条最大值
	private int mainCurProgress; // 主进度条当前值
	private int subCurProgress; // 子进度条当前值
	// 是否填充模式
	private boolean isFill;
	// 画笔颜色
	private int paintColor;
	// 画笔宽度
	private int paintWidth;
	// 环形缩进距离
	private int sideInterval;

	private CartoomEngine mCartoomEngine; // 动画引擎

	private Drawable mBackgroundPicture; // 背景图

	public CircleProgress(Context context) {
		super(context);
		initParam();
	}

	public CircleProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		initParam();
	}

	private void initParam() {
		mCircleAttribute = new CircleAttribute();
		mCartoomEngine = new CartoomEngine();
		maxProgress = DEFAULT_MAX_VALUE;
		mainCurProgress = 0;
		subCurProgress = 0;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { // 设置视图大小
		// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		mBackgroundPicture = getBackground();
		if (mBackgroundPicture != null) {
			width = mBackgroundPicture.getMinimumWidth();
			height = mBackgroundPicture.getMinimumHeight();
		}
		setMeasuredDimension(resolveSize(width, widthMeasureSpec),
				resolveSize(width, heightMeasureSpec));
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mCircleAttribute.autoFix(w, h);
	}

	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 没背景图的话就绘制底色
		if (mBackgroundPicture == null) {
			canvas.drawArc(mCircleAttribute.mRoundOval, 0, 360,
					mCircleAttribute.mBRoundPaintsFill,
					mCircleAttribute.mBottomPaint);
		}

		float subRate = (float) subCurProgress / maxProgress;
		float subSweep = 360 * subRate;
		canvas.drawArc(mCircleAttribute.mRoundOval, mCircleAttribute.mDrawPos,
				subSweep, mCircleAttribute.mBRoundPaintsFill,
				mCircleAttribute.mSubPaint);

		float rate = (float) mainCurProgress / maxProgress;
		float sweep = 360 * rate;
		canvas.drawArc(mCircleAttribute.mRoundOval, mCircleAttribute.mDrawPos,
				sweep, mCircleAttribute.mBRoundPaintsFill,
				mCircleAttribute.mMainPaints);

	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
	}

	public void setFill(boolean isFill) {
		this.isFill = isFill;
		mCircleAttribute.setFill(isFill);
	}

	public void setPaintColor(int paintColor) {
		this.paintColor = paintColor;
		mCircleAttribute.setPaintColor(paintColor);
	}

	public void setPaintWidth(int paintWidth) {
		this.paintWidth = paintWidth;
		if (!isFill) {
			mCircleAttribute.setPaintWidth(paintWidth);
		}
	}

	public void setSideInterval(int sideInterval) {
		this.sideInterval = sideInterval;
		mCircleAttribute.mSidePaintInterval = sideInterval;
	}

	/*
	 * 设置主进度值
	 */
	public synchronized void setMainProgress(int progress) {
		mainCurProgress = progress;
		if (mainCurProgress < 0) {
			mainCurProgress = 0;
		}

		if (mainCurProgress > maxProgress) {
			mainCurProgress = maxProgress;
		}

		invalidate();
	}

	public synchronized int getMainProgress() {
		return mainCurProgress;
	}

	/*
	 * 设置子进度值
	 */
	public synchronized void setSubProgress(int progress) {
		subCurProgress = progress;
		if (subCurProgress < 0) {
			subCurProgress = 0;
		}

		if (subCurProgress > maxProgress) {
			subCurProgress = maxProgress;
		}

		invalidate();
	}

	public synchronized int getSubProgress() {
		return subCurProgress;
	}

	/*
	 * 开启动画
	 */
	public void startCartoom(int time) {
		mCartoomEngine.startCartoom(time);

	}

	/*
	 * 结束动画
	 */
	public void stopCartoom() {
		mCartoomEngine.stopCartoom();
	}

	class CircleAttribute {
		public RectF mRoundOval; // 圆形所在矩形区域
		public boolean mBRoundPaintsFill; // 是否填充以填充模式绘制圆形
		public int mSidePaintInterval; // 圆形向里缩进的距离
		public int mPaintWidth; // 圆形画笔宽度（填充模式下无视）
		public int mPaintColor; // 画笔颜色 （即主进度条画笔颜色，子进度条画笔颜色为其半透明值）
		public int mDrawPos; // 绘制圆形的起点（默认为-90度即12点钟方向）

		public Paint mMainPaints; // 主进度条画笔
		public Paint mSubPaint; // 子进度条画笔

		public Paint mBottomPaint; // 无背景图时绘制所用画笔

		public CircleAttribute() {
			mRoundOval = new RectF();
			mBRoundPaintsFill = DEFAULT_FILL_MODE;
			mSidePaintInterval = DEFAULT_INSIDE_VALUE;
			mPaintWidth = 0;
			mPaintColor = DEFAULT_PAINT_COLOR;
			mDrawPos = -90;

			mMainPaints = new Paint();
			mMainPaints.setAntiAlias(true);
			mMainPaints.setStyle(Paint.Style.FILL);
			mMainPaints.setStrokeWidth(mPaintWidth);
			mMainPaints.setColor(mPaintColor);

			mSubPaint = new Paint();
			mSubPaint.setAntiAlias(true);
			mSubPaint.setStyle(Paint.Style.FILL);
			mSubPaint.setStrokeWidth(mPaintWidth);
			mSubPaint.setColor(mPaintColor);

			mBottomPaint = new Paint();
			mBottomPaint.setAntiAlias(true);
			mBottomPaint.setStyle(Paint.Style.FILL);
			mBottomPaint.setStrokeWidth(mPaintWidth);
			mBottomPaint.setColor(Color.GRAY);

		}

		/*
		 * 设置画笔宽度
		 */
		public void setPaintWidth(int width) {
			mMainPaints.setStrokeWidth(width);
			mSubPaint.setStrokeWidth(width);
			mBottomPaint.setStrokeWidth(width);
		}

		/*
		 * 设置画笔颜色
		 */
		public void setPaintColor(int color) {
			mMainPaints.setColor(color);
			int color1 = color & 0x00ffffff | 0x66000000;
			mSubPaint.setColor(color1);
		}

		/*
		 * 设置填充模式
		 */
		public void setFill(boolean fill) {
			mBRoundPaintsFill = fill;
			if (fill) {
				mMainPaints.setStyle(Paint.Style.FILL);
				mSubPaint.setStyle(Paint.Style.FILL);
				mBottomPaint.setStyle(Paint.Style.FILL);
			} else {
				mMainPaints.setStyle(Paint.Style.STROKE);
				mSubPaint.setStyle(Paint.Style.STROKE);
				mBottomPaint.setStyle(Paint.Style.STROKE);
			}
		}

		/*
		 * 自动修正
		 */
		public void autoFix(int w, int h) {
			if (mSidePaintInterval != 0) {
				mRoundOval.set(mPaintWidth / 2 + mSidePaintInterval,
						mPaintWidth / 2 + mSidePaintInterval, w - mPaintWidth
								/ 2 - mSidePaintInterval, h - mPaintWidth / 2
								- mSidePaintInterval);
			} else {
				int sl = getPaddingLeft();
				int sr = getPaddingRight();
				int st = getPaddingTop();
				int sb = getPaddingBottom();

				mRoundOval.set(sl + mPaintWidth / 2, st + mPaintWidth / 2, w
						- sr - mPaintWidth / 2, h - sb - mPaintWidth / 2);
			}
		}

	}

	class CartoomEngine {
		public Handler mHandler;
		public boolean mBCartoom; // 是否正在作动画
		public Timer mTimer; // 用于作动画的TIMER
		public MyTimerTask mTimerTask; // 动画任务
		public int mSaveMax; // 在作动画时会临时改变MAX值，该变量用于保存值以便恢复
		public int mTimerInterval; // 定时器触发间隔时间(ms)
		public float mCurFloatProcess; // 作动画时当前进度值
		private long timeMil;

		public CartoomEngine() {
			mHandler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case TIMER_ID: {
						if (mBCartoom == false) {
							return;
						}
						mCurFloatProcess += 1;
						setMainProgress((int) mCurFloatProcess);
						long curtimeMil = System.currentTimeMillis();
						timeMil = curtimeMil;
						if (mCurFloatProcess >= maxProgress) {
							stopCartoom();
						}
					}
						break;
					}
				}

			};
			mBCartoom = false;
			mTimer = new Timer();
			mSaveMax = 0;
			mTimerInterval = 50;
			mCurFloatProcess = 0;
		}

		public synchronized void startCartoom(int time) {
			if (time <= 0 || mBCartoom == true) {
				return;
			}
			timeMil = 0;
			mBCartoom = true;
			setMainProgress(0);
			setSubProgress(0);
			mSaveMax = maxProgress;
			maxProgress = (1000 / mTimerInterval) * time;
			mCurFloatProcess = 0;
			mTimerTask = new MyTimerTask();
			mTimer.schedule(mTimerTask, mTimerInterval, mTimerInterval);
		}

		public synchronized void stopCartoom() {
			if (mBCartoom == false) {
				return;
			}
			mBCartoom = false;
			maxProgress = mSaveMax;
			setMainProgress(0);
			setSubProgress(0);
			if (mTimerTask != null) {
				mTimerTask.cancel();
				mTimerTask = null;
			}
		}

		private final static int TIMER_ID = 0x0010;

		class MyTimerTask extends TimerTask {
			public void run() {
				Message msg = mHandler.obtainMessage(TIMER_ID);
				msg.sendToTarget();
			}
		}
	}
}