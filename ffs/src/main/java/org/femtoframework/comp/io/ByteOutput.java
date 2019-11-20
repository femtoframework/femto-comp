package org.femtoframework.comp.io;

import java.io.IOException;

/**
 * 字节输出
 * 
 * @author fengyun
 * @version 1.00 2005-5-17 22:08:37
 */
public interface ByteOutput
{
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
}
