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

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class FrontFrameLayout extends FrameLayout {

	private SwipeLayoutInterface mISwipeLayout;

	public FrontFrameLayout(Context context) {
		super(context);
	}

	public FrontFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FrontFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setSwipeLayout(SwipeLayoutInterface mSwipeLayout){
		this.mISwipeLayout = mSwipeLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mISwipeLayout.getCurrentStatus() == SwipeLayout.Status.Close){
			return super.onInterceptTouchEvent(ev);
		}else {
			return true;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mISwipeLayout.getCurrentStatus() == SwipeLayout.Status.Close){
			return super.onTouchEvent(event);
		}else {
			if(event.getActionMasked() == MotionEvent.ACTION_UP){
				mISwipeLayout.close();
			}
			return true;
		}
	}

}
