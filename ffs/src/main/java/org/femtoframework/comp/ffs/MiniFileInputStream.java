package org.femtoframework.comp.ffs;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件输入流
 *
 * @author fengyun
 * @version 1.00 May 15, 2002 6:59:35 PM
 * @see org.femtoframework.comp.ffs.File
 */
public class MiniFileInputStream extends InputStream
{
    /**
     * 所在的文件系统
     */
    private MiniFilesys fs;

    /**
     * I节点
     */
    private Inode inode;

    /**
     * 文件
     */
    private MiniFile file;

    /**
     * 下一个块索引
     */
    private int nextIndex;

    /**
     * 当前数据块
     */
    private DataBlock data;

    /**
     * 当前块
     */
    private Block block;

//    /**
//     * 已经读取长度
//     */
//    private int read = 0;
//
    /**
     * 文件长度
     */
    private int length;

    /**
     * 块大小
     */
    private int blockSize;

    private int blockCount;

    private int lastBlock;

    private int currentBlockSize;

    /**
     * 构造文件输入流
     *
     * @param file 文件
     */
    MiniFileInputStream(MiniFile file)
    {
        this.file = file;
        this.fs = file.mfs;
        init();
    }

    /**
     * 初始化
     */
    private void init()
    {
        this.inode = file.inode;
        this.length = inode.length;
        this.blockSize = fs.getBlockSize();
        int shiftBits = fs.getShiftBits();
        int blockSizeMask = fs.getBlockSizeMask();
        int i = length >> shiftBits;
        int j = length & blockSizeMask;
        this.blockCount = j == 0 ? i : i + 1;
        this.lastBlock = blockCount - 1;
    }

//    private void seek() throws IOException
//    {
//        int i = read >> shiftBits;
//        int j = read & blockSizeMask;
//
//        if (i > nextIndex || data == null) {
//            //Fetch Next
//            DataBlock old = data;
//            //要去的 i 块的实际数据大小
//            int bs = i == blockCount ? length & blockSizeMask : blockSize;
//
//            block = data.getBlock();
//            nextIndex = i;
//        }
//        block.seek(j);
//    }

    public int read() throws IOException
    {
//        if (read >= length) {
//            return -1;
//        }
        if (block == null || block.isEnd()) {
            if (nextIndex < blockCount) {
                DataBlock old = data;
                currentBlockSize = nextIndex == lastBlock ? length & fs.getBlockSizeMask() : blockSize;
                data = file.readBlock(nextIndex, currentBlockSize);
                fs.recycleBlock(old);
                block = data.getBlock();
                nextIndex++;
            }
            else {
                //文件结尾
                return -1;
            }
        }
        return block.read();
    }

    public int read(byte[] bytes, int off, int len)
        throws IOException
    {
//        if ((off | len | (off + len) | (bytes.length - (off + len))) < 0) {
//            throw new IndexOutOfBoundsException();
//        }
//        else if (len == 0) {
//            return 0;
//        }

//        if (read >= length) {
//            return -1;
//        }

        if (block == null || block.isEnd()) {
            if (nextIndex < blockCount) {
                DataBlock old = data;
                currentBlockSize = nextIndex == lastBlock ? length & fs.getBlockSizeMask() : blockSize;
                data = file.readBlock(nextIndex, currentBlockSize);
                fs.recycleBlock(old);
                block = data.getBlock();
                nextIndex++;
            }
            else {
                //文件结尾
                return -1;
            }
        }

        int r = currentBlockSize < len ? currentBlockSize : len;
        return block.read(bytes, off, r);
//        int max = length - read;
//        max = len < max ? (int) len : max;
//        int r = block.read(bytes, off, max);
//        read += r;
//        return r;
    }

//    public long skip(long len) throws IOException
//    {
//        int skip = length - read;
//        skip = len < skip ? (int) len : skip;
//        read += skip;
//        return read;
//    }

    public int available() throws IOException
    {
        return length - currentBlockSize * blockSize - block.pos();
    }

    public void close() throws IOException
    {
        this.inode = null;
//        this.read = 0;
        fs.recycleBlock(data);
        this.data = null;
        this.block = null;
        this.file = null;
    }
}
