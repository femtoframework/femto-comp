package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.comp.io.Channel;
import org.femtoframework.comp.io.Storable;

/**
 * 块的基本定义<br>
 * <p/>
 * offset 是块偏址<br>
 * position 是实际以字节为单位的位置
 *
 * @author fengyun
 * @version 1.00 Apr 30, 2002 2:11:17 PM
 */
public interface BlockBase
    extends Storable
{
    /**
     * 块大小
     */
    public int size();

    /**
     * 块在扇区中的偏址
     */
    public int offset();

    /**
     * 块在扇区中的位置
     */
    public long position();

    /**
     * 保存块
     *
     * @param channel 存取通道
     */
    public void save(Channel channel)
        throws IOException;

    /**
     * 读取块
     *
     * @param channel 存取通道
     */
    public void load(Channel channel)
        throws IOException;
}
