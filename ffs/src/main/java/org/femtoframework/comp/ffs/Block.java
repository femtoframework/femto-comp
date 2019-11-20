package org.femtoframework.comp.ffs;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import org.femtoframework.comp.io.Channel;
import org.femtoframework.comp.io.Storable;
import org.femtoframework.lang.Binary;

/**
 * 二进制处理缓冲区<br>
 * <p/>
 * 特性：<br>
 * 固定长度<br>
 * 不检查索引<br>
 * 单线程访问<br>
 *
 * @see org.femtoframework.comp.io.ByteArrayChannel
 */
public class Block
    implements Storable, Channel
{

    private int offset;

    private long position;

    private int unit;


    /**
     * 构造块
     *
     * @param offset   块偏址
     * @param position 块位置
     * @param capacity 最大容积
     */
    public Block(int offset,
                 long position,
                 int capacity)
    {
        this.value = new byte[capacity];
        this.pos = 0;
        this.count = 0;
        this.capacity = capacity;
        this.offset = offset;
        this.position = position;
        this.unit = (int) (Math.log(capacity) / Math.log(2));
    }

    /**
     * 返回块偏址
     *
     * @return 块偏址
     */
    public int offset()
    {
        return offset;
    }

    /**
     * Set OFfset
     */
    public void setOffset(int offset)
    {
        this.offset = offset;
        this.position = offset << unit;
    }

    /**
     * 返回该块在通道中地绝对位置
     */
    public long getAbsolutePosition()
    {
        return position;
    }

    /**
     * 保存块
     *
     * @param channel 存取通道
     */
    public void save(Channel channel)
        throws IOException
    {
        //直接写一个块的数据
        //要写一个快的大小，性能才比较好
        save(channel, capacity);
    }

    /**
     * 保存块
     *
     * @param channel 存取通道
     */
    public void save(Channel channel, int size)
        throws IOException
    {
        channel.seek(position);
        channel.write(value, 0, size);
    }

    /**
     * 读取块
     *
     * @param channel 存取通道
     */
    public void load(Channel channel)
        throws IOException
    {
        //要写一个快的大小，性能才比较好
        load(channel, capacity);
    }

    /**
     * 读取块
     *
     * @param channel 存取通道
     */
    public void load(Channel channel, int blockSize)
        throws IOException
    {
        channel.seek(position);
        channel.readFully(value, 0, blockSize);
        this.pos = 0;
        this.count = blockSize;
    }

    /**
     * 填充
     *
     * @param block 另一数据块填充
     */
    void fill(Block block)
    {
        System.arraycopy(block.value, 0, value, 0, block.count);
        this.count = block.count;
        this.pos = 0;
    }

    int capacity;

    int count;

    int pos;

    byte[] value;

    public long length()
    {
        return count;
    }

    int len()
    {
        return count;
    }

    /**
     * 数据是否结束
     *
     * @return
     */
    public boolean isEnd()
    {
        return pos >= count;
    }

    public int capacity()
    {
        return capacity;
    }

    public long position()
    {
        return pos;
    }

    public int pos()
    {
        return pos;
    }

    public void seek(long pos)
    {
        this.pos = (int) pos;
    }

    public void seek(int pos)
    {
        this.pos = pos;
    }

    public int read()
    {
        return (pos < count) ? (value[pos++] & 0xFF) : -1;
    }

    /**
     * 读取<br>
     *
     * @param b   读入的数组
     * @param off
     * @param len 读取长度
     */
    public int read(byte[] b, int off, int len)
    {
        if (pos >= count) {
            return -1;
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(value, pos, b, off, len);
        pos += len;
        return len;
    }

    public int read(byte[] b)
    {
        return read(b, 0, b.length);
    }

    //计算新的Count
    public void newCount()
    {
        if (pos > count) {
            count = pos;
        }
    }

    /**
     * 必须小于256
     */
    public final void write(int b)
    {
        value[pos++] = (byte) b;
    }

    public void write(byte bytes[])
    {
        write(bytes, 0, bytes.length);
    }

    public void write(byte bytes[], int offset, int len)
    {
        int newpos = pos + len;
        System.arraycopy(bytes, offset, value, pos, len);
        pos = newpos;
    }

    public void writeBoolean(boolean b)
    {
        writeInt(b ? 1 : 0);
    }

    public final void writeByte(int b)
    {
        value[pos++] = (byte) (b & 0xFF);
    }

    /**
     * 必须小于256
     */
    public final void writeUnsignedByte(int i)
    {
        value[pos++] = (byte) i;
    }

    public final void writeChar(int c)
    {
        value[pos++] = (byte) (c >> 8);
        value[pos++] = (byte) c;
    }

    public final void writeShort(int s)
    {
        value[pos++] = (byte) (s >> 8);
        value[pos++] = (byte) s;
    }

    public final void writeUnsignedShort(int i)
    {
        value[pos++] = (byte) (i >> 8);
        value[pos++] = (byte) i;
    }

    public final void writeInt(int i)
    {
        value[pos++] = (byte) (i >> 24);
        value[pos++] = (byte) (i >> 16);
        value[pos++] = (byte) (i >> 8);
        value[pos++] = (byte) i;
    }

    public final void writeLong(long l)
    {
        value[pos++] = (byte) (l >> 56);
        value[pos++] = (byte) (l >> 48);
        value[pos++] = (byte) (l >> 40);
        value[pos++] = (byte) (l >> 32);
        value[pos++] = (byte) (l >> 24);
        value[pos++] = (byte) (l >> 16);
        value[pos++] = (byte) (l >> 8);
        value[pos++] = (byte) l;
    }

    public void writeFloat(float f)
    {
        Binary.append(value, pos, f);
        pos += 4;
    }

    public void writeDouble(double d)
    {
        Binary.append(value, pos, d);
        pos += 8;
    }

    public void writeBytes(String str)
        throws IOException
    {
        throw new IOException("Unsupported");
    }

    public void writeChars(String str)
    {
        if (str == null) {
            writeInt(-1);
            return;
        }

        int len = str.length();
        int v;

        writeInt(len);
        for (int i = 0; i < len; i++) {
            v = str.charAt(i);
            value[pos++] = (byte) ((v >>> 8) & 0xFF);
            value[pos++] = (byte) ((v) & 0xFF);
        }
    }

    public void writeUTF(String str)
        throws IOException
    {
        throw new IOException("Unsupported");
    }

    public void readFully(byte[] bytes)
        throws IOException
    {
        readFully(bytes, 0, bytes.length);
    }

    public void readFully(byte[] bytes, int off, int len)
        throws IOException
    {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int c = read(bytes, off + n, len - n);
            if (c < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

    //For reading
    public int skipBytes(int size)
    {
        if (size <= 0) {
            return 0;
        }
        int avail = count - pos;
        if (avail <= 0) {
            return 0;
        }

        int skipped = (avail < size) ? avail : size;
        pos += skipped;
        return skipped;
    }

    public final void writeSpaces(int size)
    {
        pos += size;
    }

    public final byte readByte()
    {
        return value[pos++];
    }

    public final boolean readBoolean()
    {
        return (value[pos++] & 0xFF) != 0;
    }

    /**
     * 还原以Binary形式存储的Char
     */
    public final char readChar()
    {
        int ch1 = (int) value[pos++] & 0xFF;
        int ch2 = (int) value[pos++] & 0xFF;
        return (char) ((ch1 << 8) + (ch2));
    }

    /**
     * 还原以Binary形式存储的Short
     */
    public final short readShort()
    {
        int ch1 = (int) value[pos++] & 0xFF;
        int ch2 = (int) value[pos++] & 0xFF;
        return (short) ((ch1 << 8) + (ch2));
    }

    public final int readUnsignedByte()
    {
        return (int) value[pos++] & 0XFF;
    }

    public final int readUnsignedShort()
    {
        int ch1 = (int) value[pos++] & 0xFF;
        int ch2 = (int) value[pos++] & 0xFF;
        return (ch1 << 8) + (ch2);
    }

    /**
     * 还原以Binary形式存储的Integer
     */
    public final int readInt()
    {
        int ch1 = (int) value[pos++] & 0xFF;
        int ch2 = (int) value[pos++] & 0xFF;
        int ch3 = (int) value[pos++] & 0xFF;
        int ch4 = (int) value[pos++] & 0xFF;
        return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4));
    }

    public final long readLong()
    {
        int ch1 = readInt();
        int ch2 = readInt();
        return ((long) ch1 << 32) | ((long) ch2 & 0xFFFFFFFFL);
    }

    public final float readFloat()
    {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble()
    {
        return Double.longBitsToDouble(readLong());
    }

    public String readLine() throws IOException
    {
        throw new IOException("Unsupported");
    }

    public String readUTF() throws IOException
    {
        return DataInputStream.readUTF(this);
    }

    public void reset()
    {
        this.pos = 0;
        this.count = 0;
    }

    public void close()
    {
        reset();
    }
}
