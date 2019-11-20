package org.femtoframework.comp.util;

/**
 * System.currentTimeMillis() is too slow for ffs.
 *
 * @author fengyun
 * @version 1.00 May 20, 2002 12:29:11 PM
 */
public final class SystemTime
    implements Runnable
{

    private SystemTime()
    {
    }

    private static int second;
    private static long deciSecond;
    private static long centiSecond;

    static
    {
        long time = System.currentTimeMillis();
        centiSecond = time / 10;
        deciSecond = centiSecond / 10;
        second = (int) deciSecond / 10;

        Thread thread = new Thread(new SystemTime(), "SystemTime");
        thread.setDaemon(true);
        thread.start();
    }

    public void run()
    {
        long time;
        while (true) {
            time = System.currentTimeMillis();
            centiSecond = time / 10;
            deciSecond = centiSecond / 10;
            second = (int) (deciSecond / 10);

            try {
                Thread.sleep(10);
            }
            catch (Exception e) {
            }
        }
    }

    public static final int getSecond()
    {
        return second;
    }

    public static final long getMilliSecond()
    {
        return System.currentTimeMillis();
    }

    public static final long getDeciSecond()
    {
        return deciSecond;
    }

    public static final long getCentiSecond()
    {
        return centiSecond;
    }
}
