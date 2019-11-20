package org.femtoframework.comp.ffs;

import org.femtoframework.comp.util.LongIterator;

/**
 * File List
 * LongKeyMap is slow for ffs
 *
 * @author fengyun
 * @version 1.00 May 20, 2002 4:17:43 PM
 */
public class FileList
{
    public static final long NULL = Long.MIN_VALUE + 2L;

    /**
     * File names
     */
    private long[] names;

    /**
     * Files
     */
    private File[] files;


    /**
     * @param size Size
     */
    public FileList(int size)
    {
        this.names = new long[size];
        this.files = new File[size];

        for (int i = 0; i < size; i++) {
            names[i] = NULL;
        }
    }

    public void add(File file)
    {
        int index = file.index;
        names[index] = file.getName();
        files[index] = file;
    }

    public File get(long name)
    {
        int index = (int) (name & 0xFFFF);
        if (names[index] == name) {
            return files[index];
        }
        return null;
    }

    public File remove(long name)
    {
        int index = (int) (name & 0xFFFF);
        File file = null;
        if (names[index] == name) {
            file = files[index];
            files[index] = null;
            names[index] = NULL;
        }
        return file;
    }

    public final NameIterator names()
    {
        return new NameIterator(names);
    }

    public final FileIterator files()
    {
        return new FileIterator(files);
    }

    public static class NameIterator
        implements LongIterator
    {
        private long[] names;

        private int next;

        NameIterator(long[] names)
        {
            this.names = names;
            this.next = -1;
        }

        public boolean hasNext()
        {
            for (int i = next + 1; i < names.length; i++) {
                if (names[i] != NULL) {
                    next = i;
                    return true;
                }
            }
            return false;
        }

        public long next()
        {
            return names[next];
        }
    }

    public static class FileIterator
    {
        private File[] files;

        private int next;

        FileIterator(File[] files)
        {
            this.files = files;
            this.next = -1;
        }

        public boolean hasNext()
        {
            for (int i = next + 1; i < files.length; i++) {
                if (files[i] != null) {
                    next = i;
                    return true;
                }
            }
            return false;
        }

        public File next()
        {
            return files[next];
        }
    }
}
