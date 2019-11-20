package org.femtoframework.comp.io;

import java.io.IOException;

/**
 * 通道过滤器
 *
 * @see Channel
 */
public abstract class FilterChannel
    implements Channel
{
    protected Channel channel;

    public FilterChannel(Channel channel)
    {
        this.channel = channel;
    }

    public long length()
        throws IOException
    {
        return channel.length();
    }

    public long position()
        throws IOException
    {
        return channel.position();
    }

    public void seek(long pos)
        throws IOException
    {
        channel.seek(pos);
    }

    public int read() throws IOException
    {
        return channel.read();
    }

    /**
     * 读取<br>
     *
     * @param b   读入的数组
     * @param off
     * @param len 读取长度
     */
    public int read(byte[] b, int off, int len)
        throws IOException
    {
        return channel.read(b, off, len);
    }

    public int read(byte[] b)
        throws IOException
    {
        return channel.read(b);
    }

    public void write(int b)
        throws IOException
    {
        channel.write(b);
    }

    public void write(byte bytes[])
        throws IOException
    {
        channel.write(bytes);
    }

    public void write(byte bytes[], int off, int len)
        throws IOException
    {
        channel.write(bytes, off, len);
    }

    public void writeBoolean(boolean b)
        throws IOException
    {
        channel.writeBoolean(b);
    }

    public void writeByte(int b)
        throws IOException
    {
        channel.writeByte(b);
    }

    public void writeUnsignedByte(int i)
        throws IOException
    {
        channel.writeUnsignedByte(i);
    }

    public void writeChar(int c)
        throws IOException
    {
        channel.writeChar(c);
    }

    public void writeShort(int s)
        throws IOException
    {
        channel.writeShort(s);
    }

    public void writeUnsignedShort(int i)
        throws IOException
    {
        channel.writeUnsignedShort(i);
    }

    public void writeInt(int i)
        throws IOException
    {
        channel.writeInt(i);
    }

    public void writeLong(long l)
        throws IOException
    {
        channel.writeLong(l);
    }

    public void writeFloat(float f)
        throws IOException
    {
        channel.writeFloat(f);
    }

    public void writeDouble(double d)
        throws IOException
    {
        channel.writeDouble(d);
    }

    public void writeBytes(String str)
        throws IOException
    {
        channel.writeBytes(str);
    }

    public void writeChars(String str)
        throws IOException
    {
        channel.writeChars(str);
    }

    public void writeUTF(String str)
        throws IOException
    {
        channel.writeUTF(str);
    }

    public void readFully(byte[] bytes)
        throws IOException
    {
        channel.readFully(bytes);
    }

    public void readFully(byte[] bytes, int off, int len)
        throws IOException
    {
        channel.readFully(bytes, off, len);
    }

    public int skipBytes(int size)
        throws IOException
    {
        return channel.skipBytes(size);
    }

    public byte readByte() throws IOException
    {
        return channel.readByte();
    }

    public boolean readBoolean() throws IOException
    {
        return channel.readBoolean();
    }

    /**
     * 还原以Binary形式存储的Char
     */
    public char readChar() throws IOException
    {
        return channel.readChar();
    }

    /**
     * 还原以Binary形式存储的Short
     */
    public short readShort() throws IOException
    {
        return channel.readShort();
    }

    public int readUnsignedByte() throws IOException
    {
        return channel.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException
    {
        return channel.readUnsignedShort();
    }

    public int readInt() throws IOException
    {
        return channel.readInt();
    }

    public long readLong() throws IOException
    {
        return channel.readLong();
    }

    public float readFloat() throws IOException
    {
        return channel.readFloat();
    }

    public double readDouble() throws IOException
    {
        return channel.readDouble();
    }

    public String readLine() throws IOException
    {
        return channel.readLine();
    }

    public String readUTF() throws IOException
    {
        return channel.readUTF();
    }

    public void close() throws IOException
    {
        channel.close();
    }
}
