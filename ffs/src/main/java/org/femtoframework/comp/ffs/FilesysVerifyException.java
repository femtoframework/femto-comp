package org.femtoframework.comp.ffs;

/**
 * 文件系统校验异常
 *
 * @author fengyun
 * @version 1.00 May 13, 2002 5:04:12 PM
 */
public class FilesysVerifyException
    extends RuntimeException
{
    /**
     * 构造文件系统校验异常
     *
     * @param message    消息
     */
    public FilesysVerifyException(String message)
    {
        super(message);
    }
}
