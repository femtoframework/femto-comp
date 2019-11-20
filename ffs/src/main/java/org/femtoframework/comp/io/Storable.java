package org.femtoframework.comp.io;

import java.io.IOException;

/**
 * 可以存储的<br>
 *
 * @author fengyun
 * @version 1.00 Apr 28, 2002 9:35:45 PM
 */
public interface Storable
{
    /**
     * 装载
     *
     * @param channel 存储通道
     */
    public void load(Channel channel) throws IOException;


    /**
     * 保存
     *
     * @param channel 存储通道
     */
    public void save(Channel channel) throws IOException;
}
