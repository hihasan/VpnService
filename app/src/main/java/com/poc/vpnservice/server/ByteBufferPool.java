package com.poc.vpnservice.server;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ByteBufferPool
{
    private static final int BUFFER_SIZE = 16384;
    private static ConcurrentLinkedQueue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();

    public static ByteBuffer acquire()
    {
        synchronized (pool)
        {
            ByteBuffer buffer = pool.poll();
            if (buffer == null)
            {
                buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
            }
            buffer.clear();
            return buffer;
        }

    }

    public static void release(ByteBuffer buffer)
    {
        synchronized (pool)
        {
            buffer.clear();
            //  pool.offer(buffer);
        }

    }

    public static void clear()
    {
        synchronized (pool)
        {
            pool.clear();
        }
    }
}
