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
package org.apache.qpid.server.queue;

import org.apache.qpid.server.message.ServerMessage;

import org.apache.qpid.AMQException;
import org.apache.qpid.framing.AMQShortString;
import org.apache.qpid.framing.BasicContentHeaderProperties;
import org.apache.qpid.server.queue.SortedQueueEntryImpl.Colour;
import org.apache.qpid.server.store.StoreContext;

/**
 * A sorted implementation of QueueEntryList.
 * Uses the red/black tree algorithm specified in "Introduction to Algorithms".
 * ISBN-10: 0262033844
 * ISBN-13: 978-0262033848
 * @see http://en.wikipedia.org/wiki/Red-black_tree
 */
public class SortedQueueEntryList implements QueueEntryList<SortedQueueEntryImpl>
{
    private final SortedQueueEntryImpl _head;
    private SortedQueueEntryImpl _root;
    private long _entryId = Long.MIN_VALUE;
    private final Object _lock = new Object();
    private final AMQQueue _queue;
    private final String _propertyName;

    public SortedQueueEntryList(final AMQQueue queue, final String propertyName)
    {
        _queue = queue;
        _head = new SortedQueueEntryImpl(this);
        _propertyName = propertyName;
    }

    @Override
    public AMQQueue getQueue()
    {
        return _queue;
    }

    @Override
    public SortedQueueEntryImpl add(final ServerMessage message)
    {
        synchronized(_lock)
        {
            String key = null;
            final Object val = message.getMessageHeader().getHeader(_propertyName);
            if(val != null)
            {
                key = val.toString();
            }

            final SortedQueueEntryImpl entry = new SortedQueueEntryImpl(this,message, ++_entryId);
            entry.setKey(key);

            insert(entry);

            return entry;
        }
    }

    /**
     * Red Black Tree insert implementation.
     * @param entry the entry to insert.
     */
    private void insert(final SortedQueueEntryImpl entry)
    {
        SortedQueueEntryImpl node = _root;
        if((node = _root) == null)
        {
            _root = entry;
            _head.setNext(entry);
            entry.setPrev(_head);
            return;
        }
        else
        {
            SortedQueueEntryImpl parent = null;
            while(node != null)
            {
                parent = node;
                if(entry.compareTo(node) < 0)
                {
                    node = node.getLeft();
                }
                else
                {
                    node = node.getRight();
                }
            }
            entry.setParent(parent);

            if(entry.compareTo(parent) < 0)
            {
                parent.setLeft(entry);
                final SortedQueueEntryImpl prev = parent.getPrev();
                entry.setNext(parent);
                prev.setNext(entry);
                entry.setPrev(prev);
                parent.setPrev(entry);
            }
            else
            {
                parent.setRight(entry);

                final SortedQueueEntryImpl next = parent.getNextValidEntry();
                entry.setNext(next);
                parent.setNext(entry);

                if(next != null)
                {
                    next.setPrev(entry);
                }
                entry.setPrev(parent);
            }
        }
        entry.setColour(Colour.RED);
        insertFixup(entry);
    }

    private void insertFixup(SortedQueueEntryImpl entry)
    {
        while(isParentColour(entry, Colour.RED))
        {
            final SortedQueueEntryImpl grandparent = nodeGrandparent(entry);

            if(nodeParent(entry) == leftChild(grandparent))
            {
                final SortedQueueEntryImpl y = rightChild(grandparent);
                if(isNodeColour(y, Colour.RED))
                {
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(y, Colour.BLACK);
                    setColour(grandparent, Colour.RED);
                    entry = grandparent;
                }
                else
                {
                    if(entry == rightChild(nodeParent(entry)))
                    {
                        entry = nodeParent(entry);
                        leftRotate(entry);
                    }
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(nodeGrandparent(entry), Colour.RED);
                    rightRotate(nodeGrandparent(entry));
                }
            }
            else
            {
                final SortedQueueEntryImpl y = leftChild(grandparent);
                if(isNodeColour(y, Colour.RED))
                {
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(y, Colour.BLACK);
                    setColour(grandparent, Colour.RED);
                    entry = grandparent;
                }
                else
                {
                    if(entry == leftChild(nodeParent(entry)))
                    {
                        entry = nodeParent(entry);
                        rightRotate(entry);
                    }
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(nodeGrandparent(entry), Colour.RED);
                    leftRotate(nodeGrandparent(entry));
                }
            }
        }
        _root.setColour(Colour.BLACK);
    }

