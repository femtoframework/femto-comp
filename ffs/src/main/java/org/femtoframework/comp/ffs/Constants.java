package org.femtoframework.comp.ffs;

/**
 * 常量集
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 10:37:36 AM
 */
public interface Constants
{
    /**
     * 用来说明，没有分配节点
     */
    public static final int NULL = Integer.MIN_VALUE + 1;

    /**
     * 默认超级块偏址
     */
    public static final int DEFAULT_SUPER_BLOCK_OFFSET = 0;

    /**
     * 默认块位图偏址
     */
    public static final int DEFAULT_BITMAP_BLOCK_OFFSET = 1;

    /**
     * 默认I节点起始偏址
     */
    public static final int DEFAULT_INODE_BLOCK_OFFSET = 2;

    /**
     * 默认数据块大小(8K)
     */
    public static final int DEFAULT_BLOCK_SIZE = 8 * 1024;

//    /**
//     * 默认头信息辑块大小(1K)
//     */
//    public static final int DEFAULT_HEAD_BLOCK_SIZE = 1024;

    /**
     * 默认头信息块数目(66)，<br>
     * 超级块1块<br>
     * 块位图1块<br>
     * I节点64块<br>
     */
    public static final int DEFAULT_HEAD_BLOCK_COUNT = 1 + 1 + 64;

    /**
     * 默认数据块数目(32K)
     */
    public static final int DEFAULT_DATA_BLOCK_COUNT = 32 * 1024;

    /**
     * 默认最大文件数(8K)
     */
    public static final int DEFAULT_MAX_FILE_COUNT = 8 * 1024;

    /**
     * 默认最大文件(64K)
     */
    public static final int DEFAULT_MAX_FILE_SIZE = 64 * 1024;

    /**
     * 默认每个I节点
     */
    public static final int DEFAULT_INODE_INDEX_COUNT = 24;

    /**
     * 默认I节点大小
     */
    public static final int DEFAULT_INODE_SIZE = 64;

//    /**
//     * Cached Blocks in file
//     */
//    public static final int DEFAULT_CACHED_BLOCKS = 2;


    public static final char SEP = java.io.File.separatorChar;


    public static final String HEAD = SEP + "head";


    /**
     * 文件系统已满
     */
    public static final int SC_INODE_LIST_OVERFLOW = 302;

    /**
     * 文件系统空间已满
     */
    public static final int SC_SPACE_OVERFLOW = 303;

    /**
     * IOException
     */
    public static final int SC_IOEXCEPTION = 401;


    /**
     * 文件找不到
     */
    public static final int SC_FILE_NOT_FOUND = 404;


    /**
     * 超级块被损坏
     */
    public static final int SC_SUPER_BLOCK_DAMAGED = 501;
}
