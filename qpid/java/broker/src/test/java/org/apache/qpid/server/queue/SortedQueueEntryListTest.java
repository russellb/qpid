package org.apache.qpid.server.queue;

import org.apache.qpid.server.message.AMQMessage;

import java.util.Arrays;
import org.apache.qpid.AMQException;
import org.apache.qpid.server.message.ServerMessage;

public class SortedQueueEntryListTest extends QueueEntryListTestBase
{
    private static SelfValidatingSortedQueueEntryList _sqel;

    public final static String keys[] = { " 73", " 18", " 11", "127", "166", "163", " 69", " 60", "191", "144",
                                          " 17", "161", "145", "140", "157", " 47", "136", " 56", "176", " 81",
                                          "195", " 96", "  2", " 68", "101", "141", "159", "187", "149", " 45",
                                          " 64", "100", " 83", " 51", " 79", " 82", "180", " 26", " 61", " 62",
                                          " 78", " 46", "147", " 91", "120", "164", " 92", "172", "188", " 50",
                                          "111", " 89", "  4", "  8", " 16", "151", "122", "178", " 33", "124",
                                          "171", "165", "116", "113", "155", "148", " 29", "  0", " 37", "131",
                                          "146", " 57", "112", " 97", " 23", "108", "123", "117", "167", " 52",
                                          " 98", "  6", "160", " 25", " 49", " 34", "182", "185", " 30", " 66",
                                          "152", " 58", " 86", "118", "189", " 84", " 36", "104", "  7", " 76",
                                          " 87", "  1", " 80", " 10", "142", " 59", "137", " 12", " 67", " 22",
                                          "  9", "106", " 75", "109", " 93", " 42", "177", "134", " 77", " 88",
                                          "114", " 43", "143", "135", " 55", "181", " 32", "174", "175", "184",
                                          "133", "107", " 28", "126", "103", " 85", " 38", "158", " 39", "162",
                                          "129", "194", " 15", " 24", " 19", " 35", "186", " 31", " 65", " 99",
                                          "192", " 74", "156", " 27", " 95", " 54", " 70", " 13", "110", " 41",
                                          " 90", "173", "125", "196", "130", "183", "102", "190", "132", "105",
                                          " 21", " 53", "139", " 94", "115", " 48", " 44", "179", "128", " 14",
                                          " 72", "119", "153", "168", "197", " 40", "150", "138", "  5", "154",
                                          "169", " 71", "199", "198", "170", "  3", "121", " 20", " 63", "193" };

    public final static String textkeys[] = { "AAA", "BBB", "CCC", "DDD", "EEE", "FFF", "GGG", "HHH", "III", "JJJ",
                                            "KKK", "LLL", "MMM", "NNN", "OOO", "PPP", "QQQ", "RRR", "SSS", "TTT",
                                            "UUU", "VVV", "XXX", "YYY", "ZZZ"};

    private final static String keysSorted[] = keys.clone();

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        // Create result array
        Arrays.sort(keysSorted);