    private void leftRotate(final SortedQueueEntryImpl entry)
    {
        if(entry != null)
        {
            final SortedQueueEntryImpl rightChild = rightChild(entry);
            entry.setRight(rightChild.getLeft());
            if(entry.getRight() != null)
            {
                entry.getRight().setParent(entry);
            }
            rightChild.setParent(entry.getParent());
            if(entry.getParent() == null)
            {
                _root = rightChild;
            }
            else if(entry == entry.getParent().getLeft())
            {
                entry.getParent().setLeft(rightChild);
            }
            else
            {
                entry.getParent().setRight(rightChild);
            }
            rightChild.setLeft(entry);
            entry.setParent(rightChild);
        }
    }

    private void rightRotate(final SortedQueueEntryImpl entry)
    {
        if(entry != null)
        {
            final SortedQueueEntryImpl leftChild = leftChild(entry);
            entry.setLeft(leftChild.getRight());
            if(entry.getLeft() != null)
            {
                leftChild.getRight().setParent(entry);
            }
            leftChild.setParent(entry.getParent());
            if(leftChild.getParent() == null)
            {
                _root = leftChild;
            }
            else if(entry == entry.getParent().getRight())
            {
                entry.getParent().setRight(leftChild);
            }
            else
            {
                entry.getParent().setLeft(leftChild);
            }
            leftChild.setRight(entry);
            entry.setParent(leftChild);
        }
    }

    private void setColour(final SortedQueueEntryImpl node, final Colour colour)
    {
        if(node != null)
        {
            node.setColour(colour);
        }
    }

    private SortedQueueEntryImpl leftChild(final SortedQueueEntryImpl node)
    {
        return node == null ? null : node.getLeft();
    }

    private SortedQueueEntryImpl rightChild(final SortedQueueEntryImpl node)
    {
        return node == null ? null : node.getRight();
    }

    private SortedQueueEntryImpl nodeParent(final SortedQueueEntryImpl node)
    {
        return node == null ? null : node.getParent();
    }

    private SortedQueueEntryImpl nodeGrandparent(final SortedQueueEntryImpl node)
    {
        return nodeParent(nodeParent(node));
    }

    private boolean isParentColour(final SortedQueueEntryImpl node, final SortedQueueEntryImpl.Colour colour)
    {

        return node != null && isNodeColour(node.getParent(), colour);
    }

    protected boolean isNodeColour(final SortedQueueEntryImpl node, final SortedQueueEntryImpl.Colour colour)
    {
        return (node == null ? Colour.BLACK : node.getColour()) == colour;
    }

    @Override
    public SortedQueueEntryImpl next(final SortedQueueEntryImpl node)
    {
        synchronized(_lock)
        {
            if(node.isDispensed() && _head != node)
            {
                SortedQueueEntryImpl current = _head;
                SortedQueueEntryImpl next;
                while(current != null)
                {
                    next = current.getNextValidEntry();
                    if(current.compareTo(node)>0 && !current.isDispensed())
                    {
                        break;
                    }
                    else
                    {
                        current = next;
                    }
                }
                return current;
            }
            else
            {
                return node.getNextValidEntry();
            }
        }
    }

    @Override
    public QueueEntryIterator<SortedQueueEntryImpl> iterator()
    {
        return new QueueEntryIteratorImpl(_head);
    }

    @Override
    public SortedQueueEntryImpl getHead()
    {
        return _head;
    }

    protected SortedQueueEntryImpl getRoot()
    {
        return _root;
    }

