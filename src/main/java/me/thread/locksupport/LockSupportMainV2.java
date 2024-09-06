package me.thread.locksupport;

import java.util.concurrent.locks.LockSupport;

import static me.util.MyLogger.log;
import static me.util.ThreadUtils.sleep;

public class LockSupportMainV2 {
    public static void main(String[] args) {
        Thread thread1 = new Thread(new ParkTask(), "Thread-1");
        thread1.start();

        // 잠시 대기하여 Thread-1이 park 상태에 빠질 시간을 준다.
        sleep(100);
        log("Thread-1 state: " + thread1.getState());
    }

    private static class ParkTask implements Runnable {
        @Override
        public void run() {
            log("park 시작");
            LockSupport.parkNanos(2000_000000);
            log("park 종료, state: " + Thread.currentThread().getState());
            log("인터럽트 상태: " + Thread.currentThread().isInterrupted());
            log("work thread interrupted=" + Thread.currentThread().isInterrupted());
        }
    }
}
