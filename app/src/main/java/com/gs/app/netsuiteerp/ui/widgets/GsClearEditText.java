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

package com.gs.app.netsuiteerp.ui.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.AppCompatEditText;

import com.gs.app.netsuiteerp.R;

import java.util.Objects;

/**
 * 带清除按钮的输入框
 */
public class GsClearEditText extends AppCompatEditText implements
        OnFocusChangeListener, TextWatcher {
	/**
	 * 删除按钮的引用
	 */
    private Drawable mClearDrawable;
    /**
     * 控件是否有焦点
     */
    private boolean hasFocus;

    private boolean autoHideIcon=true;
    private boolean autoShowClearIcon=true;

    private int maxInputLength=-1;

    public GsClearEditText(Context context) {
    	this(context, null); 
    } 
 
    public GsClearEditText(Context context, AttributeSet attrs) {
    	//这里构造方法也很重要，不加这个很多属性不能再XML里面定义
    	this(context, attrs, android.R.attr.editTextStyle); 
    } 
    
    public GsClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    
    @SuppressLint({"SuspiciousIndentation", "UseCompatLoadingForDrawables"})
    @SuppressWarnings("deprecation")
	private void init() { 
    	//获取EditText的DrawableRight/DrawableEnd,假如没有设置我们就使用默认的图片
    	mClearDrawable = getCompoundDrawablesRelative()[2];                     //获取EditText的DrawableEnd
    	if(mClearDrawable==null) mClearDrawable = getCompoundDrawables()[2];   //获取EditText的DrawableRight

        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
        	mClearDrawable = getResources().getDrawable(R.drawable.ic_clear_box);
        } 
        
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicHeight(), mClearDrawable.getIntrinsicHeight());//mClearDrawable.getIntrinsicHeight()
        //默认设置隐藏图标
        setClearIconVisible(false); 
        //设置焦点改变的监听
        setOnFocusChangeListener(this); 
        //设置输入框里面内容发生改变的监听
        addTextChangedListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setClearDrawable(@DrawableRes int res){
        mClearDrawable = getResources().getDrawable(res);
        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicHeight(), mClearDrawable.getIntrinsicHeight());
    }


    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));
				
				if (touchable) {
				if(clearListener==null)	this.setText("");
				else clearListener.onClick(this);
				}
			}
		}

		return super.onTouchEvent(event);
	}
 
    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus && isAutoShowClearIcon()) {
            setClearIconVisible(Objects.requireNonNull(getText()).length() > 0);
        } else if(autoHideIcon){
            setClearIconVisible(false);
        }
        if(focusChangedListener!=null) focusChangedListener.onFocusChange(v,hasFocus);
    } 
 
 
    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     */
    public void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mClearDrawable : null;
        setCompoundDrawablesRelative(
                getCompoundDrawablesRelative()[0],
                getCompoundDrawablesRelative()[1],
                right,
                getCompoundDrawablesRelative()[3]
        );
    } 
     
    
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
        if(hasFocus && isAutoShowClearIcon()){
            setClearIconVisible(s.length() > 0);
        }
    } 
 
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    } 
 
    @Override
    public void afterTextChanged(Editable s) {
         if(maxInputLength!=-1 && s.toString().length()>maxInputLength){
             s.delete(maxInputLength,s.toString().length());
             return;
         }
         if(textChanged!=null) textChanged.afterTextChanged(s);
    } 

    public String getInputText(){
        return Objects.requireNonNull(getText()).toString().trim();
    }

    public boolean isAutoHideIcon() {
        return autoHideIcon;
    }

    public void setAutoHideIcon(boolean autoHideIcon) {
        this.autoHideIcon = autoHideIcon;
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
    	this.startAnimation(shakeAnimation(3));
    }
    
    
    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }

    public int getMaxInputLength() {
        return maxInputLength;
    }

    public void setMaxInputLength(int maxInputLength) {
        this.maxInputLength = maxInputLength;
    }

    public void setTextChangedListener(TextChangedListener textChanged) {
        this.textChanged = textChanged;
    }

    public void setFocusChangedListener(FocusChangedListener focusChangedListener) {
        this.focusChangedListener = focusChangedListener;
    }

    public void setClearListener(ClearListener clearListener) {
        this.clearListener = clearListener;
    }

    public boolean isAutoShowClearIcon() {
        return autoShowClearIcon;
    }

    public void setAutoShowClearIcon(boolean autoShowClearIcon) {
        this.autoShowClearIcon = autoShowClearIcon;
    }

    TextChangedListener textChanged;
    public interface  TextChangedListener{
       void afterTextChanged(Editable s);
    }

    FocusChangedListener focusChangedListener;
    public interface  FocusChangedListener{
        void onFocusChange(View v, boolean hasFocus);
    }

    ClearListener clearListener;
    public interface ClearListener{
       void onClick(View view);
    }
}
