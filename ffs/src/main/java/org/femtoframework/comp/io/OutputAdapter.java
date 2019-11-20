package org.femtoframework.comp.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 输出流与ByteOutput的适配器
 *
 * @author fengyun
 * @version 1.00 Apr 28, 2002 10:46:38 PM
 */
public class OutputAdapter
    extends OutputStream
    implements ByteOutput
{
    /**
     * 数据输出
     */
    protected Channel channel;

    /**
     * 构造
     *
     * @param channel Channel
     */
    public OutputAdapter(Channel channel)
    {
        this.channel = channel;
    }

    /**
     * 输出字节
     *
     * @param b 字节
     */
    public void write(int b) throws IOException
    {
        channel.write(b);
    }

    /**
     * 输出byte数组
     *
     * @param bytes byte数组
     * @param off   起始位置
     * @param len   长度
     */
    public void write(byte[] bytes, int off, int len)
        throws IOException
    {
        channel.write(bytes, off, len);
    }

    /**
     * 关闭
     */
    public void close() throws IOException
    {
        channel.close();
        channel = null;
    }
}
