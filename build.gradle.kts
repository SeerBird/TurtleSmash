import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("dev.hydraulic.conveyor") version "1.0.1"
}

group = "seerbird.games"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
javafx {
    modules("javafx.controls")
}
kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.apache.commons:commons-math3:3.0")
                implementation("com.esotericsoftware:kryonet:2.22.0-RC1")
                implementation("org.openjfx:javafx-base:11")
            }
        }
        val jvmTest by getting
    }
}
compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "TurtleSmash"
            packageVersion = "1.0.0"
        }
    }
}