    @Override
    public void entryDeleted(final SortedQueueEntryImpl entry)
    {
        synchronized(_lock)
        {
            // If the node to be removed has two children, we swap the position
            // of the node and its successor in the tree
            if(leftChild(entry) != null && rightChild(entry) != null)
            {
                swapWithSuccessor(entry);
            }

            // Then deal with the easy doubly linked list deletion (need to do
            // this after the swap as the swap uses next
            final SortedQueueEntryImpl prev = entry.getPrev();
            if(prev != null)
            {
                prev.setNext(entry.getNextValidEntry());
            }

            final SortedQueueEntryImpl next = entry.getNextValidEntry();
            if(next != null)
            {
                next.setPrev(prev);
            }

            // now deal with splicing
            final SortedQueueEntryImpl chosenChild;

            if(leftChild(entry) != null)
            {
                chosenChild = leftChild(entry);
            }
            else
            {
                chosenChild = rightChild(entry);
            }

            if(chosenChild != null)
            {
                // we have one child (x), we can move it up to replace x;
                chosenChild.setParent(entry.getParent());
                if(chosenChild.getParent() == null)
                {
                    _root = chosenChild;
                }
                else if(entry == entry.getParent().getLeft())
                {
                    entry.getParent().setLeft(chosenChild);
                }
                else
                {
                    entry.getParent().setRight(chosenChild);
                }

                entry.setLeft(null);
                entry.setRight(null);
                entry.setParent(null);

                if(entry.getColour() == Colour.BLACK)
                {
                    deleteFixup(chosenChild);
                }

            }
            else
            {
                // no children
                if(entry.getParent() == null)
                {
                    // no parent either - the tree is empty
                    _root = null;
                }
                else
                {
                    if(entry.getColour() == Colour.BLACK)
                    {
                        deleteFixup(entry);
                    }

                    if(entry.getParent() != null)
                    {
                        if(entry.getParent().getLeft() == entry)
                        {
                            entry.getParent().setLeft(null);
                        }
                        else if(entry.getParent().getRight() == entry)
                        {
                            entry.getParent().setRight(null);
                        }
                        entry.setParent(null);
                    }
                }
            }

        }
    }

    /**
     * Swaps the position of the node in the tree with it's successor
     * (that is the node with the next highest key)
     * @param entry
     */
    private void swapWithSuccessor(final SortedQueueEntryImpl entry)
    {
        final SortedQueueEntryImpl next = entry.getNextValidEntry();
        final SortedQueueEntryImpl nextParent = next.getParent();
        final SortedQueueEntryImpl nextLeft = next.getLeft();
        final SortedQueueEntryImpl nextRight = next.getRight();
        final Colour nextColour = next.getColour();

        // Special case - the successor is the right child of the node
        if(next == entry.getRight())
        {
            next.setParent(entry.getParent());
            if(next.getParent() == null)
            {
                _root = next;
            }
            else if(next.getParent().getLeft() == entry)
            {
                next.getParent().setLeft(next);
            }
            else
            {
                next.getParent().setRight(next);
            }

            next.setRight(entry);
            entry.setParent(next);
            next.setLeft(entry.getLeft());

            if(next.getLeft() != null)
            {
                next.getLeft().setParent(next);
            }

            next.setColour(entry.getColour());
            entry.setColour(nextColour);
            entry.setLeft(nextLeft);

            if(nextLeft != null)
            {
                nextLeft.setParent(entry);
            }
            entry.setRight(nextRight);
            if(nextRight != null)
            {
                nextRight.setParent(entry);
            }
        }
        else
        {
            next.setParent(entry.getParent());
            if(next.getParent() == null)
            {
                _root = next;
            }
            else if(next.getParent().getLeft() == entry)
            {
                next.getParent().setLeft(next);
            }
            else
            {
                next.getParent().setRight(next);
            }

            next.setLeft(entry.getLeft());
            if(next.getLeft() != null)
            {
                next.getLeft().setParent(next);
            }
            next.setRight(entry.getRight());
            if(next.getRight() != null)
            {
                next.getRight().setParent(next);
            }
            next.setColour(entry.getColour());

            entry.setParent(nextParent);
            if(nextParent.getLeft() == next)
            {
                nextParent.setLeft(entry);
            }
            else
            {
                nextParent.setRight(entry);
            }

            entry.setLeft(nextLeft);
            if(nextLeft != null)
            {
                nextLeft.setParent(entry);
            }
            entry.setRight(nextRight);
            if(nextRight != null)
            {
                nextRight.setParent(entry);
            }
            entry.setColour(nextColour);
        }
    }

