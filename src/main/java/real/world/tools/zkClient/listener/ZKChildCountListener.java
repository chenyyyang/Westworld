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
package real.world.tools.zkClient.listener;

import org.apache.zookeeper.Watcher.Event.EventType;

import java.util.List;

/**
 * 监听子节点数量变化，不监听子节点内容的变化
 * @author: zhaojie/zh_jie@163.com.com 
 */
public abstract class ZKChildCountListener implements ZKListener {

    @Override
    public void handle(String path, EventType eventType, Object data) throws Exception {
        List<String> children = null;
        if(data!=null){
            children = (List<String>)data;
        }
        if(eventType == eventType.None){
            handleSessionExpired(path,children);
        }else{
            
            handleChildCountChanged(path,children);
        }
        
    }
    
    /**
     * 子节点数量变化的回调函数
     * @param path
     * @param children
     * @throws Exception 
     * @return void
     * @author: zhaojie/zh_jie@163.com 
     */
    public abstract void handleChildCountChanged(String path, List<String> children) throws Exception;
    
    /**
     * 会话失效并重新连接后会回调此方法。
     * 因为在会话失效时，服务端会注销Wather监听，
     * 所以在会话失效后到连接成功这段时间内，数据可能发生变化，会触发此方法
     * @param path
     * @throws Exception 
     * @return void
     * @author: zhaojie/zh_jie@163.com 
     */
    public abstract void handleSessionExpired(String path, List<String> children) throws Exception;

}
