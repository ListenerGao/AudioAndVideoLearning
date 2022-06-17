# lame mp3 编译Android平台so库

## 操作步骤
**1. Android Studio 新建项目；**

**2. 安装NDK（此处是使用 ndk-build 编译so库），并配置NDK环境变量；**

**3. 项目中配置jni**


* 项目切换到 project 模式
* 在 app/src/main/ 下新建目录 jni
* 在 app module 的 build.gradle 中 android field 添加：

    ```
    sourceSets {
        main {
            jni.srcDirs = []
            jniLibs.srcDir 'src/main/libs'
        }
    }
    ```

**4. 下载 lame mp3 源码**
    
* 下载地址：http://lame.sourceforge.net/download.php
   
* 解压lame-3.100.tar.gz -> lame-3.100
* 在 jni 目录下新建目录：lame-3.100_libmp3lame
* 将解压后 lame-3.100/libmp3lame/下面的所有.c和.h拷贝到项目jni/lame-3.100_libmp3lame 目录下,同时将lame-3.100/include/下的lame.h也拷贝到jni/lame-3.100_libmp3lame 目录下
    

**5. 修改 jni/lame-3.100_libmp3lame 目录下的 .c 和 .h 代码**

* 删除fft.c文件47行的 include "vector/lame_intrin.h"
* 删除set_get.h文件的24行的 #include <lame.h>
* 将util.h文件的570行的 extern ieee754_float32_t fast_log2(ieee754_float32_t x); 替换为 extern float fast_log2(float x);


**6. 在 jni 目录下新建 Application.mk 文件，内容如下：**

```
APP_ABI := all
APP_MODULES := mp3lame
APP_CFLAGS += -DSTDC_HEADERS
APP_PLATFORM := android-19
```
**7. 在 jni 目录下新建 Android.mk 文件，内容如下：**

```
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
    
LAME_LIBMP3_DIR := lame-3.100_libmp3lame
    
    
LOCAL_MODULE    := mp3lame
    
LOCAL_SRC_FILES :=\
$(LAME_LIBMP3_DIR)/bitstream.c \
$(LAME_LIBMP3_DIR)/fft.c \
$(LAME_LIBMP3_DIR)/id3tag.c \
$(LAME_LIBMP3_DIR)/mpglib_interface.c \
$(LAME_LIBMP3_DIR)/presets.c \
$(LAME_LIBMP3_DIR)/quantize.c \
$(LAME_LIBMP3_DIR)/reservoir.c \
$(LAME_LIBMP3_DIR)/tables.c  \
$(LAME_LIBMP3_DIR)/util.c \
$(LAME_LIBMP3_DIR)/VbrTag.c \
$(LAME_LIBMP3_DIR)/encoder.c \
$(LAME_LIBMP3_DIR)/gain_analysis.c \
$(LAME_LIBMP3_DIR)/lame.c \
$(LAME_LIBMP3_DIR)/newmdct.c \
$(LAME_LIBMP3_DIR)/psymodel.c \
$(LAME_LIBMP3_DIR)/quantize_pvt.c \
$(LAME_LIBMP3_DIR)/set_get.c \
$(LAME_LIBMP3_DIR)/takehiro.c \
$(LAME_LIBMP3_DIR)/vbrquantize.c \
$(LAME_LIBMP3_DIR)/version.c \
Mp3Encoder.c
    
include $(BUILD_SHARED_LIBRARY)
```

**8. 编写 java 类和 C 文件**

