package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.comp.io.FileChannel;

/**
 * 创建简单文件系统
 *
 * @author fengyun
 * @version 1.00 May 16, 2002 6:52:15 PM
 */
public class MiniMakeFilesys
    implements MakeFilesys
{
    protected SuperBlock info;

    protected String head;

    protected long position;

    public MiniMakeFilesys(SuperBlock info, String head)
    {
        this(info, head, 0L);
    }

    public MiniMakeFilesys(SuperBlock info, String head, long position)
    {
        this.info = info;
        this.head = head;
        this.position = position;
    }


    /**
     * 创建文件系统
     */
    public void make() throws IOException
    {
        make(false);
    }

    /**
     * 是否仅仅清除数据
     */
    public void make(boolean fast) throws IOException
    {
        info.compute();

        FileChannel headChannel = new FileChannel(head, "rw");
        headChannel.seek(position);

        FileChannel dataChannel;
        String data = info.getDataChannel();
        if (data != null) {
            dataChannel = new FileChannel(data, "rw");
            dataChannel.seek(info.position());
        }
        else {
            throw new IllegalArgumentException("info.dataChannel==null");
        }

        //格式化
        byte[] block = new byte[info.getBlockSize()];
        int count = info.getHeadBlockCount();
        //headChannel.setLength(count * info.getBlockSize());
        for (int i = 0; i < count; i++) {
            headChannel.write(block);
        }

        count = info.getDataBlockCount();

        if (!fast) {
            for (int i = 0; i < count; i++) {
                dataChannel.write(block);
            }
        }

        //写超级块
        headChannel.seek(position);
        info.save(headChannel);

        //关闭文件
        headChannel.close();
        dataChannel.close();
    }

    /**
     * 设置块大小
     */
    public void setBlockSize(int size)
    {
        info.setBlockSize(size);
    }

    /**
     * 设置数据块数目
     */
    public void setDataBlockCount(int count)
    {
        info.setDataBlockCount(count);
    }

    /**
     * 设置最大文件数
     */
    public void setMaxFileCount(int count)
    {
        info.setMaxFileCount(count);
    }

    /**
     * 设置数据通道
     */
    public void setDataChannel(String data, long pos)
    {
        info.setDataChannel(data, pos);
    }

//    /**
//     * 创建文件系统
//     */
//    public static void main(String[] args) throws Exception
//    {
//        if (args.length != 1) {
//            System.out.println("Usage: java org.femtoframework.comp.ffs.MiniMakeFilesys config");
//            System.out.println("Config file like this:");
//            System.out.println("##############################################");
//            System.out.println("naisa.ares.ffs.mini.head=/queue/0/0/head");
//            System.out.println("naisa.ares.ffs.mini.head_position=0");
//            System.out.println("naisa.ares.ffs.mini.block_size=4096");
//            System.out.println("naisa.ares.ffs.mini.data_block_count=32768");
//            System.out.println("naisa.ares.ffs.mini.max_file_count=4096");
//            System.out.println("naisa.ares.ffs.mini.data=/queue/0/0/data");
//            System.out.println("naisa.ares.ffs.mini.data_position=0");
//            System.out.println("##############################################");
//            System.exit(0);
//        }
//
//        Config config = Main.loadConfig(args[0]);
//        Config next = config.getConfig("naisa.ares.ffs.mini");
//        make(next);
//    }

//    static void make(Parameters next) throws IOException
//    {
//        String head = next.getString("head");
//        long headPosition = next.getLong("head_position", 0L);
//        int blockSize = next.getInt("block_size", Constants.DEFAULT_BLOCK_SIZE);
//        int maxFileCount = next.getInt("max_file_count",
//            Constants.DEFAULT_MAX_FILE_COUNT);
//        int dataBlockCount = next.getInt("data_block_count",
//            Constants.DEFAULT_DATA_BLOCK_COUNT);
//        String data = next.getString("data");
//        long dataPosition = next.getLong("data_position", 0L);
//
//        SuperBlock info = new SuperBlock();
//        MiniMakeFilesys mkfs = new MiniMakeFilesys(info, head,
//            headPosition);
//        mkfs.setBlockSize(blockSize);
//        mkfs.setDataBlockCount(dataBlockCount);
//        mkfs.setMaxFileCount(maxFileCount);
//        mkfs.setDataChannel(data, dataPosition);
//        mkfs.make();
//    }
}
