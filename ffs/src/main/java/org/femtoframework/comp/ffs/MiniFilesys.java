package org.femtoframework.comp.ffs;

import java.io.IOException;

import org.femtoframework.bean.exception.InitializeException;
import org.femtoframework.comp.io.Channel;
import org.femtoframework.comp.io.FileChannel;
import org.femtoframework.comp.util.LimitedChannel;
import org.femtoframework.util.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 单个文件的迷你文件系统
 */
public class MiniFilesys
    extends AbstractFilesys
{
    /**
     * 文件系统所在路径
     */
    private String path;

    /**
     * 头文件
     */
    protected String head;

    /**
     * 头信息区写通道
     */
    Channel headWriteChannel = null;

    /**
     * 数据区读通道
     */
    private Channel dataReadChannel = null;

    /**
     * 数据区写通道
     */
    private Channel dataWriteChannel = null;

    /**
     * 块位图
     */
    protected BitMap bitMap;

    /**
     * 空闲列表
     */
    protected FreeList freeList;

    /**
     * Logger
     */
    protected Logger log;

    /**
     * 构造迷你文件系统
     *
     * @param sid  服务器标识
     * @param fsid 文件系统标识
     * @param path 头信息文件
     */
    public MiniFilesys(int sid,
                       int fsid,
                       String path)
    {
        super(sid, fsid);
        this.log = LoggerFactory.getLogger("ffs/mini");
        this.path = path;
        resetId(fsid);
    }

    /**
     * 设置新的文件系统标识
     *
     * @param id 文件系统标识
     */
    protected void resetId(int id)
    {
        super.resetId(id);
        if (path != null) {
            this.head = path + Constants.HEAD;
        }
    }

    protected void initHead() throws IOException
    {
        Channel headReadChannel;
        try {
            headReadChannel = new FileChannel(head, "r");
            headWriteChannel = new FileChannel(head, "rw");
        }
        catch (IOException ioe) {
            log.error("Loading head file fatal", ioe);
            throw ioe;
        }

        loadSuperBlock(headReadChannel);
        loadBitMap(headReadChannel);
        loadInodeList(headReadChannel);

        try {
            headReadChannel.close();
        }
        catch (IOException ioe) {
            log.warn("Closing head file exception", ioe);
        }
    }


    protected void initData() throws IOException
    {
        String data = info.getDataChannel();
        if (data == null) {
            data = head;
        }

        dataReadChannel = new FileChannel(data, "r");
        dataWriteChannel = new FileChannel(data, "rw");
        long pos = info.getDataPosition();
        if (pos > 0) {
            dataReadChannel = new LimitedChannel(dataReadChannel, pos);
            dataWriteChannel = new LimitedChannel(dataWriteChannel, pos);
        }
    }

    protected MiniFile createFile()
    {
        return new MiniFile(this);
    }

    private static final int INIT_OBJECT_SIZE = 8;
    private static final int MAX_OBJECT_SIZE = 16;

    protected void initMemory()
    {
        //Add BLocks
        DataBlock data;
        for (int i = 0; i < INIT_OBJECT_SIZE; i++) {
            data = new DataBlock(getBlockSize());
            pool1.put(data);
        }
    }

    /**
     * 装载超级块
     */
    protected void loadSuperBlock(Channel channel)
        throws IOException
    {
        info = new SuperBlock();
        try {
            info.load(channel);
        }
        catch (IOException ioe) {
            log.error("Super block malformed", ioe);
            throw ioe;
        }

        int blockSize = info.getBlockSize();
        this.spaceSize = info.getDataBlockCount() * blockSize;
        this.spaceUsed = 0;
        setBlockSize(blockSize);

        if (log.isDebugEnabled()) {
            log.debug(info.toString());
        }
    }

    protected void loadBitMap(Channel channel)
        throws IOException
    {
        bitMap = new BitMap(info.getBitMapBlockOffset() * info.getBlockSize(),
            info.getBitMapSize());
        try {
            bitMap.load(channel);
        }
        catch (IOException ioe) {
            log.warn("Loading bit map error", ioe);
            throw ioe;
        }
    }

    protected void saveBitMap()
        throws IOException
    {
        try {
            bitMap.save(headWriteChannel);
        }
        catch (IOException ioe) {
            log.warn("Saving bit map error", ioe);
            throw ioe;
        }
    }

    protected void loadInodeList(Channel channel)
        throws IOException
    {
        int off = info.getDataBlockOffset();
        freeList = new FreeList(bitMap, off, off + info.getDataBlockCount());

        inodeList = createInodeList();
        try {
            inodeList.load(channel);
        }
        catch (IOException ioe) {
            log.error("Loading inode list error", ioe);
            throw ioe;
        }

//        fileList = new FileList(inodeList.length);

        if (log.isDebugEnabled()) {
            log.debug(bitMap.toString());
        }
    }

    /**
     * 关闭文件系统
     */
    public void close()
    {
        if (headWriteChannel != null) {
            try {
                headWriteChannel.close();
            }
            catch (IOException e) {
            }
        }

        if (dataReadChannel != null) {
            try {
                dataReadChannel.close();
            }
            catch (IOException e) {
            }
        }

        if (dataWriteChannel != null) {
            try {
                dataWriteChannel.close();
            }
            catch (IOException e) {
            }
        }

//  超级块保留
//        info = null;
        inodeList = null;
        bitMap = null;
        freeList = null;

        //回收内存
        while (pool1.get() != null) {
        }
    }

    protected InodeList createInodeList()
    {
        return new InodeList(this, info);
    }


    /**
     * 文件系统校验
     */
    public void verify()
    {
        if (log.isInfoEnabled()) {
            log.info("Verify queue file system: sid=" + sid + " fsid=" + fsid);
        }
        int size = info.getBitMapSize();
        BoolSet blockMap = new BoolSet(size);
        checkBlockBitMap(blockMap, size);
    }

    /**
     * 格式化文件系统
     *
     * @param mode 格式化模式
     */
    public void format(FormatMode mode) throws IOException
    {
        if (info == null) {
            throw new IllegalStateException("No super block information");
        }

        MiniMakeFilesys mmfs = new MiniMakeFilesys(info, head, 0);
        mmfs.make(mode == FormatMode.FAST);
    }


    private void checkBlockBitMap(BoolSet blockMap, int size)
    {
        //列出所有使用中的I节点
        Inode[] inodes = inodeList.list();
        MiniInode inode;
        int count;
        int block;
        for (Inode inode1 : inodes) {
            inode = (MiniInode)inode1;
            count = inode.count();
            for (int j = 0; j < count; j++) {
                block = inode.getBlock(j);
                blockMap.set(block);
            }
        }

        boolean verify;
        boolean bit;
        boolean hasBad = false;
        for (int i = 0; i < size; i++) {
            verify = blockMap.get(i);
            bit = bitMap.get(i);
            if (bit != verify) {
                hasBad = true;
                if (log.isInfoEnabled()) {
                    log.info("Bad block index:" + i);
                }
                if (verify) {
                    bitMap.set(i);
                }
                else {
                    bitMap.clear(i);
                }
            }
        }

        if (hasBad) {
            try {
                saveBitMap();
            }
            catch (Exception e) {
                log.warn("Exception", e);
            }
        }
    }

    /**
     * 删除文件
     *
     * @param name 文件名
     */
    public boolean delete(long name) throws IOException
    {
        MiniInode inode;
        int index = (int)(name & 0xFFFFL);
        //删除文件
        inode = (MiniInode)inodeList.open(index);

        //释放空间
        short[] blocks = inode.getBlocks();
        int count = inode.count();
        for (int i = 0; i < count; i++) {
            bitMap.clear(blocks[i]);
        }
        //回收节点
            inodeList.dele(inode);
            doSync(inode);

        return true;
    }

    /**
     * 返回可用空间
     */
    public long getSpaceAvailable()
    {
        return freeList.freeSize() * info.getBlockSize();
    }

    /**
     * 读取指定数据块
     */
    protected DataBlock readBlock(MiniFile file, int index)
        throws IOException
    {
        int block = ((MiniInode)file.inode).getBlock(index);
        DataBlock db = allocateBlock();
        db.setOffset(block);
        synchronized (dataReadChannel) {
            db.load(dataReadChannel);
        }
        return db;
    }

    /**
     * 读取指定数据块
     */
    protected DataBlock readBlock(MiniFile file, int index, int blockSize)
        throws IOException
    {
        int block = ((MiniInode)file.inode).getBlock(index);
        DataBlock db = allocateBlock();
        db.setOffset(block);
        synchronized (dataReadChannel) {
                db.load(dataReadChannel, blockSize);
        }
        return db;
    }

    /**
     * 写入指定文件块
     */
    protected void writeBlock(MiniFile file, DataBlock block)
        throws IOException
    {
        int offset = block.offset();
        if (offset == Constants.NULL) {
            //New Block append
            appendBlock(file, block);
        }
        else {
            writeBlock0(block);
        }
    }

    private void writeBlock0(DataBlock block)
        throws IOException
    {
            synchronized (dataWriteChannel) {
                block.save(dataWriteChannel);
            }
    }

    /**
     * 追加文件块
     */
    protected void appendBlock(MiniFile file, DataBlock block)
        throws IOException
    {
        int offset = freeList.allc();
        if (offset == Constants.NULL) {
            throw new IOException("Space overflow");
        }
        block.setOffset(offset);
        block.newCount();
        Inode inode = file.inode;
        ((MiniInode)inode).addBlock((short)offset, block.size());
        writeBlock0(block);
    }

    /**
     * 关闭文件,保存文件节点
     */
    protected void syncFile(File file)
        throws IOException
    {
        Inode inode = file.inode;
        if (inode.getStatus() == Inode.STATUS_CREATING) {
            inode.setStatus(Inode.STATUS_NORMAL);
        }
        doSync(inode);
    }

    /**
     * 同步文件节点到硬盘
     *
     * @param inode I节点
     */
    protected void doSync(Inode inode)
        throws IOException
    {
            int index = inodeList.updated(inode);
            synchronized (headWriteChannel) {
                bitMap.flush(headWriteChannel);
                inodeList.flush(headWriteChannel, index);
            }
    }

    protected void closeFile(File file)
    {
    }


    private final ObjectPool<DataBlock> pool1 = new ObjectPool<>(MAX_OBJECT_SIZE);

    /**
     * 返回一个数据块
     */
    protected final DataBlock allocateBlock()
    {
        DataBlock data = pool1.get();
        if (data == null) {
            data = new DataBlock(getBlockSize());
        }
        else {
            data.setOffset(Constants.NULL);
            data.reset();
        }

        return data;
    }

    /**
     * 回收一个数据块
     */
    protected final void recycleBlock(DataBlock data)
    {
        if (data != null) {
            pool1.put(data);
        }
    }

    /**
     * Allocate File
     */
    protected final File allocateFile(Inode inode)
    {
        MiniFile file = createFile();
        file.init(inode);
        return file;
    }

    /**
     * Recycle File
     */
    protected final void recycleFile(File file)
    {
//        if (file != null) {
//            pool2.put(file);
//        }
    }

    /**
     * 实际真正初始化
     */
    public void _doInit()
    {
        try {
            initHead();
            initData();
            initMemory();

            verify();
        }
        catch (IOException fe) {
            throw new InitializeException("Create mini file system exception", fe);
        }
    }
}
