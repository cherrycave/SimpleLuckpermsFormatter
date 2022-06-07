plugins {
    kotlin("jvm") version "1.6.21"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

group = "de.nycode"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperDevBundle("1.18.2-R0.1-SNAPSHOT")
    compileOnly("net.luckperms", "api", "5.4")
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    build {
        dependsOn(reobfJar)
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}
