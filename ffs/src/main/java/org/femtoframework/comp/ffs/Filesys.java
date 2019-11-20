package org.femtoframework.comp.ffs;


import java.io.IOException;

/**
 * 返回文件系统
 */
public interface Filesys
{
    /**
     * 创建文件
     *
     * @return File文件
     */
    public File create();

    /**
     * 删除文件
     *
     * @param name 文件名
     */
    public boolean delete(long name) throws IOException;

    /**
     * 返回所有文件
     *
     * @return 所有文件的列表
     */
    public abstract File[] list();

    /**
     * 根据标识返回文件
     *
     * @param name 文件名
     */
    public abstract File getFile(long name);

    /**
     * 返回最大允许文件数目
     */
    public abstract int getMaxFileCount();

    /**
     * 返回最大允许文件大小
     */
    public abstract int getMaxFileSize();

    /**
     * 返回文件总数
     */
    public abstract int getFileCount();


    /**
     * 文件系统总空间
     *
     * @return 文件系统总空间
     */
    public abstract long getSpaceSize();

    /**
     * 返回使用空间,不包括浪费的空间
     */
    public abstract long getSpaceUsed();

    /**
     * 返回可用空间,不等于总空间减去使用的空间,可用块的数目
     */
    public abstract long getSpaceAvailable();

    /**
     * 返回块大小
     */
    public abstract int getBlockSize();


    /**
     * 关闭文件系统
     */
    public abstract void close();

    /**
     * 文件系统校验
     */
    public void verify();

    /**
     * 格式化文件系统
     *
     * @param mode 格式化模式
     */
    public void format(FormatMode mode) throws IOException;
}
