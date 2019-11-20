package org.femtoframework.comp.ffs;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 文件输出流
 *
 * @author fengyun
 * @version 1.00 May 15, 2002 6:59:44 PM
 */
public class MiniFileOutputStream extends OutputStream {
    /**
     * 所在的文件系统
     */
    private MiniFilesys fs;

//    /**
//     * I节点
//     */
//    private Inode inode;

    /**
     * 文件
     */
    private MiniFile file;

    /**
     * 当前块在I节点中的索引
     */
    private int index = 0;

    /**
     * 当前数据块
     */
    private DataBlock data;

    /**
     * 当前块
     */
    private Block block;

    /**
     * 当前位置
     */
    //private int position = 0;

    /**
     * 块大小
     */
    private int blockSize;

    /**
     * 当前块是否被更新
     */
    private boolean updated = false;

//    /**
//     * 是否Append
//     */
//    private boolean append = true;

    /**
     * 构造文件输出流
     *
     * @param file 文件
     */
    MiniFileOutputStream(MiniFile file) {
        this.file = file;
        this.fs = file.mfs;
        init();
    }

    /**
     * 初始化
     */
    private final void init() {
//        this.inode = file.inode;
        this.blockSize = fs.getBlockSize();
//        this.append = inode.length == 0;
        initBlock();
    }

    private final void initBlock() {
//        if (append) {
        this.data = fs.allocateBlock();
        this.data.index = index++;
//        }
//        else {
//            this.data = file.readBlock(index++);
//        }
        this.block = data.getBlock();
    }

    public final void write(int b) throws IOException {
        write(new byte[]{(byte)b});
    }

    public final void write(byte[] bytes) throws IOException {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int off, int len)
        throws IOException {
        if ((off | len | (off + len) | (bytes.length - (off + len))) < 0) {
            throw new IndexOutOfBoundsException();
        }
        else if (len == 0) {
            return;
        }

        int left = blockSize - block.pos;
        if (len < left) {
            write1(bytes, off, len, false);
        }
        else if (len == left) {
            write1(bytes, off, len, true);
        }
        else {
            write1(bytes, off, left, true);
            off += left;
            len -= left;
            int w;
            boolean flush;
            while (len > 0) {
                flush = len >= blockSize;
                w = flush ? blockSize : len;
                write1(bytes, off, w, flush);
                off += w;
                len -= w;
            }
        }
    }

    /**
     * 写大小与块大小相同的
     */
    private final void write1(byte[] bytes,
                              int off, int len,
                              boolean flush)
        throws IOException {
        block.write(bytes, off, len);
        //position += len;
        updated = true;
        if (flush) {
            flushBlock(true);
        }
    }

    private void flushBlock(boolean fetch)
        throws IOException {
        if (updated) {
            file.writeBlock(data);
            updated = false;
            if (block.pos == block.capacity && fetch) {
                data.setOffset(Constants.NULL);
                data.index = index++;
                block.reset();
            }
        }
    }

    public final void flush()
        throws IOException {
        flushBlock(true);
    }

    public final void close()
        throws IOException {
        flushBlock(false);

        fs.syncFile(file);
        fs.recycleBlock(data);

//        this.inode = null;
        this.data = null;
        this.block = null;
        this.file = null;
    }
}
