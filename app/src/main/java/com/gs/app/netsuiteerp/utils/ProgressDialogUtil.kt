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

package com.gs.app.netsuiteerp.utils

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.gs.app.netsuiteerp.R
import java.lang.ref.SoftReference

class ProgressDialogUtil private constructor() : LifecycleObserver {
    private object DialogHolder {
        val instance: ProgressDialogUtil = ProgressDialogUtil()
    }

    private var mDialog: SoftReference<Dialog?>? = null
    private var maxProgress = 0

    fun showDialog(context: Context, cancelable: Boolean) {
        showDialog(context, 0, cancelable)
    }

    @JvmOverloads
    fun showDialog(context: Context, msg: Int = 0) {
        showDialog(context, msg, false)
    }

    fun showDialog(context: Context, msg: String?) {
        showDialog(context, msg, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showDialog(context: Context, msg: Int, cancelable: Boolean) {
        showDialog(context, msg, context.getDrawable(R.drawable.ic_loading), cancelable)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun showDialog(context: Context, msg: String?, cancelable: Boolean) {
        showDialog(context, msg, context.getDrawable(R.drawable.ic_loading), cancelable)
    }

    fun showDialog(context: Context?, msg: Int, res: Drawable?, cancelable: Boolean) {
        try {
            if (isShowing) {
                val dialog = mDialog?.get()
                val tv_msg = dialog?.findViewById<TextView>(R.id.tv_msg)
                if (msg != 0) {
                    tv_msg?.setText(msg)
                    tv_msg?.visibility = View.VISIBLE
                } else {
                    tv_msg?.setText("")
                    tv_msg?.visibility = View.GONE
                }
                dialog?.show()
                return
            }
            if (context is LifecycleOwner) {
                (context as LifecycleOwner).lifecycle.addObserver(this) //监听activity生命周期
            }
            dismissDialog()
            val dialog = Dialog(context!!, R.style.Theme_Light_Dialog)
            mDialog = SoftReference(dialog)
            dialog.setCanceledOnTouchOutside(cancelable)
            dialog.setContentView(R.layout.layout_progressdialog)
            dialog.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener !cancelable
                }
                false
            })
            val img_loading = dialog.findViewById<View>(R.id.img_loading) as ImageView
            val tv_msg = dialog.findViewById<View>(R.id.tv_msg) as TextView
            if (msg != 0) {
                tv_msg.setText(msg)
                tv_msg.visibility = View.VISIBLE
            }
            if (true) {
                val anim_rotate = AnimationUtils.loadAnimation(context, R.anim.loading_rotate)
                val linearInterpolator = LinearInterpolator() //控制匀速转动,防止xml匀速转动属性不生效
                anim_rotate.interpolator = linearInterpolator
                img_loading.startAnimation(anim_rotate)
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showDialog(context: Context?, msg: String?, res: Drawable?, cancelable: Boolean) {
        try {
            if (isShowing) {
                val dialog = mDialog?.get()
                val tv_msg = dialog?.findViewById<TextView>(R.id.tv_msg)
                if (!TextUtils.isEmpty(msg)) {
                    tv_msg?.setText(msg)
                    tv_msg?.visibility = View.VISIBLE
                } else {
                    tv_msg?.setText("")
                    tv_msg?.visibility = View.GONE
                }
                dialog?.show()
                return
            }
            if (context is LifecycleOwner) {
                (context as LifecycleOwner).lifecycle.addObserver(this) //监听activity生命周期
            }
            dismissDialog()
            val dialog = Dialog(context!!, R.style.Theme_Light_Dialog)
            mDialog = SoftReference(dialog)
            dialog.setCanceledOnTouchOutside(cancelable)
            dialog.setContentView(R.layout.layout_progressdialog)
            dialog.setOnKeyListener(DialogInterface.OnKeyListener { dialog, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener !cancelable
                }
                false
            })
            val img_loading = dialog.findViewById<View>(R.id.img_loading) as ImageView
            val tv_msg = dialog.findViewById<View>(R.id.tv_msg) as TextView
            img_loading.setImageDrawable(res)
            if (!TextUtils.isEmpty(msg)) {
                tv_msg.setText(msg)
                tv_msg.visibility = View.VISIBLE
            }
            if (true) {
                val anim_rotate = AnimationUtils.loadAnimation(context, R.anim.loading_rotate)
                val linearInterpolator = LinearInterpolator() //控制匀速转动,防止xml匀速转动属性不生效
                anim_rotate.interpolator = linearInterpolator
                img_loading.startAnimation(anim_rotate)
            }
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val dialog: Dialog?
        get() {
            if (mDialog != null) return mDialog!!.get()
            return null
        }

    val isShowing: Boolean
        get() {
            if (dialog != null) return dialog!!.isShowing
            return false
        }

    fun setMessage(text: Int) {
        if (dialog != null) {
            setMessage(dialog!!.context.getString(text))
        }
    }

    fun setMessage(text: String?) {
        if (mDialog == null) return
        val dialog = mDialog!!.get()
        if (dialog != null && dialog.isShowing) {
            val tv_msg = dialog.findViewById<View>(R.id.tv_msg) as TextView
            tv_msg.text = text
            tv_msg.visibility = View.VISIBLE
        }
    }

    fun setProgress(progress: Int) {
        if (mDialog == null) return
        //Dialog dialog = mDialog.get();
        if (maxProgress != 0) setMessage("$progress/$maxProgress")
        else setMessage(progress.toString() + "")
        //  if (dialog != null && progress == maxProgress) dialog.dismiss();
    }

    fun setMaxProgress(progress: Int) {
        maxProgress = progress
        setProgress(0)
    }

    /**
     * 关闭Dialog
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun dismissDialog() {
        try {
            if (mDialog != null && mDialog!!.get() != null && mDialog!!.get()!!.isShowing) {
                mDialog!!.get()!!.dismiss()
                mDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val instance = DialogHolder.instance
    }
}
