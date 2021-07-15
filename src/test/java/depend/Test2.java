package depend;

import java.util.concurrent.*;


/**
 * 后面请求依赖于前面请求的执行结果
 */
public class Test2 {

    /**
     * 默认线程池
     */
    private static final ThreadPoolExecutor COMMON_POOL =
            new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 1024,
                    15L, TimeUnit.SECONDS,
                    new LinkedBlockingQueue<>(),
                    (ThreadFactory) Thread::new);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        COMMON_POOL.execute(()->{
            System.out.println("123");
        });

    }
}
