/*
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.apache.qpid.test.unit.client.connection;

import org.apache.qpid.test.utils.QpidBrokerTestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * ExceptionListenerTest
 *
 */

public class ExceptionListenerTest extends QpidBrokerTestCase
{

    public void testBrokerDeath() throws Exception
    {
        Connection conn = getConnection("guest", "guest");

        conn.start();

        final CountDownLatch fired = new CountDownLatch(1);
        conn.setExceptionListener(new ExceptionListener()
        {
            public void onException(JMSException e)
            {
                _logger.debug("&&&&&&&&&&&&&&&&&&&&&&&&&&&& Caught exception &&&&&&&&&&&&&&&&&&&&&&&&&&&& ", e);
                fired.countDown();
            }
        });
        _logger.debug("%%%%%%%%%%%%%%%% Stopping Broker %%%%%%%%%%%%%%%%%%%%%");
        stopBroker();
        _logger.debug("%%%%%%%%%%%%%%%% Stopped Broker  %%%%%%%%%%%%%%%%%%%%%");

        if (!fired.await(5, TimeUnit.SECONDS))
        {
            fail("exception listener was not fired");
        }
    }

}
