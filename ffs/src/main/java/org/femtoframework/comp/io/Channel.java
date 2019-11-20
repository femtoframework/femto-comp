package org.femtoframework.comp.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 抽象数据通道
 *
 * @author fengyun
 * @version 1.00 Apr 29, 2002 10:09:58 PM
 */
public interface Channel
    extends DataInput, DataOutput
{
    /**
     * 读取byte数组
     *
     * @param bytes Byte数组
     * @param off   起始位置
     * @param len   长度
     * @return 读取长度
     */
    public int read(byte bytes[], int off, int len) throws IOException;

    /**
     * 读取byte数组
     *
     * @param b Byte数组
     * @return 读取长度
     */
    public int read(byte b[]) throws IOException;


    /**
     * 读取byte
     *
     * @return 读取的字节
     */
    public int read() throws IOException;

    /**
     * 写出byte数组
     *
     * @param bytes Byte数组
     * @param off   起始位置
     * @param len   长度
     */
    public void write(byte bytes[], int off, int len)
        throws IOException;

    /**
     * 写出byte数组
     *
     * @param b Byte数组
     */
    public void write(byte b[]) throws IOException;

    /**
     * 写出byte
     */
    public void write(int b) throws IOException;

    /**
     * 定位到指定位置
     *
     * @param offset 偏址
     */
    public void seek(long offset) throws IOException;


    /**
     * 返回当前在通道中的位置
     *
     * @return 位置
     */
    public long position() throws IOException;


    /**
     * 返回通道长度
     *
     * @return 长度
     */
    public long length() throws IOException;

    /**
     * 关闭通道
     */
    public void close() throws IOException;

    /**
     * 写无符号的字节
     *
     * @param b 字节
     */
    public void writeUnsignedByte(int b) throws IOException;

    /**
     * 写两字节的无符号整数
     *
     * @param s 无符号整数
     */
    public void writeUnsignedShort(int s) throws IOException;


}
