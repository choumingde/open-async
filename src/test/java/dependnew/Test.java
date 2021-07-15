package dependnew;

import cn.cmd.concurrent.async.executor.Async;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.concurrent.*;


/**
 * 后面请求依赖于前面请求的执行结果
 */
public class Test {

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 1024,
                    15L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    (ThreadFactory) Thread::new);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DeWorker w = new DeWorker();
        DeWorker1 w1 = new DeWorker1();
        DeWorker2 w2 = new DeWorker2();

        WorkerWrapper<User, String> workerWrapper2 =  new WorkerWrapper.Builder<User, String>()
                .worker(w2)
                .callback(w2)
                .id("third")
                .build();

        WorkerWrapper<String, User> workerWrapper1 = new WorkerWrapper.Builder<String, User>()
                .worker(w1)
                .callback(w1)
                .id("second")
                .next(workerWrapper2)
                .build();

        WorkerWrapper<String, User> workerWrapper = new WorkerWrapper.Builder<String, User>()
                .worker(w)
                .param("0")
                .id("first")
                .next(workerWrapper1)
                .callback(w)
                .build();

        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可

        Async.beginWork(3500,COMMON_POOL, workerWrapper);

        System.out.println(workerWrapper2.getWorkResult());
        System.err.println(Async.getThreadCount(COMMON_POOL));
    }
}
