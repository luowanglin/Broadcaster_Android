package com.example.luowanglin.android.broadcaster;

/**
 * Created by luowanglin on 2017/4/9.
 */

public class Wave {
    /** 正弦波的高度 **/
    public static final int HEIGHT = 127;
    /** 2PI**/
    public static final double TWOPI = 2 * 3.1415;
    /**
     * 生成正弦波
     * @param wave
     * @param waveLen 每段正弦波的长度
     * @param length 总长度
     * @return
     */
    public static byte[] sin(byte[] wave, int waveLen, int length) {
        for (int i = 0; i < length; i++) {
            wave[i] = (byte) (HEIGHT * (1 - Math.sin(TWOPI
                    * ((i % waveLen) * 1.00 / waveLen))));
        }
        return wave;
    }
}
