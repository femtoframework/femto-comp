package org.femtoframework.comp.util;

import java.io.IOException;

import org.femtoframework.comp.io.Channel;
import org.femtoframework.comp.io.FilterChannel;

/**
 * 限制通道<br>
 * 将通道中的给定位置后的一段作为一个新的通道<br>
 */
public class LimitedChannel
    extends FilterChannel
{
    private long start;

    public LimitedChannel(Channel channel,
                          long start)
    {
        super(channel);
        this.start = start;
    }

    public long length() throws IOException
    {
        return channel.length() - start;
    }

    public long position() throws IOException
    {
        return channel.position() - start;
    }

    public void seek(long pos)
        throws IOException
    {
        channel.seek(pos + start);
    }
}

