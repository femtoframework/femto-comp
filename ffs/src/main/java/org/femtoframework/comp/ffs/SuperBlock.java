package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.comp.io.Channel;

/**
 * 文件系统超级块<br>
 * 用于描述块大小、块分配、特殊块等情况的特殊块<br>
 * 构造的时候按照以下顺序设置参数：<br>
 * setBlockSize setBlockCount<br>
 * setFreeListBlockOffset setInodeBlockOffset<br>
 * setDataBlockOffset<br>
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 1:15:36 PM
 */
public class SuperBlock
    implements BlockBase
{
    /**
     * 检查点1
     */
    protected int checkpoint1;

    /**
     * 块大小
     */
    protected int blockSize = Constants.DEFAULT_BLOCK_SIZE;

    /**
     * 块总数
     */
    private int blockCount = Constants.DEFAULT_DATA_BLOCK_COUNT
                             + Constants.DEFAULT_HEAD_BLOCK_COUNT;

    /**
     * 头信息逻辑块大小
     */
    protected int headBlockSize = Constants.DEFAULT_BLOCK_SIZE;

    /**
     * 块位图块偏址
     */
    private int bitMapBlockOffset
        = Constants.DEFAULT_BITMAP_BLOCK_OFFSET;

    /**
     * 块位图所占块数目
     */
    private int bitMapBlockCount;

    /**
     * 块位图的有效大小(有多少位)
     */
    private int bitMapSize = Constants.DEFAULT_DATA_BLOCK_COUNT;

    /**
     * I节点块起始偏址
     */
    protected int inodeBlockOffset;

    /**
     * I节点块数量
     */
    protected int inodeBlockCount;

    /**
     * I节点块索引数目
     */
    protected int inodeIndexCount = Constants.DEFAULT_INODE_INDEX_COUNT;

    /**
     * 允许最大文件
     */
    protected int maxFileSize = Constants.DEFAULT_MAX_FILE_SIZE;

    /**
     * 允许最大文件数目，当前系统中等于INODE数目
     */
    protected int maxFileCount = Constants.DEFAULT_MAX_FILE_COUNT;

    /**
     * 文件系统数据块起始偏址，如果头信息跟数据块绑定在<br>
     * 一个通道中，表现为在实际块中的偏址；否则从0开始。
     */
    private int dataBlockOffset = 0;

    /**
     * 文件系统数据块数量
     */
    private int dataBlockCount;

    /**
     * 数据块位置，最大长度256，用于头信息和数据不在同一通道中的情况。
     */
    private String dataChannel;

    /**
     * 在数据块中的位置
     */
    private long dataPosition;

    /**
     * 检查点2
     */
    protected int checkpoint2;

    /**
     * 构造
     */
    public SuperBlock()
    {
        this.checkpoint1 = this.checkpoint2 = CheckPoint.getInt();
    }

    //Implements BlockBase

    /**
     * 块大小
     */
    public int size()
    {
        return getBlockSize();
    }

    /**
     * 块在扇区中的偏址
     */
    public int offset()
    {
        return 0;
    }

    /**
     * 块在扇区中的位置
     */
    public long position()
    {
        return 0L;
    }

    /**
     * 返回块大小
     *
     * @return 块大小
     */
    public final int getBlockSize()
    {
        return blockSize;
    }

    /**
     * 返回头节点逻辑块大小
     */
    public int getHeadBlockSize()
    {
        return headBlockSize;
    }

    /**
     * 返回头信息占用块数目
     */
    public int getHeadBlockCount()
    {
        return blockCount - dataBlockCount;
    }

    /**
     * 设置块总数
     *
     * @param count 块总数
     */
    public void setBlockCount(int count)
    {
        this.blockCount = count;
    }

    /**
     * 返回块总数
     *
     * @return 块总数
     */
    public int getBlockCount()
    {
        return blockCount;
    }

    /**
     * 设置块位图起始偏址
     *
     * @param offset 位置
     */
    public void setBitMapBlockOffset(int offset)
    {
        this.bitMapBlockOffset = offset;
    }

    /**
     * 返回块位图起始偏址
     *
     * @return 块位图起始偏址
     */
    public int getBitMapBlockOffset()
    {
        return bitMapBlockOffset;
    }

    /**
     * 返回块位图所占块数目
     *
     * @return 块位图所占块数目
     */
    public int getBitMapBlockCount()
    {
        return bitMapBlockCount;
    }

    /**
     * 返回块位图位数
     */
    public int getBitMapSize()
    {
        return bitMapSize;
    }

    /**
     * 设置I节点块起始偏址
     *
     * @param offset 偏址
     */
    void setInodeBlockOffset(int offset)
    {
        this.inodeBlockOffset = offset;
        this.bitMapBlockCount = offset - bitMapBlockOffset;
    }

    /**
     * 设置块大小
     */
    public void setBlockSize(int size)
    {
        this.blockSize = size;
        this.headBlockSize = size;
    }

    /**
     * 设置数据块数目
     */
    public void setDataBlockCount(int count)
    {
        this.dataBlockCount = count;
        this.bitMapSize = count;
    }

    /**
     * 设置最大文件数
     */
    public void setMaxFileCount(int count)
    {
        this.maxFileCount = count;
    }

    /**
     * 设置数据通道
     */
    public void setDataChannel(String data, long pos)
    {
        this.dataChannel = data;
        this.dataPosition = pos;
    }

    /**
     * 计算参数
     */
    void compute()
    {
        this.bitMapBlockCount = (int)Math.ceil((float)bitMapSize / (8 * blockSize));
        this.inodeBlockCount
            = (int)Math.ceil((float)(maxFileCount * Constants.DEFAULT_INODE_SIZE)
                             / blockSize);

        this.inodeBlockOffset
            = bitMapBlockOffset + bitMapBlockCount;
        this.maxFileSize = blockSize * inodeIndexCount;
        this.blockCount = 1 + bitMapBlockCount + inodeBlockCount
                          + dataBlockCount;
    }


    /**
     * 返回I节点块起始偏址
     *
     * @return I节点起始偏址
     */
    public int getInodeBlockOffset()
    {
        return inodeBlockOffset;
    }

    /**
     * 返回I节点块总数
     *
     * @return I节点总数
     */
    public int getInodeBlockCount()
    {
        return inodeBlockCount;
    }

    /**
     * 返回I节点块索引数目
     *
     * @return I节点索引块数目
     */
    public int getInodeIndexCount()
    {
        return inodeIndexCount;
    }

    /**
     * 返回I节点大小
     */
    public int getInodeSize()
    {
        return Constants.DEFAULT_INODE_SIZE;
    }

    /**
     * 设置数据块起始偏址
     *
     * @param offset 偏址
     */
    void setDataBlockOffset(int offset)
    {
        this.dataBlockOffset = offset;
        this.inodeBlockCount = offset - inodeBlockOffset;
        this.dataBlockCount
            = blockCount - bitMapBlockCount
              - inodeBlockCount - 1;
    }

    /**
     * 检查是否是有效的偏址
     *
     * @param offset 有效偏址 [0, BlockCount)
     */
    public boolean isValidOffset(int offset)
    {
        return offset >= 0 && offset < blockCount;
    }

    /**
     * 检查是否是有效的数据块偏址
     *
     * @param offset 有效偏址 [DataBlockOffset, blockCount)
     */
    public boolean isValidDataOffset(int offset)
    {
        return offset >= dataBlockOffset && offset < blockCount;
    }

    /**
     * 返回数据块起始偏址
     *
     * @return 数据块起始偏址
     */
    public int getDataBlockOffset()
    {
        return dataBlockOffset;
    }

    /**
     * 返回数据块总数
     *
     * @return 数据块总数
     */
    public int getDataBlockCount()
    {
        return dataBlockCount;
    }

    /**
     * 返回最大允许文件数目
     */
    public int getMaxFileCount()
    {
        return maxFileCount;
    }

    /**
     * 返回最大允许文件大小
     */
    public int getMaxFileSize()
    {
        return maxFileSize;
    }

    /**
     * 返回数据区通道标识
     */
    public String getDataChannel()
    {
        return dataChannel;
    }

    /**
     * 返回数据区起始位置
     */
    public long getDataPosition()
    {
        return dataPosition;
    }

    /**
     * 保存超级块
     *
     * @param channel 通道
     */
    public void save(Channel channel)
        throws IOException
    {
        channel.writeInt(checkpoint1);
        channel.writeInt(blockSize);
        channel.writeInt(blockCount);
        channel.writeInt(headBlockSize);
        channel.writeInt(bitMapBlockOffset);
        channel.writeInt(bitMapBlockCount);
        channel.writeInt(bitMapSize);
        channel.writeInt(inodeBlockOffset);
        channel.writeInt(inodeBlockCount);
        channel.writeInt(inodeIndexCount);
        channel.writeInt(maxFileCount);
        channel.writeInt(maxFileSize);
        channel.writeInt(dataBlockOffset);
        channel.writeInt(dataBlockCount);
        channel.writeUTF(dataChannel);
        channel.writeLong(dataPosition);
        channel.writeInt(checkpoint2);
    }

    /**
     * 读取超级块
     *
     * @param channel 通道
     */
    public void load(Channel channel)
        throws IOException
    {
        checkpoint1 = channel.readInt();
        blockSize = channel.readInt();
        blockCount = channel.readInt();
        headBlockSize = channel.readInt();
        bitMapBlockOffset = channel.readInt();
        bitMapBlockCount = channel.readInt();
        bitMapSize = channel.readInt();
        inodeBlockOffset = channel.readInt();
        inodeBlockCount = channel.readInt();
        inodeIndexCount = channel.readInt();
        maxFileCount = channel.readInt();
        maxFileSize = channel.readInt();
        dataBlockOffset = channel.readInt();
        dataBlockCount = channel.readInt();
        dataChannel = channel.readUTF();
        dataPosition = channel.readLong();
        checkpoint2 = channel.readInt();

        if (checkpoint1 != checkpoint2) {
            throw new FilesysVerifyException("Super block is demage");
        }
    }

    private transient String str;

    public String toString()
    {
        if (str == null) {
            StringBuilder sb = new StringBuilder(64);
            sb.append("SuperBlock[\n");
            sb.append("BlockSize:").append(blockSize).append("\n");
            sb.append("BlockCount:").append(blockCount).append("\n");
            sb.append("MaxFileCount:").append(maxFileCount).append("\n");
            sb.append("MaxFileSize:").append(maxFileSize).append("\n");
            sb.append(']');
            str = sb.toString();
        }
        return str;
    }
}

