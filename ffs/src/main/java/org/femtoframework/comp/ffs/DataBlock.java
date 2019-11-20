package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.comp.io.Channel;

/**
 * 数据块
 *
 * @author fengyun
 * @version 1.00 Apr 29, 2002 10:20:56 PM
 */
public class DataBlock
    implements BlockBase
{
    /**
     * 块
     */
    private Block block;

    /**
     * 在文件中的索引
     */
    int index = Constants.NULL;

    DataBlock(int size)
    {
        this.block = new Block(Constants.NULL, Constants.NULL, size);
    }

    /**
     * 数据大小
     */
    public final int size()
    {
        return block.count;
    }

    /**
     * 块在扇区中的偏址
     */
    public final int offset()
    {
        return block.offset();
    }

    /**
     * 设置块偏址
     */
    public final void setOffset(int offset)
    {
        block.setOffset(offset);
    }

    /**
     * 块在扇区中的位置
     */
    public long position()
    {
        return block.getAbsolutePosition();
    }

    public final void reset()
    {
        block.reset();
        index = Constants.NULL;
    }

    /**
     * 装载块
     */
    public void load(Channel channel) throws IOException
    {
        block.load(channel);
    }

    /**
     * 装载块
     */
    public void load(Channel channel, int blockSize) throws IOException
    {
        block.load(channel, blockSize);
    }

    /**
     * 保存块
     */
    public void save(Channel channel) throws IOException
    {
        block.save(channel);
    }

    /**
     * 保存块
     *
     * @param channel 存取通道
     */
    public void save(Channel channel, int size)
        throws IOException
    {
        block.save(channel, size);
    }

    /**
     * 返回块
     */
    public Block getBlock()
    {
        return block;
    }

    final void newCount()
    {
        block.newCount();
    }

    void fill(DataBlock data)
    {
        block.fill(data.block);
        setOffset(data.offset());
    }
}

