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
package org.apache.qpid.server.subscription;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.qpid.AMQException;
import org.apache.qpid.common.AMQPFilterTypes;
import org.apache.qpid.framing.AMQShortString;
import org.apache.qpid.framing.FieldTable;
import org.apache.qpid.protocol.AMQConstant;
import org.apache.qpid.server.AMQChannel;
import org.apache.qpid.server.filter.FilterManager;
import org.apache.qpid.server.flow.FlowCreditManager;
import org.apache.qpid.server.flow.FlowCreditManager_0_10;
import org.apache.qpid.server.protocol.AMQProtocolSession;
import org.apache.qpid.server.transport.ServerSession;
import org.apache.qpid.transport.MessageAcceptMode;
import org.apache.qpid.transport.MessageAcquireMode;
import org.apache.qpid.transport.MessageFlowMode;

public class SubscriptionFactoryImpl implements SubscriptionFactory
{
    private static final AtomicLong SUB_ID_GENERATOR = new AtomicLong(0);

    public Subscription createSubscription(int channelId, AMQProtocolSession protocolSession,
                                           AMQShortString consumerTag, boolean acks, FieldTable filters,
                                           boolean noLocal, FlowCreditManager creditManager) throws AMQException
    {
        AMQChannel channel = protocolSession.getChannel(channelId);
        if (channel == null)
        {
            throw new AMQException(AMQConstant.NOT_FOUND, "channel :" + channelId + " not found in protocol session");
        }
        ClientDeliveryMethod clientMethod = channel.getClientDeliveryMethod();
        RecordDeliveryMethod recordMethod = channel.getRecordDeliveryMethod();


        return createSubscription(channel, protocolSession, consumerTag, acks, filters,
                                  noLocal,
                                  creditManager,
                                  clientMethod,
                                  recordMethod
        );
    }

    public Subscription createSubscription(final AMQChannel channel,
                                            final AMQProtocolSession protocolSession,
                                            final AMQShortString consumerTag,
                                            final boolean acks,
                                            final FieldTable filters,
                                            final boolean noLocal,
                                            final FlowCreditManager creditManager,
                                            final ClientDeliveryMethod clientMethod,
                                            final RecordDeliveryMethod recordMethod
    )
            throws AMQException
    {
        boolean isBrowser;

        if (filters != null)
        {
            Boolean isBrowserObj = (Boolean) filters.get(AMQPFilterTypes.NO_CONSUME.getValue());
            isBrowser = (isBrowserObj != null) && isBrowserObj.booleanValue();
        }
        else
        {
            isBrowser = false;
        }

        if(isBrowser)
        {
            return new SubscriptionImpl.BrowserSubscription(channel, protocolSession, consumerTag,  filters, noLocal, creditManager, clientMethod, recordMethod, getNextSubscriptionId());
        }
        else if(acks)
        {
            return new SubscriptionImpl.AckSubscription(channel, protocolSession, consumerTag,  filters, noLocal, creditManager, clientMethod, recordMethod, getNextSubscriptionId());
        }
        else
        {
            return new SubscriptionImpl.NoAckSubscription(channel, protocolSession, consumerTag,  filters, noLocal, creditManager, clientMethod, recordMethod, getNextSubscriptionId());
        }
    }

    public SubscriptionImpl.GetNoAckSubscription createBasicGetNoAckSubscription(final AMQChannel channel,
                                                                                 final AMQProtocolSession session,
                                                                                 final AMQShortString consumerTag,
                                                                                 final FieldTable filters,
                                                                                 final boolean noLocal,
                                                                                 final FlowCreditManager creditManager,
                                                                                 final ClientDeliveryMethod deliveryMethod,
                                                                                 final RecordDeliveryMethod recordMethod) throws AMQException
    {
        return new SubscriptionImpl.GetNoAckSubscription(channel, session, null, null, false, creditManager, deliveryMethod, recordMethod, getNextSubscriptionId());
    }

    public Subscription_0_10 createSubscription(final ServerSession session,
                                                final String destination,
                                                final MessageAcceptMode acceptMode,
                                                final MessageAcquireMode acquireMode,
                                                final MessageFlowMode flowMode,
                                                final FlowCreditManager_0_10 creditManager,
                                                final FilterManager filterManager,
                                                final Map<String,Object> arguments)
    {
        return new Subscription_0_10(session, destination, acceptMode, acquireMode,
                                flowMode, creditManager, filterManager, arguments, getNextSubscriptionId());
    }

    public static final SubscriptionFactoryImpl INSTANCE = new SubscriptionFactoryImpl();

    private static long getNextSubscriptionId()
    {
        return SUB_ID_GENERATOR.getAndIncrement();
    }
}
