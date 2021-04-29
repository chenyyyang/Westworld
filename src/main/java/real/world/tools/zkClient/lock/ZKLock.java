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
package real.world.tools.zkClient.lock;

public interface ZKLock {
    /**
     * 获得锁
     
     * @return 
     * @return boolean 成功获得锁返回true，否则返回false
     */
    boolean lock();
    
    /**
     * 释放锁
     * @return 
     * @return boolean 
     *      如果释放锁成功返回true，否则返回false
     */
    boolean unlock();
}
