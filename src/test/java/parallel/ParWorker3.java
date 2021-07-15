package parallel;


import cn.cmd.concurrent.async.callback.ICallback;
import cn.cmd.concurrent.async.callback.IWorker;
import cn.cmd.concurrent.async.executor.timer.SystemClock;
import cn.cmd.concurrent.async.worker.WorkResult;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.Map;

/**
 *
 */
public class ParWorker3 implements IWorker<String, String>, ICallback<String, String> {
    private long sleepTime = 1000;

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }
    @Override
    public String action(String object, Map<String, WorkerWrapper> allWrappers) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result = " + SystemClock.now() + "---param = " + object + " from 3";
    }


    @Override
    public String defaultValue() {
        return "worker3--default";
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, String param, WorkResult<String> workResult) {
        if (success) {
            System.out.println("callback worker3 success--" + SystemClock.now() + "----" + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        } else {
            System.err.println("callback worker3 failure--" + SystemClock.now() + "----"  + workResult.getResult()
                    + "-threadName:" +Thread.currentThread().getName());
        }
    }

}
