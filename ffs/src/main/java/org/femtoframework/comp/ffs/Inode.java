package org.femtoframework.comp.ffs;


import org.femtoframework.comp.util.SystemTime;

/**
 * I节点(Index Node)
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 10:29:39 AM
 */
public abstract class Inode
{
    protected SuperBlock info;

    /**
     * I节点头状态位
     * (1Byte)
     */
    byte status = STATUS_FREE;

    /**
     * 空闲状态
     */
    public static final byte STATUS_FREE = 0;
    /**
     * 创建中
     */
    public static final byte STATUS_CREATING = 3;

    /**
     * 使用中
     */
    public static final byte STATUS_NORMAL = 4;

    /**
     * 删除中
     */
    public static final byte STATUS_DELETING = 5;

    /**
     * 已经删除
     */
    public static final byte STATUS_DELETED = 8;

    /**
     * 节点长度(小于2G 文件大小）
     * (4Byte)
     */
    int length;

    /**
     * 创建时间，单位是秒
     * (4Byte)
     */
    int ctime;

    /**
     * 检查是否有效
     */
    boolean valid = true;

    /**
     * 检查点
     * (1Byte)
     */
    int checkpoint;

    /**
     * I节点块在I节点区域中的索引
     */
    int index;


    /**
     * 选项
     */
    byte options;

    /**
     * 设置选项
     *
     * @param index 索引[0,8)
     * @param value 选项
     */
    public void setOption(int index, boolean value)
    {
        byte bit = (byte) (1 << (index & 0x7));
        if (value) {
            options |= bit;
        }
        else {
            options &= ~bit;
        }
    }

    /**
     * 返回选项的值，选项是可以在INODE中增加一些额外的Boolean信息，<br>
     * 用于应用层的扩展
     *
     * @param index 索引[0,8)
     * @return 选项
     */
    public boolean getOption(int index)
    {
        byte bit = (byte) (1 << (index & 0x7));
        return (options & bit) != 0;
    }


    protected Inode(SuperBlock info)
    {
        this.info = info;
    }

    /**
     * 初始化I节点
     *
     * @param index I节点索引
     */
    void init(int index)
    {
        this.index = index;
        this.status = STATUS_FREE;
        this.length = 0;
        this.ctime = SystemTime.getSecond();
        this.checkpoint = CheckPoint.getUnsignedByte();
    }

    /**
     * 简单检查是否有效：<br>
     * checkpoint1 == checkpoint2<br>
     */
    public boolean isValid()
    {
        return valid;
    }


    /**
     * 返回I节点状态
     */
    public int getStatus()
    {
        return status;
    }

    /**
     * 设置I节点状态
     */
    public void setStatus(byte status)
    {
        this.status = status;
    }

    protected void dele()
    {
        setStatus(STATUS_DELETED);
        this.options = 0;
        this.length = 0;
    }

    /**
     * 返回文件长度
     */
    public int length()
    {
        return length;
    }

    /**
     * 设置文件长度
     */
    void setLength(int length)
    {
        this.length = length;
    }

    /**
     * 更新创建时间
     */
    public void updateCTime()
    {
        this.ctime = SystemTime.getSecond();
    }

    /**
     * 返回创建时间
     */
    public int getCTime()
    {
        return ctime;
    }

    /**
     * 块大小
     */
    public int size()
    {
        return info.getInodeSize();
    }

    /**
     * 块在Inode空间中的索引
     */
    public int index()
    {
        return index;
    }

    /**
     * 保存节点
     *
     * @param block 块
     */
    public abstract void save(Block block);

    /**
     * 读取节点
     *
     * @param block 块
     */
    public abstract void load(Block block);
}