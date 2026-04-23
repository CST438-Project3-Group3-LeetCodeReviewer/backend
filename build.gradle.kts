plugins {
	java
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.group3"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.postgresql:postgresql")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	// implementation("org.springframework.boot:spring-boot-starter-webmvc")
	// testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	
	testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.fasterxml.jackson.core:jackson-databind")


	//I think we should leave this for local test files it wont break supabase
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("com.h2database:h2")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
