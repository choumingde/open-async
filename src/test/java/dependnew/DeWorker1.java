package dependnew;


import cn.cmd.concurrent.async.callback.ICallback;
import cn.cmd.concurrent.async.callback.IWorker;
import cn.cmd.concurrent.async.worker.WorkResult;
import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.Map;

/**
 *
 */
public class DeWorker1 implements IWorker<String, User>, ICallback<String, User> {

    @Override
    public User action(String object, Map<String, WorkerWrapper> allWrappers) {
        System.out.println("-----------------");
        System.out.println("获取par0的执行结果： " + allWrappers.get("first").getWorkResult());
        System.out.println("取par0的结果作为自己的入参，并将par0的结果加上一些东西");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        User user0 = (User) allWrappers.get("first").getWorkResult().getResult();
        return new User(user0.getName() + " worker1 add");
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
    public void result(boolean success, String param, WorkResult<User> workResult) {
        System.out.println("worker1 的结果是：" + workResult.getResult());
    }

}
