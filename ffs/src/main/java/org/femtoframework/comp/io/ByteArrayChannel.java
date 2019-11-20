package org.femtoframework.comp.io;

import org.femtoframework.io.CodecUtil;
import org.femtoframework.io.DataCodec;
import org.femtoframework.lang.Binary;

import java.io.EOFException;
import java.io.IOException;

/**
 * 可以随机存储的数据通道<br>
 * 单线程读写<br>
 * 没有参数检查<br>
 *
 * @author fengyun
 * @version 1.00 May 15, 2002 12:55:00 PM
 */
public class ByteArrayChannel
    implements Channel
{
    protected int capacity;

    protected int count;

    protected int pos;

    protected byte[] value;

    public ByteArrayChannel(int capacity)
    {
        this.value = new byte[capacity];
        this.pos = 0;
        this.count = 0;
        this.capacity = capacity;
    }

    public long length()
    {
        return count;
    }

    public long capacity()
    {
        return capacity;
    }

    public long position()
    {
        return pos;
    }

    public void seek(long pos)
        throws IOException
    {
//        if (pos > count)
//            throw new EOFException();
        this.pos = (int) pos;
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
/*     if (b == null) {
            throw new NullPointerException();
        }
        else if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }*/
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

    public void write(int b)
    {
        value[pos++] = (byte) b;
        count = pos > count ? pos : count;
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
        count = pos > count ? pos : count;
    }

    public void writeBoolean(boolean b)
    {
        write(b ? 1 : 0);
    }

    public void writeByte(int b)
    {
        write(b);
    }

    public void writeUnsignedByte(int i)
    {
        write(i);
    }

    public void writeChar(int c)
    {
        value[pos++] = (byte) (c >> 8);
        value[pos++] = (byte) (c);
        count = pos > count ? pos : count;
    }

    public void writeShort(int s)
    {
        value[pos++] = (byte) (s >> 8);
        value[pos++] = (byte) (s);
        count = pos > count ? pos : count;
    }

    public void writeUnsignedShort(int i)
    {
        value[pos++] = (byte) (i >> 8);
        value[pos++] = (byte) (i);
        count = pos > count ? pos : count;
    }

    public void writeInt(int i)
    {
        value[pos++] = (byte) (i >> 24);
        value[pos++] = (byte) (i >> 16);
        value[pos++] = (byte) (i >> 8);
        value[pos++] = (byte) (i);
        count = pos > count ? pos : count;
    }

    public void writeLong(long l)
    {
        value[pos++] = (byte) (l >> 56);
        value[pos++] = (byte) (l >> 48);
        value[pos++] = (byte) (l >> 40);
        value[pos++] = (byte) (l >> 32);
        value[pos++] = (byte) (l >> 24);
        value[pos++] = (byte) (l >> 16);
        value[pos++] = (byte) (l >> 8);
        value[pos++] = (byte) (l);
        count = pos > count ? pos : count;
    }

    public void writeFloat(float f)
    {
        Binary.append(value, pos, f);
        pos += 4;
        count = pos > count ? pos : count;
    }

    public void writeDouble(double d)
    {
        Binary.append(value, pos, d);
        pos += 8;
        count = pos > count ? pos : count;
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
        DataCodec.writeUTFX(this, str);
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
            n += c;
        }
    }

    public int skipBytes(int size)
        throws IOException
    {
        if (size <= 0) {
            return 0;
        }
        int avail = count - pos;
        if (avail <= 0) {
            throw new EOFException();
        }

        int skipped = (avail < size) ? avail : size;
        pos += skipped;
        return skipped;
    }

    public byte readByte()
    {
        int b = read();
        return (byte) b;
    }

    public boolean readBoolean()
    {
        int b = read();
        return b != 0;
    }

    /**
     * 还原以Binary形式存储的Char
     */
    public char readChar()
    {
        char c = Binary.toChar(value, pos);
        pos += 2;
        return c;
    }

    /**
     * 还原以Binary形式存储的Short
     */
    public short readShort()
    {
        short s = Binary.toShort(value, pos);
        pos += 2;
        return s;
    }

    public int readUnsignedByte()
    {
        return Binary.toUnsignedByte(value, pos++);
    }

    public int readUnsignedShort()
    {
        int i = Binary.toUnsignedShort(value, pos);
        pos += 2;
        return i;
    }

    /**
     * 还原以Binary形式存储的Integer
     */
    public int readInt()
    {
        int i = Binary.toInt(value, pos);
        pos += 4;
        return i;
    }

    public long readLong()
    {
        long l = Binary.toLong(value, pos);
        pos += 8;
        return l;
    }

    public float readFloat()
    {
        float f = Binary.toFloat(value, pos);
        pos += 4;
        return f;
    }

    public double readDouble()
    {
        double d = Binary.toDouble(value, pos);
        pos += 8;
        return d;
    }

    public String readLine() throws IOException
    {
        throw new IOException("Unsupported");
    }

    public String readUTF() throws IOException
    {
        return DataCodec.readUTFX(this);
    }

    public void reset()
    {
        this.pos = 0;
    }

    public void clear()
    {
        this.count = 0;
    }

    public void close()
    {
        clear();
    }
}
