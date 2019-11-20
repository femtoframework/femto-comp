package org.femtoframework.comp.util;

/**
 * 固定大小的位集合
 *
 * @author fengyun
 * @version 1.00
 */
public class BitSet
{
    public final static int ADDRESS_BITS_PER_UNIT = 3;
    protected final static int BITS_PER_UNIT = 1 << ADDRESS_BITS_PER_UNIT;
    public final static int BIT_INDEX_MASK = BITS_PER_UNIT - 1;

    protected byte bits[];

    protected int size;

    protected int trueSize;

    protected int unitIndex(int bitIndex)
    {
        return bitIndex >> ADDRESS_BITS_PER_UNIT;
    }

    protected byte bit(int bitIndex)
    {
        return (byte) (1 << (bitIndex & BIT_INDEX_MASK));
    }

    public byte[] values()
    {
        return bits;
    }

    /**
     * 构造位集合
     *
     * @param size 位总数
     */
    public BitSet(int size)
    {
        if (size <= 0) {
            throw new IndexOutOfBoundsException("Illegal Index:" + size);
        }

        this.size = size;
        this.trueSize = 0;
        int len = ((size - 1) >> ADDRESS_BITS_PER_UNIT) + 1;
        this.bits = new byte[len];
    }

    /**
     * 设置给定位
     *
     * @param index 索引
     */
    public void set(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Illegal Index:" + index);
        }

        //int unitIndex = unitIndex(index);
        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        if (unitIndex < 0) {
            return;
        }

        //int bit = bit(index);
        byte bit = (byte) (1 << (index & BIT_INDEX_MASK));
        if ((bits[unitIndex] & bit) == 0) {
            trueSize++;
        }
        bits[unitIndex] |= bit;
    }

    /**
     * 清除给定位
     *
     * @param index 索引
     */
    public void clear(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Illegal index:" + index);
        }

        //int unitIndex = unitIndex(index);
        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        //int bit = bit(index);
        byte bit = (byte) (1 << (index & BIT_INDEX_MASK));
        if ((bits[unitIndex] & bit) != 0) {
            trueSize--;
        }

        bits[unitIndex] &= ~bit;
    }

    /**
     * 返回给定位置的值
     *
     * @param index 索引
     */
    public boolean get(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Illegal index:" + index);
        }

        //int unitIndex = unitIndex(index);
        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        if (unitIndex < 0) {
            return true;
        }

        byte bit = (byte) (1 << (index & BIT_INDEX_MASK));
        return (bits[unitIndex] & bit) != 0;
    }

    /**
     * 位总数
     *
     * @return 位总数
     */
    public int size()
    {
        return size;
    }

    /**
     * 为真的位总数
     *
     * @return 位总数
     */
    public int trueSize()
    {
        return trueSize;
    }

    public int falseSize()
    {
        return size - trueSize;
    }

    private static final char TRUE = '1';
    private static final char FALSE = '0';

    /**
     * 字符串信息
     *
     * @return [String] 字符串
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            if (get(i)) {
                sb.append(TRUE);
            }
            else {
                sb.append(FALSE);
            }
            if (i % 8 == 7 && i != size - 1) {
                sb.append(' ');
            }
        }
        return sb.toString();
    }

//    public static void main(String[] args)
//    {
//        BitSet bit = new BitSet(215);
//        bit.set(14);
//        bit.set(0);
//        bit.set(8);
//        bit.clear(10);
//        bit.clear(8);
//        bit.set(200);
//        System.out.println(bit.get(15) + " " + bit.get(14));
//        System.out.println(bit.trueSize());
//        System.out.println(bit);
//    }
}
