package org.apache.qpid.server.queue;

import java.util.Map;
import org.apache.qpid.server.subscription.Subscription;
import org.apache.qpid.server.subscription.SubscriptionList;
import org.apache.qpid.server.virtualhost.VirtualHost;

public abstract class OutOfOrderQueue extends SimpleAMQQueue
{
    protected OutOfOrderQueue(String name, boolean durable, String owner,
                              boolean autoDelete, boolean exclusive, VirtualHost virtualHost,
                              QueueEntryListFactory entryListFactory, Map<String, Object> arguments)
    {
        super(name, durable, owner, autoDelete, exclusive, virtualHost, entryListFactory, arguments);
    }

    @Override
    protected void checkSubscriptionsNotAheadOfDelivery(final QueueEntry entry)
    {
        // check that all subscriptions are not in advance of the entry
        SubscriptionList.SubscriptionNodeIterator subIter = _subscriptionList.iterator();
        while(subIter.advance() && !entry.isAcquired())
        {
            final Subscription subscription = subIter.getNode().getSubscription();
            if(!subscription.isClosed())
            {
                QueueContext context = (QueueContext) subscription.getQueueContext();
                if(context != null)
                {
                    QueueEntry subnode = context._lastSeenEntry;
                    QueueEntry released = context._releasedEntry;

                    while(subnode != null && entry.compareTo(subnode) < 0 && !entry.isAcquired()
                            && (released == null || released.compareTo(entry) > 0))
                    {
                        if(QueueContext._releasedUpdater.compareAndSet(context,released,entry))
                        {
                            break;
                        }
                        else
                        {
                            subnode = context._lastSeenEntry;
                            released = context._releasedEntry;
                        }

                    }
                }
            }

        }
    }

}
