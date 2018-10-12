package com.linglong.lame;

/**
 * create on 2018/10/11
 *  加载音频转mp3 so库
 * @author ListenerGao
 */
public class Lame {

    static {
        System.loadLibrary("lamemp3");
    }

    public native void init(int channel, int sampleRate, int brate);
    public native void destroy();
    public native byte[] encode(short[] buffer, int len);
}
