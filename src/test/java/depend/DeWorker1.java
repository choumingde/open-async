package depend;


import cn.cmd.concurrent.async.callback.ICallback;
import cn.cmd.concurrent.async.callback.IWorker;
import cn.cmd.concurrent.async.worker.WorkResult;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.Map;

public class DeWorker1 implements IWorker<WorkResult<User>, User>, ICallback<WorkResult<User>, User> {

    @Override
    public User action(WorkResult<User> result, Map<String, WorkerWrapper> allWrappers) {
        System.out.println("par1的入参来自于par0： " + result.getResult());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new User("user1");
    }


    @Override
    public User defaultValue() {
        return new User("default User");
    }

    @Override
    public void begin() {
        //System.out.println(Thread.currentThread().getName() + "- start --" + System.currentTimeMillis());
    }

    @Override
    public void result(boolean success, WorkResult<User> param, WorkResult<User> workResult) {
        System.out.println("worker1 的结果是：" + workResult.getResult());
    }

}
