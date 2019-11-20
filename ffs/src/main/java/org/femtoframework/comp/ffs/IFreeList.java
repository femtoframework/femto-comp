package org.femtoframework.comp.ffs;


/**
 * 空闲块列表
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 2:08:46 PM
 */
public class IFreeList
{

    /**
     * 分配指针
     */
    private int cursor;

    /**
     * 分配表
     */
    private BoolSet list;

    /**
     * Size
     */
    private int size;

//    public IFreeList(BoolSet list)
//    {
//        this.list = list;
//        this.size = list.size();
//    }

    public IFreeList(int size)
    {
        this.size = size;
        this.list = new BoolSet(size);
    }

    /**
     * 当前分配位置
     */
    public int cursor()
    {
        return cursor;
    }

    /**
     * 分配表大小
     */
    public final int size()
    {
        return size;
    }

    /**
     * 空闲数目
     */
    public int freeSize()
    {
        return list.falseSize();
    }

    /**
     * 使用了的数目
     */
    public int usedSize()
    {
        return list.trueSize();
    }

    /**
     * 是否空闲
     *
     * @param index 是否空闲
     */
    public final boolean isFree(int index)
    {
        return !list.get(index);
    }

    public final boolean isUsed(int index)
    {
        return list.get(index);
    }

    /**
     * 分配指定块
     *
     * @param index 指定块
     */
    final void allc(int index)
    {
        list.set(index);
    }

    /**
     * 释放指定块
     */
    public void free(int index)
    {
/*       if (index < 0 || index >= size)
            throw new IllegalArgumentException("Illegal Access Inode:" + index);
*/
        list.clear(index);
    }

    /**
     * 返回下一个空闲块
     */
    public synchronized int allc()
    {
        if (list.trueSize() >= size) {
            return Constants.NULL;
        }

//        int loop = 0;
        boolean[] bools = list.bool;
        int i = cursor;
        while (true) {
            if (!bools[i]) {
                bools[i] = true;
                cursor = (i + 1) % size;
                return i;
            }
            i = (i + 1) % size;
            //i  Index of booleans
            if (cursor == i) {
                break;
            }
        }
        return Constants.NULL;
    }
}
