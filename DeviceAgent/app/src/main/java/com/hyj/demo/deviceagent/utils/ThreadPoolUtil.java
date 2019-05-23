package com.hyj.demo.deviceagent.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil {

    private static ThreadPoolExecutor mExecutor;
    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(10);

    private ThreadPoolUtil() {
        // cannot be instantiated
    }

    static {
        int CORE_POOL_SIZE = 5;
        int MAX_POOL_SIZE = 100;
        int KEEP_ALIVE_TIME = 10000;
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
                                           KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger();

            @Override
            public Thread newThread(@NonNull Runnable runnable) {
                return new Thread(runnable, "threadPool thread:"
                        + count.getAndIncrement());
            }
        });
    }

    public static void execute(Runnable runnable) {
        mExecutor.execute(runnable);
    }
}
