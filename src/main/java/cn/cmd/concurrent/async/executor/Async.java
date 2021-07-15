package cn.cmd.concurrent.async.executor;


import cn.cmd.concurrent.async.callback.DefaultGroupCallback;
import cn.cmd.concurrent.async.callback.IGroupCallback;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;


/**
 * 异步并发入口类
 * @author choumingde
 * @since 2021/7/15
 */
public class Async {

    /**
     * 并发入口
     * @param timeout
     * @param executorService
     * @param workerWrappers
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, List<WorkerWrapper> workerWrappers) throws ExecutionException, InterruptedException {
        if(workerWrappers == null || workerWrappers.size() == 0) {
            return false;
        }
        //定义一个map，存放所有的wrapper，key为wrapper的唯一id，value是该wrapper，可以从value中获取wrapper的result
        Map<String, WorkerWrapper> forParamUseWrappers = new ConcurrentHashMap<>();
        CompletableFuture[] futures = new CompletableFuture[workerWrappers.size()];
        for (int i = 0; i < workerWrappers.size(); i++) {
            WorkerWrapper wrapper = workerWrappers.get(i);
            futures[i] = CompletableFuture.runAsync(() -> wrapper.work(executorService, timeout, forParamUseWrappers), executorService);
        }
        try {
            CompletableFuture.allOf(futures).get(timeout, TimeUnit.MILLISECONDS);
            return true;
        } catch (TimeoutException e) {
            Set<WorkerWrapper> set = new HashSet<>();
            totalWorkers(workerWrappers, set);
            for (WorkerWrapper wrapper : set) {
                wrapper.stopNow();
            }
            return false;
        }
    }

    /**
     * 并发入口
     * @param timeout
     * @param executorService
     * @param workerWrapper
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static boolean beginWork(long timeout, ExecutorService executorService, WorkerWrapper... workerWrapper) throws ExecutionException, InterruptedException {
        if(workerWrapper == null || workerWrapper.length == 0) {
            return false;
        }
        List<WorkerWrapper> workerWrappers =  Arrays.stream(workerWrapper).collect(Collectors.toList());
        return beginWork(timeout, executorService, workerWrappers);
    }

    /**
     * 异步执行,直到所有都完成,或失败后，发起回调
     * @param timeout
     * @param executorService
     * @param groupCallback
     * @param workerWrapper
     */
    public static void beginWorkAsync(long timeout, ExecutorService executorService, IGroupCallback groupCallback, WorkerWrapper... workerWrapper) {
        if (groupCallback == null) {
            groupCallback = new DefaultGroupCallback();
        }
        IGroupCallback finalGroupCallback = groupCallback;
        executorService.submit(() -> {
            try {
                boolean success = beginWork(timeout, executorService, workerWrapper);
                if (success) {
                    finalGroupCallback.success(Arrays.asList(workerWrapper));
                } else {
                    finalGroupCallback.failure(Arrays.asList(workerWrapper), new TimeoutException());
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                finalGroupCallback.failure(Arrays.asList(workerWrapper), e);
            }
        });
    }

    /**
     * 总共多少个执行单元
     */
    @SuppressWarnings("unchecked")
    private static void totalWorkers(List<WorkerWrapper> workerWrappers, Set<WorkerWrapper> set) {
        set.addAll(workerWrappers);
        for (WorkerWrapper wrapper : workerWrappers) {
            if (wrapper.getNextWrappers() == null) {
                continue;
            }
            List<WorkerWrapper> wrappers = wrapper.getNextWrappers();
            totalWorkers(wrappers, set);
        }

    }

    public static String getThreadCount(ThreadPoolExecutor threadPool) {
        return "activeCount=" + threadPool.getActiveCount() +
                "  completedCount " + threadPool.getCompletedTaskCount() +
                "  largestCount " + threadPool.getLargestPoolSize();
    }
}
