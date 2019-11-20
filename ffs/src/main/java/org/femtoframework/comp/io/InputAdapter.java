package org.femtoframework.comp.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流与ByteInput的适配器
 *
 * @author fengyun
 * @version 1.00 Apr 28, 2002 10:50:08 PM
 */
public class InputAdapter
    extends InputStream
{
    /**
     * 数据输入
     */
    protected Channel channel;

    public InputAdapter(Channel channel)
    {
        this.channel = channel;
    }

    public int read(byte[] bytes, int off, int len)
        throws IOException
    {
        return channel.read(bytes, off, len);
    }

    public int read() throws IOException
    {
        return channel.read();
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
