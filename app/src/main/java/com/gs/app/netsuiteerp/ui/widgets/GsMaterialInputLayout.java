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
import android.text.Editable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.gs.app.netsuiteerp.R;

public class GsMaterialInputLayout extends TextInputLayout {
    GsMaterialEditText editText;

    public GsMaterialInputLayout(Context context) {
        super(context);
        init();
    }


    public GsMaterialInputLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GsMaterialInputLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    public void init() {
        inflate(getContext(), R.layout.layout_material_edittext,this);
     //   setBoxStrokeColorStateList(getResources().getColorStateList(R.color.pop_bottom_text_color));
     //   setHintTextColor(getResources().getColorStateList(R.color.color_edit_hint));
        editText=findViewById(R.id.edit);
        setBoxStrokeColorStateList(getResources().getColorStateList(R.color.edit_border_color));
        setHintTextColor(getResources().getColorStateList(R.color.edit_border_hint_color));

       /* setTextChangedListener(s -> {
            if(!TextUtils.isEmpty(editText.getInputText())){
                setHintTextColor(getResources().getColorStateList(R.color.edit_border_color));
            }else   setHintTextColor(getResources().getColorStateList(R.color.edit_border_hint_color));
        });*/
    }

    @NonNull
    @Override
    public GsMaterialEditText getEditText() {
        return editText;
    }


    public void setEditText(GsMaterialEditText editText) {
        this.editText = editText;
    }

    public void setTextChangedListener(GsMaterialEditText.TextChangedListener textChanged) {
        editText.setTextChangedListener(textChanged);
    }


    public void setFocusChangedListener(GsMaterialEditText.FocusChangedListener focusChangedListener) {
        editText.setFocusChangedListener(focusChangedListener);
    }

    public void setMaxInputLength(int maxInputLength) {
        editText.setMaxInputLength(maxInputLength);
    }

    public int getMaxInputLength() {
        return editText.getMaxInputLength();
    }

    public int length() {
        return editText.length();
    }

    public void setText(CharSequence text) {
        editText.setText(text);
    }
    public Editable getText(){
        return editText.getText();
    }
}