pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "stream_relay"

include(":app")
include(":libutils")
include(":libausbc")
include(":libuvc")
include(":libnative")
include(":libuvccommon")