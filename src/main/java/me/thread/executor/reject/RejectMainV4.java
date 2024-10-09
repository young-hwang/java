package me.thread.executor.reject;

import me.thread.executor.RunnableTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static me.util.MyLogger.log;

public class RejectMainV4 {
    public static void main(String[] args) {
        ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), new MyRejectedExecutionHandler());
        executorService.submit(new RunnableTask("task1"));
        executorService.submit(new RunnableTask("task2"));
        executorService.submit(new RunnableTask("task3"));
        executorService.close();
    }

    static class MyRejectedExecutionHandler implements RejectedExecutionHandler {
        static AtomicInteger count = new AtomicInteger(0);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int i = count.incrementAndGet();
            log("[경고] 거절된 누적 작업 수: " + i);
        }
    }
}
