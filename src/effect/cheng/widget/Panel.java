package effect.cheng.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import effect.cheng.main.R;
/**
 * ����һ��������---��ߺͶ����޷����ðٷֱ�
 * @author chengkai
 *
 */
public class Panel extends LinearLayout {
	//����������չ����
	protected int mPosition;
	protected int mDuration;
	protected boolean mLinearFlying;
	// ����---�ұߵױ���Ч
	protected float mWeight;
	protected int mHandleId;
	protected int mContentId;
	protected Drawable mOpenedHandle;
	protected Drawable mClosedHandle;
	
	private View mHandle;
	private View mContent;
	
	private boolean mIsShrinking;
	
	// X�����ϵı仯����
	private float mTrackX;
	// Y�����ϵı仯����
	private float mTrackY;
	// �˶�������
	private float mVelocity;
	//�˶�����С����
	private float minVelocity=300;

	private OnPanelListener panelListener;

	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	private enum State {
		ABOUT_TO_ANIMATE, 
		ANIMATING, // ���ڶ�����
		READY, //����״̬
		TRACKING, 
		FLYING, // ����
	};

	private State mState;
	private Interpolator mInterpolator;
	private GestureDetector mGestureDetector;
	private int mContentHeight;
	private int mContentWidth;
	// ���ַ�λ
	private int mOrientation;
	
	private PanelOnGestureListener mGestureListener;
	// ��ť�Ƿ��ö�
	private boolean mBringToFront;

