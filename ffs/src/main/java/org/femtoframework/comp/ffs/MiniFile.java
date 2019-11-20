package org.femtoframework.comp.ffs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MiniFile extends File
{
    /**
     * 迷你文件系统
     */
    MiniFilesys mfs;

    protected MiniFile(MiniFilesys fs)
    {
        super(fs);
        this.mfs = fs;
    }

    /**
     * 构造文件
     */
    protected void init(Inode inode)
    {
        this.inode = inode;
        this.index = inode.index;
        this.name = (((long) fs.sid) << 36) | (((long) fs.fsid) << 24) | index;
    }

    /**
     * 返回文件名
     *
     * @return id 文件名
     */
    public long getName()
    {
        return name;
    }

    /**
     * 返回创建时间
     */
    public int lastModified()
    {
        return inode.getCTime();
    }

    /**
     * 返回文件长度
     */
    public int length()
    {
        return inode.length;
    }

    /**
     * 判断文件是否存在
     *
     * @return 文件是否存在
     */
    public boolean exists()
    {
        return inode != null;
    }

    /**
     * 判断是否可读
     */
    public boolean canRead()
    {
        return exists() && inode.getStatus() == Inode.STATUS_NORMAL;
    }

    /**
     * 判断是否可读
     */
    public boolean canWrite()
    {
        return canRead();
    }

    /**
     * 删除文件
     *
     * @return 是否删除
     */

    public boolean delete() throws IOException
    {
        boolean deleted = fs.delete(getName());
        if (deleted) {
            inode = null;
        }
        return deleted;
    }

    /**
     * 获取文件块
     *
     * @param index 文件块索引（在该文件所有块中的索引）
     */
    DataBlock readBlock(int index) throws IOException
    {
        DataBlock data = mfs.readBlock(this, index);
        data.index = index;
        return data;
    }

    /**
     * 获取文件块
     *
     * @param index 文件块索引（在该文件所有块中的索引）
     */
    DataBlock readBlock(int index, int blockSize) throws IOException
    {
        DataBlock data = mfs.readBlock(this, index, blockSize);
        data.index = index;
        return data;
    }

    /**
     * 写入文件块
     */
    void writeBlock(DataBlock data)
        throws IOException
    {
        mfs.writeBlock(this, data);
    }

    public void close()
    {
        if (inode != null) {
            fs.closeFile(this);
            inode = null;
        }
    }

    public InputStream getInputStream()
        throws IOException
    {
        return new MiniFileInputStream(this);
    }

    public OutputStream getOutputStream()
        throws IOException
    {
        return new MiniFileOutputStream(this);
    }
}