plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.zonlykroks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("org.bouncycastle:bcprov-jdk18on:1.78.1")
}

application {
    mainClass = "de.zonlykroks.OpenEncryptor"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "de.zonlykroks.OpenEncryptor"
    }
}

tasks.withType<JavaCompile> {
    options.release = 22
}

