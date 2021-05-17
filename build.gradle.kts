plugins {
    kotlin("jvm") version "1.5.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
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
    jar {
        enabled = false
    }
    build {
        finalizedBy(shadowJar)
    }
}
