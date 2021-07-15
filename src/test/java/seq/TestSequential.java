package seq;


import cn.cmd.concurrent.async.executor.Async;
import cn.cmd.concurrent.async.executor.timer.SystemClock;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.concurrent.*;

/**
 * 串行测试
 */
public class TestSequential {

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL =
            new ThreadPoolExecutor(5, 5,
                    15L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    (ThreadFactory) Thread::new);

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        SeqWorker w = new SeqWorker();
        SeqWorker1 w1 = new SeqWorker1();
        SeqWorker2 w2 = new SeqWorker2();

        //顺序0-1-2
        WorkerWrapper<String, String> workerWrapper2 =  new WorkerWrapper.Builder<String, String>()
                .worker(w2)
                .callback(w2)
                .param("2")
                .build();

        WorkerWrapper<String, String> workerWrapper1 =  new WorkerWrapper.Builder<String, String>()
                .worker(w1)
                .callback(w1)
                .param("1")
                .next(workerWrapper2)
                .build();

        WorkerWrapper<String, String> workerWrapper =  new WorkerWrapper.Builder<String, String>()
                .worker(w)
                .callback(w)
                .param("0")
                .next(workerWrapper1)
                .build();

//        testNormal(workerWrapper);

        for (int i=0;i<10;i++){
            testGroupTimeout(workerWrapper);
        }
    }

    private static void testNormal(WorkerWrapper<String, String> workerWrapper) throws ExecutionException, InterruptedException {
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(3500,COMMON_POOL, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.err.println(Async.getThreadCount(COMMON_POOL));
    }

    private static void testGroupTimeout(WorkerWrapper<String, String> workerWrapper) throws ExecutionException, InterruptedException {
        long now = SystemClock.now();
        System.out.println("begin-" + now);

        Async.beginWork(2500,COMMON_POOL, workerWrapper);

        System.out.println("end-" + SystemClock.now());
        System.err.println("cost-" + (SystemClock.now() - now));
        System.err.println(Async.getThreadCount(COMMON_POOL));

        for (int i = 0;i<2;i++){
            Thread.sleep(1000);
            System.err.println(Async.getThreadCount(COMMON_POOL));
        }

    }
}
