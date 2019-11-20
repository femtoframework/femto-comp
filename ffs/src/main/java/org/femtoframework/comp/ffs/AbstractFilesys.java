package org.femtoframework.comp.ffs;

import org.femtoframework.bean.InitializableMBean;

import java.io.IOException;


/**
 * 抽象文件系统
 *
 * @author fengyun
 * @version 1.00 May 24, 2002 1:36:29 PM
 */
public abstract class AbstractFilesys
    implements Filesys, InitializableMBean
{
    /**
     * 服务器标识
     */
    protected int sid;

    /**
     * 文件系统标识
     */
    protected int fsid;

    /**
     * 超级块
     */
    protected SuperBlock info;

    /**
     * I节点列表
     */
    protected InodeList inodeList;

//    /**
//     * 文件列表
//     */
//    protected FileList fileList;

    /**
     * int BlockSize
     */
    private int blockSize;

    /**
     * 块Mask
     */
    private int blockSizeMask;

    /**
     * 位移量
     */
    private int shiftBits;

    /**
     * 构造
     *
     * @param sid  服务器标识
     * @param fsid 文件系统标识
     */
    protected AbstractFilesys(int sid,
                              int fsid)
    {
        super();
        this.sid = sid;
        this.fsid = fsid;
    }

    /**
     * 设置新的文件系统标识
     *
     * @param id 文件系统标识
     */
    protected void resetId(int id)
    {
        this.fsid = id;
    }


    private boolean initialized = false;

    /**
     * Return whether it is initialized
     *
     * @return whether it is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }


    /**
     * Initialized setter for internal
     *
     * @param initialized BeanPhase
     */
    public void _doSetInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    /**
     * 创建文件
     *
     * @return File文件
     */
    public File create()
    {
        Inode inode = inodeList.allc();
        if (inode == null) {
            throw new IllegalStateException("No room for storing this file");
        }

        return allocateFile(inode);
    }


    /**
     * 根据标识返回文件
     *
     * @param name 文件名
     */
    public File getFile(long name)
    {
        int index = (int)(name & 0xFFFFL);
        Inode inode = inodeList.open(index);
        if (inode == null) {
            return null;
        }
        return allocateFile(inode);
    }

    /**
     * 返回所有文件
     *
     * @return 所有文件的列表
     */
    public File[] list()
    {
        Inode[] nodes = inodeList.list();
        File[] list = new File[nodes.length];
        File file;
        for (int i = 0; i < nodes.length; i++) {
            file = allocateFile(nodes[i]);
            list[i] = file;
        }
        return list;
    }

    /**
     * 总空间
     */
    protected long spaceSize;

    /**
     * 使用空间
     */
    protected long spaceUsed;

    /**
     * 文件系统大小
     *
     * @return 文件系统大小
     */
    public long getSpaceSize()
    {
        return spaceSize;
    }

    /**
     * 返回使用空间
     */
    public long getSpaceUsed()
    {
        return spaceUsed;
    }

    /**
     * 返回可用空间
     */
    public long getSpaceAvailable()
    {
        return spaceSize - spaceUsed;
    }

    /**
     * 返回最大允许文件数目
     */
    public int getMaxFileCount()
    {
        return info.getMaxFileCount();
    }

    /**
     * 返回最大允许文件大小
     */
    public int getMaxFileSize()
    {
        return info.getMaxFileSize();
    }

    /**
     * 返回文件总数
     */
    public int getFileCount()
    {
        return inodeList.getInodeCount();
    }

    /**
     * 返回块大小
     */
    public final int getBlockSize()
    {
        return blockSize;
    }

    /**
     * 关闭文件,保存文件节点
     */
    protected abstract void syncFile(File file) throws IOException;

    /**
     * Close File
     */
    protected abstract void closeFile(File file);

    /**
     * Allocate File
     */
    protected abstract File allocateFile(Inode inode);


    /**
     * 关闭文件系统
     */
    public void close() {
    }

    public void setBlockSize(int blockSize)
    {
        this.blockSize = blockSize;
        this.blockSizeMask = blockSize - 1;
        this.shiftBits = ln(blockSize);
    }

    protected static int ln(int blockSize)
    {
        int b = blockSize;
        int c = 0;
        while (true) {
            if ((b & 0x02) == 0) {
                c++;
                b >>= 1;
            }
            else {
                break;
            }
        }
        return c;
    }

    public int getBlockSizeMask()
    {
        return blockSizeMask;
    }

    public int getShiftBits()
    {
        return shiftBits;
    }


    @Override
    public void _doInit() {

    }
}
