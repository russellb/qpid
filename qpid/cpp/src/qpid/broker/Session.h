#ifndef QPID_BROKER_SESSION_H
#define QPID_BROKER_SESSION_H

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

#include "AccumulatedAck.h"
#include "Consumer.h"
#include "Deliverable.h"
#include "DeliveryAdapter.h"
#include "DeliveryRecord.h"
#include "DeliveryToken.h"
#include "DtxBuffer.h"
#include "DtxManager.h"
#include "NameGenerator.h"
#include "Prefetch.h"
#include "TxBuffer.h"
#include "SemanticHandler.h"  // FIXME aconway 2007-08-31: remove
#include "qpid/framing/FrameHandler.h"
#include "qpid/shared_ptr.h"

#include <boost/ptr_container/ptr_vector.hpp>

#include <list>

namespace qpid {

namespace framing {
class AMQP_ClientProxy;
}

namespace broker {

class SessionAdapter;
class Broker;

/**
 * Session holds the state of an open session, whether attached to a
 * channel or suspended. It also holds the handler chains associated
 * with the session. 
 */
class Session : public framing::FrameHandler::Chains,
                private boost::noncopyable
{
    class ConsumerImpl : public Consumer
    {
        sys::Mutex lock;
        Session* const parent;
        const DeliveryToken::shared_ptr token;
        const string name;
        const Queue::shared_ptr queue;
        const bool ackExpected;
        const bool nolocal;
        const bool acquire;
        bool blocked;
        bool windowing;
        uint32_t msgCredit;
        uint32_t byteCredit;

        bool checkCredit(Message::shared_ptr& msg);

      public:
        ConsumerImpl(Session* parent, DeliveryToken::shared_ptr token, 
                     const string& name, Queue::shared_ptr queue,
                     bool ack, bool nolocal, bool acquire=true);
        ~ConsumerImpl();
        bool deliver(QueuedMessage& msg);            
        void redeliver(Message::shared_ptr& msg, DeliveryId deliveryTag);
        void cancel();
        void requestDispatch();

        void setWindowMode();
        void setCreditMode();
        void addByteCredit(uint32_t value);
        void addMessageCredit(uint32_t value);
        void flush();
        void stop();
        void acknowledged(const DeliveryRecord&);    
    };

    typedef boost::ptr_map<string,ConsumerImpl> ConsumerImplMap;

    SessionAdapter* adapter;
    Broker& broker;
    uint32_t timeout;
    boost::ptr_vector<framing::FrameHandler>  handlers;

    DeliveryAdapter* deliveryAdapter;
    Queue::shared_ptr defaultQueue;
    ConsumerImplMap consumers;
    uint32_t prefetchSize;    
    uint16_t prefetchCount;    
    Prefetch outstanding;
    NameGenerator tagGenerator;
    std::list<DeliveryRecord> unacked;
    sys::Mutex deliveryLock;
    TxBuffer::shared_ptr txBuffer;
    DtxBuffer::shared_ptr dtxBuffer;
    bool dtxSelected;
    AccumulatedAck accumulatedAck;
    bool opened;
    bool flowActive;

    boost::shared_ptr<Exchange> cacheExchange;
    
    void route(Message::shared_ptr msg, Deliverable& strategy);
    void record(const DeliveryRecord& delivery);
    bool checkPrefetch(Message::shared_ptr& msg);
    void checkDtxTimeout();
    ConsumerImpl& find(const std::string& destination);
    void ack(DeliveryId deliveryTag, DeliveryId endTag, bool cumulative);
    void acknowledged(const DeliveryRecord&);

    // FIXME aconway 2007-08-31: remove, temporary hack.
    SemanticHandler* semanticHandler;
    

  public:
    Session(SessionAdapter&, uint32_t timeout);
    ~Session();

    /** Returns 0 if this session is not currently attached */
    SessionAdapter* getAdapter() { return adapter; }
    const SessionAdapter* getAdapter() const { return adapter; }

    Broker& getBroker() const { return broker; }
    
    /** Session timeout. */
    uint32_t getTimeout() const { return timeout; }
    
    /**
     * Get named queue, never returns 0.
     * @return: named queue or default queue for session if name=""
     * @exception: ChannelException if no queue of that name is found.
     * @exception: ConnectionException if name="" and session has no default.
     */
    Queue::shared_ptr getQueue(const std::string& name) const;
    

    void setDefaultQueue(Queue::shared_ptr queue){ defaultQueue = queue; }
    Queue::shared_ptr getDefaultQueue() const { return defaultQueue; }
    uint32_t setPrefetchSize(uint32_t size){ return prefetchSize = size; }
    uint16_t setPrefetchCount(uint16_t n){ return prefetchCount = n; }

    bool exists(const string& consumerTag);

    /**
     *@param tagInOut - if empty it is updated with the generated token.
     */
    void consume(DeliveryToken::shared_ptr token, string& tagInOut, Queue::shared_ptr queue, 
                 bool nolocal, bool acks, bool exclusive, const framing::FieldTable* = 0);

    void cancel(const string& tag);

    void setWindowMode(const std::string& destination);
    void setCreditMode(const std::string& destination);
    void addByteCredit(const std::string& destination, uint32_t value);
    void addMessageCredit(const std::string& destination, uint32_t value);
    void flush(const std::string& destination);
    void stop(const std::string& destination);

    bool get(DeliveryToken::shared_ptr token, Queue::shared_ptr queue, bool ackExpected);
    void close();
    void startTx();
    void commit(MessageStore* const store);
    void rollback();
    void selectDtx();
    void startDtx(const std::string& xid, DtxManager& mgr, bool join);
    void endDtx(const std::string& xid, bool fail);
    void suspendDtx(const std::string& xid);
    void resumeDtx(const std::string& xid);
    void ackCumulative(DeliveryId deliveryTag);
    void ackRange(DeliveryId deliveryTag, DeliveryId endTag);
    void recover(bool requeue);
    void flow(bool active);
    void deliver(Message::shared_ptr& msg, const string& consumerTag, DeliveryId deliveryTag);            

    void handle(Message::shared_ptr msg);

    framing::AMQP_ClientProxy& getProxy() {
        // FIXME aconway 2007-08-31: Move proxy to Session.
        return semanticHandler->getProxy();
    }    
};

}} // namespace qpid::broker



#endif  /*!QPID_BROKER_SESSION_H*/