* 新建 Mp3Encoder.java 类：

    ```
    package com.listenergao.lamemp3;

    /**
     * create on 2022/06/17
     *
     * @author ListenerGao
     */
    public class Mp3Encoder {
    
        static {
            System.loadLibrary("mp3lame");
        }
    
        /**
         * 初始化 lame编码器
         *
         * @param inSampleRate
         *              输入采样率
         * @param outChannel
         *              声道数
         * @param outSampleRate
         *              输出采样率
         * @param outBitrate
         *              比特率(kbps)
         * @param quality
         *              0~9，0最好
         */
        public static native void init(int inSampleRate, int outChannel, int outSampleRate, int outBitrate, int quality);
    
        /**
         *  编码，把 AudioRecord 录制的 PCM 数据转换成 mp3 格式
         *
         * @param buffer_l
         *          左声道输入数据
         * @param buffer_r
         *          右声道输入数据
         * @param samples
         *          输入数据的size
         * @param mp3buf
         *          输出数据
         * @return
         *          输出到mp3buf的byte数量
         */
        public static native int encode(short[] buffer_l, short[] buffer_r, int samples, byte[] mp3buf);
    
        /**
         *  刷写
         *
         * @param mp3buf
         *          mp3数据缓存区
         * @return
         *          返回刷写的数量
         */
        public static native int flush(byte[] mp3buf);
    
        /**
         * 关闭 lame 编码器，释放资源
         */
        public static native void close();
    }

    ```
    
* 生成 Mp3Encoder.h 文件

    打开android studio 的 terminal 命令行，输入cd app/src/main/java 命令切换到java目录下，输入 javah -o Mp3Encoder.h com.listenergao.lamemp3.Mp3Encoder（注意，后面是Mp3Encoder的全类名，修改成你的）生成 Mp3Encoder.h 文件
    
    
* 新建 Mp3Encoder.c 文件
    
    在 jni 目录下新建 Mp3Encoder.c 文件，注意修改方法名，参照 Mp3Encoder.h 中的方法名：
    
    ```
    #include "lame-3.100_libmp3lame/lame.h"
    #include "Mp3Encoder.h"
    
    static lame_global_flags *glf = NULL;
    
    JNIEXPORT void JNICALL Java_com_listenergao_lamemp3_Mp3Encoder_init(JNIEnv *env, jobject instance, jint inSamplerate, jint outChannel, jint outSamplerate, jint outBitrate, jint quality) {
        if (glf != NULL) {
            lame_close(glf);
            glf = NULL;
        }
        glf = lame_init();
        lame_set_in_samplerate(glf, inSamplerate);
        lame_set_num_channels(glf, outChannel);
        lame_set_out_samplerate(glf, outSamplerate);
        lame_set_brate(glf, outBitrate);
        lame_set_quality(glf, quality);
        lame_init_params(glf);
    }
    
    JNIEXPORT jint JNICALL Java_com_listenergao_lamemp3_Mp3Encoder_encode(JNIEnv *env, jobject instance, jshortArray buffer_l, jshortArray buffer_r, jint samples, jbyteArray mp3buf) {
        jshort* j_buffer_l = (*env)->GetShortArrayElements(env, buffer_l, NULL);
    
        jshort* j_buffer_r = (*env)->GetShortArrayElements(env, buffer_r, NULL);
    
        const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
        jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);
    
        int result = lame_encode_buffer(glf, j_buffer_l, j_buffer_r,
                                        samples, j_mp3buf, mp3buf_size);
    
        (*env)->ReleaseShortArrayElements(env, buffer_l, j_buffer_l, 0);
        (*env)->ReleaseShortArrayElements(env, buffer_r, j_buffer_r, 0);
        (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);
    
        return result;
    }
    
    JNIEXPORT jint JNICALL Java_com_listenergao_lamemp3_Mp3Encoder_flush(JNIEnv *env, jobject instance, jbyteArray mp3buf) {
        const jsize mp3buf_size = (*env)->GetArrayLength(env, mp3buf);
        jbyte* j_mp3buf = (*env)->GetByteArrayElements(env, mp3buf, NULL);
    
        int result = lame_encode_flush(glf, j_mp3buf, mp3buf_size);
    
        (*env)->ReleaseByteArrayElements(env, mp3buf, j_mp3buf, 0);
    
        return result;
    }
    
    JNIEXPORT void JNICALL Java_com_listenergao_lamemp3_Mp3Encoder_close(JNIEnv *env, jobject instance) {
        lame_close(glf);
        glf = NULL;
    }
    ```

    
**9. 编译 .so**

* 终端进入 jni 目录，执行 ndk-build 命令进行编译
* 编译成功后，即可在 main 目录下生成 libs 目录，包含各平台的 so 库

