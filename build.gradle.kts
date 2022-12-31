import org.gradle.internal.os.OperatingSystem

plugins {
    java
    application
    id("org.beryx.jlink") version "2.26.0"
}

group = "io.gitlab.jfronny"
version = "1.0"

application {
    mainClass.set("io.gitlab.jfronny.javagi.example.Main")
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

jlink {
    addOptions("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages", "--verbose")
    launcher {
        name = "javagi-multiplatform"
    }
    jpackage {
        vendor = "Some Corp"
        jvmArgs.add("--enable-preview")
        if(OperatingSystem.current().isMacOsX) {
            //installerType = "app-image"
        } else if(OperatingSystem.current().isWindows) {
            //installerType = "msi"
            installerOptions.addAll(listOf("--win-per-user-install", "--win-dir-chooser", "--win-menu"))
        } else {
            //installerType = "deb"
        }
    }
}