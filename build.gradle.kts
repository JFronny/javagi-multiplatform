plugins {
    java
    application
    id("org.beryx.jlink") version "2.26.0"
    id("de.undercouch.download") version "5.3.0"
}

group = "io.gitlab.jfronny"
version = "1.0"

application {
    mainClass.set("io.gitlab.jfronny.javagi.example.Main")
    mainModule.set("io.gitlab.jfronny.javagi.example")
}

repositories {
    mavenCentral()
    // Mirror for javagi maven, since that requires authentication
    // and I didn't want to figure out how to pass the GitHub Actions token here
    maven("https://maven.frohnmeyer-wds.de/java-gi")
}

dependencies {
    implementation("io.github.jwharm.javagi:gtk4:0.3-SNAPSHOT")
    implementation("io.github.jwharm.javagi:glib:0.3-SNAPSHOT")
}

tasks.compileJava.get().options.compilerArgs.add("--enable-preview")
tasks.run.get().jvmArgs!!.add("--enable-preview")

val os = org.gradle.internal.os.OperatingSystem.current()!!

if (os.isWindows) {
    val downloadNatives by tasks.registering(de.undercouch.gradle.tasks.download.Download::class) {
        src("https://github.com/jwharm/java-gi/releases/download/natives/natives.zip")
        dest(buildDir.resolve("natives.zip"))
        overwrite(false)
    }

    tasks.jpackageImage {
        dependsOn(downloadNatives)
        doLast {
            copy {
                from(zipTree(downloadNatives.get().dest))
                into(buildDir.resolve("jpackage/${project.name}/app"))
            }
        }
    }
}

jlink {
    addOptions(
        "--strip-debug",
        "--compress", "2",
        "--no-header-files",
        "--no-man-pages",
        "--verbose"
    )
    jpackage {
        vendor = "Some Corp"
        jvmArgs.addAll(listOf(
            "--enable-preview",
            "--enable-native-access=org.glib",
            "--enable-native-access=org.gtk"
        ))
        installerName = "JavaGI Multiplatform Example"
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
            //imageOptions.add("--win-console") // Enable this for debugging
        } else {
            //installerType = "deb"
        }
    }
}
