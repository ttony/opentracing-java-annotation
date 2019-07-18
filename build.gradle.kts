plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm").version("1.3.20")
    id("io.freefair.aspectj.post-compile-weaving").version("3.8.0")
}

repositories {
    maven("https://artifactory.global.standardchartered.com/artifactory/maven-release")
}


tasks.withType<Test> {
    jvmArgs("-javaagent:/Users/tony/Downloads/aspectjweaver-1.9.4.jar") // add line
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.aspectj:aspectjrt:1.9.4")
    implementation("org.aspectj:aspectjweaver:1.9.4")

//    testImplementation("org.springframework:spring-context:5.1.8.RELEASE")
//    testImplementation("org.springframework:spring-aop:5.1.8.RELEASE")
//    testImplementation("org.springframework:spring-test:5.1.8.RELEASE")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}
