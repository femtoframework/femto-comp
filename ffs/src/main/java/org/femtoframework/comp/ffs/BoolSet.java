package org.femtoframework.comp.ffs;

/**
 * 能够内部访问其中的成员
 *
 * @author fengyun
 * @version 1.00
 */
public class BoolSet
{
    /**
     * Boolean
     */
    boolean[] bool;


    /**
     * Size
     */
    protected int size;

    /**
     * True Size
     */
    protected int trueSize;


    public BoolSet(int size)
    {
        if (size <= 0)
            throw new IllegalArgumentException("Ju ran hui xiao yu 0:" + size);

        this.size = size;
        this.trueSize = 0;
        bool = new boolean[size];
    }

    public boolean[] values()
    {
        return bool;
    }

    public final boolean get(int index)
    {
        return bool[index];
    }

    public final void set(int index)
    {
        if (!bool[index]) trueSize++;
        bool[index] = true;
    }

    public final void clear(int index)
    {
        if (bool[index]) trueSize--;
        bool[index] = false;
    }

    public final int size()
    {
        return size;
    }

    public final int trueSize()
    {
        return trueSize;
    }

    public final int falseSize()
    {
        return size - trueSize;
    }
}
