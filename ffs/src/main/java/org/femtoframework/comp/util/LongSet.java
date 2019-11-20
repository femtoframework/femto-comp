package org.femtoframework.comp.util;

import java.util.NoSuchElementException;

/**
 * 长整数集合
 *
 * @author fengyun
 * @version 1.00 Jul 24, 2002 11:50:24 PM
 */
public class LongSet
{
    public static final long NULL = -1;

    /**
     * The hash table data.
     */
    private transient Entry table[];

    /**
     * The total number of mappings in the hash table.
     */
    private transient int count;

    /**
     * Constructs a new, empty map with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param capacity the initial capacity of the LongMap.
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero.
     */
    public LongSet(int capacity)
    {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal Initial Capacity: " +
                                               capacity);
        }
        if (capacity == 0) {
            capacity = 1;
        }
        table = new Entry[capacity];
    }

    /**
     * Constructs a new, empty map with a default capacity and load
     * factor, which is <tt>0.75</tt>.
     */
    public LongSet()
    {
        this(11);
    }

    /**
     * Returns the number of key-value mappings in this map.
     *
     * @return the number of key-value mappings in this map.
     */
    public int size()
    {
        return count;
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     *
     * @return <tt>true</tt> if this map contains no key-value mappings.
     */
    public boolean isEmpty()
    {
        return count == 0;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param value key whose presence in this Map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     */
    public boolean contains(long value)
    {
        if (value == NULL) {
            return false;
        }

        Entry tab[] = table;
        int hash = (int) (value & 0x7FFFFFFF);
        int index = hash % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (value == e.value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rehashes the contents of this map into a new <tt>LongSet</tt> instance
     * with a larger capacity. This method is called automatically when the
     * number of keys in this map exceeds its capacity and load factor.
     */
    /*   private void rehash()
        {
            int oldCapacity = table.length;
            Entry oldMap[] = table;

            int newCapacity = oldCapacity * 2 + 1;
            Entry newMap[] = new Entry[newCapacity];
            table = newMap;

            for (int i = oldCapacity; i-- > 0;) {
                for (Entry old = oldMap[i]; old != null;) {
                    Entry e = old;
                    old = old.next;

                    int index = (e.hash & 0x7FFFFFFF) % newCapacity;
                    e.next = newMap[index];
                    newMap[index] = e;
                }
            }
        }
    */
    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param value key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.  A <tt>null</tt> return can
     *         also indicate that the LongSet previously associated
     *         <tt>null</tt> with the specified key.
     */
    public long add(long value)
    {
        if (value == NULL) {
            return NULL;
        }

        // Makes sure the key is not already in the LongSet.
        Entry tab[] = table;

        int hash = (int) (value & 0x7FFFFFFF);
        int index = hash % tab.length;
        for (Entry e = tab[index]; e != null; e = e.next) {
            if (value == e.value) {
                return value;
            }
        }

        // Creates the new entry.
        tab[index] = new Entry(hash, value, tab[index]);
        count++;
        return NULL;
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param value key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no ma
     *         pping for key.  A <tt>null</tt> return can
     *         also indicate that the map previously associated <tt>null</tt>
     *         with the specified key.
     */
    public boolean remove(long value)
    {
        if (value == NULL) {
            return false;
        }

        Entry tab[] = table;

        int hash = (int) (value & 0x7FFFFFFF);
        int index = hash % tab.length;

        for (Entry e = tab[index], prev = null; e != null;
             prev = e, e = e.next) {
            if (value == e.value) {
                if (prev != null) {
                    prev.next = e.next;
                }
                else {
                    tab[index] = e.next;
                }

                return true;
            }
        }

        return false;
    }

    /**
     * Removes all mappings from this map.
     */
    public void clear()
    {
        Entry tab[] = table;
        for (int index = tab.length; --index >= 0;) {
            tab[index] = null;
        }
        count = 0;
    }

    public LongIterator values()
    {
        return getLongIterator();
    }

    private LongIterator getLongIterator()
    {
        return new LongIteratorImpl();
    }

    /**
     * LongSet collision list entry.
     */
    private static class Entry
    {
        int hash;
        long value;
        Entry next;

        Entry(int hash, long value, Entry next)
        {
            this.hash = hash;
            this.value = value;
            this.next = next;
        }

        protected Object clone()
        {
            return new Entry(hash, value,
                             (next == null ? null : (Entry) next.clone()));
        }

        public long getValue()
        {
            return value;
        }

        public long setValue(long value)
        {
            long oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public boolean equals(Object o)
        {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry e = (Entry) o;

            return value == e.value;
        }

        public int hashCode()
        {
            return hash ^ (value == NULL ? 0 : (int) (value & 0x7FFFFFFF));
        }

        public String toString()
        {
            return String.valueOf(value);
        }
    }

    private class LongIteratorImpl
        implements LongIterator
    {
        Entry[] table = LongSet.this.table;
        int index = table.length;
        Entry entry = null;
        Entry lastReturned = null;

        LongIteratorImpl()
        {
        }

        public boolean hasNext()
        {
            Entry e = entry;
            int i = index;
            Entry t[] = table;
            /* Use locals for faster loop iteration */
            while (e == null && i > 0) {
                e = t[--i];
            }
            entry = e;
            index = i;
            return e != null;
        }

        public long next()
        {
            Entry et = entry;
            int i = index;
            Entry t[] = table;

            /* Use locals for faster loop iteration */
            while (et == null && i > 0) {
                et = t[--i];
            }

            entry = et;
            index = i;
            if (et != null) {
                Entry e = lastReturned = entry;
                entry = e.next;
                return e.value;
            }
            throw new NoSuchElementException();
        }
    }
}
