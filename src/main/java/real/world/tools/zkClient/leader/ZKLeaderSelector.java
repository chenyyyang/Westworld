/**
 *Copyright 2016 zhaojie
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software
 *distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and
 *limitations under the License.
 */
package real.world.tools.zkClient.leader;

import org.apache.zookeeper.Watcher.Event.KeeperState;
import real.world.tools.zkClient.ZKClient;
import real.world.tools.zkClient.exception.ZKException;
import real.world.tools.zkClient.listener.ZKStateListener;
import real.world.tools.zkClient.lock.ZKDistributedLock;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 选举Leader
 * @author: zhaojie/zh_jie@163.com.com 
 * @version: 2016年6月29日 下午8:51:03
 */
public class ZKLeaderSelector implements LeaderSelector {
    private final String id;
    private final ZKClient client;
    private final ZKDistributedLock lock;
    private final String leaderPath;
    private final ExecutorService executorService;
    private final ZKLeaderSelectorListener listener;
    private final ZKStateListener stateListener;
    private final AtomicBoolean isInterrupted = new AtomicBoolean(false);
    private final AtomicBoolean autoRequeue = new AtomicBoolean(false);
    private final AtomicReference<Future<?>> ourTask = new AtomicReference<Future<?>>(null);
    private final AtomicReference<State> state = new AtomicReference<State>(State.LATENT);
    
    private enum State
    {
        LATENT,
        STARTED,
        CLOSED
    }
    
    /**
     * 创建Leader选举对象
     * ZKLeaderSelector. 
     * 
     * @param id 每个Leader选举的参与者都有一个ID标识，用于区分各个参与者。
     * @param autoRequue 是否在由于网络问题造成与服务器断开连接后，自动参与到选举队列中。
     * @param client ZKClient
     * @param leaderPath 选举的路径
     * @param listener 成为Leader后执行的的监听器
     */
    public ZKLeaderSelector(String id, Boolean autoRequue, ZKClient client, String leaderPath, ZKLeaderSelectorListener listener) {
        this.id = id;
        this.client = client;
        this.autoRequeue.set(autoRequue);
        this.leaderPath = leaderPath;
        this.lock = ZKDistributedLock.newInstance(client, leaderPath);
        this.lock.setLockNodeData(this.id);
        this.executorService = Executors.newSingleThreadExecutor();
        this.listener = listener;
        
        this.stateListener = new ZKStateListener() {
            @Override
            public void handleStateChanged(KeeperState state) throws Exception {
               if(state == KeeperState.SyncConnected){//如果重新连接
                   if(isInterrupted.get() == false) {
                       requeue();
                   }
               }
            }
            
            @Override
            public void handleSessionError(Throwable error) throws Exception {
                //ignore
            }
            
            @Override
            public void handleNewSession() throws Exception {
                //ignore
            }
        };
    }
    
    /**
     * 启动参与选举Leader
     * @return void
     */
    @Override
    public void start() {
        if (!state.compareAndSet(State.LATENT, State.STARTED)) {
            throw new ZKException("Cannot be started more than once");
        }
        client.listenStateChanges(stateListener);
        requeue();
    }
    
    /**
     * 重新添加当前线程到选举队列
     * @return void
     */
    @Override
    public synchronized void requeue() {
       if (state.get() != State.STARTED) {
           throw new ZKException("close() has already been called");
       }
       
       
       isInterrupted.set(false);
       if(ourTask.get() != null) {
           ourTask.get().cancel(true);
       }
       
       Future<Void> task = executorService.submit(new Callable<Void>() {
        @Override
        public Void call() throws Exception {
           lock.lock();
           listener.takeLeadership(client, ZKLeaderSelector.this);
           return null;
        }
       
       });
       ourTask.set(task);
    }

    /**
     * 获得
     * @return 
     * @return String
     */
    @Override
    public String getLeader() {
        if(lock.getParticipantNodes().size()>0){
            return client.getData(leaderPath+"/"+lock.getParticipantNodes().get(0));
        }
        return null;
    }
    
    @Override
    public boolean isLeader(){
        if (client.getCurrentState() == KeeperState.SyncConnected ){
            if(lock.getParticipantNodes().size()>0){
                return id.equals(client.getData(leaderPath+"/"+lock.getParticipantNodes().get(0)));
            }
        }
        
        return false;
    }
    /**
     * 获得当前的所有参与者的路径名
     * @return 
     * @return List<String>
     */
    @Override
    public List<String> getParticipantNodes(){
        return lock.getParticipantNodes();
    }
    
    /**
     * 终止等待成为Leader
     * @return void
     */
    @Override
    public synchronized void interruptLeadership(){
        Future<?> task = ourTask.get();
        if ( task != null ) {
            task.cancel(true);
        }
        isInterrupted.set(true);
    }
    
    /**
     * 关闭Leader选举
     * @return void
     */
    @Override
    public synchronized void close() {
        if(!state.compareAndSet(State.STARTED, State.CLOSED)){
            throw new ZKException("Already closed or has not been started");
        }
        lock.unlock();
        client.unlistenStateChanges(stateListener);
        executorService.shutdown();
        ourTask.set(null);
    }
    
    
}
