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
package real.world.tools.zkClient.exception;

import org.apache.zookeeper.KeeperException;

public class ZKTimeoutException extends ZKException {

    private static final long serialVersionUID = 1L;

    public ZKTimeoutException() {
        super();
    }

    public ZKTimeoutException(KeeperException cause) {
        super(cause);
    }

    public ZKTimeoutException(String message, KeeperException cause) {
        super(message, cause);
    }

    public ZKTimeoutException(String message) {
        super(message);
    }
}
