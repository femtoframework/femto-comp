package org.femtoframework.comp.ffs;

import org.femtoframework.comp.util.SystemTime;

/**
 * 检查点
 *
 * @author fengyun
 * @version 1.00 May 1, 2002 9:01:08 AM
 */
public class CheckPoint
{

    /**
     * 返回字节型
     */
    public static final int getUnsignedByte()
    {
        long time = SystemTime.getCentiSecond();
        return getByte((int) time);
    }

    /**
     * 返回字节型
     */
    public static final int getByte(int key)
    {
        return MATRIX[key & 0xFF];
    }

    /**
     * 返回字节型
     */
    public static final int getUnsignedByte(int key)
    {
        return MATRIX[key];
    }

    public static final int[] MATRIX = new int[]{
        2, 4, 7, 10, 15, 18, 23, 26, 31, 38, 41, 48, 53, 56, 61, 68,
        75, 78, 85, 90, 93, 100, 105, 112, 121, 126, 129, 134, 137, 142, 157, 162,
        169, 172, 183, 186, 193, 200, 205, 212, 219, 222, 233, 236, 241, 244, 1, 14,
        19, 22, 27, 34, 37, 55, 62, 69, 76, 72, 79, 84, 87, 98, 113, 118,
        128, 133, 141, 148, 159, 176, 167, 174, 190, 197, 204, 202, 209, 218, 223, 232,
        243, 246, 8, 11, 25, 16, 30, 32, 44, 40, 45, 58, 67, 86, 81, 107,
        114, 106, 109, 135, 149, 146, 153, 160, 163, 170, 181, 188, 195, 198, 226, 240,
        217, 220, 247, 251, 254, 252, 3, 17, 13, 33, 52, 59, 47, 65, 74, 92,
        94, 101, 108, 120, 115, 122, 127, 136, 155, 156, 216, 180, 211, 194, 225, 230,
        5, 237, 231, 250, 239, 9, 24, 29, 39, 51, 177, 42, 184, 83, 71, 97,
        104, 111, 119, 110, 117, 124, 140, 147, 191, 158, 165, 168, 179, 182, 189, 207,
        203, 214, 224, 238, 36, 248, 12, 0, 21, 28, 54, 82, 89, 60, 63, 88,
        125, 143, 150, 164, 131, 130, 96, 103, 138, 152, 166, 221, 253, 145, 173, 210,
        245, 227, 234, 46, 235, 187, 95, 35, 49, 70, 77, 91, 201, 154, 102, 208,
        171, 132, 123, 144, 139, 151, 161, 175, 215, 178, 229, 228, 196, 43, 50, 242,
        255, 57, 249, 66, 64, 99, 73, 116, 80, 185, 192, 199, 206, 213, 6, 20
    };

    /**
     * 返回短整型检查数
     */
    public static final int getUnsignedShort()
    {
        long time = getLong();
        return (int) time & 0xFFFF;
    }

    /**
     * 返回整形检查数
     */
    public static final int getInt()
    {
        long time = getLong();
        return (int) (time & 0XFFFFFFFFL);
    }

    public static final int getInt(long key)
    {
        return (int) (key & 0xFFFFFFFFL);
    }

    public static final int getInt(int key)
    {
        return key << 1;
    }

    /**
     * 返回长整型检查数
     */
    public static final long getLong()
    {
        return SystemTime.getCentiSecond();
    }
}
