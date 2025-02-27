// Copyright 2025 Grandstream
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// 
//     https://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.gs.app.netsuiteerp.ui.widgets.swipelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * 滑动条目侧滑布局
 */
public class SwipeRelativeLayout extends RelativeLayout implements SwipeLayoutInterface{

	private static final String TAG = "SwipeLayout";
	private View mFrontView;
	private View mBackView;
	private int mDragDistance;
	private SwipeLayout.ShowEdge mShowEdge = SwipeLayout.ShowEdge.Right;
	private SwipeLayout.Status mStatus = SwipeLayout.Status.Close;
	private ViewDragHelper mDragHelper;
	private SwipeListener mSwipeListener;
	private GestureDetectorCompat mGestureDetector;

	public static interface SwipeListener{
		void onClose(SwipeRelativeLayout swipeLayout);
		void onOpen(SwipeRelativeLayout swipeLayout);
		void onStartClose(SwipeRelativeLayout swipeLayout);
		void onStartOpen(SwipeRelativeLayout swipeLayout);
	}


	public SwipeRelativeLayout(Context context) {
		this(context, null);
	}

	public SwipeRelativeLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SwipeRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mDragHelper = ViewDragHelper.create(this, mCallback);
		//初始化手势识别器
		mGestureDetector = new GestureDetectorCompat(context, mOnGestureListener);
		
	}
	private SimpleOnGestureListener mOnGestureListener = new SimpleOnGestureListener(){
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
			// 当横向移动距离大于等于纵向时，返回true
			return Math.abs(distanceX) >= Math.abs(distanceY);
		}
	};
	
	ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			// 根据返回的boolean值决定是否要滑动我们刚刚按下的child。
			return child == getFrontView() || child == getBackView();
		}

		@Override
		public int getViewHorizontalDragRange(View child) {
			// 返回滑动范围，用来设置拖拽的范围。
				// 大于0即可，不会真正限制child的移动范围。
				// 内部用来计算是否此方向是否可以拖拽，以及释放时动画执行时间
			return mDragDistance;
		};
		
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// 修正最新的建议值left和所产生的偏移量dx 以得到横向滑动的最终左边位置
			int newLeft = left;
			if(child == mFrontView){
				// 如果滑动的是前View
				switch (mShowEdge) {
					case Left:
						if(newLeft < 0) newLeft = 0;
						else if(newLeft > mDragDistance) newLeft = mDragDistance;
						
						break;
					case Right:
						if(newLeft < 0 - mDragDistance) newLeft = 0 - mDragDistance;
						else if(newLeft > 0) newLeft = 0;
						
						break;
				}
			}else if (child == mBackView) {
				// 如果滑动的是后View
				switch (mShowEdge) {
					case Left:
						if(newLeft < 0 - mDragDistance) newLeft = 0 - mDragDistance;
						else if(newLeft > 0) newLeft = 0;
						break;
					case Right:
						if(newLeft < getMeasuredWidth() - mDragDistance) {
							newLeft = getMeasuredWidth() - mDragDistance;
						}else if (newLeft > getMeasuredWidth()) {
							newLeft = getMeasuredWidth();
						}
						break;
				}
			}
			
			return newLeft;
		};
		
		@Override
		public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
			// 当view的位置改变时，在这里处理位置改变后要做的事情。
			if(changedView == mFrontView){
				// 如果用户用手指滑动的是前景View，那么也要将横向变化量dx交给背景View
				getBackView().offsetLeftAndRight(dx);
			}else if(changedView == mBackView){
				// 如果用户用手指滑动的是背景View，那么也要将横向变化量dx交给前景View
				getFrontView().offsetLeftAndRight(dx);
			}
			
			// 实时更新当前的状态
			updateStatus();
			// 重绘界面
			invalidate();
		};

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 当释放滑动的view时，处理最后的事情。（执行开启或关闭的动画）
			Log.d(TAG, "xvel: " + xvel + " mShowEdge: " + mShowEdge);
			if(releasedChild == getFrontView()){
				processFrontViewRelease(xvel ,yvel);
			}else if (releasedChild == getBackView()) {
				processBackViewRelease(xvel,yvel);
			}
			invalidate();
		};
		
		
		
	};
	private float mDownX;
	
	protected void processBackViewRelease(float xvel, float yvel) {
		switch (mShowEdge) {
			case Left:
				if(xvel == 0){
					if(getBackView().getLeft() > (0 - mDragDistance * 0.5f)) {
						open();
						return;
					}
				}else if (xvel > 0) {
					open();
					return;
				}
				break;
			case Right:
				if(xvel == 0){
					if(getBackView().getLeft() < (getMeasuredWidth() - mDragDistance * 0.5f	)){
						open();
						return;
					}
				}else if (xvel < 0){
					open();
					return;
				}
				break;
		}
		close();
	}

	@Override
	public SwipeLayout.Status getCurrentStatus() {
		int left = getFrontView().getLeft();
		if(left == 0){
			return SwipeLayout.Status.Close;
		}
		if((left == 0 - mDragDistance) || (left == mDragDistance)){
			return SwipeLayout.Status.Open;
		}
		return SwipeLayout.Status.Swiping;
	}

	protected void processFrontViewRelease(float xvel, float yvel) {
		switch (mShowEdge) {
			case Left:
				if(xvel == 0){
					if(getFrontView().getLeft() > mDragDistance * 0.5f){
						open();
						return;
					}
				}else if (xvel > 0) {
					open();
					return;
				}
				break;
			case Right:
				if(xvel == 0){
					if(getFrontView().getLeft() < 0 - mDragDistance * 0.5f){
						open();
						return;
					}
				}else if (xvel < 0) {
					open();
					return;
				}
				break;
		}
		close();
	}
	
	public void close(){
		close(true);
	}

	public void close(boolean isSmooth){
		close(isSmooth, true);
	}
	
	public void close(boolean isSmooth, boolean isNotify) {
		if (isSmooth) {
			Rect rect = computeFrontLayout(false);
			if(mDragHelper.smoothSlideViewTo(getFrontView(), rect.left, rect.top)){
				ViewCompat.postInvalidateOnAnimation(this);
			};
		} else {
			layoutContent(false);
			updateStatus(isNotify);
		}
	}

	public void open(){
		open(true, true);
	}
	public void open(boolean isSmooth){
		open(isSmooth, true);
	}

	/**
	 * 展开layout
	 * @param isSmooth 是否是平滑的动画。
	 * @param isNotify 是否进行通知回调
	 */
	public void open(boolean isSmooth, boolean isNotify) {
		if (isSmooth) {
			Rect rect = computeFrontLayout(true);
			if(mDragHelper.smoothSlideViewTo(getFrontView(), rect.left, rect.top)){
				ViewCompat.postInvalidateOnAnimation(this);
			};
		} else {
			layoutContent(true);
			updateStatus(isNotify);
		}
	}
	private void updateStatus(){
		updateStatus(true);
	}

	/**
	 * 更新当前状态
	 * @param isNotify
	 */
	private void updateStatus(boolean isNotify) {
		SwipeLayout.Status lastStatus = mStatus;
		SwipeLayout.Status status = getCurrentStatus();

		if (status != mStatus) {
			mStatus = status;

			if (!isNotify || mSwipeListener == null) {
				return;
			}

			if (mStatus == SwipeLayout.Status.Open) {
				mSwipeListener.onOpen(this);
			} else if (mStatus == SwipeLayout.Status.Close) {
				mSwipeListener.onClose(this);
			} else if (mStatus == SwipeLayout.Status.Swiping) {
				if (lastStatus == SwipeLayout.Status.Open) {
					mSwipeListener.onStartClose(this);
				} else if (lastStatus == SwipeLayout.Status.Close) {
					mSwipeListener.onStartOpen(this);
				}
			}
		} else {
			mStatus = status;
		}
	}
	@Override
	public void computeScroll() {
		
		// 在这里判断动画是否需要继续执行。会在View.draw(Canvas mCanvas)之前执行。
		if(mDragHelper.continueSettling(true)){
			// 返回true，表示动画还没执行完，需要继续执行。
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}


	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// 决定当前的SwipeLayout是否要把touch事件拦截下来，直接交由自己的onTouchEvent处理
		// 返回true则为拦截
		return  mDragHelper.shouldInterceptTouchEvent(ev) & mGestureDetector.onTouchEvent(ev);
	};
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// 当处理touch事件时，不希望被父类onInterceptTouchEvent的代码所影响。
		// 比如处理向右滑动关闭已打开的条目时，如果进行以下逻辑，则不会在关闭的同时引发左边菜单的打开。
		
		switch (MotionEventCompat.getActionMasked(event)) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getRawX();
				break;
			case MotionEvent.ACTION_MOVE:
                
				float deltaX = event.getRawX() - mDownX;
				if(deltaX > mDragHelper.getTouchSlop()){
					// 请求父级View不拦截touch事件
					requestDisallowInterceptTouchEvent(true);
				}
				break;
			case MotionEvent.ACTION_UP:
				mDownX = 0;
			default :
				break;
		}
		
		try {
			mDragHelper.processTouchEvent(event);
		} catch (IllegalArgumentException e) {
		}

		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mDragDistance = getBackView().getMeasuredWidth();
		
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		layoutContent(false);
		
	}
	private void layoutContent(boolean isOpen) {
		Rect rect = computeFrontLayout(isOpen);
		getFrontView().layout(rect.left, rect.top, rect.right, rect.bottom);
		rect = computeBackLayoutViaFront(rect);
		getBackView().layout(rect.left, rect.top, rect.right, rect.bottom);
		bringChildToFront(getFrontView());
	}

	private Rect computeBackLayoutViaFront(Rect mFrontRect) {
		Rect rect = mFrontRect;

		int bl = rect.left, bt = rect.top, br = rect.right, bb = rect.bottom;
		if (mShowEdge == SwipeLayout.ShowEdge.Left) {
			bl = rect.left - mDragDistance;
		} else if (mShowEdge == SwipeLayout.ShowEdge.Right) {
			bl = rect.right;
		}
		br = bl + getBackView().getMeasuredWidth();

		return new Rect(bl, bt, br, bb);
	}

	private Rect computeFrontLayout(boolean isOpen) {
		int l = 0, t = 0;
		if(isOpen){
			if(mShowEdge == SwipeLayout.ShowEdge.Left){
				l = 0 + mDragDistance;
			}else if (mShowEdge == SwipeLayout.ShowEdge.Right) {
				l = 0 - mDragDistance;
			}
		}
		
		return new Rect(l, t, l + getMeasuredWidth(), t + getMeasuredHeight());
	}

	@SuppressLint("MissingSuperCall")
	@Override
	protected void onFinishInflate() {
		if(getChildCount() != 2){
			throw new IllegalStateException("At least 2 views in SwipeLayout");
		}
		
		mFrontView = getChildAt(0);
		if(mFrontView instanceof FrontFrameLayout){
			((FrontFrameLayout) mFrontView).setSwipeLayout(this);
		} else if(mFrontView instanceof FrontLinearLayout){
			((FrontLinearLayout) mFrontView).setSwipeLayout(this);
		} else if(mFrontView instanceof FrontRelativeLayout){
			((FrontRelativeLayout) mFrontView).setSwipeLayout(this);
		} else {
			throw new IllegalArgumentException("Front view must be an instanceof FrontLayout");
		}
		
		mBackView = getChildAt(1);
		
	}
	public View getFrontView(){
		return mFrontView;
	}
	public View getBackView(){
		return mBackView;
	}

	public void setShowEdge(SwipeLayout.ShowEdge showEdit) {
		mShowEdge = showEdit;
		requestLayout();
	}

	public SwipeListener getSwipeListener() {
		return mSwipeListener;
	}

	public void setSwipeListener(SwipeListener mSwipeListener) {
		this.mSwipeListener = mSwipeListener;
	}
}
