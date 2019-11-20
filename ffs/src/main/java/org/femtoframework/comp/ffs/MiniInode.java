package org.femtoframework.comp.ffs;


/**
 * 迷你I节点
 *
 * @author fengyun
 * @version 1.00 May 28, 2002 5:18:11 PM
 */
public class MiniInode extends Inode
{
    public static final short FREE = (short)0xFFFF;

    protected static final short[] FREE_BLOCKS
        = new short[]{
            FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE,
            FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE,
            FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE,
            FREE, FREE, FREE, FREE, FREE, FREE, FREE, FREE
        };

    /**
     * 本节点中的块数量
     */
    int count = 0;

    /**
     * 块索引, 如果Blocks[i] == FREE，表示该块不存在
     */
    short[] blocks = null;

    /**
     * I节点中最大块索引数目
     */
    private int capacity;

    protected MiniInode(SuperBlock info)
    {
        super(info);
        this.capacity = info.getInodeIndexCount();
        this.blocks = new short[capacity];
    }

    void init(int index)
    {
        super.init(index);
        this.count = 0;

        //Clear blocks
        System.arraycopy(FREE_BLOCKS, 0, blocks, 0, blocks.length);
    }

    protected void dele()
    {
        super.dele();

        System.arraycopy(FREE_BLOCKS, 0, blocks, 0, count);
        this.count = 0;
    }

    /**
     * 简单检查是否有效：<br>
     * count >= 0 && count <= indexCount<br>
     * checkpoint1 == checkpoint2<br>
     */
    public boolean isValid()
    {
        return super.isValid() && count >= 0 && count <= capacity;
    }

    /**
     * 返回块总数
     *
     * @return 块总数
     */
    public int count()
    {
        return count;
    }


    /**
     * 返回节点索引
     *
     * @param index 块索引
     */
    public final int getBlock(int index)
    {
        if (index < 0 || index >= count)
            throw new IllegalArgumentException("Invalid block:" + index);

        return blocks[index];
    }

    /**
     * 返回块列表
     */
    short[] getBlocks()
    {
        return blocks;
    }

    /**
     * 设置块标识
     *
     * @param index 块索引(节点中）  [0, 8)
     * @param block 块标识(文件系统中） [DataBlockOffset, blockCount)
     */
    final void setBlock(int index, short block)
    {
/*      if (index < 0 || index >= indexCount)
            throw new IllegalArgumentException("Invalid index:" + index);
        if (info.isValidDataOffset(block))
            throw new IllegalArgumentException("Invalid block offset:" + block);
*/
        blocks[index] = block;
    }

    /**
     * 添加块
     *
     * @param block 块索引
     * @param size  块大小
     */
    final void addBlock(short block, int size)
    {
        if (count >= capacity) {
            throw new IllegalStateException("Inode space overflow");
        }

        /*
        if (!info.isValidDataOffset(block))
            throw new IllegalArgumentException("Invalid block offset:" + block);
*/
        blocks[count++] = block;
        this.length += size;
    }

    public void save(Block block)
    {
        int old = block.pos();
        int pos = old;

        this.checkpoint = CheckPoint.getUnsignedByte(checkpoint);

        byte[] value = block.value;

        value[pos++] = (byte) checkpoint;
        value[pos++] = (byte) status;

        value[pos++] = (byte) ((length >> 24) & 0xFF);
        value[pos++] = (byte) ((length >> 16) & 0xFF);
        value[pos++] = (byte) ((length >> 8) & 0xFF);
        value[pos++] = (byte) ((length) & 0xFF);

        value[pos++] = (byte) ((ctime >> 24) & 0xFF);
        value[pos++] = (byte) ((ctime >> 16) & 0xFF);
        value[pos++] = (byte) ((ctime >> 8) & 0xFF);
        value[pos++] = (byte) ((ctime) & 0xFF);

        value[pos++] = (byte) (count);

        for (int i = 0; i < count; i++) {
            value[pos++] = (byte) ((blocks[i] >> 8) & 0xFF);
            value[pos++] = (byte) ((blocks[i]) & 0xFF);
        }
        pos = saveExtend(value, old, pos);
        value[pos++] = options;
        value[pos++] = (byte) checkpoint;
        block.pos = pos;
        block.newCount();
    }

    public void load(Block block)
    {
        int start = block.pos;
        checkpoint = block.readUnsignedByte();
        status = (byte)block.readUnsignedByte();
        length = block.readInt();
        ctime = block.readInt();
        count = block.readUnsignedByte();
        int c = count < 0 || count > capacity ? capacity : count;
        for (int i = 0; i < c; i++) {
            blocks[i] = block.readShort();
            if (blocks[i] == FREE) {
                count = i;
                break;
            }
        }
        loadExtend(block, start);
        options = block.readByte();
        valid = (checkpoint == block.readUnsignedByte());
    }

    /**
     * 用于I节点中的空间扩展
     * <p/>
     * 剩余可以用字节数：28字节
     */
    protected int saveExtend(byte[] value, int start, int pos)
    {
        return start + (Constants.DEFAULT_INODE_SIZE - 2);
    }

    /**
     * 用于I节点中的空间扩展
     */
    protected int loadExtend(Block block, int start)
    {
        int space = Constants.DEFAULT_INODE_SIZE
            - block.pos
            + start - 2;
        block.skipBytes(space);
        return block.pos;
    }
}
