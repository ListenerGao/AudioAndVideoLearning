class Config {
    static applicationId = 'com.listenergao.audioandvideolearning'
    static appName = ''

    static compileSdkVersion = 30
    static buildToolsVersion = '30.0.3'
    static minSdkVersion = 19
    static targetSdkVersion = 30

    static versionCode = 1
    static versionName = '1.0.0'

    static gradlePluginVersion = '4.2.2'
    static kotlinVersion = '1.6.10'


    static pluginConfig = [
            plugin_gradle: "com.android.tools.build:gradle:$gradlePluginVersion",
            plugin_kotlin: "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    ]

    static depConfig = [
            kotlin:"org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion",
            androidx_appcompat:"androidx.appcompat:appcompat:1.0.0",
            androidx_constraintlayout:"androidx.constraintlayout:constraintlayout:1.1.3",
            androidx_recyclerview:"androidx.recyclerview:recyclerview:1.2.0",
            androidx_cardview:"androidx.cardview:cardview:1.0.0",
            BaseRecyclerViewAdapterHelper:"com.github.CymChad:BaseRecyclerViewAdapterHelper:3.0.7",
    ]
}