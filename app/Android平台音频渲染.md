# Android 平台音频渲染

## SDK 层的音频渲染
Android 系统在 SDK 层（Java 层提供的 API）提供 3 套常用的音频渲染方式，分别是：MediaPlayer、SoundPool 和 AudioTrack。这三种 API 的推荐使用场景不同。

* MediaPlayer：适合在后台长时间播放本地音乐文件或者在线播放流媒体文件，相当于是一个端到端的播放器，可以播放音频也可以播放视频，封装层次比较高，使用当时比较简单。

* SoundPool：也是一个端到端的音频播放器，优点是：延时较低，比较适合有交互反馈音的场景，适合播放比较短的音频片段，比如游戏声音、按键音、铃声片段等，它可以同时播放多个音频。

* AudioTrack：是直接面向 PCM 数据的音频渲染 API，所以也是更加底层的 API，提供非常强大的控制力，适合低延时的播放、流媒体的音频渲染等场景，由于是直接面向 PCM 的数据进行渲染，所以一般情况下需要结合编码器来使用。

## NDK 层的音频渲染 

Android 系统在 NDK 层（Native 层提供的 API，即 C 或者 C++ 层可以调用的 API）提供了 2 套常用的音频渲染方式，分别是 openSL ES 和 AAudio，它们都是为 Android 的低延时场景（实时耳返、RTC、实时反馈交互）而设计的。

* OpenSL ES：是 Khronos Group 开发的  OpenSL ES™ API 规范的实现，专用于 Android 低延迟高性能的音频场景，API 接口设计会有一些晦涩、复杂，目前 Google 已经不推荐开发者把 OpenSL ES 用于新应用的开发了。但是在 Android8.0 系统以下以及一些碎片化的 Android 设备上它具有更好的兼容性，所以掌握这种音频渲染方法也是十分重要的。

* AAudio：专门为低延迟、高性能音频应用而设计的，API 设计精简，是 Google 推荐的新应用构建音频的应用接口。掌握这种音频渲染方法，为现有应用中增加这种音频的渲染能力是十分有益的。但是它仅适合 Android 8.0 及以上版本，并且在一些品牌的特殊 Rom 版本中适配性不是特别好。