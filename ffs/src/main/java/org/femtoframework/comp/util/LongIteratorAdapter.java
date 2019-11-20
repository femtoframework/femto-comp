package org.femtoframework.comp.util;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 适配器
 *
 * @author fengyun
 * @version 1.00 Nov 20, 2002 11:37:51 PM
 */
public class LongIteratorAdapter
    implements LongIterator, Enumeration
{
    private Iterator it;

    public LongIteratorAdapter(Iterator iterator)
    {
        this.it = iterator;
    }

    public boolean hasNext()
    {
        return it.hasNext();
    }

    public boolean hasMoreElements()
    {
        return hasNext();
    }

    public long next()
    {
        return (Long) nextElement();
    }

    public Object nextElement()
    {
        return it.next();
    }
}
