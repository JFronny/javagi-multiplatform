plugins {
    java
    application
    id("org.beryx.jlink") version "3.0.1"
    id("de.undercouch.download") version "5.6.0"
}

group = "io.gitlab.jfronny"
version = "1.0"

application {
    mainClass.set("io.gitlab.jfronny.javagi.example.Main")
    mainModule.set("io.gitlab.jfronny.javagi.example")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.github.jwharm.javagi:gtk:0.10.2")
}

val os = org.gradle.internal.os.OperatingSystem.current()!!
val appName = "JavaGI Multiplatform Example"

if (os.isWindows) {
    val downloadNatives by tasks.registering(de.undercouch.gradle.tasks.download.Download::class) {
        src("https://github.com/jwharm/java-gi/releases/download/libraries/natives.zip")
        dest(layout.buildDirectory.file("natives.zip"))
        overwrite(false)
    }

    tasks.jpackageImage {
        dependsOn(downloadNatives)
        doLast {
            copy {
                from(zipTree(downloadNatives.get().dest))
                into(layout.buildDirectory.dir("jpackage/$appName/app"))
            }
            copy {
                from(zipTree(downloadNatives.get().dest))
                into(layout.buildDirectory.dir("jpackage/$appName/runtime/bin"))
            }
        }
    }
}

fun computeDebugVersion(): String {
    val time = System.currentTimeMillis() / 1000 / 60 / 5 // 5 minute intervals
    // installer versions MUST be  [0-255].[0-255].[0-65535]
    // in other words,  8,8,16 bits
    val major = (time / (256 * 65536))
    val minor = (time / 65536) % 256
    val patch = time % 65536
    return "$major.$minor.$patch"
}

jlink {
    addOptions(
        "--strip-debug",
        "--no-header-files",
        "--no-man-pages",
        "--verbose"
    )
    launcher {
        name = appName
    }
    jpackage {
        vendor = "Some Corp"
        jvmArgs.addAll(listOf(
            "--enable-native-access=org.gnome.gtk",
            "--enable-native-access=org.gnome.glib",
            "--enable-native-access=org.gnome.gobject",
            "--enable-native-access=org.gnome.gio"
        ))
        if(os.isMacOsX) {
            //installerType = "app-image"
        } else if(os.isWindows) {
            installerType = "msi" // Only generate the msi installer, since the behavior for MSIs and EXEs differs
            installerOptions.addAll(listOf(
                "--win-per-user-install",
                "--win-dir-chooser",
                "--win-menu",
                "--win-upgrade-uuid", "1d2e433e-f2e1-43bc-9cd4-60d1ec6b7833" // Update this UUID if you fork the project!!!
           ))
            appVersion = computeDebugVersion() // ensure every debug build has a unique version to allow updates. ONLY do this for debug builds (and ensure they use a different UUID!)
            //imageOptions.add("--win-console") // Enable this for debugging
        } else {
            //installerType = "deb"
        }
    }
}
