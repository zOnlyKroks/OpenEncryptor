plugins {
    id("java")
}

group = "de.zonlykroks"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:24.0.0")
    implementation ("org.bouncycastle:bcprov-jdk18on:1.77")
}