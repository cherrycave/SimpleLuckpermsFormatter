plugins {
    java
    kotlin("jvm") version "2.3.21"
    `maven-publish`
}

group = "dev.boecker.cherrycave"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.+")
    compileOnly("net.luckperms:api:5.5")
}

kotlin {
    jvmToolchain(25)
}

publishing {
    repositories {
        maven {
            setUrl("https://maven.boecker.dev/releases")

            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_SECRET")
            }
        }
    }

    publications {
        publications {
            create<MavenPublication>(project.name) {
                from(components["kotlin"])
                pom {
                    name.set(project.name)
                    description.set("simple luck perms formatter, as a library for disabling formatting on the fly")
                    url.set("https://github.com/cherrycave/simpleluckpermsformatter")

                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://github.com/cherrycave/simpleluckpermsformatter/LICENSE")
                        }
                    }

                    developers {
                        developer {
                            name.set("Lou Emma Böcker")
                            email.set("lou@boecker.dev")
                            organizationUrl.set("https://www.boecker.dev")
                        }
                    }
                }
            }
        }
    }
}
