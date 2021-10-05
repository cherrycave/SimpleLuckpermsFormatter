plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.16.5-R0.1-SNAPSHOT")
    compileOnly("net.luckperms", "api", "5.3")
    implementation("org.bstats", "bstats-bukkit", "2.2.1")
}

tasks {
    shadowJar {
        relocate("org.bstats", "de.nycode.slpf.bstats")
        relocate("kotlin", "de.nycode.slpf.kotlin")
        relocate("org.jetbrains", "de.nycode.slpf.jetbrains")
        relocate("org.intellij", "de.nycode.slpf.intellij")
    }
    jar {
        enabled = false
    }
    build {
        finalizedBy(shadowJar)
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
}
