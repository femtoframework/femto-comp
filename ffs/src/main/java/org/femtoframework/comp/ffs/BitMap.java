package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.comp.io.Channel;
import org.femtoframework.comp.io.Storable;
import org.femtoframework.comp.util.BitSet;

public class BitMap extends BitSet
    implements Storable
{

//    private int unit;

    /**
     * 通道位置
     */
    private long position;

//    /**
//     * 字节修改情况
//     */
//    private BoolSet status;

//    /**
//     * 字节状态位数
//     */
//    private int statusSize;

//    /**
//     * Flush Size
//     */
//    private int flushSize;

//    /**
//     * BlockPosition
//     */
//    private int[] blockPos;

//    /**
//     * BlockOffset
//     */
//    private int[] blockOff;

    /**
     * 构造位列表
     *
     * @param position 在通道中的位置
     * @param size     位总数
     */
    public BitMap(long position, int size)
    {
        super(size);
        this.position = position;
//        this.flushSize = flushSize;
//        this.unit = (int) (Math.log(flushSize) / Math.log(2));
//        this.statusSize = ((bits.length - 1) >> unit) + 1;
//        unit += 3;

//        this.status = new BoolSet(statusSize);
//        this.blockPos = new int[statusSize];
//        this.blockOff = new int[statusSize];

//        blockPos[0] = (int) position;
//        blockOff[0] = 0;
//        for (int i = 1; i < statusSize; i++) {
//            blockPos[i] = blockPos[i - 1] + flushSize;
//            blockOff[i] = blockOff[i - 1] + flushSize;
//        }
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder(32);
        sb.append("BitMap[");
        sb.append("size:").append(size).append(',');
//        sb.append("used:").append(status.trueSize()).append(',');
//        sb.append("free:").append(status.falseSize()).append(']');
        return sb.toString();
    }

    /**
     * 清除给定位
     *
     * @param index 索引
     */
    public final void clear(int index)
    {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Illegal index:" + index);
        }

        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        //byte bit = (byte)(1 << (index & BIT_INDEX_MASK));
        byte bit = MASKS[index & BIT_INDEX_MASK];
        bits[unitIndex] &= ~bit;
        //status.set(index >> unit);
    }

    /**
     * 返回给定位置的值
     *
     * @param index 索引
     */
    public final boolean get(int index)
    {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Illegal index:" + index);
        }

        //int unitIndex = unitIndex(index);
        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        if (unitIndex < 0) {
            return true;
        }

        //byte bit = (byte)(1 << (index & BIT_INDEX_MASK));
        byte bit = MASKS[index & BIT_INDEX_MASK];
        return (bits[unitIndex] & bit) != 0;
    }

    /**
     * 设置给定位
     *
     * @param index 索引
     */
    public final void set(int index)
    {
        int unitIndex = index >> ADDRESS_BITS_PER_UNIT;
        if (unitIndex < 0) {
            return;
        }

        //byte bit = (byte)(1 << (index & BIT_INDEX_MASK));
        byte bit = MASKS[index & BIT_INDEX_MASK];
        bits[unitIndex] |= bit;
//        status.set(index >> unit);
    }

    final void set(int index, int unitIndex, int bit)
    {
        bits[unitIndex] |= bit;
//        status.set(index >> unit);
    }

    /**
     * 保持同步
     */
    public void flush(Channel channel) throws IOException
    {
        channel.seek(position);
        channel.write(bits);

//        boolean[] bools = status.bool;
//        for (int i = 0; i < statusSize; i++) {
//            if (bools[i]) {
//                channel.seek(blockPos[i]);
//                channel.write(bits, blockOff[i], flushSize);
//                status.clear(i);
//            }
//        }
    }

    /**
     * 保存
     *
     * @param channel 存取通道
     */
    public void save(Channel channel) throws IOException
    {
        //需要同步 请保证通道只被单线程使用
        channel.seek(position);
        channel.write(bits);
    }

    /**
     * 读取
     *
     * @param channel 存取通道
     */
    public void load(Channel channel) throws IOException
    {
        channel.seek(position);
        channel.read(bits);
        this.trueSize = 0;
        for (int i = 0; i < bits.length; i++) {
            this.trueSize += BIT_COUNT[((int)bits[i]) & 0xFF];
        }
    }

    public static final byte[] MASKS = new byte[]{
        0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte)0x80
    };

    private static final int[] BIT_COUNT = new int[]{
        0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,

        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,

        1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,

        2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
        3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
        4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
    };
}

