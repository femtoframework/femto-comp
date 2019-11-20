package org.femtoframework.comp.util;

import org.femtoframework.util.ArrayUtil;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Long型数字到Long型数字的哈希
 *
 * @author fengyun
 * @version 1.00 Jul 7, 2002 9:38:57 PM
 */
public class LongMap
{
    public static final long NULL = Long.MIN_VALUE + 1;
    /**
     * The hash table data.
     */
    private transient Entry table[];

    /**
     * The total number of mappings in the hash table.
     */
    private transient int count;

    /**
     * The table is rehashed when its size exceeds this threshold.  (The
     * value of this field is (int)(capacity * loadFactor).)
     *
     * @serial
     */
    private int threshold;

    /**
     * The load factor for the hashtable.
     *
     * @serial
     */
    private float loadFactor;

    /**
     * Constructs a new, empty map with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param capacity the initial capacity of the LongMap.
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero.
     */
    public LongMap(int capacity, float loadFactor)
    {
        if (capacity < 0) {
            throw new IllegalArgumentException("Illegal Initial Capacity: " +
                                               capacity);
        }
        if (capacity == 0) {
            capacity = 1;
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal Load factor: " +
                                               loadFactor);
        }
        this.loadFactor = loadFactor;
        table = new Entry[capacity];
        this.threshold = (int) (capacity * loadFactor);
    }

    /**
     * Constructs a new, empty map with the specified initial capacity
     * and default load factor, which is <tt>0.75</tt>.
     *
     * @param capacity the initial capacity of the HashMap.
     * @throws IllegalArgumentException if the initial capacity is less
     *                                  than zero.
     */
    public LongMap(int capacity)
    {
        this(capacity, 0.75f);
    }

    /**
     * Constructs a new, empty map with a default capacity and load
     * factor, which is <tt>0.75</tt>.
     */
    public LongMap()
    {
        this(11, 0.75f);
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
     * Returns <tt>true</tt> if this map maps one or more keys to the
     * specified value.
     *
     * @param value value whose presence in this map is to be tested.
     * @return <tt>true</tt> if this map maps one or more keys to the
     *         specified value.
     */
    public boolean containsValue(long value)
    {
        Entry tab[] = table;

        if (value == NULL) {
            for (int i = tab.length; i-- > 0;) {
                for (Entry e = tab[i]; e != null; e = e.next) {
                    if (e.value == NULL) {
                        return true;
                    }
                }
            }
        }
        else {
            for (int i = tab.length;
                 i-- > 0;) {
                for (Entry e = tab[i];
                     e != null;
                     e = e.next) {
                    if (value == e.value) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     *
     * @param key key whose presence in this Map is to be tested.
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key.
     */
    public boolean containsKey(long key)
    {
        Entry tab[] = table;
        if (key != NULL) {
            int hash = (int) (key & 0x7FFFFFFF);
            int index = hash % tab.length;
            for (Entry e = tab[index]; e != null; e = e.next) {
                if (e.hash == hash && key == e.key) {
                    return true;
                }
            }
        }
        else {
            for (Entry e = tab[0]; e != null; e = e.next) {
                if (e.key == NULL) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Returns the value to which this map maps the specified key.  Returns
     * <tt>null</tt> if the map contains no mapping for this key.  A return
     * value of <tt>null</tt> does not <i>necessarily</i> indicate that the
     * map contains no mapping for the key; it's also possible that the map
     * explicitly maps the key to <tt>null</tt>.  The <tt>containsKey</tt>
     * operation may be used to distinguish these two cases.
     *
     * @param key key whose associated value is to be returned.
     * @return the value to which this map maps the specified key.
     */
    public synchronized long get(long key)
    {
        Entry tab[] = table;

        if (key != NULL) {
            int hash = (int) (key & 0x7FFFFFFF);
            int index = hash % tab.length;
            for (Entry e = tab[index]; e != null; e = e.next) {
                if ((e.hash == hash) && key == e.key) {
                    return e.value;
                }
            }
        }
        else {
            for (Entry e = tab[0]; e != null; e = e.next) {
                if (e.key == NULL) {
                    return e.value;
                }
            }
        }

        return NULL;
    }

    /**
     * Rehashes the contents of this map into a new <tt>LongMap</tt> instance
     * with a larger capacity. This method is called automatically when the
     * number of keys in this map exceeds its capacity and load factor.
     */
    private void rehash()
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
        this.threshold = (int) (newCapacity * loadFactor);
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for this key, the old
     * value is replaced.
     *
     * @param key   key with which the specified value is to be associated.
     * @param value value to be associated with the specified key.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.  A <tt>null</tt> return can
     *         also indicate that the LongMap previously associated
     *         <tt>null</tt> with the specified key.
     */
    public synchronized long put(long key, long value)
    {
        // Makes sure the key is not already in the LongMap.
        Entry tab[] = table;
        int hash = 0;
        int index = 0;

        if (key != NULL) {
            hash = (int) (key & 0x7FFFFFFF);
            index = hash % tab.length;
            for (Entry e = tab[index]; e != null; e = e.next) {
                if ((e.hash == hash) && key == e.key) {
                    long old = e.value;
                    e.value = value;
                    return old;
                }
            }
        }
        else {
            for (Entry e = tab[0]; e != null; e = e.next) {
                if (e.key == NULL) {
                    long old = e.value;
                    e.value = value;
                    return old;
                }
            }
        }

        if (count >= threshold) {
            rehash();

            tab = table;
            index = (hash & 0x7FFFFFFF) % tab.length;
        }

        // Creates the new entry.
        tab[index] = new Entry(hash, key, value, tab[index]);
        count++;
        return NULL;
    }

    /**
     * Removes the mapping for this key from this map if present.
     *
     * @param key key whose mapping is to be removed from the map.
     * @return previous value associated with specified key, or <tt>null</tt>
     *         if there was no mapping for key.  A <tt>null</tt> return can
     *         also indicate that the map previously associated <tt>null</tt>
     *         with the specified key.
     */
    public synchronized long remove(long key)
    {
        Entry tab[] = table;

        if (key != NULL) {
            int hash = (int) (key & 0x7FFFFFFF);
            int index = hash % tab.length;

            for (Entry e = tab[index], prev = null; e != null;
                 prev = e, e = e.next) {
                if ((e.hash == hash) && key == e.key) {
                    if (prev != null) {
                        prev.next = e.next;
                    }
                    else {
                        tab[index] = e.next;
                    }

                    count--;
                    long oldValue = e.value;
                    e.value = NULL;
                    return oldValue;
                }
            }
        }
        else {
            for (Entry e = tab[0], prev = null; e != null;
                 prev = e, e = e.next) {
                if (e.key == NULL) {
                    if (prev != null) {
                        prev.next = e.next;
                    }
                    else {
                        tab[0] = e.next;
                    }

                    count--;
                    long oldValue = e.value;
                    e.value = NULL;
                    return oldValue;
                }
            }
        }

        return NULL;
    }

    /**
     * Removes all mappings from this map.
     */
    public synchronized void clear()
    {
        Entry tab[] = table;
        for (int index = tab.length; --index >= 0;) {
            tab[index] = null;
        }
        count = 0;
    }

    public LongIterator keys()
    {
        return getLongIterator(KEYS);
    }

    public LongIterator values()
    {
        return getLongIterator(VALUES);
    }

    public Iterator entries()
    {
        return Arrays.asList(table).iterator();
    }

    private LongIterator getLongIterator(int type)
    {
        if (count == 0) {
            return emptyHashIterator;
        }
        else {
            return new LongIteratorImpl(type);
        }
    }

    /**
     * LongMap collision list entry.
     */
    private static class Entry
    {
        int hash;
        long key;
        long value;
        Entry next;

        Entry(int hash, long key, long value, Entry next)
        {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        protected Object clone()
        {
            return new Entry(hash, key, value,
                             (next == null ? null : (Entry) next.clone()));
        }

        // Map.Entry Ops

        public long getKey()
        {
            return key;
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

            return key == e.key && value == e.value;
        }

        public int hashCode()
        {
            return hash ^ (value == NULL ? 0 : (int) (value & 0x7FFFFFFF));
        }

        public String toString()
        {
            return key + "=" + value;
        }
    }

    // Types of Iterators
    private static final int KEYS = 0;
    private static final int VALUES = 1;

    private static EmptyHashIterator emptyHashIterator
        = new EmptyHashIterator();

    private static class EmptyHashIterator
        implements LongIterator
    {
        EmptyHashIterator()
        {
        }

        public boolean hasNext()
        {
            return false;
        }

        public long next()
        {
            throw new NoSuchElementException();
        }
    }

    private class LongIteratorImpl
        implements LongIterator
    {
        Entry[] table = LongMap.this.table;
        int index = table.length;
        Entry entry = null;
        Entry lastReturned = null;
        int type;

        LongIteratorImpl(int type)
        {
            this.type = type;
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
                if (type == KEYS) {
                    return e.key;
                }
                else if (type == VALUES) {
                    return e.value;
                }
            }
            throw new NoSuchElementException();
        }
    }
}
