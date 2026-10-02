plugins {
    `java`
}

group = "kmp.plugin.fix"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-commons:9.7.1")

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to "1.0",
            "Premain-Class" to "kmp.plugin.fix.IosSimulatorFix",
            "Agent-Class" to "kmp.plugin.fix.IosSimulatorFix",
            "Can-Retransform-Classes" to "true",
            "Can-Redefine-Classes" to "true"
        )
    }
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("kmp-plugin-ios-sim-fixes.jar")
}

tasks.test {
    useJUnitPlatform()
    dependsOn(tasks.jar)
    jvmArgs("-javaagent:${tasks.jar.get().archiveFile.get().asFile.absolutePath}")
}