	public Panel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// �����Զ�������
		TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.Panel);
		// �Զ�������
		mDuration = typed.getInteger(R.styleable.Panel_animationDuration, 750);
		mPosition = typed.getInteger(R.styleable.Panel_position, BOTTOM);
		mLinearFlying = typed.getBoolean(R.styleable.Panel_linearFlying, false);
		mWeight = typed.getFraction(R.styleable.Panel_weight, 0, 1, 0.0f);
		mOpenedHandle = typed.getDrawable(R.styleable.Panel_openedHandle);
		mClosedHandle = typed.getDrawable(R.styleable.Panel_closedHandle);
		mHandleId = typed.getResourceId(R.styleable.Panel_handle, 0);
		mContentId = typed.getResourceId(R.styleable.Panel_content, 0);
		init(typed);
	}
	private void init(TypedArray typed){
		if (mWeight < 0 || mWeight > 1) {
			mWeight = 0.0f;
			Log.w("Panel", typed.getPositionDescription()
					+ ": weight must be > 0 and <= 1");
		}

		RuntimeException e = null;
		if (mHandleId == 0) {
			e = new IllegalArgumentException(
					typed.getPositionDescription()
							+ ": The handle attribute is required and must refer to a valid child.");
		}
		if (mContentId == 0) {
			e = new IllegalArgumentException(
					typed.getPositionDescription()
							+ ": The content attribute is required and must refer to a valid child.");
		}
		typed.recycle();

		if (e != null) {
			throw e;
		}
		mOrientation = (mPosition == TOP || mPosition == BOTTOM) ? VERTICAL
				: HORIZONTAL;
		setOrientation(mOrientation);
		mState = State.READY;
		mGestureListener = new PanelOnGestureListener();
		mGestureDetector = new GestureDetector(mGestureListener);
		mGestureDetector.setIsLongpressEnabled(false);
		// ��������Ƿ���������ֻ��߶���
		setBaselineAligned(false);
	}

	public void setOnPanelListener(OnPanelListener onPanelListener) {
		panelListener = onPanelListener;
	}

	public View getHandle() {
		return mHandle;
	}

	public View getContent() {
		return mContent;
	}

	public void setInterpolator(Interpolator i) {
		mInterpolator = i;
	}

	/**
	 * ���ô���ر�
	 * @param open
	 * @param isAnimate �Ƿ�ʹ�ö���ִ��
	 * @return
	 */
	public boolean setOpen(boolean open, boolean isAnimate) {
		if (mState == State.READY && isOpen() ^ open) {
			mIsShrinking = !open;
			if (isAnimate) {
				mState = State.ABOUT_TO_ANIMATE;
				if (!mIsShrinking) {
					mContent.setVisibility(VISIBLE);
				}

				post(startAnimation);
			} else {
				mContent.setVisibility(open ? VISIBLE : GONE);
				postProcess();
			}
			return true;
		}
		return false;
	}

	public boolean isOpen() {
		return mContent.getVisibility() == VISIBLE;
	}

	@Override//����xml�ļ�����
	protected void onFinishInflate() {
		super.onFinishInflate();
		mHandle = findViewById(mHandleId);
		if (mHandle == null) {
			String name = getResources().getResourceEntryName(mHandleId);
			throw new RuntimeException(
					"Your Panel must have a child View whose id attribute is 'R.id."
							+ name + "'");
		}
		mHandle.setOnTouchListener(touchListener);
		mHandle.setOnClickListener(clickListener);
		mContent = findViewById(mContentId);
		if (mContent == null) {
			String name = getResources().getResourceEntryName(mHandleId);
			throw new RuntimeException(
					"Your Panel must have a child View whose id attribute is 'R.id."
							+ name + "'");
		}
		removeView(mHandle);
		removeView(mContent);
		if (mPosition == TOP || mPosition == LEFT) {
			addView(mContent);
			addView(mHandle);
		} else {
			addView(mHandle);
			addView(mContent);
		}
		if (mClosedHandle != null) {
			mHandle.setBackgroundDrawable(mClosedHandle);
		}
		//����������ͼ���ɼ�
		mContent.setClickable(true);
		mContent.setVisibility(GONE);
		if (mWeight > 0) {
			ViewGroup.LayoutParams params = mContent.getLayoutParams();
			if (mOrientation == VERTICAL) {
				params.height = ViewGroup.LayoutParams.MATCH_PARENT;
			} else {
				params.width = ViewGroup.LayoutParams.MATCH_PARENT;
			}
			mContent.setLayoutParams(params);
		}
	}

	@Override//����ͼ����������ʱֻ��onDraw����ǰ����һ��
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		ViewParent parent = getParent();
		if (parent != null && parent instanceof FrameLayout) {
			mBringToFront = true;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (mWeight > 0 && mContent.getVisibility() == VISIBLE) {
			View parent = (View) getParent();
			if (parent != null) {
				//��ֱ����
				if (mOrientation == VERTICAL&&mPosition==BOTTOM) {
					heightMeasureSpec = MeasureSpec.makeMeasureSpec(
							(int) (parent.getHeight() * mWeight),
							MeasureSpec.EXACTLY);
				} else if(mOrientation == HORIZONTAL&&mPosition==RIGHT){
					widthMeasureSpec = MeasureSpec.makeMeasureSpec(
							(int) (parent.getWidth() * mWeight),
							MeasureSpec.EXACTLY);
				}
			}
		}
		System.out.println(widthMeasureSpec+"==="+heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		mContentWidth = mContent.getWidth();
		mContentHeight = mContent.getHeight();
	}

	@Override//��������ͼʱ�ص��÷���
	protected void dispatchDraw(Canvas canvas) {
		//���ڶ�����
		if (mState == State.ABOUT_TO_ANIMATE && !mIsShrinking) {
			int delta = mOrientation == VERTICAL ? mContentHeight
					: mContentWidth;
			if (mPosition == LEFT || mPosition == TOP) {
				delta = -delta;
			}
			if (mOrientation == VERTICAL) {
				canvas.translate(0, delta);
			} else {
				canvas.translate(delta, 0);
			}
		}
		if (mState == State.TRACKING || mState == State.FLYING) {
			canvas.translate(mTrackX, mTrackY);
		}
		super.dispatchDraw(canvas);
	}

	private float ensureRange(float v, int min, int max) {
		v = Math.max(v, min);
		v = Math.min(v, max);
		return v;
	}

	OnTouchListener touchListener = new OnTouchListener() {
		int initX;
		int initY;
		boolean setInitialPosition;
		public boolean onTouch(View v, MotionEvent event) {
			if (mState == State.ANIMATING) {
				return false;
			}
			// Log.d(TAG, "state: " + mState + " x: " + event.getX() + " y: " + event.getY();
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				if (mBringToFront) {
					bringToFront();
				}
				initX = 0;
				initY = 0;
				if (mContent.getVisibility() == GONE) {
					// since we may not know content dimensions we use factors
					// here
					if (mOrientation == VERTICAL) {
						initY = mPosition == TOP ? -1 : 1;
					} else {
						initX = mPosition == LEFT ? -1 : 1;
					}
				}
				setInitialPosition = true;
			} else {
				if (setInitialPosition) {
					// now we know content dimensions, so we multiply factors...
					initX *= mContentWidth;
					initY *= mContentHeight;
					// ... and set initial panel's position
					mGestureListener.setScroll(initX, initY);
					setInitialPosition = false;
					// for offsetLocation we have to invert values
					initX = -initX;
					initY = -initY;
				}
				// offset every ACTION_MOVE & ACTION_UP event
				event.offsetLocation(initX, initY);
			}
			// �������
			if (!mGestureDetector.onTouchEvent(event)) {
				if (action == MotionEvent.ACTION_UP) {
					// �ɿ�ʱִ�й���
					post(startAnimation);
				}
			}
			return false;
		}
	};
	OnClickListener clickListener = new OnClickListener() {
		public void onClick(View v) {
			if (mBringToFront) {
				// ����ͼ��ʾ����Ļ����ǰ��
				bringToFront();
			}
			// �������ִ���У���ôִ�ж���
			if (initChange()) {
				post(startAnimation);
			}
		}
	};

	public boolean initChange() {
		// �����Ϊ׼��״̬---����ִ�ж���
		if (mState != State.READY) {
			return false;
		}
		mState = State.ABOUT_TO_ANIMATE;
		mIsShrinking = mContent.getVisibility() == VISIBLE;
		// �����δ����״̬
		if (!mIsShrinking) {
			// this could make flicker so we test mState in dispatchDraw()
			// to see if is equal to ABOUT_TO_ANIMATE
			// ����������ͼ�ɼ�
			mContent.setVisibility(VISIBLE);
		}
		return true;
	}

	// ��ʼ�������߳�
	Runnable startAnimation = new Runnable() {
		public void run() {
			// this is why we post this Runnable couple of lines above:
			// now its save to use mContent.getHeight() && mContent.getWidth()
			TranslateAnimation animation;
			int fromXDelta = 0, toXDelta = 0, fromYDelta = 0, toYDelta = 0;
			if (mState == State.FLYING) {
				mIsShrinking = (mPosition == TOP || mPosition == LEFT)
						^ (mVelocity > 0);
			}
			int calculatedDuration;
			// ��ֱ����
			if (mOrientation == VERTICAL) {
				int height = mContentHeight;
				if (!mIsShrinking) {
					fromYDelta = mPosition == TOP ? -height : height;
				} else {
					toYDelta = mPosition == TOP ? -height : height;
				}
				if (mState == State.TRACKING) {
					if (Math.abs(mTrackY - fromYDelta) < Math.abs(mTrackY
							- toYDelta)) {
						mIsShrinking = !mIsShrinking;
						toYDelta = fromYDelta;
					}
					fromYDelta = (int) mTrackY;
				} else if (mState == State.FLYING) {
					fromYDelta = (int) mTrackY;
				}
				// for FLYING events we calculate animation duration based on
				// flying velocity
				// also for very high velocity make sure duration >= 20 ms
				if (mState == State.FLYING && mLinearFlying) {
					calculatedDuration = (int) (1000 * Math
							.abs((toYDelta - fromYDelta) / mVelocity));
					calculatedDuration = Math.max(calculatedDuration, 20);
				} else {
					calculatedDuration = mDuration
							* Math.abs(toYDelta - fromYDelta) / mContentHeight;
				}
			} else {
				int width = mContentWidth;
				if (!mIsShrinking) {
					fromXDelta = mPosition == LEFT ? -width : width;
				} else {
					toXDelta = mPosition == LEFT ? -width : width;
				}
				if (mState == State.TRACKING) {
					if (Math.abs(mTrackX - fromXDelta) < Math.abs(mTrackX
							- toXDelta)) {
						mIsShrinking = !mIsShrinking;
						toXDelta = fromXDelta;
					}
					fromXDelta = (int) mTrackX;
				} else if (mState == State.FLYING) {
					fromXDelta = (int) mTrackX;
				}
				// for FLYING events we calculate animation duration based on
				// flying velocity
				// also for very high velocity make sure duration >= 20 ms
				if (mState == State.FLYING && mLinearFlying) {
					calculatedDuration = (int) (1000 * Math
							.abs((toXDelta - fromXDelta) / mVelocity));
					calculatedDuration = Math.max(calculatedDuration, 20);
				} else {
					calculatedDuration = mDuration
							* Math.abs(toXDelta - fromXDelta) / mContentWidth;
				}
			}

			mTrackX = mTrackY = 0;
			if (calculatedDuration == 0) {
				mState = State.READY;
				if (mIsShrinking) {
					mContent.setVisibility(GONE);
				}
				postProcess();
				return;
			}

			animation = new TranslateAnimation(fromXDelta, toXDelta,
					fromYDelta, toYDelta);
			animation.setDuration(calculatedDuration);
			animation.setAnimationListener(animationListener);
			if (mState == State.FLYING && mLinearFlying) {
				animation.setInterpolator(new LinearInterpolator());
			} else if (mInterpolator != null) {
				animation.setInterpolator(mInterpolator);
			}
			startAnimation(animation);
		}
	};

	private AnimationListener animationListener = new AnimationListener() {
		public void onAnimationEnd(Animation animation) {
			mState = State.READY;
			if (mIsShrinking) {
				mContent.setVisibility(GONE);
			}
			postProcess();
		}
		public void onAnimationRepeat(Animation animation) {
		}
		public void onAnimationStart(Animation animation) {
			mState = State.ANIMATING;
		}
	};
	//ִ��ͼƬ�л�---�ص��л�ʱ�ķ���
	private void postProcess() {
		// ���ͼƬ��Ϊnull����ô��������ʱ�ı���
		if (mIsShrinking && mClosedHandle != null) {
			mHandle.setBackgroundDrawable(mClosedHandle);
		} else if (!mIsShrinking && mOpenedHandle != null) {
			// ����Ϊչ���µı���
			mHandle.setBackgroundDrawable(mOpenedHandle);
		}
		// ִ�м������Ļص�����
		if (panelListener != null) {
			if (mIsShrinking) {
				panelListener.onPanelClosed(Panel.this);
			} else {
				panelListener.onPanelOpened(Panel.this);
			}
		}
	}

	// ���Ƽ��
	class PanelOnGestureListener implements OnGestureListener {
		float scrollY;
		float scrollX;

		public void setScroll(int initScrollX, int initScrollY) {
			scrollX = initScrollX;
			scrollY = initScrollY;
		}

		public boolean onDown(MotionEvent e) {
			scrollX = scrollY = 0;
			initChange();
			return true;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			mState = State.FLYING;
			// �������Ƽ�������
			mVelocity = mOrientation == VERTICAL ? velocityY : velocityX;
			if(Math.abs(mVelocity)<minVelocity){
				if(mVelocity<=0){
					mVelocity=-minVelocity;
				}else{
					mVelocity=minVelocity;
				}
			}
			post(startAnimation);
			return true;
		}

		public void onLongPress(MotionEvent e) {
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			mState = State.TRACKING;
			float tmpY = 0, tmpX = 0;
			// ����Ǵ�ֱ����
			if (mOrientation == VERTICAL) {
				scrollY -= distanceY;
				if (mPosition == TOP) {
					tmpY = ensureRange(scrollY, -mContentHeight, 0);
				} else {
					tmpY = ensureRange(scrollY, 0, mContentHeight);
				}
			} else {
				scrollX -= distanceX;
				if (mPosition == LEFT) {
					tmpX = ensureRange(scrollX, -mContentWidth, 0);
				} else {
					tmpX = ensureRange(scrollX, 0, mContentWidth);
				}
			}
			if (tmpX != mTrackX || tmpY != mTrackY) {
				mTrackX = tmpX;
				mTrackY = tmpY;
				invalidate();
			}
			return true;
		}

		public void onShowPress(MotionEvent e) {
		}

		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	}
	public static interface OnPanelListener {
		public void onPanelClosed(Panel panel);
		public void onPanelOpened(Panel panel);
	}
}
