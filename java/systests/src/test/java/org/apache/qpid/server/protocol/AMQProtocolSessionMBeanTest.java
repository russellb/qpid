/*
 *
 * Copyright (c) 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.qpid.server.protocol;

import junit.framework.TestCase;
import org.apache.mina.common.IoSession;
import org.apache.qpid.codec.AMQCodecFactory;
import org.apache.qpid.server.AMQChannel;
import org.apache.qpid.server.exchange.DefaultExchangeFactory;
import org.apache.qpid.server.exchange.DefaultExchangeRegistry;
import org.apache.qpid.server.exchange.ExchangeRegistry;
import org.apache.qpid.server.queue.DefaultQueueRegistry;
import org.apache.qpid.server.queue.QueueRegistry;
import org.apache.qpid.server.store.MessageStore;
import org.apache.qpid.server.store.SkeletonMessageStore;
import org.apache.qpid.AMQException;

import javax.management.JMException;
import javax.management.openmbean.OpenDataException;

/**
 * Test class to test MBean operations for AMQMinaProtocolSession.
 */
public class AMQProtocolSessionMBeanTest   extends TestCase
{
    private IoSession _mockIOSession;
    private MessageStore _messageStore = new SkeletonMessageStore();
    private AMQMinaProtocolSession _protocolSession;
    private AMQChannel _channel;
    private QueueRegistry _queueRegistry;
    private ExchangeRegistry _exchangeRegistry;
    private AMQProtocolSessionMBean _mbean;

    public void testChannels() throws Exception
    {
        // check the channel count is correct
        int channelCount = _mbean.channels().size();
        assertTrue(channelCount == 2);
        _protocolSession.addChannel(new AMQChannel(2,_protocolSession, _messageStore, null,null));
        channelCount = _mbean.channels().size();
        assertTrue(channelCount == 3);

        // general properties test
        _mbean.setMaximumNumberOfChannels(1000L);
        assertTrue(_mbean.getMaximumNumberOfChannels() == 1000L);

        // check APIs
        AMQChannel channel3 = new AMQChannel(3,_protocolSession, _messageStore, null,null);
        channel3.setTransactional(true);
        _protocolSession.addChannel(channel3);
        _mbean.rollbackTransactions(2);
        _mbean.rollbackTransactions(3);
        _mbean.commitTransactions(2);
        _mbean.commitTransactions(3);

        // This should throw exception, because the channel does't exist
        try
        {
            _mbean.commitTransactions(4);
            fail();
        }
        catch (JMException ex)
        {
            System.out.println("expected exception is thrown :" + ex.getMessage());     
        }

        // check if closing of session works
        _protocolSession.addChannel(new AMQChannel(5,_protocolSession, _messageStore, null,null));
        _mbean.closeConnection();
        channelCount = _mbean.channels().size();
        assertTrue(channelCount == 0);
        try
        {
            // session is now closed so adding another channel should throw an exception
            _protocolSession.addChannel(new AMQChannel(6,_protocolSession, _messageStore, null,null));
            fail();
        }
        catch(IllegalStateException ex)
        {
            System.out.println("expected exception is thrown :" + ex.getMessage());
        }
    }
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();        
        _queueRegistry = new DefaultQueueRegistry();
        _exchangeRegistry = new DefaultExchangeRegistry(new DefaultExchangeFactory());
        _mockIOSession = new MockIoSession();
        _protocolSession = new AMQMinaProtocolSession(_mockIOSession, _queueRegistry, _exchangeRegistry, new AMQCodecFactory(true));
        _channel = new AMQChannel(1,_protocolSession, _messageStore, null,null);
        _protocolSession.addChannel(_channel);
        _mbean = (AMQProtocolSessionMBean)_protocolSession.getManagedObject();
    }
}
