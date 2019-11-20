package org.femtoframework.comp.ffs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 抽象文件
 *
 * @author fengyun
 * @version 1.00 May 24, 2002 1:12:08 PM
 */
public abstract class File
{
    /**
     * 所在文件系统
     */
    protected AbstractFilesys fs;

    /**
     * 文件索引
     */
    protected int index;

    /**
     * 文件名
     */
    protected long name;

    /**
     * I节点
     */
    protected Inode inode;

    /**
     * 构造文件
     *
     * @param fs 文件系统
     */
    protected File(AbstractFilesys fs)
    {
        this.fs = fs;
    }

    /**
     * 返回文件名
     *
     * @return id 文件名
     */
    public abstract long getName();

    /**
     * 返回更新时间
     */
    public abstract int lastModified();

    /**
     * 返回文件长度
     */
    public abstract int length();

    /**
     * 判断文件是否存在
     *
     * @return 文件是否存在
     */
    public abstract boolean exists();

    /**
     * 判断是否可读
     */
    public abstract boolean canRead();

    /**
     * 判断是否可读
     */
    public abstract boolean canWrite();

    /**
     * 删除文件
     *
     * @return 是否删除
     */

    public abstract boolean delete() throws IOException;


    /**
     * 关闭文件<br>
     * 注意：使用完文件后一定要关闭<br>
     */
    public abstract void close();

    /**
     * 设置选项
     *
     * @param index 索引[0,8)
     * @param value 选项
     */
    public void setOption(int index, boolean value)
    {
        inode.setOption(index, value);
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
        return inode.getOption(index);
    }

    /**
     * 返回输入流
     */
    public abstract InputStream getInputStream()
        throws IOException;

    /**
     * 返回输出流
     */
    public abstract OutputStream getOutputStream()
        throws IOException;


    /**
     * 字符串形式
     */
    public String toString()
    {
        return String.valueOf(name);
    }
}
