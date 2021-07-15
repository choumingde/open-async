package depend;

import cn.cmd.concurrent.async.executor.Async;
import cn.cmd.concurrent.async.worker.WorkResult;
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

        WorkerWrapper<WorkResult<User>, String> workerWrapper2 =  new WorkerWrapper.Builder<WorkResult<User>, String>()
                .worker(w2)
                .callback(w2)
                .id("third")
                .build();

        WorkerWrapper<WorkResult<User>, User> workerWrapper1 = new WorkerWrapper.Builder<WorkResult<User>, User>()
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

        //虽然尚未执行，但是也可以先取得结果的引用，作为下一个任务的入参。V1.2前写法，需要手工给
        //V1.3后，不用给wrapper setParam了，直接在worker的action里自行根据id获取即可.参考dependnew包下代码
        WorkResult<User> result = workerWrapper.getWorkResult();
        WorkResult<User> result1 = workerWrapper1.getWorkResult();
        workerWrapper1.setParam(result);
        workerWrapper2.setParam(result1);


        Async.beginWork(1000,COMMON_POOL,workerWrapper);
        System.out.println(workerWrapper2.getWorkResult());
        System.err.println(Async.getThreadCount(COMMON_POOL));
    }
}
