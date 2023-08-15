val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val exposedVersion:String by project

plugins {
    kotlin("jvm") version "1.8.22"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.8.22"
}

group = "dev.vengateshm"
version = "0.0.1"

application {
    mainClass.set("dev.vengateshm.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    // Embedded server
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    //Auth
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-auth-jwt:$ktorVersion")
    //Logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    //Content Negotiation
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    //Serialization
    //implementation("io.ktor:ktor-serialization-gson:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    //Call Logging
    implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
    //Status Pages
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    //Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-kotlin-datetime:$exposedVersion")
    //MySQL connector
    implementation("mysql:mysql-connector-java:8.0.33")
    // Create database connection pool
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("commons-codec:commons-codec:1.16.0")
    // Google API Client
    implementation("com.google.api-client:google-api-client:2.2.0")
    //Tests
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}