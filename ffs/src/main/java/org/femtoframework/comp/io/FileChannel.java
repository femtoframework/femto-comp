package org.femtoframework.comp.io;

import org.femtoframework.lang.Binary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 文件存取通道
 *
 * @author fengyun
 * @version 1.00
 * @see RandomAccessFile
 */
public class FileChannel
    extends RandomAccessFile
    implements Channel
{
    /**
     * 构造文件存取通道
     *
     * @param file 文件
     * @param mode 模式
     */
    public FileChannel(String file, String mode)
        throws FileNotFoundException
    {
        super(file, mode);
    }

    /**
     * 构造文件存取通道
     *
     * @param file 文件
     * @param mode 模式
     */
    public FileChannel(File file, String mode)
        throws FileNotFoundException
    {
        super(file, mode);
    }

//    /**
//     * Writes <code>b.length</code> bytes from the specified byte array
//     * to this file, starting at the current file pointer.
//     *
//     * @param b the data.
//     * @throws IOException if an I/O error occurs.
//     */
//    public void write(byte b[]) throws IOException
//    {
//        super.write(b);
//    }
//
//    /**
//     * Writes <code>len</code> bytes from the specified byte array
//     * starting at offset <code>off</code> to this file.
//     *
//     * @param b   the data.
//     * @param off the start offset in the data.
//     * @param len the number of bytes to write.
//     * @throws IOException if an I/O error occurs.
//     */
//    public void write(byte b[], int off, int len) throws IOException
//    {
//        super.write(b, off, len);
//    }

    /**
     * 返回当前在通道中的位置
     *
     * @return 偏址
     */
    public long position() throws IOException
    {
        return getFilePointer();
    }

    /**
     * 写无符号的字节
     *
     * @param b 字节
     */
    public void writeUnsignedByte(int b) throws IOException
    {
        write(b);
    }

    /**
     * 写两字节的无符号整数
     *
     * @param s 无符号整数
     */
    public void writeUnsignedShort(int s)
        throws IOException
    {
        byte[] bytes = Binary.toShortBytes(s);
        write(bytes);
    }

    /**
     * Ensures that the <code>close</code> method of this file input stream is
     * called when there are no more references to it.
     *
     * @throws IOException if an I/O error occurs.
     * @see org.femtoframework.comp.io.FileChannel#close()
     */
    protected void finalize() throws IOException
    {
        close();
        try {
            super.finalize();
        }
        catch (Throwable throwable) {
        }
    }

}
