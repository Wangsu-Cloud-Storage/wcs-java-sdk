package com.chinanetcenter.api.util;

/**
 * Created by chenql on 2022/03/15.
 */
public class BandwidthLimiter {


    /* KB */
    private long KB = 1024L;
    /**
     * The smallest count chunk length in bytes,默认值时8K
     */
    private long CHUNK_LENGTH = 8 * KB;

    // How many bytes will be sent or receive
    private int bytesWillBeSentOrReceive = 0;

    // When the last piece was sent or receive
    private long lastPieceSentOrReceiveTick = System.nanoTime();

    /**
     * Default rate is 1024K/s
     * Set the max upload or download rate in KB/s. maxRate must be grater than 0. If maxRate is zero, it means there is no bandwidth limit.
     */
    private int maxRate = 1024;

    // Time cost for sending CHUNK_LENGTH bytes in nanoseconds
    private long timeCostPerChunk = (1000000000L * CHUNK_LENGTH)
            / (maxRate * KB);

    private long nextStartTime = 0L;
    public BandwidthLimiter() {
    }
    public BandwidthLimiter(int maxRate) {
        //非正数代表不限速；小于100着设置为100
        this.maxRate = maxRate < 0 ? 0 : (Math.max(maxRate, 100));

        if (this.maxRate == 0) {
            timeCostPerChunk = 0;
        } else {
            CHUNK_LENGTH = this.maxRate * KB / 100;
            if (CHUNK_LENGTH < 8192L) {
                CHUNK_LENGTH = 8192L;
            }
            timeCostPerChunk = (1000000000L * CHUNK_LENGTH)
                    / (this.maxRate * KB);
        }
    }

    public int getChunkSize() {
        if (CHUNK_LENGTH > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) CHUNK_LENGTH;
    }


    /**
     * Next 1 byte should do bandwidth limit.
     */
    public synchronized void limitNextBytes() {
        limitNextBytes(1);
    }

    /**
     * 是否允许读写
     *
     * @return
     */
    public synchronized boolean isReadWriteable() {
        if (nextStartTime == 0L) {
            nextStartTime = System.nanoTime();
        }
        return nextStartTime <= System.nanoTime();
    }

    /**
     * 统计写入读取的大小
     *
     * @param len
     */
    public synchronized void readWriteSize(int len) {
        long nowTick = System.nanoTime();
        //限速下传输len长度需要耗时多少毫秒
        long costTime = timeCostPerChunk * len / CHUNK_LENGTH;
        if ((nowTick - nextStartTime) < costTime) {
            //传输len的实际时间如果比限速时间小，则下次执行时间需要往后延时costtime
            nextStartTime = nowTick + costTime;
        } else {
            //传输len的实际时间大于等于限速时间，则下次执行时间设置为当前时间
            nextStartTime = nowTick;
        }
    }
    /**
     * Next len bytes should do bandwidth limit
     *
     * @param len
     */
    public synchronized void limitNextBytes(int len) {
        if (maxRate < 1) {
            return;
        }
        bytesWillBeSentOrReceive += len;

        /* We have sent CHUNK_LENGTH bytes */
        while (bytesWillBeSentOrReceive >= CHUNK_LENGTH) {
            long nowTick = System.nanoTime();
            long missedTime = timeCostPerChunk
                    - (nowTick - lastPieceSentOrReceiveTick);
            if (missedTime > 0) {
                try {
                    Thread.sleep(missedTime / 1000000,
                            (int) (missedTime % 1000000));
                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
                }
            }
            bytesWillBeSentOrReceive -= CHUNK_LENGTH;
            lastPieceSentOrReceiveTick = nowTick
                    + (missedTime > 0 ? missedTime : 0);
        }
    }

    public synchronized void limitNextBytes(long len) {
        if (maxRate < 1) {
            return;
        }
        while (len > Integer.MAX_VALUE) {
            limitNextBytes(Integer.MAX_VALUE);
            len -= Integer.MAX_VALUE;
        }
        limitNextBytes((int) len);
    }
}
