package org.femtoframework.comp.io;

import java.io.IOException;

/**
 * 字节输入
 *
 * @author fengyun
 * @version 1.00 2005-5-17 22:12:00
 */
public interface ByteInput
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
}
