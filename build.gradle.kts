plugins {
    id("java")
}

group = "moe.nea"
version = "1.0-SNAPSHOT"


java.toolchain.languageVersion.set(JavaLanguageVersion.of(21))

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("com.google.code.gson:gson:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}
