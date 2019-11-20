package org.femtoframework.comp.ffs;

import java.io.IOException;

/**
 * 创建文件系统
 *
 * @author fengyun
 * @version 1.00 May 16, 2002 6:47:06 PM
 */
public interface MakeFilesys
{
    /**
     * 创建文件系统
     */
    public void make() throws IOException;

    /**
     * 是否仅仅清除数据
     */
    public void make(boolean fast) throws IOException;
}