        // Create test list
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        // Build test list
        long messageId = 0L;
        for(final String key : keys)
        {
            final ServerMessage msg = generateTestMessage(messageId++, key);
            _sqel.add(msg);
        }

    }

    public QueueEntryList getTestList()
    {
        return _sqel;
    }

    public int getExpectedListLength()
    {
        return keys.length;
    }

    public long getExpectedFirstMsgId()
    {
        return 67L;
    }

    public ServerMessage getTestMessageToAdd() throws AMQException
    {
        return generateTestMessage(1, "test value");
    }

    private ServerMessage generateTestMessage(final long id, final String keyValue) throws AMQException
    {
        return new AMQMessage(new MockStoredMessage(id, "KEY", keyValue));
    }

    public void testIterator()
    {
        super.testIterator();

        // Test sorted order of list
        final QueueEntryIterator<?> iter = getTestList().iterator();
        int count = 0;
        while(iter.advance())
        {
            assertEquals("Sorted queue entry value does not match sorted key array",
                            keysSorted[count++], getSortedKeyValue(iter));
        }
    }

    private Object getSortedKeyValue(QueueEntryIterator<?> iter)
    {
        return ((SortedQueueEntryImpl) iter.getNode()).getMessage().getMessageHeader().getHeader("KEY");
    }

    private Long getMessageId(QueueEntryIterator<?> iter)
    {
        return ((SortedQueueEntryImpl) iter.getNode()).getMessage().getMessageNumber();
    }

    public void testNonUniqueSortKeys() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        // Build test list
        long messageId = 0L;
        while(messageId < 200)
        {
            final ServerMessage msg = generateTestMessage(messageId++, "samekey");
            _sqel.add(msg);
        }

        final QueueEntryIterator<?> iter = getTestList().iterator();
        int count=0;
        while(iter.advance())
        {
            assertEquals("Sorted queue entry value is not as expected", "samekey", getSortedKeyValue(iter));
            assertEquals("Message id not as expected", Long.valueOf(count++), getMessageId(iter));
        }
    }

    public void testNullSortKeys() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        // Build test list
        long messageId = 0L;
        while(messageId < 200)
        {
            final ServerMessage msg = generateTestMessage(messageId++, null);
            _sqel.add(msg);
        }

        final QueueEntryIterator<?> iter = getTestList().iterator();
        int count=0;
        while(iter.advance())
        {
            assertNull("Sorted queue entry value is not as expected", getSortedKeyValue(iter));
            assertEquals("Message id not as expected", Long.valueOf(count++), getMessageId(iter));        }
    }

    public void testAscendingSortKeys() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        // Build test list
        long messageId = 0L;
        for(String textKey : textkeys)
        {
            final ServerMessage msg = generateTestMessage(messageId, textKey);
            messageId++;
            _sqel.add(msg);
        }

        final QueueEntryIterator<?> iter = getTestList().iterator();
        int count=0;
        while(iter.advance())
        {
            assertEquals("Sorted queue entry value is not as expected", textkeys[count], getSortedKeyValue(iter));
            assertEquals("Message id not as expected", Long.valueOf(count), getMessageId(iter));
            count++;
        }
    }

    public void testDescendingSortKeys() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        // Build test list
        long messageId = 0L;
        for(int i=textkeys.length-1; i >=0; i--)
        {
            final ServerMessage msg = generateTestMessage(messageId, textkeys[i]);
            messageId++;
            _sqel.add(msg);
        }

        final QueueEntryIterator<?> iter = getTestList().iterator();
        int count=0;
        while(iter.advance())
        {
            assertEquals("Sorted queue entry value is not as expected", textkeys[count], getSortedKeyValue(iter));
            assertEquals("Message id not as expected", Long.valueOf(textkeys.length-count-1), getMessageId(iter));
            count++;
        }
    }

    public void testInsertAfter() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        ServerMessage msg = generateTestMessage(1, "A");
        _sqel.add(msg);

        SortedQueueEntryImpl entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 1);

        msg = generateTestMessage(2, "B");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "B", 2);
    }

    public void testInsertBefore() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        ServerMessage msg = generateTestMessage(1, "B");
        _sqel.add(msg);

        SortedQueueEntryImpl entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "B", 1);

        msg = generateTestMessage(2, "A");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 2);

        entry = _sqel.next(entry);
        validateEntry(entry, "B", 1);
    }

    public void testInsertInbetween() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        ServerMessage msg = generateTestMessage(1, "A");
        _sqel.add(msg);
        SortedQueueEntryImpl entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 1);

        msg = generateTestMessage(2, "C");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "C", 2);

        msg = generateTestMessage(3, "B");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "B", 3);

        entry = _sqel.next(entry);
        validateEntry(entry, "C", 2);
    }

    public void testInsertAtHead() throws Exception
    {
        _sqel = new SelfValidatingSortedQueueEntryList(_testQueue, "KEY");

        ServerMessage msg = generateTestMessage(1, "B");
        _sqel.add(msg);

        SortedQueueEntryImpl entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "B", 1);

        msg = generateTestMessage(2, "D");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "B", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "D", 2);

        msg = generateTestMessage(3, "C");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "B", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "C", 3);

        entry = _sqel.next(entry);
        validateEntry(entry, "D", 2);

        msg = generateTestMessage(4, "A");
        _sqel.add(msg);

        entry = _sqel.next(_sqel.getHead());
        validateEntry(entry, "A", 4);

        entry = _sqel.next(entry);
        validateEntry(entry, "B", 1);

        entry = _sqel.next(entry);
        validateEntry(entry, "C", 3);

        entry = _sqel.next(entry);
        validateEntry(entry, "D", 2);
    }

    private void validateEntry(final SortedQueueEntryImpl entry, final String expectedSortKey, final long expectedMessageId)
    {
        assertEquals("Sorted queue entry value is not as expected",
                        expectedSortKey, entry.getMessage().getMessageHeader().getHeader("KEY"));
        assertEquals("Sorted queue entry id is not as expected",
                        Long.valueOf(expectedMessageId), entry.getMessage().getMessageNumber());
    }

}
