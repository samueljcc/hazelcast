/*
 * Copyright (c) 2008-2012, Hazel Bilisim Ltd. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.impl.spi;

import com.hazelcast.nio.Address;

class SingleTargetInvocation extends SingleInvocation {

    private final Address target;

    SingleTargetInvocation(NodeServiceImpl nodeService, String serviceName, Operation op, int partitionId,
                           int replicaIndex, Address target, int tryCount, long tryPauseMillis) {
        super(nodeService, serviceName, op, partitionId, replicaIndex, tryCount, tryPauseMillis);
        this.target = target;
    }

    @Override
    Address getTarget() {
        return target;
    }

    @Override
    void setResult(final Object obj) {
        if (obj instanceof RetryableException) {
            if (invokeCount < tryCount) {
                try {
                    Thread.sleep(tryPauseMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                invoke();
            } else {
                setException((Throwable) obj);
            }
        } else {
            if (obj instanceof Exception) {
                setException((Throwable) obj);
            } else {
                set(obj);
            }
        }
    }

}
