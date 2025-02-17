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

package com.gs.app.netsuiteerp.common;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolManager {
    // CPU核数
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    // 线程池中最少2个，最多4个核心线程
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    // 最大线程数
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    // 线程空闲时，最大存活时间
    private static final int KEEP_ALIVE_SECONDS = 60;
    // 线程工厂
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    // 任务队列
    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingQueue<Runnable>(256);
    // 线程池的任务执行器
    private static final Executor THREAD_POOL_EXECUTOR;

    static {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                sPoolWorkQueue,
                sThreadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        THREAD_POOL_EXECUTOR = threadPoolExecutor;
    }

    private ThreadPoolManager() {

    }

    private static class Singleton {
        private static final ThreadPoolManager instance = new ThreadPoolManager();
    }

    public static ThreadPoolManager getInstance() {
        return Singleton.instance;
    }

    public void execute(Runnable runnable) {
        if (runnable == null) return;
        THREAD_POOL_EXECUTOR.execute(runnable);
    }

    public void execute(String key, Runnable runnable) {
        if (runnable == null) return;
        THREAD_POOL_EXECUTOR.execute(runnable);
    }
}