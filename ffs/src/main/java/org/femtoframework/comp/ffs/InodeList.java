package org.femtoframework.comp.ffs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.femtoframework.comp.io.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * I节点列表
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 4:50:20 PM
 */
public class InodeList
    implements BlockBase
{
    /**
     * Filesys
     */
    private AbstractFilesys fs;

    /**
     * SuperBlock
     */
    protected SuperBlock info;

    /**
     * 列表长度
     */
    int length;

    /**
     * I节点大小
     */
    private int inodeSize;

    /**
     * 逻辑块拥有的I节点数目
     */
    private int inodePerBlock;

    /**
     * 节点列表
     */
    private Inode[] nodes;

    /**
     * 逻辑块
     */
    private Block[] blocks;


    private boolean[] blockUpdate;

    /**
     * 逻辑块数目
     */
    private int blockCount;

    /**
     * 偏址
     */
    private transient int offset;

    /**
     * 位置
     */
    private transient long position;

    /**
     * I节点列表状态位<br>
     * true 表示该节点在使用中<br>
     * false 表示该节点Free<br>
     */
    private IFreeList status;

//    /**
//     * 逻辑块修改列表
//     */
//    private BoolSet blockUpdate;

    /**
     * 抽象头信息大小
     */
    private int headBlockSize;

    /**
     * 构造
     *
     * @param fs   Filesys
     * @param info I节点超级块
     */
    protected InodeList(AbstractFilesys fs,
                        SuperBlock info)
    {
        this.fs = fs;
        this.info = info;
        this.length = info.getMaxFileCount();
        this.inodeSize = info.getInodeSize();
        this.headBlockSize = info.getHeadBlockSize();
        this.inodePerBlock = headBlockSize / inodeSize;

        this.unit = (int)(Math.log(inodePerBlock) / Math.log(2));
        this.bitMask = inodePerBlock - 1;

        this.nodes = new Inode[length];
        this.status = new IFreeList(length);

        //XXX 如果留有小数不能这样子
        this.blockCount = (int)Math.ceil((float)(inodeSize * length) / headBlockSize);
        this.blocks = new Block[blockCount];
        this.blockUpdate = new boolean[blockCount];

        //位置
        this.offset = info.getInodeBlockOffset();
        this.position = info.position() + offset * info.getBlockSize();
    }

    /**
     * 块大小
     */
    public int size()
    {
        return length * inodeSize;
    }

    /**
     * 块在I节点区域中的偏值
     */
    public int offset()
    {
        return offset;
    }

    /**
     * 块在扇区中的位置
     */
    public long position()
    {
        return position;
    }

    /**
     * 返回I节点个数
     */
    public int getInodeCount()
    {
        return status.usedSize();
    }

    /**
     * 打开I节点<br>
     *
     * @return 如果当前节点没有被使用，则返回<code>null</code>
     */
    public Inode open(int index)
    {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException("Invalid index:" + index);
        }

        return nodes[index];
    }

    /**
     * 删除I节点
     */
    public void dele(int index) throws IOException
    {
        Inode inode = open(index);
        dele(inode);
    }

    /**
     * 删除I节点
     */
    public void dele(Inode inode) throws IOException
    {
        fs.spaceUsed -= inode.length;
        inode.dele();
        flushInode(inode);
        status.free(inode.index);
    }

    /**
     * 返回下一个可以使用的节点<br>
     * 在FreeList找一个空位，然后创建一个空闲的I节点<br>
     * 并且将I节点放到列表中去<br>
     *
     * @return 返回下一个可以使用的I节点,如果没有空闲返回<code>null</code>
     */
    public Inode allc()
    {
        int index = status.allc();
        if (index == Constants.NULL) {
            return null;
        }

        Inode inode = nodes[index];
        inode.init(index);
        inode.setStatus(Inode.STATUS_CREATING);
        return inode;
    }

    /**
     * 返回所有使用中的I节点
     */
    public Inode[] list()
    {
        List<Inode> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            if (status.isUsed(i)) {
                list.add(nodes[i]);
            }
        }
        Inode[] array = new Inode[list.size()];
        list.toArray(array);
        return array;
    }

    /**
     * I节点已经更新
     *
     * @param index I节点索引
     */
    final int updated(int index)
    {
        Inode inode = open(index);
        if (inode == null) {
            throw new IllegalArgumentException("The inode not found:" + index);
        }

        return flushInode(inode);
    }

    /**
     * I节点已经更新
     *
     * @param inode I节点
     */
    final int updated(Inode inode)
    {
        return flushInode(inode);
    }

    private static final int ADDRESS_BITS_PER_INODE = 6;

    private int unit;

    private int bitMask;

    /**
     * 返回I节点的逻辑块索引
     *
     * @param index I节点索引
     */
    int blockIndex(int index)
    {
        return index >> unit;
    }

    /**
     * 返回I节点在逻辑块中的位置
     *
     * @param index I节点索引
     */
    int positionInBlock(int index)
    {
        return (index & bitMask) << ADDRESS_BITS_PER_INODE;
    }

    /**
     * 保持同步
     */
    public void flush(Channel channel)
        throws IOException
    {
//        boolean[] bools = blockUpdate.bool;
        Block block;
        for (int i = 0; i < blockCount; i++) {
            if (blockUpdate[i]) {
                block = blocks[i];
                synchronized (block) {
                    block.save(channel);
                }
                blockUpdate[i] = false;
            }
        }
    }


    public void flush(Channel channel, int blockIndex)
        throws IOException
    {
        Block block = blocks[blockIndex];
        synchronized (block) {
            block.save(channel);
        }
        blockUpdate[blockIndex] = false;
//        blockUpdate.clear(blockIndex);
    }

    /**
     * 保存指定的I节点到缓冲区中
     *
     * @param index I节点块索引
     */
    /*    private void flushInode(int index)
        {
            Inode inode = open(index);
            if (inode == null)
                return;

            flushInode(inode);
        }
    */
    /**
     * @return Return Block Index
     */
    private int flushInode(Inode inode)
    {
        int index = inode.index;
        int blockIndex = index >> unit;
        int pos = (index & bitMask) << ADDRESS_BITS_PER_INODE;

        Block block = blocks[blockIndex];
        synchronized (block) {
            block.seek(pos);
            inode.save(block);
        }
        //Update
        blockUpdate[blockIndex] = true;
        return blockIndex;
    }

    /**
     * 保存块
     *
     * @param channel 存取通道
     */
    public synchronized void save(Channel channel)
        throws IOException
    {
        flush(channel);
    }

    /**
     * 读取块
     *
     * @param channel 存取通道
     */
    public synchronized void load(Channel channel)
        throws IOException
    {
        //装载逻辑头信息块
        long pos;
        Block block;
        Inode inode;
        int inodeIndex;
        for (int i = 0; i < blockCount; i++) {
            pos = position + headBlockSize * i;
            block = new Block(i, pos, headBlockSize);
            block.load(channel);

            //读取I节点
            inodeIndex = i * inodePerBlock;
            for (int j = 0; j < inodePerBlock; j++) {
                inode = createInode();
                inode.init(inodeIndex + j);
                inode.load(block);
                if (isAllocated(inode)) {
                    inode = null;
                }
            }

            block.reset();
            blocks[i] = block;
        }
    }

    /**
     * 创建I节点
     */
    protected Inode createInode()
    {
        return new MiniInode(info);
    }


    private static Logger log = LoggerFactory.getLogger(InodeList.class);

    /**
     * 处理I节点
     *
     * @param inode I节点
     * @return 返回该节点是否已经分配，如果分配了返回<code>true</code>
     *         否则返回<code>flase</code>
     */
    private boolean isAllocated(Inode inode)
    {
        int index = inode.index;
        if (inode.getStatus() != 0) {
            if (inode.isValid()) {
                if (inode.getStatus() == Inode.STATUS_NORMAL) {
                    status.allc(index);
                    //增加总空间
                    fs.spaceUsed += inode.length;
                }
            }
            else {
                log.warn("Invalid inode:" + inode);
            }
        }
        nodes[index] = inode;
        return true;
    }
}