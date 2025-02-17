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
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gs.app.netsuiteerp.R;

public class GsMaterialSpinner extends TextInputLayout {
    private TextInputEditText editText;
    private int checkedPos;
    private String[] itemLabels;
    private OnClickListener onClickListener;
    private OnItemSelectListener onItemSelectListener;
    private OnDismissListener onDismissListener;

    public GsMaterialSpinner(Context context) {
        super(context);
        init();
    }

    public GsMaterialSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GsMaterialSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    private void init() {
        inflate(getContext(), R.layout.layout_material_spinner, this);
        //    setBoxStrokeColorStateList(getResources().getColorStateList(R.color.pop_bottom_text_color));
        //    setHintTextColor(getResources().getColorStateList(R.color.color_edit_hint));
        editText = findViewById(R.id.spinner_edit);
        editText.setPaddingRelative(editText.getPaddingStart(),0,editText.getPaddingEnd(),0);
        //  setDrawableEnd(R.drawable.down_arrow);
        setBoxStrokeColorStateList(getResources().getColorStateList(R.color.edit_border_color));
        setHintTextColor(getResources().getColorStateList(R.color.edit_border_hint_color));
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        if (!clickable) {
            setDrawableEnd(0);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setDrawableEnd(@DrawableRes int res) {
        Drawable drawable = getResources().getDrawable(res, null);
        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(editText.getCompoundDrawablesRelative()[0], null, drawable, null);
    }

    public void setDropDownItem(String[] items) {
        setDropDownItem(items, 0);
    }

    public void setDropDownItem(String[] items, int selectedPos) {
        setPopShowState(false);
        if (items == null) return;
        itemLabels = items;
        if (selectedPos >= items.length) selectedPos = items.length - 1;
        if (selectedPos < 0) selectedPos = 0;
        checkedPos = selectedPos;
        editText.setText(items[selectedPos]);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showPop();
            }
        });
        editText.setOnClickListener(v -> {
            showPop();
        });
    }

    public void showPop() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
//                .setTitle(editText.getHint())
                .setSingleChoiceItems(itemLabels, checkedPos, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        checkedPos = which;
                        editText.setText(itemLabels[which]);
                        if (onItemSelectListener != null) {
                            onItemSelectListener.onItemSelected(which);
                        }
                    }
                })
                .show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (onDismissListener != null) {
                    onDismissListener.onDismiss();
                }
            }
        });
    }

    public void setPopShowState(boolean isShowState) {
        setDrawableEnd(isShowState ? R.drawable.ic_arrow_up : R.drawable.ic_arrow_down);
    }

    @Override
    public TextInputEditText getEditText() {
        return editText;
    }

    public void setText(CharSequence charSequence) {
        setPopShowState(false);
        editText.setText(charSequence);
    }

    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (onClickListener != null) onClickListener.onClick(v);
            }
        });
        editText.setOnClickListener(v -> {
            if (onClickListener != null) onClickListener.onClick(v);
        });
    }

    public int getCheckedPos() {
        return checkedPos;
    }

    public void setCheckedPos(int selectedPos) {
        if (itemLabels != null && itemLabels.length > 0 && selectedPos < itemLabels.length && selectedPos >= 0) {
            checkedPos = selectedPos;
            editText.setText(itemLabels[selectedPos]);
            if (onItemSelectListener != null) {
                onItemSelectListener.onItemSelected(selectedPos);
            }
        }
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public interface OnItemSelectListener {
        void onItemSelected(int position);
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}