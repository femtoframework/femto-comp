package org.femtoframework.comp.ffs;


/**
 * 空闲块列表
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 2:08:46 PM
 */
public class FreeList
{

    /**
     * 分配指针
     */
    private int cursor = 0;

    /**
     * 有效块位置
     */
    private int minOffset, maxOffset;

    /**
     * 分配表
     */
    private BitMap map;

    /**
     * Size
     */
    private int size;

    public FreeList(BitMap map,
                    int minOffset,
                    int maxOffset)
    {
        this.map = map;
        this.size = map.size();
        this.minOffset = minOffset;
        this.maxOffset = maxOffset;
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
    public int size()
    {
        return size;
    }

    /**
     * 空闲数目
     */
    public int freeSize()
    {
        return map.falseSize();
    }

    /**
     * 使用了的数目
     */
    public int usedSize()
    {
        return map.trueSize();
    }

    /**
     * 是否空闲
     *
     * @param block 是否空闲
     */
    public boolean isFree(int block)
    {
        if (block < minOffset || block >= maxOffset) {
            throw new IllegalArgumentException("Illegal Access Block:" + block);
        }

        return map.get(block);
    }

    /**
     * 分配指定块
     *
     * @param block 指定块
     */
/*  void allc(int block)
    {
        if (block < minOffset || block >= maxOffset)
            throw new IllegalArgumentException("Illegal Access Block:"+block);

        map.set(block);
    }*/

    /**
     * 释放指定块
     */
    public final synchronized void free(int block)
    {
//        if (block < minOffset || block >= maxOffset)
//            throw new IllegalArgumentException("Illegal Access Block:"+block);
        map.clear(block);
    }

    /**
     * 返回下一个空闲块
     */
    public synchronized int allc()
    {
        if (map.trueSize() >= size) {
            return Constants.NULL;
        }

        byte[] bits = map.values();
        int i = cursor;
        int loop = (cursor + size - 1) % size;
        int unit = cursor >> 3;
        while (i != loop) {
            byte bit = BitMap.MASKS[i & BitMap.BIT_INDEX_MASK];
            if ((bits[unit] & bit) == 0) {
                //bits[j] |= bit;
                map.set(i, unit, bit);
                cursor = (i + 1) % size;
                return i;
            }
            i = (i + 1) % size;
            if ((i & 0x07) == 0) {
                unit++;
                if (unit >= bits.length) {
                    return Constants.NULL;
                }
            }
        }
        return Constants.NULL;
    }
}
