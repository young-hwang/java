package me.thread.executor.future;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static me.util.MyLogger.log;

public class SumTaskMainV2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SumTask task1 = new SumTask(1, 50);
        SumTask task2 = new SumTask(51, 100);

        ExecutorService es = Executors.newFixedThreadPool(2);
        Future<Integer> future1 = es.submit(task1);
        Future<Integer> future2 = es.submit(task2);

        Integer result1 = future1.get();
        Integer result2 = future2.get();

        log("task1.result=" + result1);
        log("task2.result=" + result2);
        log("task1 + task2 = " + (result1 + result2));
        log("end");
        es.close();
    }

    static class SumTask implements Callable<Integer> {
        private int start;
        private int end;

        public SumTask(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Integer call() throws Exception {
            log("작업 시작");
            int sum = 0;
            for (int i = start; i <= end; i++) {
                sum += i;
            }
            log("작업 완료, result = " + sum);
            return sum;
        }
    }
}