    private void deleteFixup(SortedQueueEntryImpl entry)
    {
        int i = 0;
        while(entry != null && entry != _root
                && isNodeColour(entry, Colour.BLACK))
        {
            i++;

            if(i > 1000)
            {
                return;
            }

            if(entry == leftChild(nodeParent(entry)))
            {
                SortedQueueEntryImpl rightSibling = rightChild(nodeParent(entry));
                if(isNodeColour(rightSibling, Colour.RED))
                {
                    setColour(rightSibling, Colour.BLACK);
                    nodeParent(entry).setColour(Colour.RED);
                    leftRotate(nodeParent(entry));
                    rightSibling = rightChild(nodeParent(entry));
                }

                if(isNodeColour(leftChild(rightSibling), Colour.BLACK)
                        && isNodeColour(rightChild(rightSibling), Colour.BLACK))
                {
                    setColour(rightSibling, Colour.RED);
                    entry = nodeParent(entry);
                }
                else
                {
                    if(isNodeColour(rightChild(rightSibling), Colour.BLACK))
                    {
                        setColour(leftChild(rightSibling), Colour.BLACK);
                        rightSibling.setColour(Colour.RED);
                        rightRotate(rightSibling);
                        rightSibling = rightChild(nodeParent(entry));
                    }
                    setColour(rightSibling, getColour(nodeParent(entry)));
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(rightChild(rightSibling), Colour.BLACK);
                    leftRotate(nodeParent(entry));
                    entry = _root;
                }
            }
            else
            {
                SortedQueueEntryImpl leftSibling = leftChild(nodeParent(entry));
                if(isNodeColour(leftSibling, Colour.RED))
                {
                    setColour(leftSibling, Colour.BLACK);
                    nodeParent(entry).setColour(Colour.RED);
                    rightRotate(nodeParent(entry));
                    leftSibling = leftChild(nodeParent(entry));
                }

                if(isNodeColour(leftChild(leftSibling), Colour.BLACK)
                        && isNodeColour(rightChild(leftSibling), Colour.BLACK))
                {
                    setColour(leftSibling, Colour.RED);
                    entry = nodeParent(entry);
                }
                else
                {
                    if(isNodeColour(leftChild(leftSibling), Colour.BLACK))
                    {
                        setColour(rightChild(leftSibling), Colour.BLACK);
                        leftSibling.setColour(Colour.RED);
                        leftRotate(leftSibling);
                        leftSibling = leftChild(nodeParent(entry));
                    }
                    setColour(leftSibling, getColour(nodeParent(entry)));
                    setColour(nodeParent(entry), Colour.BLACK);
                    setColour(leftChild(leftSibling), Colour.BLACK);
                    rightRotate(nodeParent(entry));
                    entry = _root;
                }
            }
        }
        setColour(entry, Colour.BLACK);
    }

    private Colour getColour(final SortedQueueEntryImpl x)
    {
        return x == null ? null : x.getColour();
    }

    public class QueueEntryIteratorImpl implements QueueEntryIterator<SortedQueueEntryImpl>
    {
        private SortedQueueEntryImpl _lastNode;

        public QueueEntryIteratorImpl(final SortedQueueEntryImpl startNode)
        {
            _lastNode = startNode;
        }

        public boolean atTail()
        {
            return next(_lastNode) == null;
        }

        public SortedQueueEntryImpl getNode()
        {
            return _lastNode;
        }

        public boolean advance()
        {
            if(!atTail())
            {
                SortedQueueEntryImpl nextNode = next(_lastNode);
                while(nextNode.isDispensed() && next(nextNode) != null)
                {
                    nextNode = next(nextNode);
                }
                _lastNode = nextNode;
                return true;

            }
            else
            {
                return false;
            }
        }
    }
}
