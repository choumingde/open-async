package cn.cmd.concurrent.async.callback;

import cn.cmd.concurrent.async.wrapper.WorkerWrapper;

import java.util.List;

/**
 *
 */
public class DefaultGroupCallback implements IGroupCallback {
    @Override
    public void success(List<WorkerWrapper> workerWrappers) {

    }

    @Override
    public void failure(List<WorkerWrapper> workerWrappers, Exception e) {

    }
}
