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

package com.gs.app.netsuiteerp.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalAlertDialogUtil {
    public static Handler mainHandler = null;
    private static ConcurrentHashMap<String, AlertDialog> dialogMap = new ConcurrentHashMap<>();

    synchronized public static void init(@NonNull Context context) {
        mainHandler = new Handler(context.getMainLooper());
    }

    synchronized public static void showDialog(AlertDialog.Builder builder) {
        showDialog(builder, false);
    }

    synchronized public static void showDialog(AlertDialog.Builder builder, boolean systemWindow) {
        showDialog(builder, systemWindow, false);
    }

    synchronized public static void showDialog(AlertDialog.Builder builder, boolean systemWindow, boolean softAlwaysHidden) {
        showDialog("", builder, systemWindow, softAlwaysHidden);
    }

    synchronized public static void showDialog(@NonNull String key, AlertDialog.Builder builder, boolean systemWindow, boolean softAlwaysHidden) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            AlertDialog dialog = dialogMap.get(key);
            if (builder == null) {
                if (dialog == null) {
                    return;
                }
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            } else {
                if (dialog != null) {
                    dialog.dismiss();
                    dialogMap.remove(key);
                }
                dialog = builder.create();
                if (systemWindow) {
                    // 设置系统弹框
                    Objects.requireNonNull(dialog.getWindow()).setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }
                try {
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        dialogMap.remove("");
                    }
                });
                // 设置不要全屏
                Objects.requireNonNull(dialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                if (softAlwaysHidden) {
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                dialogMap.put(key, dialog);
            }
        } else {
            if (mainHandler == null) {
                mainHandler = new Handler(Looper.getMainLooper());
            }
            mainHandler.post(new ShowTask(key, builder, systemWindow, softAlwaysHidden));
        }
    }

    synchronized public static void hideDialog() {
        hideDialog("");
    }

    synchronized public static void hideDialog(@NonNull String key) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            AlertDialog dialog = dialogMap.get(key);
            if (dialog != null) {
                dialog.hide();
            }
        } else {
            if (mainHandler == null) {
                mainHandler = new Handler(Looper.getMainLooper());
            }
            mainHandler.post(new HideTask(key));
        }
    }

    synchronized public static void dismissDialog() {
        dismissDialog("");
    }

    synchronized public static void dismissDialog(@NonNull String key) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            AlertDialog dialog = dialogMap.get(key);
            if (dialog != null) {
                dialog.dismiss();
                dialogMap.remove(key);
            }
        } else {
            if (mainHandler == null) {
                mainHandler = new Handler(Looper.getMainLooper());
            }
            mainHandler.post(new DismissTask(key));
        }
    }

    public static boolean isShowing() {
        return isShowing("");
    }

    public static boolean isShowing(@NonNull String key) {
        AlertDialog dialog = dialogMap.get(key);
        return dialog != null && dialog.isShowing();
    }

    private static class ShowTask implements Runnable {
        private final String key;
        private final AlertDialog.Builder builder;
        private final boolean systemWindow;
        private final boolean softAlwaysHidden;

        public ShowTask(@NonNull String key, AlertDialog.Builder builder, boolean systemWindow, boolean softAlwaysHidden) {
            this.key = key;
            this.builder = builder;
            this.systemWindow = systemWindow;
            this.softAlwaysHidden = softAlwaysHidden;
        }

        @Override
        public void run() {
            showDialog(key, builder, systemWindow, softAlwaysHidden);
        }
    }

    private static class HideTask implements Runnable {
        private final String key;

        public HideTask(@NonNull String key) {
            this.key = key;
        }

        @Override
        public void run() {
            hideDialog(key);
        }
    }

    private static class DismissTask implements Runnable {
        private final String key;

        public DismissTask(@NonNull String key) {
            this.key = key;
        }

        @Override
        public void run() {
            dismissDialog(key);
        }
    }
}